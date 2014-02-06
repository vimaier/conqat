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
package org.conqat.engine.java.assessment;

import java.util.HashSet;

import org.apache.bcel.classfile.Method;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.generic.Type;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.java.base.MethodAssessorBase;

/**
 * This processor checks if the return types of methods are of certain type.
 * This works plainly on names, type hierarchy is not taken into consideration.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 8F37F4A963CFC40D747CCB53347F901A
 */
@AConQATProcessor(description = "This processor checks if the return types of "
		+ "methods are of certain type. This works plainly on names, type "
		+ "hierarchy is not taken into consideration.")
public class ReturnTypeAssessor extends MethodAssessorBase {

	/** Assessment key. */
	@AConQATKey(description = "Assessment key", type = "org.conqat.lib.commons.assessment.Assessment")
	public final static String ASSESSMENT_KEY = "ReturnTypeAssessment";

	/** Message key. */
	@AConQATKey(description = "Message key", type = "java.util.List<String>")
	public final static String MESSAGE_KEY = "ReturnTypeMessages";

	/** Set of discouraged types. */
	private final HashSet<String> discouragedTypes = new HashSet<String>();

	/** Add a discouraged. */
	@AConQATParameter(name = "discouraged-type", minOccurrences = 1, description = "Add discouraged type.")
	public void addDiscouragedType(
			@AConQATAttribute(name = "name", description = "Full qualified name of the discouraged type.") String name) {
		discouragedTypes.add(name);
	}

	/**
	 * Checks if method return type is discouraged. This works plainly on names,
	 * type hierarchy is not taken into consideration.
	 */
	@Override
	protected String assess(Method method) {
		Type type = method.getReturnType();
		String returnTypeName = Utility.signatureToString(type.getSignature());
		if (discouragedTypes.contains(returnTypeName)) {
			return "Discouraged return type '" + returnTypeName + "'";
		}
		return null;
	}

	/** Returns {@value #ASSESSMENT_KEY}. */
	@Override
	protected String getAssessmentKey() {
		return ASSESSMENT_KEY;
	}

	/** Returns {@value #MESSAGE_KEY}. */
	@Override
	protected String getMessageKey() {
		return MESSAGE_KEY;
	}
}