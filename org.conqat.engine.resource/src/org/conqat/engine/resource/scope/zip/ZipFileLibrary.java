/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 The ConQAT Project                                   |
|                                                                          |
| Licensed under the Apache License, Version 2.0 (the "License");          |
| you may not use this file except in compliance with the License.         |
| You may obtain a copy of the License at                                  |
|                                                                          |
|    http://www.apache.org/licenses/LICENSE-2.0                            |
|                                                                          |
| Unless required by applicable law or agreed to in writing, software      |
| distributed under the License is distributed on an "AS IS" BASIS,        |
| WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. |
| See the License for the specific language governing permissions and      |
| limitations under the License.                                           |
+-------------------------------------------------------------------------*/
package org.conqat.engine.resource.scope.zip;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.collections.CaseInsensitiveStringSet;
import org.conqat.lib.commons.filesystem.CanonicalFile;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Library that coordinates the access to ZIP files.
 * <p>
 * The implementation is slightly more complicated that one would initially
 * expect. The reason is, that we basically have to support two use cases. The
 * first is, that a large system consisting of many files in contained in a
 * single ZIP file. In this case we must ensure to reuse the same
 * {@link ZipFile} instance, as reopening it for each single contained file
 * would be expensive (reloading of ZIP index structures). The second case is
 * that we analyze files that are scattered over many individual ZIP files. In
 * this case we can not keep all of them opened, as the system would run out of
 * file handles pretty fast. Combinations of both scenarios are possible as
 * well.
 * <p>
 * To solve this issue, we keep only a small set of {@link ZipFile}s actually
 * open (managed in a LRU fashion). Thus, we can keep the number of file handles
 * that are used bounded, while we still can avoid reopening a ZIP file in many
 * cases. The rationale here is that due to the way we construct scopes, we are
 * very likely to have most accesses to entries into a single ZIP file clustered
 * together (unless we have some strange sorting).
 * <p>
 * The currently open {@link ZipFile}s are managed in {@link #zipFileCache}. We
 * also want to be thread safe (a processor might potentially use
 * multi-threading), so we have to protect the cache against closing a
 * {@link ZipFile}, while another thread still reads it. One the other hand, we
 * want to exploit concurrency. To achieve this, we implement reference counting
 * via the methods {@link #access(CanonicalFile, boolean)} and
 * {@link #release(CanonicalFile)}. As we limit this reference counting to this
 * single class, we think it is still manageable.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46960 $
 * @ConQAT.Rating GREEN Hash: A1B761B0A12B4B8BD4024F463C1EC656
 */
public class ZipFileLibrary {

	/** The logger. */
	private static final Logger LOGGER = Logger.getLogger(ZipFile.class);

	/** Maximal number of entries kept. */
	private static final int MAX_SIZE = 6;

	/** Singleton instance. */
	private static ZipFileLibrary instance;

	/**
	 * The cache used for the ZIP files. This should only be accessed via
	 * {@link #access(CanonicalFile, boolean)} and
	 * {@link #release(CanonicalFile)} to enforce synchronization.
	 */
	@SuppressWarnings("serial")
	private final Map<CanonicalFile, ZipFileCounter> zipFileCache = new LinkedHashMap<CanonicalFile, ZipFileCounter>(
			2 * MAX_SIZE, .6f, true) {

		/**
		 * We only remove the eldest entry, if it is unused. As the usage period
		 * is typically very short (entries are used only during a single method
		 * call) this should not be a problem. However, there are cases when the
		 * number of stored entries is temporarily higher than MAX_SIZE.
		 */
		@Override
		protected boolean removeEldestEntry(
				Map.Entry<CanonicalFile, ZipFileCounter> eldest) {
			if (eldest.getValue().isUnused() && size() > MAX_SIZE) {
				try {
					eldest.getValue().close();
				} catch (IOException e) {
					// there is not much we can do here, as propagating the
					// error could mess up the map's internal structure, so we
					// just log it.
					LOGGER.error(
							"Unexpected I/O problem while closing ZIP file: "
									+ e.getMessage(), e);
				}
				return true;
			}
			return false;
		}
	};

	/** Constructor. */
	private ZipFileLibrary() {
		// nothing to do
	}

	/**
	 * Accesses the {@link ZipFile} for the given canonical file. It is
	 * important to release this in any case (i.e. in a finally block).
	 */
	private ZipFileWrapper access(CanonicalFile zipFile, boolean caseSensitive)
			throws IOException {
		synchronized (zipFileCache) {
			ZipFileCounter counter = zipFileCache.get(zipFile);
			if (counter == null) {
				counter = new ZipFileCounter(zipFile);
				zipFileCache.put(zipFile, counter);
			}
			return counter.access(caseSensitive);
		}
	}

	/**
	 * Releases a ZIP file retrieved via {@link #access(CanonicalFile, boolean)}
	 * .
	 */
	private void release(CanonicalFile zipFile) {
		synchronized (zipFileCache) {
			ZipFileCounter counter = zipFileCache.get(zipFile);
			// the get() should never be null, as we do not remove used entries
			// from the cache
			CCSMAssert.isNotNull(counter);
			counter.release();
		}
	}

	/** Returns the singleton instance. */
	public static ZipFileLibrary getInstance() {
		if (instance == null) {
			instance = new ZipFileLibrary();
		}
		return instance;
	}

	/**
	 * Returns whether in the ZIP corresponding to the given file is a certain
	 * entry.
	 */
	public boolean hasEntry(CanonicalFile file, String newEntry)
			throws IOException {
		return hasEntry(file, newEntry, true);
	}

	/**
	 * Returns whether in the ZIP corresponding to the given file is a certain
	 * entry. Optionally allows case-insensitive path resolving.
	 */
	public boolean hasEntry(CanonicalFile file, String entry,
			boolean caseSensitive) throws IOException {
		return getEntryName(file, entry, caseSensitive) != null;
	}

	/**
	 * Returns the case sensitive name of a zip entry. If the entry does not
	 * exist, null is returned.
	 */
	public String getEntryName(CanonicalFile file, String entry,
			boolean caseSensitive) throws IOException {
		ZipFileWrapper zipFile = access(file, caseSensitive);
		try {
			synchronized (zipFile) {
				ZipEntry zipEntry = zipFile.getEntry(entry);
				if (zipEntry != null) {
					return zipEntry.getName();
				}
			}
		} finally {
			release(file);
		}
		return null;
	}

	/** Lists the non-directory entries contained in a ZIP file as strings. */
	public List<String> listEntries(CanonicalFile file) throws IOException {
		// case sensitivity does not matter
		ZipFileWrapper zipFile = access(file, true);
		try {
			List<String> result = new ArrayList<String>();
			synchronized (zipFile) {
				for (ZipEntry entry : zipFile.entries()) {
					if (!entry.isDirectory()) {
						result.add(entry.getName());
					}
				}
			}
			return result;
		} finally {
			release(file);
		}
	}

	/** Returns the binary content of an entry (case-sensitive). */
	public byte[] readContent(CanonicalFile zipFile, String entryName)
			throws IOException {
		return readContent(zipFile, entryName, true);
	}

	/** Returns the binary content of an entry. */
	public byte[] readContent(CanonicalFile file, String entryName,
			boolean caseSensitive) throws IOException {
		ZipFileWrapper zipFile = access(file, caseSensitive);
		try {
			ZipEntry entry = null;
			synchronized (zipFile) {
				entry = zipFile.getEntry(entryName);
			}
			if (entry == null) {
				throw new IOException("Entry " + entryName + " not valid for "
						+ file + "! Was the ZIP file modified?");
			}
			return readEntry(zipFile, entry);
		} finally {
			release(file);
		}
	}

	/** Reads the content of a {@link ZipEntry}. */
	private static byte[] readEntry(ZipFileWrapper zipFile, ZipEntry entry)
			throws IOException, EOFException {
		int size = (int) entry.getSize();
		if (size < 0) {
			throw new IOException("Error: Size for entry " + entry.getName()
					+ " not stored in ZIP file " + zipFile.getName());
		}

		byte[] result = new byte[size];
		InputStream in = null;
		synchronized (zipFile) {
			// we assume that only the assess to the stream needs
			// synchronization; however, the documentation is not clear here so
			// it might be that we have to extend synchronization in the future.
			in = zipFile.getInputStream(entry);
		}
		try {
			FileSystemUtils.safeRead(in, result);
		} finally {
			FileSystemUtils.close(in);
		}
		return result;
	}

	/**
	 * Lists all zip file entries and ignores any encoding problems due to
	 * illegal path characters.
	 */
	private static List<ZipEntry> listEntries(ZipFile zipFile) {
		List<ZipEntry> entriesList = new ArrayList<ZipEntry>();
		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		int ignoredEntries = 0;
		while (entries.hasMoreElements()) {
			try {
				entriesList.add(entries.nextElement());
			} catch (IllegalArgumentException e) {
				if ("MALFORMED".equals(e.getMessage())) {
					// (MP) Workaround for JVM bug
					// http://bugs.sun.com/view_bug.do?bug_id=4244499
					// The Java-Specification expects filenames
					// inside ZIP files to be encoded with UTF-8.
					// Contrary, Windows Explorer or similar tools
					// encode filenames using IBM437 (CP437). If a
					// name contains a foreign character (i.e.
					// ��,��,��,...) an IllegalArgumentException with
					// message MALFORMED is thrown. Strangely, I
					// encountered this bug just on 64-bit Windows
					// JVMs.
					// Java7 'fixes' this bug by introducing a new
					// constructor for ZipFile that allows one to
					// specify the filename encoding.

					ignoredEntries++;
					continue; // ignore exception and this zip entry
				}
				throw e; // other exceptions are thrown again
			}
		}

		if (ignoredEntries > 0) {
			LOGGER.info("Ignored " + ignoredEntries + " entries of "
					+ zipFile.getName() + " due to filename encoding problems.");
		}
		return entriesList;
	}

	/** A mixture of a {@link ZipFile} and a reference counter. */
	private static final class ZipFileCounter {

		/** Current number of accesses. */
		private int numAccesses = 0;

		/** The {@link ZipFile}. May be null to indicate a closed file. */
		private ZipFile zipFile;

		/** The underlying file. */
		private final CanonicalFile file;

		/**
		 * Mapping from case-insensitive lower-cased zip entries to
		 * case-sensitive ones.
		 */
		private CaseInsensitiveStringSet caseInsensitiveEntryMapping;

		/** Constructor. */
		public ZipFileCounter(CanonicalFile file) {
			this.file = file;
		}

		/** Accesses the underlying. */
		public synchronized ZipFileWrapper access(boolean caseSensitive)
				throws ZipException, IOException {
			// lazy initialize the ZipFile
			if (zipFile == null) {
				zipFile = new ZipFile(file);
			}

			CaseInsensitiveStringSet mapping = null;
			if (!caseSensitive) {
				if (caseInsensitiveEntryMapping == null) {
					createCaseInsensitiveMapping();
				}
				mapping = caseInsensitiveEntryMapping;
			}

			numAccesses += 1;
			return new ZipFileWrapper(zipFile, mapping);
		}

		/** Returns whether the {@link #zipFile} is currently in use. */
		public synchronized boolean isUnused() {
			return numAccesses == 0;
		}

		/** Releases the {@link #zipFile}. */
		public synchronized void release() {
			CCSMPre.isTrue(numAccesses > 0, "Too many releases!");
			numAccesses -= 1;
		}

		/** Closes this file. */
		public synchronized void close() throws IOException {
			CCSMAssert.isTrue(isUnused(),
					"The class logic should avoid closing of used files!");
			zipFile.close();
			zipFile = null;
		}

		/**
		 * Creates a case-insensitive (lowercased) mapping to paths in the zip
		 * file.
		 */
		private void createCaseInsensitiveMapping() throws IOException {
			caseInsensitiveEntryMapping = new CaseInsensitiveStringSet();

			for (ZipEntry entry : listEntries(zipFile)) {
				addMapping(entry.getName());
			}
		}

		/**
		 * Adds a path to the case-insensitive mapping.
		 */
		private void addMapping(String name) throws IOException {

			if (name.endsWith("/")) {
				addMapping(StringUtils.stripSuffix("/", name));
			}

			if (!caseInsensitiveEntryMapping.add(name)) {
				throw new IOException("Case insensitive mapping in zipfile "
						+ zipFile.getName() + " not unique for " + name);
			}
		}
	}

	/**
	 * Wraps basic ZipFile functionality and adds case-insensitive entry
	 * reading.
	 */
	private static class ZipFileWrapper {

		/** The wrapped zip file. */
		private final ZipFile zipFile;

		/**
		 * Mapping from case-insensitive lower-cased zip entries to
		 * case-sensitive ones.
		 */
		private final CaseInsensitiveStringSet caseInsensitiveEntryMapping;

		/** Constructor. */
		public ZipFileWrapper(ZipFile zipFile, CaseInsensitiveStringSet mapping) {
			this.zipFile = zipFile;
			this.caseInsensitiveEntryMapping = mapping;
		}

		/** @see ZipFile#getEntry(String) */
		public ZipEntry getEntry(String name) {
			name = getZipEntryPath(name, caseInsensitiveEntryMapping == null);

			if (name == null)
				return null;

			return zipFile.getEntry(name);
		}

		/** @see ZipFile#getName() */
		public String getName() {
			return zipFile.getName();
		}

		/** @see ZipFile#entries() */
		public List<ZipEntry> entries() {
			return listEntries(zipFile);
		}

		/** @see ZipFile#getInputStream(ZipEntry) */
		public InputStream getInputStream(ZipEntry entry) throws IOException {
			return zipFile.getInputStream(entry);
		}

		/**
		 * Returns the path to an entry in the zip file, with optional
		 * case-insensitive resolution.
		 */
		private String getZipEntryPath(String name, boolean caseSensitive) {
			if (caseSensitive) {
				return name;
			}

			return caseInsensitiveEntryMapping.get(name);
		}
	}
}