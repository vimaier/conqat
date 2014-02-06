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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.util.ClassPath;
import org.apache.bcel.util.ClassPath.ClassFile;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.IdentityHashSet;
import org.conqat.lib.commons.filesystem.FileSystemUtils;

/**
 * Utility code for dealing with {@link IJavaElement}s.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 40412 $
 * @ConQAT.Rating GREEN Hash: 785C4663A8CDD8599041861DB1981E4B
 */
public class JavaElementUtils {

	/** Obtains the BCEL class for a {@link IJavaElement}. */
	public static JavaClass obtainBcelClass(IJavaElement javaElement)
			throws ConQATException {
		return javaElement.getJavaContext().getJavaClass(javaElement);
	}

	/** Lists all Java elements. */
	public static List<IJavaElement> listJavaElements(IResource input) {
		return ResourceTraversalUtils.listElements(input, IJavaElement.class);
	}

	/**
	 * Returns the unique {@link JavaContext} from a {@link IJavaResource}
	 * hierarchy. This only works if all {@link IJavaElement}s refer to the same
	 * context, i.e. were created by the same {@link JavaElementFactory}. If
	 * this does not hold, this throws an exception.
	 * 
	 * @throws ConQATException
	 *             if there is no unique context.
	 */
	public static JavaContext getUniqueContext(IJavaResource root)
			throws ConQATException {
		Set<JavaContext> contexts = new IdentityHashSet<JavaContext>();
		for (IJavaElement element : listJavaElements(root)) {
			contexts.add(element.getJavaContext());
		}

		if (contexts.isEmpty()) {
			throw new ConQATException("No context found!");
		}

		if (contexts.size() > 2) {
			throw new ConQATException(
					"Multiple contexts found! "
							+ "It seems that the input was produced by different Java scopes, "
							+ "which is not supported.");
		}

		return CollectionUtils.getAny(contexts);
	}

	/** Loads the specified class as a BCEL class from the given class path */
	public static JavaClass loadClass(ClassPath classPath, String className)
			throws IOException {
		InputStream inputStream = null;
		try {
			ClassFile classFile = classPath.getClassFile(className);
			inputStream = classFile.getInputStream();
			return new ClassParser(inputStream, className).parse();
		} finally {
			FileSystemUtils.close(inputStream);
		}
	}
}