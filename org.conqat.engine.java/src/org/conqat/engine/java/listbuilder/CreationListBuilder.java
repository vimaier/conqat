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
package org.conqat.engine.java.listbuilder;

import java.util.ArrayList;
import java.util.List;

import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.NEW;
import org.apache.bcel.generic.ObjectType;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.java.resource.IJavaElement;

/**
 * Annotates creation relationships between Java classes. Example: if class A
 * contains a statement like <code>B b = new B();</code>, then we add B to the
 * creation list of A.
 * 
 * @author Tilman Seifert
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 2D39AEEC45CAC25F594695697934A91E
 */
@AConQATProcessor(description = "Annotates creation relationsships between "
		+ "Java classes. Example: if class A contains a statement like "
		+ "B b = new B(), then we add B to the creation list of A.")
public class CreationListBuilder extends ListBuilderBase {

	/** The Key */
	@AConQATKey(description = "List of Classes which are instantiated "
			+ "(with 'new') from this class", type = "java.util.List<String>")
	public static final String CREATION_KEY = "Creation";

	/**
	 * For each method in the given class, look at each statement; if it is a
	 * 'new' instruction, the class of the created object is added to the
	 * CREATION dependency list.
	 */
	@Override
	protected void analyze(IJavaElement classElement, JavaClass clazz) {
		ConstantPool cp = clazz.getConstantPool();
		ConstantPoolGen cpg = new ConstantPoolGen(cp);

		List<String> list = new ArrayList<String>();
		for (Method m : clazz.getMethods()) {
			Code c = m.getCode();
			if (c != null) {
				// not an abstract method
				handleCodeFragment(list, cpg, c);
			}
		}
		classElement.setValue(CREATION_KEY, list);
	}

	/** Handles a single code fragment. */
	private void handleCodeFragment(List<String> resultList,
			ConstantPoolGen cpg, Code code) {
		for (Instruction i : new InstructionList(code.getCode())
				.getInstructions()) {
			if (i instanceof NEW) {
				NEW newInstruction = (NEW) i;
				ObjectType ot = newInstruction.getLoadClassType(cpg);

				if (ot == null) { // ot is primitive type
					continue;
				}

				String newClassName = ot.getClassName();
				if (!resultList.contains(newClassName)
						&& !isBlacklisted(newClassName)) {
					resultList.add(newClassName);
				}
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	protected String[] getKeys() {
		return new String[] { CREATION_KEY };
	}
}