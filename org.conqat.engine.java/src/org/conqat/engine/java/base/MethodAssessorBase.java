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

import java.util.ArrayList;
import java.util.Set;

import org.apache.bcel.classfile.LineNumber;
import org.apache.bcel.classfile.LineNumberTable;
import org.apache.bcel.classfile.Method;
import org.conqat.engine.java.resource.IJavaElement;
import org.conqat.lib.commons.assessment.Assessment;
import org.conqat.lib.commons.assessment.ETrafficLightColor;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Base class for processors that assess methods.
 * 
 * @author Florian Deissenboeck
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: DFD08E654A47738C441A7C20D60AD5E9
 */
public abstract class MethodAssessorBase extends MethodProcessorBase {

	/**
	 * Analyze methods. This forward assessment of methods to
	 * {@link #assess(Method)}.
	 */
	@Override
	protected void analyzeMethods(IJavaElement classElement, Set<Method> methods) {

		ArrayList<String> messages = new ArrayList<String>();

		for (Method method : methods) {
			String message = assess(method);
			if (!StringUtils.isEmpty(message)) {
				messages.add(createMessage(method, message));
			}
		}

		if (messages.isEmpty()) {
			classElement.setValue(getAssessmentKey(), new Assessment(
					ETrafficLightColor.GREEN));
			return;
		}

		classElement.setValue(getAssessmentKey(), new Assessment(
				ETrafficLightColor.RED));
		classElement.setValue(getMessageKey(), messages);
	}

	/**
	 * Create message for storage at message key. The message includes the
	 * provided message, the method name and, if obtainable, the line number.
	 */
	private String createMessage(Method method, String message) {
		StringBuilder result = new StringBuilder();

		result.append(method.getName());
		LineNumberTable table = method.getLineNumberTable();

		if (table != null) {
			LineNumber[] lineNumbers = table.getLineNumberTable();
			LineNumber linenumber = lineNumbers[0];
			int number = linenumber.getLineNumber();
			result.append(" [" + number + "]");
		}

		result.append(": ");
		result.append(message);

		return result.toString();
	}

	/** Forwards to {@link #getAssessmentKey()} and {@link #getMessageKey()}. */
	@Override
	protected String[] getKeys() {
		return new String[] { getAssessmentKey(), getMessageKey() };
	}

	/**
	 * Template method for assessing a method.
	 * 
	 * @param method
	 *            method to assess
	 * @return if this returns a string the method will be rated red and the
	 *         string attached to the message list. If this returns
	 *         <code>null</code> message will be rated green.
	 */
	protected abstract String assess(Method method);

	/** Key for assessment. */
	protected abstract String getAssessmentKey();

	/** Key for messages. */
	protected abstract String getMessageKey();

}