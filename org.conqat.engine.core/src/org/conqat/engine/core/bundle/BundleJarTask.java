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
package org.conqat.engine.core.bundle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import org.apache.tools.ant.BuildException;
import org.conqat.lib.commons.filesystem.FileOnlyFilter;
import org.conqat.lib.commons.filesystem.FileSystemUtils;

/**
 * This task creates a single jar file for a bundles classes and libraries plus
 * all classes the bundle depends on. Additional libraries and directories, e.g.
 * the libraries ConQAT core depends on, may be added manually. The name of the
 * jar file is <code>&lt;bundle-id&gt;.jar</code>.
 * <p>
 * See <code>conqat-ant-base</code> for usage of this task.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 8739CB14CFA75EB87032FA77F2E37309
 */
public class BundleJarTask extends BundleTaskBase {

	/** List of additional libraries. */
	private final List<AntElement> libraries = new ArrayList<AntElement>();

	/** List of additional directories. */
	private final List<AntElement> directories = new ArrayList<AntElement>();

	/** Set of libraries that should be excluded from jar file. */
	private final HashSet<String> excludedLibraries = new HashSet<String>();

	/**
	 * This set is used to prevent duplicate entries in the jar file causing
	 * exceptions.
	 */
	private final HashSet<String> jarEntries = new HashSet<String>();

	/** Counts the number of files added to jar for logging purposes. */
	private int fileCount;

	/** This stream is used to create the jar file. */
	private JarOutputStream outStream;

	/** Add an additional library. Called by ANT for &lt;library&gt;-elements. */
	public void addLibrary(AntElement library) {
		libraries.add(library);
	}

	/**
	 * Add an additional directory. Called by ANT for
	 * &lt;directory&gt;-elements.
	 */
	public void addDirectory(AntElement directory) {
		directories.add(directory);
	}

	/**
	 * Set comma separated list of libraries to exclude, eg.
	 * <code>servlet-api.jar, ant.jar</code>. This is encoded in a single string
	 * to allow users of <code>conqat-ant-base</code> to set this parameter by
	 * specifying a property.
	 */
	public void setExcludedLibraries(String libraries) {
		for (String library : libraries.split(",")) {
			excludedLibraries.add(library.trim());
		}
	}

	/** Create jar file. */
	@Override
	protected void execute(Set<BundleInfo> bundleClosure) throws BuildException {

		File jarFile = new File(getProject().getBaseDir().getName() + ".jar");
		jarEntries.clear();
		fileCount = 0;

		try {

			outStream = new JarOutputStream(new FileOutputStream(jarFile));

			addBundles(bundleClosure);

			// add additional libraries and directories
			for (AntElement library : libraries) {
				addJar(new File(library.getName()));
			}
			for (AntElement directory : directories) {
				addDirectory(new File(directory.getName()));
			}

			outStream.close();

			System.out.println("Created " + jarFile.getName() + " from "
					+ fileCount + " files.");

		} catch (IOException e) {
			throw new BuildException(e);
		}

	}

	/** Add all bundles of the bundle closure. */
	private void addBundles(Set<BundleInfo> bundleClosure) throws IOException {
		for (BundleInfo bundleInfo : bundleClosure) {
			addBundle(bundleInfo);
		}
	}

	/** Add libraries and classes directory for a bundle to the jar file. */
	private void addBundle(BundleInfo bundleInfo) throws IOException {
		for (File library : bundleInfo.getLibraries()) {
			addJar(library);

		}
		if (bundleInfo.hasClasses()) {
			addDirectory(bundleInfo.getClassesDirectory());
		}
	}

	/** Add all members of a jar file to the result jar file. */
	private void addJar(File jarFile) throws IOException {

		if (excludedLibraries.contains(jarFile.getName())) {
			System.out.println("Skipping excluded library " + jarFile.getName()
					+ ".");
			return;
		}

		JarFile jar = new JarFile(jarFile);
		Enumeration<JarEntry> entries = jar.entries();

		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			if (!entry.isDirectory()) {
				InputStream entryStream = jar.getInputStream(entry);
				createJarEntry(entryStream, entry.getName());
			}
		}

	}

	/** Add all files of a directory to the result jar file. */
	private void addDirectory(File directory) throws IOException {
		List<File> files = FileSystemUtils.listFilesRecursively(directory,
				new FileOnlyFilter());

		for (File file : files) {
			FileInputStream input = new FileInputStream(file);
			String entryName = file.getAbsolutePath().substring(
					directory.getAbsolutePath().length() + 1);
			// works for forward slashes only
			entryName = entryName.replace(File.separatorChar, '/');

			createJarEntry(input, entryName);
		}
	}

	/**
	 * Create new jar entry. This creates the entry, copies the input stream to
	 * the jar file and <em>closes</em> the input stream.
	 */
	private void createJarEntry(InputStream input, String entryName)
			throws IOException {
		if (jarEntries.contains(entryName)) {
			System.out.println("Skipping duplicate entry " + entryName + ".");
			return;
		}
		outStream.putNextEntry(new ZipEntry(entryName));
		FileSystemUtils.copy(input, outStream);
		outStream.closeEntry();
		input.close();
		jarEntries.add(entryName);
		fileCount++;
	}

}