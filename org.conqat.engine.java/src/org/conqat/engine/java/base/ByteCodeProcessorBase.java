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
package org.conqat.engine.java.base;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.engine.resource.util.UniformPathUtils;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.objectweb.asm.ClassReader;

/**
 * Base class for processors that work directly on the byte-code level using
 * ASM. Problems with single elements are logged as warnings.
 * 
 * @author hummelb
 * @author $Author: goede $
 * @version $Rev: 44120 $
 * @ConQAT.Rating GREEN Hash: D884C9E4A035DE3B6E50DC6C40147CDB
 */
public abstract class ByteCodeProcessorBase extends ConQATProcessorBase {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.INPUT_NAME, attribute = ConQATParamDoc.INPUT_REF_NAME, description = ""
			+ "The elements to read the Java byte-code from. Elements that are no class files are ignored. "
			+ "JARs are treated transparently, i.e. the contained classes are traversed as well.")
	public IResource input;

	/**
	 * Visits all class elements in the input scope (also in JARs) and calls
	 * {@link #visitClass(ClassReader)} for each of them.
	 */
	protected void visitAllClasses() {
		for (IElement element : ResourceTraversalUtils.listElements(input)) {
			String extension = UniformPathUtils.getExtension(element
					.getUniformPath());
			try {
				if ("jar".equalsIgnoreCase(extension)) {
					visitJar(element);
				} else if ("class".equalsIgnoreCase(extension)) {
					visitClassStream(new ByteArrayInputStream(
							element.getContent()));
				}
			} catch (IOException e) {
				getLogger()
						.warn("Had problems reading from element "
								+ element.getLocation() + ": " + e.getMessage());
			} catch (ConQATException e) {
				getLogger()
						.warn("Had problems reading from element "
								+ element.getLocation() + ": " + e.getMessage());
			}

		}
	}

	/** Visits all class code stored in a JAR. */
	private void visitJar(IElement element) throws IOException, ConQATException {
		JarInputStream jarInputStream = new JarInputStream(
				new ByteArrayInputStream(element.getContent()));
		try {
			JarEntry entry;
			while ((entry = jarInputStream.getNextJarEntry()) != null) {
				if (entry.getName().endsWith("class")) {
					visitClassStream(jarInputStream);
				}

			}
		} finally {
			jarInputStream.close();
		}
	}

	/** Perform visiting for a class read from a stream. */
	private void visitClassStream(InputStream in) throws IOException {
		try {
			ClassReader reader = new ClassReader(in);
			visitClass(reader);
		} finally {
			FileSystemUtils.close(in);
		}
	}

	/**
	 * Template method that is called for each class read. The
	 * {@link ClassReader} provided will be initialized with the class. To make
	 * this method called, the {@link #visitAllClasses()} method must be called
	 * to start the scanning process.
	 */
	protected abstract void visitClass(ClassReader reader);
}