/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
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
package org.conqat.engine.core.bundle.library;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.conqat.engine.core.bundle.BundleInfo;
import org.conqat.engine.core.bundle.library.license.CustomLicense;
import org.conqat.engine.core.bundle.library.license.ELicense;
import org.conqat.engine.core.bundle.library.license.ILicense;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableSet;
import org.conqat.lib.commons.filesystem.FileSystemUtils;

/**
 * Descriptor for a Java library holding information about its name, version and
 * license. A library can consist of multiple jar files. The link between
 * library descriptor files and jar files is based on file names. I.e.
 * <code>my-library.lib</code> will cover all jar files with a name matching
 * <code>my-library(.*).jar</code>.
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 46536 $
 * @ConQAT.Rating YELLOW Hash: A9251E088A7DF092B057597466D38322
 */
public class LibraryDescriptor {

	/** The name of the library. */
	private final String name;

	/** A short description of the library. */
	private final String description;

	/** The version string of the library. */
	private final String version;

	/** The website url with information about the library. */
	private final String website;

	/** Optional remarks about the usage of the library. */
	private final String remarks;

	/** The license of the library. */
	private ILicense license;

	/** The library descriptor file. */
	private final File descriptorFile;

	/** The bundle this descriptor lies in. */
	private final BundleInfo bundle;

	/** Constructor. */
	public LibraryDescriptor(File descriptorFile, BundleInfo bundle)
			throws IOException {
		this.descriptorFile = descriptorFile;
		this.bundle = bundle;

		Properties props = new Properties();
		props.load(new FileReader(descriptorFile));

		name = props.getProperty("name");
		version = props.getProperty("version");
		website = props.getProperty("website");
		description = props.getProperty("description");
		remarks = props.getProperty("remarks");

		String licenseName = props.getProperty("license");
		license = ELicense.fromName(licenseName);
		if (license == null) {
			String descriptorName = FileSystemUtils
					.getFilenameWithoutExtension(descriptorFile);
			File noticeFile = new File(descriptorFile.getParentFile(),
					descriptorName.concat(".notice"));
			license = CustomLicense.fromProperties(props, noticeFile);
		}
	}

	/**
	 * @return The unique identifier of the descriptor within its bundle, i.e.
	 *         the filename without the .lib extension.
	 */
	public String getId() {
		return FileSystemUtils.getFilenameWithoutExtension(descriptorFile);
	}

	/** @return The name of the library. */
	public String getName() {
		return name;
	}

	/** @return The short description of the library. */
	public String getDescription() {
		return description;
	}

	/** @return The version string of the library. */
	public String getVersion() {
		return version;
	}

	/** @return The website url with information about the library. */
	public String getWebsite() {
		return website;
	}

	/** @return optional remarks about the usage of the library or null. */
	public String getRemarks() {
		return remarks;
	}

	/** @return The license of the library. */
	public ILicense getLicense() {
		return license;
	}

	/** @return The list of all libraries covered by this descriptor. */
	public UnmodifiableSet<File> getLibraries() {
		Set<File> libraries = new HashSet<File>();
		final String descriptorId = getId();
		for (File library : bundle.getLibraries()) {
			if (library.getName().startsWith(descriptorId)) {
				libraries.add(library);
			}
		}
		return CollectionUtils.asUnmodifiable(libraries);
	}

	/** @return The library descriptor property file. */
	public File getDescriptorFile() {
		return descriptorFile;
	}

	/** @returns The bundle containing the descriptor. */
	public BundleInfo getBundle() {
		return bundle;
	}
}
