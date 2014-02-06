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
package org.conqat.engine.java.resource;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.bcel.classfile.JavaClass;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.java.library.CachingRepository;
import org.conqat.engine.java.library.JavaLibrary;
import org.conqat.lib.commons.clone.IDeepCloneable;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.conqat.lib.commons.collections.UnmodifiableSet;
import org.conqat.lib.commons.filesystem.CanonicalFile;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * This class encapsulates global information on a Java project, such as the
 * class path and the Java language version.
 * <p>
 * This class is mostly immutable, but allows some changes via package visible
 * methods. This is to allow the {@link JavaElementFactory} to adjust the
 * context while the factory is alive.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 44858 $
 * @ConQAT.Rating GREEN Hash: 0F8067E069EDD9FF0DCD6C77769D452F
 */
public class JavaContext implements IDeepCloneable {

	/** The classpath. */
	private final List<String> classPath;

	/** The repository used for looking up java classes. Initialized lazily. */
	private CachingRepository bcelRepository;

	/** The set of missing files */
	private final Set<String> missingFiles = new HashSet<String>();

	/** Source directories of this Java scope. */
	private final Set<CanonicalFile> sourceDirectories = new HashSet<CanonicalFile>();

	/** Byte code directories of this Java scope. */
	private final Set<CanonicalFile> byteCodeDirectories = new HashSet<CanonicalFile>();

	/** Constructor. */
	public JavaContext(List<String> classPath) {
		// use a linked list as we add to front in updateContext()
		this.classPath = new LinkedList<String>(classPath);
	}

	/**
	 * This method is called from the {@link JavaElement} in its constructor.
	 * This is used to reflect this new element in the context.
	 */
	/* package */void updateContext(JavaElement javaElement,
			String sourceLocation, String byteCodeLocation) {
		String javaFile = JavaLibrary.getByteCodeName(javaElement) + ".java";
		String classFile = JavaLibrary.getByteCodeName(javaElement) + ".class";

		CanonicalFile sourceDirectory = determineDirectory(javaFile,
				sourceLocation);
		if (sourceDirectory != null) {
			sourceDirectories.add(sourceDirectory);
		} else {
			missingFiles.add(javaFile);
		}

		CanonicalFile byteCodeDirectory = determineDirectory(classFile,
				byteCodeLocation);
		if (byteCodeDirectory != null) {
			if (byteCodeDirectories.add(byteCodeDirectory)) {
				// add to front
				classPath.add(0, byteCodeDirectory.getCanonicalPath());
			}
		} else {
			missingFiles.add(classFile);
		}
	}

	/**
	 * Determines the directory for a filename and a given location if possible.
	 * If this fails, returns null.
	 */
	private CanonicalFile determineDirectory(String fileName, String location) {
		location = FileSystemUtils.normalizeSeparators(location);
		if (!location.endsWith(fileName)) {
			return null;
		}
		String basePath = StringUtils.stripSuffix(fileName, location);

		// try to make a file from it and canonize
		File directory = new File(basePath);
		if (!directory.isDirectory()) {
			return null;
		}

		try {
			return new CanonicalFile(directory);
		} catch (IOException e) {
			return null;
		}
	}

	/** Returns the BCEL java class for a given class name. */
	public JavaClass getJavaClass(IJavaElement javaElement)
			throws ConQATException {
		if (bcelRepository == null) {
			bcelRepository = new CachingRepository(getClassPath());
		}
		return bcelRepository.loadClass(javaElement);
	}

	/** Returns the classpath. */
	public UnmodifiableList<String> getClassPath() {
		return CollectionUtils.asUnmodifiable(classPath);
	}

	/**
	 * Returns whether all code has been read from plain files. If this is the
	 * case, the methods {@link #getSourceDirectories()} and
	 * {@link #getByteCodeDirectories()} should provide complete data. If this
	 * is not the case, {@link #getMissingFiles()} provides a list of files that
	 * were not found in the file system.
	 */
	public boolean hasMissingFiles() {
		return !missingFiles.isEmpty();
	}

	/** Returns missingFiles. */
	public UnmodifiableSet<String> getMissingFiles() {
		return CollectionUtils.asUnmodifiable(missingFiles);
	}

	/**
	 * Returns the byte-code directories of this context. The results may not be
	 * complete, as byte-code may also be used from other (non-disk) locations.
	 * This is indicated by {@link #hasMissingFiles()}.
	 */
	public UnmodifiableSet<CanonicalFile> getByteCodeDirectories() {
		return CollectionUtils.asUnmodifiable(byteCodeDirectories);
	}

	/**
	 * Returns the source directories of this context. The results may not be
	 * complete, as source code may also be used from other (non-disk)
	 * locations. This is indicated by {@link #hasMissingFiles()}.
	 */
	public UnmodifiableSet<CanonicalFile> getSourceDirectories() {
		return CollectionUtils.asUnmodifiable(sourceDirectories);
	}

	/**
	 * Returns this. We do not clone as this class is mostly immutable, i.e. can
	 * be changed only within the package.
	 */
	@Override
	public IDeepCloneable deepClone() {
		return this;
	}
}