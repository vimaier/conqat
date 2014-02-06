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
package org.conqat.engine.scripting;

import java.util.List;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.assessment.Assessment;
import org.conqat.lib.commons.assessment.ETrafficLightColor;
import org.conqat.lib.commons.collections.PairList;

import bsh.EvalError;
import bsh.Interpreter;

/**
 * {@ConQAT.Doc}
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: A5F61AA259E1E8023FEDF9D9F12AE888
 */
@AConQATProcessor(description = "This processor integrates the BeanShell "
		+ "scripting library into ConQAT to evaluate expressions "
		+ "based on key values. This processor expects the expression to "
		+ "evaluate to a boolean value. If an expression evaluates to true "
		+ "the node is rated RED, GREEN otherwise. If rated RED, an "
		+ "assessment message is added. " + LocalExpressionProcessorBase.DOC)
public class ExpressionAssessor extends
		LocalExpressionProcessorBase<IConQATNode> {

	/** Rules are pairs of expressions and messages. */
	private final PairList<String, String> rules = new PairList<String, String>();

	/** Key to store assessment. */
	private String assessmentKey;

	/** Key to store message. */
	private String messageKey;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "rule", minOccurrences = 1, description = ""
			+ "This defines the rule to evaluate.")
	public void addRule(
			@AConQATAttribute(name = "expr", description = "the expression") String expression,
			@AConQATAttribute(name = "message", description = "the message") String message) {
		rules.add(expression, message);
	}

	/** Set the key used for writing. */
	@AConQATParameter(name = ConQATParamDoc.WRITEKEY_NAME, minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "The key to write the assessment into.")
	public void setWriteKeys(
			@AConQATAttribute(name = "assessment-key", description = "Key for assessment.") String assessmentKey,
			@AConQATAttribute(name = "messages-key", description = "Key for messages. ") String messageKey) {
		this.assessmentKey = assessmentKey;
		this.messageKey = messageKey;
	}

	/** {@inheritDoc} */
	@Override
	protected String[] getKeys() {
		return new String[] { assessmentKey, messageKey };
	}

	/** Evaluate expression and store result. */
	@Override
	public void visit(IConQATNode node) throws ConQATException {
		try {
			Interpreter interpreter = createInterpreter(node);

			// optimistically red GREEN
			node.setValue(assessmentKey, new Assessment(
					ETrafficLightColor.GREEN));

			for (int i = 0; i < rules.size(); i++) {
				String expression = rules.getFirst(i);
				String message = rules.getSecond(i);
				evaluate(node, interpreter, expression, message);
			}

		} catch (EvalError e) {
			throw new ConQATException(e);
		}
	}

	/**
	 * Evaluate expression for a node. If the expression evaluate to
	 * <code>true</code>, the node is rated RED and a message is added.
	 */
	private void evaluate(IConQATNode node, Interpreter interpreter,
			String expression, String message) throws EvalError,
			ConQATException {
		Object resultObject = interpreter.eval(expression);

		if (!(resultObject instanceof Boolean)) {
			throw new ConQATException("Expression " + expression
					+ " did not evaluate to Boolean.");
		}

		boolean result = ((Boolean) resultObject).booleanValue();
		if (result) {
			node.setValue(assessmentKey, new Assessment(ETrafficLightColor.RED));
			List<String> messageList = NodeUtils.getOrCreateStringList(node,
					messageKey);
			messageList.add(message);
		}
	}
}