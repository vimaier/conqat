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

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipFile;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.logging.IncludeExcludeListLogMessage;
import org.conqat.engine.commons.logging.StructuredLogTags;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IContentAccessor;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.scope.PatternSupportingScopeBase;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.engine.resource.util.ResourceUtils;
import org.conqat.engine.resource.util.UniformPathUtils;
import org.conqat.lib.commons.filesystem.CanonicalFile;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: juergens $
 * @version $Rev: 41876 $
 * @ConQAT.Rating GREEN Hash: 1E8B1DDE88DF5350D83D01014C510C93
 */
@AConQATProcessor(description = "A scope for reading entries from ZIP (and JAR) files. "
		+ "This does not work for files compressed with gzip or bzip2. "
		+ "The patterns given to this processor affect the entries within the "
		+ "ZIP files and not the selection of ZIP files.")
public class ZipFileScope extends PatternSupportingScopeBase {

	/** Resources describing ZIP files. */
	private final List<IElement> elements = new ArrayList<IElement>();

	/** The list used for storing the accessors created. */
	private final List<IContentAccessor> result = new ArrayList<IContentAccessor>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "entry", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "If this parameter is set, only entries starting with the given prefix are respected. "
			+ "This prefix is also implicitly prepended to all include and exclude patterns, "
			+ "thus the parameter can be used to select a subdirectory in a ZIP file. "
			+ "Note that this prefix affects all ZIP files that are input for this scope.")
	public void setEntryPrefix(
			@AConQATAttribute(name = "prefix", description = "Default is empty String") String entryPrefix) {
		this.entryPrefix = UniformPathUtils.normalizeAllSeparators(entryPrefix);
	}

	/** Stores entry prefix with normalized slashes */
	private String entryPrefix = StringUtils.EMPTY_STRING;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "zip-resource", minOccurrences = 1, description = "A resource hierarchy whose elements describe ZIP files.")
	public void addResource(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) IResource resource) {
		elements.addAll(ResourceTraversalUtils.listElements(resource));
	}

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(attribute = "ref", parameter = "zip-file-logger", optional = true, description = ""
			+ "If this parameter is set, all the files that are read in the zip are logged.")
	public ZipFileLogger zipFileLogger = null;

	/** {@inheritDoc} */
	@Override
	protected IContentAccessor[] createAccessors() throws ConQATException {
		if (elements.isEmpty()) {
			throw new ConQATException(
					"No input ZIP files provided via parameters!");
		}

		for (IElement element : elements) {
			// first try to access the file via its location, as the access via
			// content is realized using temporary files and thus is way more
			// expensive.
			if (!processLocation(element.getLocation())) {
				processBinaryZip(element.getContent());
			}
		}

		getLogger().debug("Created " + result.size() + " content accessors.");

		Set<String> uniformPaths = new HashSet<String>();
		for (IContentAccessor accessor : result) {
			uniformPaths.add(accessor.getUniformPath());
		}
		getLogger().info(
				new IncludeExcludeListLogMessage("files", true, uniformPaths,
						StructuredLogTags.SCOPE, StructuredLogTags.FILES));

		return result.toArray(new IContentAccessor[result.size()]);
	}

	/**
	 * Tries to treat a location as a file. If this is possible, the file is
	 * processed as a ZIP file and true is returned. Otherwise false is returned
	 * (no success).
	 */
	private boolean processLocation(String location) throws ConQATException {
		CanonicalFile file = ResourceUtils.getFileFromLocation(location);
		if (file != null) {
			processZipFile(file);
			return true;
		}
		return false;
	}

	/**
	 * Processes a ZIP file that is given as binary content. To deal with this
	 * situation, we write the content to a temporary file, as the
	 * {@link ZipFile} class can only deal with files.
	 */
	private void processBinaryZip(byte[] content) throws ConQATException {
		CanonicalFile tempFile = getProcessorInfo().getTempFile(
				ZipFileScope.class.getSimpleName(), ".zip");

		FileOutputStream out = null;
		try {
			out = new FileOutputStream(tempFile);
			out.write(content);
		} catch (IOException e) {
			throw new ConQATException("Could not create temporary ZIP file: "
					+ e.getMessage(), e);
		} finally {
			FileSystemUtils.close(out);
		}

		processZipFile(tempFile);
	}

	/**
	 * Processes a single ZIP file and inserts the results into the
	 * {@link #result} list.
	 */
	private void processZipFile(CanonicalFile file) throws ConQATException {
		List<String> entries;
		try {
			entries = ZipFileLibrary.getInstance().listEntries(file);
		} catch (IOException e) {
			throw new ConQATException("Could not access ZIP file " + file
					+ ": " + e.getMessage(), e);
		}

		for (String entry : entries) {
			if (!entry.startsWith(entryPrefix)) {
				continue;
			}

			String relativeEntry = StringUtils.stripPrefix(entryPrefix, entry);
			relativeEntry = StringUtils.stripPrefix(UniformPathUtils.SEPARATOR,
					relativeEntry);
			if (!isIncluded(relativeEntry)) {
				continue;
			}

			result.add(new ZipEntryContentAccessor(file, entry, entryPrefix,
					projectName, caseSensitive, zipFileLogger));
		}
	}
}