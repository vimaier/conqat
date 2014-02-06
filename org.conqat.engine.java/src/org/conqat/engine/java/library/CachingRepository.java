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
package org.conqat.engine.java.library;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.ClassPath;
import org.apache.bcel.util.ClassPath.ClassFile;
import org.apache.bcel.util.Repository;
import org.apache.log4j.Logger;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.java.resource.IJavaElement;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * This is a bcel repository which additionally caches its classes in a central
 * cache to speedup answering queries. Classes are loaded using a class path
 * initially provided.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46960 $
 * @ConQAT.Rating GREEN Hash: 9A36FC97037B3330AFCBA90C39F2F503
 */
public class CachingRepository implements Repository {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** Logger. */
	private static final Logger LOGGER = Logger
			.getLogger(CachingRepository.class);

	/** The cache for storing classes. */
	private static Map<String, SoftReference<JavaClass>> cache = new HashMap<String, SoftReference<JavaClass>>();

	/** The class path used in this repository. */
	private final ClassPath classPath;

	/**
	 * Create a new caching repository.
	 * 
	 * @param classPath
	 *            the class path to be used.
	 */
	public CachingRepository(List<String> classPath) {
		this.classPath = new ClassPath(StringUtils.concat(classPath,
				File.pathSeparator));
	}

	/** This method does nothing. */
	@Override
	public void storeClass(JavaClass clazz) {
		// do nothing
	}

	/** This method does nothing. */
	@Override
	public void removeClass(JavaClass clazz) {
		// do nothing
	}

	/** We do not differentiate between load class and find class. */
	@Override
	public JavaClass findClass(String className) {
		JavaClass clazz = null;
		SoftReference<JavaClass> ref = cache.get(className);
		if (ref != null && ref.get() != null) {
			clazz = ref.get();
		} else {
			clazz = loadClassFromClassPath(className);
			if (clazz == null) {
				clazz = loadClassFromClassLoader(className);
			}
			cache.put(className, new SoftReference<JavaClass>(clazz));
		}

		if (clazz != null) {
			// this is crucial, so all classes later loaded via this classs use
			// this repository
			clazz.setRepository(this);
		}
		return clazz;
	}

	/**
	 * Obtains the class from the cache if possible. Otherwise the class is
	 * loaded via the embedded class loader.
	 */
	@Override
	public JavaClass loadClass(String className) throws ClassNotFoundException {
		JavaClass clazz = findClass(className);
		if (clazz == null) {
			throw new ClassNotFoundException("Couldn't load class: "
					+ className);
		}
		return clazz;
	}

	/** Load the given class. */
	public JavaClass loadClass(IJavaElement javaElement) throws ConQATException {
		JavaClass clazz = null;
		SoftReference<JavaClass> ref = cache.get(javaElement.getClassName());
		if (ref != null && ref.get() != null) {
			clazz = ref.get();
		} else {
			clazz = parseByteCode(javaElement);
			cache.put(javaElement.getClassName(), new SoftReference<JavaClass>(
					clazz));
		}

		if (clazz != null) {
			// this is crucial, so all classes later loaded via this classs use
			// this repository
			clazz.setRepository(this);
		}
		return clazz;
	}

	/** Parses the byte-code of the Java element as into a BCEL class. */
	private JavaClass parseByteCode(IJavaElement javaElement)
			throws ConQATException {
		try {
			return new ClassParser(new ByteArrayInputStream(
					javaElement.getByteCode()), javaElement.getClassName())
					.parse();
		} catch (ClassFormatException e) {
			throw new ConQATException("Could not parse byte-code of "
					+ javaElement.getClassName(), e);
		} catch (IOException e) {
			throw new AssertionError(
					"This can not happen as we work on an in-memory stream.");
		}
	}

	/** Loads the given class using the class path. */
	private JavaClass loadClassFromClassPath(String className) {

		ClassFile classFile;
		try {
			classFile = classPath.getClassFile(className.replace('.', '/'));
		} catch (IOException e) {
			// not found, so just return
			return null;
		}

		InputStream inputStream = null;
		try {
			inputStream = classFile.getInputStream();
			return new ClassParser(inputStream, className).parse();
		} catch (IOException e) {
			LOGGER.warn("IO error on file " + classFile.getPath());
		} catch (ClassFormatException ex) {
			LOGGER.warn("Class format error for file " + classFile.getPath());
		} finally {
			FileSystemUtils.close(inputStream);
		}
		return null;
	}

	/** Loads the given class using the system class loader. */
	private JavaClass loadClassFromClassLoader(String className) {
		InputStream stream = null;
		try {
			Class<?> clazz = Class.forName(className);
			String name = clazz.getName();

			int i = name.lastIndexOf('.');
			if (i > 0) {
				name = name.substring(i + 1);
			}
			name += ".class";

			stream = clazz.getResourceAsStream(name);
			ClassParser parser = new ClassParser(stream, clazz.getName());
			return parser.parse();
		} catch (ClassNotFoundException e) {
			LOGGER.warn("Can't find class " + className);
		} catch (ClassFormatException e) {
			LOGGER.warn("Class format error for class " + className);
		} catch (IOException e) {
			LOGGER.warn("IO error on file class " + className);
		} finally {
			FileSystemUtils.close(stream);
		}
		return null;
	}

	/**
	 * Forwards to {@link #loadClass(String)}.
	 * 
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public JavaClass loadClass(Class clazz) throws ClassNotFoundException {
		return loadClass(clazz.getName());
	}

	/** This method does nothing. */
	@Override
	public void clear() {
		// do nothing
	}

	/**
	 * This returns the class path. As far as we can tell this is never called
	 * from within BCEL.
	 */
	@Override
	public ClassPath getClassPath() {
		return classPath;
	}
}