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
package org.conqat.engine.core.driver.error;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.URL;

import org.conqat.lib.commons.filesystem.ClassPathUtils;
import org.conqat.lib.commons.filesystem.FileSystemUtils;

/**
 * This class indicates the origin of an exception that was encountered. It can
 * either point to general files, or to class files.
 * 
 * In order to indicate that the location is unknown, use the null-object
 * instance {@link #UNKNOWN}.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 36645 $
 * @ConQAT.Rating GREEN Hash: 083D3994EAE37EAC2E7274531DF8CCF1
 */
public class ErrorLocation implements Serializable {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** Null object instance */
	public static final ErrorLocation UNKNOWN = new NullLocation();

	/** File location */
	private final File file;

	/** Fully qualified name of class location */
	private final String className;

	/** Private constructor for null object */
	private ErrorLocation() {
		file = null;
		className = null;
	}

	/** File location constructor */
	public ErrorLocation(File file) {
		className = null;
		try {
			this.file = file.getCanonicalFile();
		} catch (IOException e) {
			throw new AssertionError(e.getMessage());
		}
	}

	/** Class location constructor */
	public ErrorLocation(Class<?> clazz) {
		className = clazz.getCanonicalName();
		file = getClassFile(clazz);
	}

	/** Constructor for URL error location. */
	public ErrorLocation(URL url) {
		className = null;
		file = getLocationFileFromURL(url);
	}

	/**
	 * Determines the .class file of a class. In the case of a class in a JAR
	 * file, the jar file is returned.
	 */
	private File getClassFile(Class<?> clazz) {
		return getLocationFileFromURL(ClassPathUtils.obtainClassFileURL(clazz));
	}

	/**
	 * Attempts to resolve a file location from an URL. We assume that in all
	 * contexts where we use ConQAT the classes and blocks are either loaded
	 * from a file or a JAR. As we can not imagine ConQAT running in an applet
	 * or using some other fancy classloader, we fix this assumption using an
	 * assertion.
	 */
	private File getLocationFileFromURL(URL url) throws AssertionError {
		if ("jar".equals(url.getProtocol())) {
			return FileSystemUtils.extractJarFileFromJarURL(url);
		}

		try {
			return new File(url.toURI());
		} catch (URISyntaxException e) {
			throw new AssertionError(e.getMessage());
		}
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		if (isClass()) {
			return "Class: " + className;
		}

		return "File: " + file.getAbsolutePath();
	}

	/**
	 * Returns the file the location points to. For classes this is the actual
	 * class file.
	 */
	public File getFile() {
		return file;
	}

	/**
	 * Returns the fully qualified class name, if the location points to a class
	 * file, and <code>null</code>, if not.
	 */
	public String getClassName() {
		return className;
	}

	/** Returns true, if the location is a class location */
	public boolean isClass() {
		return className != null;
	}

	/** Returns the hash code of the associated {@link File}. */
	@Override
	public int hashCode() {
		return file.hashCode();
	}

	/**
	 * Two {@link ErrorLocation}s are equal if their associated {@link File}s
	 * are equal.
	 */
	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (!(object instanceof ErrorLocation)) {
			return false;
		}

		ErrorLocation other = (ErrorLocation) object;

		// theoretically one object could point to a class file without being an
		// actual class location.
		if (isClass() != other.isClass()) {
			return false;
		}
		return file.equals(other.file);
	}

	/**
	 * Returns true, if location is unknown (i.e. the location object is the
	 * null-object {@link #UNKNOWN}).
	 */
	public boolean isUnknown() {
		return false;
	}

	/** Unknown location. */
	private static class NullLocation extends ErrorLocation {

		/** Serial version UID. */
		private static final long serialVersionUID = 1;

		/** Returns <code>true</code>. */
		@Override
		public boolean isUnknown() {
			return true;
		}

		/** Returns string for unknown location. */
		@Override
		public String toString() {
			return "Unknown location";
		}

		/** Return 0. */
		@Override
		public int hashCode() {
			return 0;
		}

		/**
		 * Only returns <code>true</code> if the other object is the one and
		 * only unknown location.
		 */
		@Override
		public boolean equals(Object object) {
			return this == object;
		}
	}

}