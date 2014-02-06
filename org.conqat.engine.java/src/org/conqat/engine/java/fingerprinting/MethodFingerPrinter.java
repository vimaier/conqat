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
package org.conqat.engine.java.fingerprinting;

import org.conqat.engine.commons.node.IRemovableConQATNode;
import org.conqat.engine.commons.node.NodeConstants;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.node.StringSetNode;
import org.conqat.engine.commons.util.SlimmingLogger;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.java.base.ByteCodeProcessorBase;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: heineman $
 * @version $Rev: 40893 $
 * @ConQAT.Rating GREEN Hash: 0DD888CF85B5C3B11A809981586893EF
 */
@AConQATProcessor(description = "This processor creates a list of all methods in the read byte-code. "
		+ "A key with a fingerprint for the method content is added to each method in the list.")
public class MethodFingerPrinter extends ByteCodeProcessorBase {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "The content hash for the method.", type = "java.lang.String")
	public static final String FINGERPRINT_KEY = "content hash";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Readable content used for debugging purposes.", type = "java.lang.String")
	public static final String DEBUG_KEY = "content debug";

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "debug", attribute = "value", optional = true, description = ""
			+ "If this is set to true, additional debug information is added in another key.")
	public boolean debug = false;

	/** The result node. */
	private final StringSetNode result = new StringSetNode();

	/** {@inheritDoc} */
	@Override
	protected void visitClass(ClassReader reader) {
		reader.accept(new HashingClassVisitor(), ClassReader.SKIP_DEBUG);
	}

	/**
	 * We use a slimming logger to reduce the number of warning messages
	 * potentially produced for large code bases.
	 */
	private SlimmingLogger logger;

	/**
	 * Returns a {@link SlimmingLogger}. We overwrite this method as most
	 * programmers are used to access the logger via method
	 * <code>getLogger()</code> and would, hence, be prone to access the
	 * non-slimming logger.
	 */
	@Override
	protected SlimmingLogger getLogger() {
		if (logger == null) {
			logger = new SlimmingLogger(super.getLogger());
		}
		return logger;
	}

	/** {@inheritDoc} */
	@Override
	public IRemovableConQATNode process() {
		NodeUtils.addToDisplayList(result, FINGERPRINT_KEY);
		if (debug) {
			NodeUtils.addToDisplayList(result, DEBUG_KEY);
		}

		result.setValue(NodeConstants.HIDE_ROOT, true);

		visitAllClasses();

		return result;
	}

	/** The visitor used to visit each method and hash its contents. */
	private class HashingClassVisitor extends ClassVisitor {

		/** Separates class name from method signature */
		private static final String SEPARATOR = "#";

		/** The name of the class. */
		private String className;

		/** Constructor. */
		private HashingClassVisitor() {
			super(Opcodes.ASM4);
		}

		/** {@inheritDoc} */
		@Override
		public void visit(int version, int access, String name,
				String signature, String superName, String[] interfaces) {
			className = name;
		}

		/** {@inheritDoc} */
		@Override
		public MethodVisitor visitMethod(int access, String name, String desc,
				String signature, String[] exceptions) {
			String id = className + SEPARATOR + name + desc;
			StringSetNode node = new StringSetNode(id);
			try {
				result.addChild(node);
			} catch (IllegalArgumentException e) {
				logInsertionProblem(e);
			}

			return new HashingMethodVisitor(node, debug);
		}

		/**
		 * Log insertion problem. E.g. occurs, if a node for this type name
		 * already exists
		 */
		private void logInsertionProblem(IllegalArgumentException e) {
			String message = e.getMessage();
			// cut to type name, since if type duplicated, insertion of every
			// single method fails. slimming logger takes care that this is
			// reported only once.
			if (message.contains(SEPARATOR)) {
				message = message.substring(0, message.indexOf(SEPARATOR));
			}
			getLogger().warn("Skipping type: " + message);
		}

	}

}