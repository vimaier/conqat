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
import java.util.Map;

import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;

import bsh.EvalError;
import bsh.Interpreter;

/**
 * A processor for integrating BeanShell scripting into ConQAT. For examples of
 * its usage have a look at the blocks coming with this bundle.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 06CBCCAABF36E3981AE83D05AD73151E
 */
@AConQATProcessor(description = "This processor integrates the BeanShell "
		+ "scripting library into ConQAT. Details on BeanShell are available "
		+ "at http://www.beanshell.org/. The result "
		+ "type is defined via a pipleline source parameter. The result "
		+ "returned from this processor is the value of the variable 'result' "
		+ "set in the script. For an example see the blocks coming with this bundle.")
public class BeanShellProcessor extends ConQATPipelineProcessorBase<Object> {

	/** The variables exported. */
	private final Map<String, Object> variables = new HashMap<String, Object>();

	/** The script to be evaluated. */
	private String script;

	/** Variable used for storing the result. */
	private Object result;

	/** Set the script used. */
	@AConQATParameter(name = "run", minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "This defines the script to be run. This gives not the name of a file "
			+ "but inline BeanShell commands.")
	public void setExpression(
			@AConQATAttribute(name = "script", description = "the script to run (inline)") String script) {
		this.script = script;
	}

	/** Add a variable to be exported to the script. */
	@AConQATParameter(name = "variable", description = ""
			+ "Adds a variable to be used in the script.")
	public void addVariable(
			@AConQATAttribute(name = "name", description = "The name of the variable.") String name,
			@AConQATAttribute(name = "value", description = "The value used for the variable.") Object value) {

		variables.put(name, value);
	}

	/** {@inheritDoc} */
	@Override
	protected void processInput(Object input) throws ConQATException {
		try {
			Interpreter interpreter = new Interpreter();

			for (String name : variables.keySet()) {
				interpreter.set(name, variables.get(name));
			}

			interpreter.set("result", input);
			interpreter.eval(script);
			result = interpreter.get("result");
		} catch (EvalError e) {
			throw new ConQATException(e);
		}
	}

	/**
	 * Override process, so we can potentially return a different object.
	 */
	@Override
	public Object process() throws ConQATException {
		Object oldResult = super.process();
		if (result != null
				&& !oldResult.getClass().isAssignableFrom(result.getClass())) {
			throw new ConQATException("Your script returned an invalid type! ["
					+ result.getClass() + "]");
		}
		return result;
	}
}