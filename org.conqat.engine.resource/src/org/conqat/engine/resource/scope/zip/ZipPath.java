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
package org.conqat.engine.resource.scope.zip;

/**
 * Common representation for paths to entries inside a Zip file. Those paths
 * have two parts: The path to the Zip file and the path inside the Zip file to
 * the entry.
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 41885 $
 * @ConQAT.Rating YELLOW Hash: 386BAC61060CA4499FE6127B38E05630
 */
public class ZipPath {

	/**
	 * Separation string used between the file name and the entry. This is
	 * consistent with ZIP file URLs in Java.
	 */
	public static final String ZIPFILE_SEPARATOR = "!";

	/** The path to a Zip file. */
	private String zipFilename;

	/** The path inside the Zip file to a entry. */
	private String zipEntryName;

	/**
	 * Creates a zip path from a single path containing an
	 * {@link ZipPath#ZIPFILE_SEPARATOR}. If no separator is found, the entry
	 * path is null.
	 */
	public ZipPath(String path) {
		if (path.contains(ZIPFILE_SEPARATOR)) {
			String[] parts = path.split(ZIPFILE_SEPARATOR, 2);
			zipFilename = parts[0];
			zipEntryName = parts[1];
		} else {
			zipFilename = path;
			zipEntryName = null;
		}
	}

	/** Constructs a ZipPath from a filename and an entry path. */
	public ZipPath(String zipFilename, String zipEntryName) {
		this.zipFilename = zipFilename;
		this.zipEntryName = zipEntryName;
	}

	/** The filesystem path to a Zip file. */
	public String getZipFilename() {
		return zipFilename;
	}

	/** The path inside a Zip file to an entry. */
	public String getEntryName() {
		return zipEntryName;
	}

	/** Returns true if the path contains an entry to a file inside the Zip. */
	public boolean isValid() {
		return zipEntryName != null;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return getPath();
	}

	/**
	 * Returns the full path to the entry, including the path to the Zip file,
	 * separated by {@link ZipPath#ZIPFILE_SEPARATOR}
	 */
	public String getPath() {
		if (!isValid()) {
			return zipFilename;
		}
		return zipFilename + ZIPFILE_SEPARATOR + zipEntryName;
	}
}