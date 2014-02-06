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

import java.util.HashMap;
import java.util.LinkedHashMap;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;

import bsh.EvalError;
import bsh.Interpreter;

/**
 * {@ConQAT.Doc}
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 788D3A16F49C61BEDE03615E193CF350
 */
@AConQATProcessor(description = "This processor integrates the BeanShell "
		+ "scripting library into ConQAT to evaluate expressions "
		+ "based on key values. " + LocalExpressionProcessorBase.DOC)
public class ExpressionProcessor extends
		LocalExpressionProcessorBase<IConQATNode> {

	/** This maps from write key to expression. */
	private final HashMap<String, String> expressions = new LinkedHashMap<String, String>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "eval", minOccurrences = 1, description = ""
			+ "This defines the expression to evaluate.")
	public void addExpression(
			@AConQATAttribute(name = ConQATParamDoc.WRITEKEY_KEY_NAME, description = ConQATParamDoc.WRITEKEY_KEY_DESC) String key,
			@AConQATAttribute(name = "expr", description = "the expression") String expression) {
		expressions.put(key, expression);
	}

	/** {@inheritDoc} */
	@Override
	protected String[] getKeys() {
		return expressions.keySet().toArray(new String[0]);
	}

	/** Evaluate expression and store result. */
	@Override
	public void visit(IConQATNode node) throws ConQATException {
		try {
			Interpreter interpreter = createInterpreter(node);

			for (String key : expressions.keySet()) {
				String expression = expressions.get(key);
				node.setValue(key, interpreter.eval(expression));
			}

		} catch (EvalError e) {
			throw new ConQATException(e);
		}
	}

}