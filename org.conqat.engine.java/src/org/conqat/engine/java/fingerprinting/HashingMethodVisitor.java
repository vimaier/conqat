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

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import org.conqat.engine.commons.node.StringSetNode;
import org.conqat.lib.commons.digest.Digester;
import org.conqat.lib.commons.io.SerializationUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * A visitor that creates a hash value of the method's byte-code (omitting some
 * parts, such as comments and names of local variables). The hash value is
 * appended on a provided ConQAT node.
 * 
 * @author $Author: heineman $
 * @version $Rev: 40893 $
 * @ConQAT.Rating GREEN Hash: 643F0056A6C13B5EB6DD09195977E531
 */
public class HashingMethodVisitor extends MethodVisitor {

	/** The node to add the finger print key to. */
	private final StringSetNode node;

	/** MD5 digester used for finger printing. */
	private final MessageDigest md5 = Digester.getMD5();

	/** Constructs a debug representation of the method's byte-code. */
	private final StringBuilder debugBuilder;

	/**
	 * Cache used to resolve label names. Maps from a byte-code specific label
	 * name to a general/logical name that is local to this visitor.
	 */
	private final Map<String, String> labelCache = new HashMap<String, String>();

	/** The annotation visitor used. */
	private final AnnotationVisitor annotationVisitor = new AnnotationVisitor(
			Opcodes.ASM4) {

		/** {@inheritDoc} */
		@Override
		public void visit(String name, Object value) {
			append(name);
			append(value);
		}

		/** {@inheritDoc} */
		@Override
		public AnnotationVisitor visitAnnotation(String name, String desc) {
			append("Annotation(inner)");
			append(name);
			append(desc);
			return this;
		}

		/** {@inheritDoc} */
		@Override
		public AnnotationVisitor visitArray(String name) {
			append("Array");
			append(name);
			return this;
		}

		/** {@inheritDoc} */
		@Override
		public void visitEnum(String name, String desc, String value) {
			append("Enum");
			append(name);
			append(desc);
			append(value);
		}
	};

	/**
	 * Constructor.
	 * 
	 * @param node
	 *            the node to add the fingerprint to (using key
	 *            {@link MethodFingerPrinter#FINGERPRINT_KEY}).
	 */
	public HashingMethodVisitor(StringSetNode node, boolean debug) {
		super(Opcodes.ASM4);
		this.node = node;
		if (debug) {
			debugBuilder = new StringBuilder();
		} else {
			debugBuilder = null;
		}
	}

	/** Appends an object to the MD5 hash. */
	private void append(Object value) {
		if (value == null) {
			md5.update((byte) 0);
		} else {
			md5.update(value.toString().getBytes());
		}

		if (debugBuilder != null) {
			debugBuilder.append(value + StringUtils.CR);
		}
	}

	/** Appends an int to the MD5 hash. */
	private void append(int value) {
		byte[] b = new byte[4];

		SerializationUtils.insertInt(value, b, 0);
		md5.update(b);

		if (debugBuilder != null) {
			debugBuilder.append(value + StringUtils.CR);
		}
	}

	/** Get a hashable representation of a label. */
	private String getLabel(Label label) {
		String value = labelCache.get(label.toString());
		if (value == null) {
			value = "label" + labelCache.size();
			labelCache.put(label.toString(), value);
		}
		return value;
	}

