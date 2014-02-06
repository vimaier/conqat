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
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.java.library.JavaLibrary;
import org.conqat.engine.resource.IContentAccessor;
import org.conqat.engine.resource.build.IElementFactory;
import org.conqat.engine.resource.text.TextElement;
import org.conqat.engine.resource.text.TextElementFactory;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.TokenElement;
import org.conqat.lib.scanner.ELanguage;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 44673 $
 * @ConQAT.Rating GREEN Hash: 19A8FD37CF8E37C784C3D60BC5233FC9
 */
@AConQATProcessor(description = "Factory for the creation of Java elements.")
public class JavaElementFactory extends TextElementFactory {

	/** The classpath. */
	private final List<String> classpath = new ArrayList<String>();

	/**
	 * Maps from class name to location of content accessor. This is used to
	 * avoid duplicate classes in a single scope.
	 */
	private final Map<String, String> classNames = new HashMap<String, String>();

	/** The content accessors for the byte code. */
	private final List<IContentAccessor> byteCodeAccessors = new ArrayList<IContentAccessor>();

	/** The context used. */
	private JavaContext context;

	/** Mapping from class name to byte code accessor. */
	private final Map<String, IContentAccessor> byteCodeByName = new HashMap<String, IContentAccessor>();

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "lenient", attribute = "value", description = ""
			+ "If this is set to true, java files without byte-code will be replaced by token elements instead of raising an error.", optional = true)
	public boolean lenient = false;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "classpath", description = ""
			+ "Add an element to the classpath.")
	public void addClasspathElement(
			@AConQATAttribute(name = "element", description = ""
					+ "The directory or JAR file to add to the classpath.") String filename) {

		if (!new File(filename).canRead()) {
			getLogger().warn(
					"Classpath element " + filename + " not found. Skipped!");
		} else {
			classpath.add(filename);
		}
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "classpath-list", description = "Adds a list of classpath elements.")
	public void addClasspathElements(
			@AConQATAttribute(name = "elements", description = ""
					+ "The list of directories and JAR files to add to the classpath.") String[] filenames) {
		for (String filename : filenames) {
			addClasspathElement(filename);
		}
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "byte-code", minOccurrences = 1, description = ""
			+ "Reference to the scope defining the byte-code to be included.")
	public void addByteCodeAccessors(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) IContentAccessor[] byteCodeAccessors) {
		this.byteCodeAccessors.addAll(Arrays.asList(byteCodeAccessors));
	}

	/** {@inheritDoc} */
	@Override
	public IElementFactory process() {
		context = new JavaContext(classpath);
		processByteCode();
		return super.process();
	}

	/** Inspects the byte code to find out which classes are available. */
	private void processByteCode() {
		for (IContentAccessor byteCodeAccessor : byteCodeAccessors) {
			String classname;
			try {
				classname = obtainClassName(byteCodeAccessor.getContent());
			} catch (ConQATException e) {
				getLogger().error(
						"Could not load byte code for "
								+ byteCodeAccessor.getLocation() + ": "
								+ e.getMessage());
				continue;
			}

			// skip inner classes
			if (!classname.contains("$")) {
				byteCodeByName.put(classname, byteCodeAccessor);
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public ITokenElement create(IContentAccessor accessor)
			throws ConQATException {
		String className = getFQClassName(accessor);
		if (classNames.containsKey(className)) {
			throw new ConQATException("Class of name " + className
					+ " already created from '" + classNames.get(className)
					+ "', now created again from '" + accessor.getLocation()
					+ "'");
		}
		classNames.put(className, accessor.getLocation());

		IContentAccessor byteCodeAccessor = byteCodeByName.get(className);
		if (byteCodeAccessor == null) {
			if (lenient) {
				return new TokenElement(accessor, encoding, ELanguage.JAVA, getFilters());
			}

			throw new ConQATException("No byte code found for class "
					+ className);
		}

		return new JavaElement(className, accessor, byteCodeAccessor, encoding,
				context, getFilters());
	}

	/**
	 * Get full qualified class name of a class. The name is derived by
	 * analyzing the package statement in the file.
	 */
	private String getFQClassName(IContentAccessor accessor)
			throws ConQATException {
		return JavaLibrary.getFQClassName(
				accessor.getLocation(),
				new StringReader(new TextElement(accessor, encoding)
						.getUnfilteredTextContent()));
	}

	/** Returns the name of a class contained in its byte-code. */
	private static String obtainClassName(byte[] byteCode) {
		ClassReader reader = new ClassReader(byteCode);
		NameExtractionVisitor visitor = new NameExtractionVisitor();
		reader.accept(visitor, ClassReader.SKIP_CODE | ClassReader.SKIP_FRAMES
				| ClassReader.SKIP_DEBUG);
		return visitor.className;
	}

	/** Visitor used to extract a class name using ASM. */
	private static class NameExtractionVisitor extends ClassVisitor {

		/** Constructor. */
		public NameExtractionVisitor() {
			super(Opcodes.ASM4);
		}

		/** The class name. */
		private String className;

		/** {@inheritDoc} */
		@Override
		public void visit(int version, int access, String name,
				String signature, String superName, String[] interfaces) {
			// conversion from Java's byte-code format
			className = name.replace('/', '.');
		}
	}
}