	/** {@inheritDoc} */
	@Override
	public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
		append("Annotation");
		append(desc);
		append(visible);
		return annotationVisitor;
	}

	/** {@inheritDoc} */
	@Override
	public AnnotationVisitor visitAnnotationDefault() {
		return annotationVisitor;
	}

	/** {@inheritDoc} */
	@Override
	public void visitEnd() {
		node.setValue(MethodFingerPrinter.FINGERPRINT_KEY,
				StringUtils.encodeAsHex(md5.digest()));

		if (debugBuilder != null) {
			node.setValue(MethodFingerPrinter.DEBUG_KEY,
					debugBuilder.toString());
		}
	}

	/** {@inheritDoc} */
	@Override
	public void visitFieldInsn(int opcode, String owner, String name,
			String desc) {
		append("FieldInsn");
		append(opcode);
		append(owner);
		append(name);
		append(desc);
	}

	/** {@inheritDoc} */
	@Override
	public void visitFrame(int type, int nLocal, Object[] local, int nStack,
			Object[] stack) {
		append("Frame");
		append(type);
		append(nLocal);
		for (Object o : local) {
			append(o);
		}
		append(nStack);
		for (Object s : stack) {
			append(s);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void visitIincInsn(int var, int increment) {
		append("IincInsn");
		append(var);
		append(increment);
	}

	/** {@inheritDoc} */
	@Override
	public void visitInsn(int opcode) {
		append("Insn");
		append(opcode);
	}

	/** {@inheritDoc} */
	@Override
	public void visitIntInsn(int opcode, int operand) {
		append("IntInsn");
		append(opcode);
		append(operand);
	}

	/** {@inheritDoc} */
	@Override
	public void visitJumpInsn(int opcode, Label label) {
		append("JumpInsn");
		append(opcode);
		append(getLabel(label));
	}

	/** {@inheritDoc} */
	@Override
	public void visitLabel(Label label) {
		append("Label");
		append(getLabel(label));
	}

	/** {@inheritDoc} */
	@Override
	public void visitLdcInsn(Object cst) {
		append("LdcInsn");
		append(cst);
	}

	/** {@inheritDoc} */
	@Override
	public void visitLocalVariable(String name, String desc, String signature,
			Label start, Label end, int index) {
		append("LocalVariable");
		// do not include name, as we don't want them in the hash
		append(desc);
		append(signature);
		append(getLabel(start));
		append(getLabel(end));
		append(index);
	}

	/** {@inheritDoc} */
	@Override
	public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
		append("LookupSwitchInsn");
		append(getLabel(dflt));
		for (int key : keys) {
			append(key);
		}
		for (Label l : labels) {
			append(getLabel(l));
		}
	}

	/** {@inheritDoc} */
	@Override
	public void visitMaxs(int maxStack, int maxLocals) {
		append("Maxs");
		append(maxStack);
		append(maxLocals);
	}

	/** {@inheritDoc} */
	@Override
	public void visitMethodInsn(int opcode, String owner, String name,
			String desc) {
		append("MethodInsn");
		append(opcode);
		append(owner);
		append(name);
		append(desc);
	}

	/** {@inheritDoc} */
	@Override
	public void visitMultiANewArrayInsn(String desc, int dims) {
		append("MultiANewArrayInsn");
		append(desc);
		append(dims);
	}

	/** {@inheritDoc} */
	@Override
	public AnnotationVisitor visitParameterAnnotation(int parameter,
			String desc, boolean visible) {
		append("ParameterAnnotation");
		append(parameter);
		append(desc);
		append(visible);
		return annotationVisitor;
	}

	/** {@inheritDoc} */
	@Override
	public void visitTableSwitchInsn(int min, int max, Label dflt,
			Label... labels) {
		append("TableSwitchInsn");
		append(min);
		append(max);
		append(getLabel(dflt));
		for (Label l : labels) {
			append(getLabel(l));
		}
	}

	/** {@inheritDoc} */
	@Override
	public void visitTryCatchBlock(Label start, Label end, Label handler,
			String type) {
		append("TryCatchBlock");
		append(getLabel(start));
		append(getLabel(end));
		append(getLabel(handler));
		append(type);
	}

	/** {@inheritDoc} */
	@Override
	public void visitTypeInsn(int opcode, String type) {
		append("TypeInsn");
		append(opcode);
		append(type);
	}

	/** {@inheritDoc} */
	@Override
	public void visitVarInsn(int opcode, int var) {
		append("VarInsn");
		append(opcode);
		append(var);
	}
}