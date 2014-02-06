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

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.string.StringUtils;

import bsh.EvalError;
import bsh.Interpreter;

/**
 * Base class for processors performing basic calculations. This is based on
 * bean shell.
 * 
 * @author Benjamin Hummel
 * @author $Author: juergens $
 * @version $Rev: 35200 $
 * @ConQAT.Rating GREEN Hash: 550A66E713AE5EE18E922EE5A3CAF77E
 */
public abstract class CalculatorBase extends ConQATProcessorBase {

	/** The prefix added to each script. */
	private static final String SCRIPT_PREFIX = "static import java.lang.Math.*;"
			+ StringUtils.CR;

	/** Documentation to be reused for the processor description in subclasses. */
	protected final static String PROCESSOR_DOC = "This processor is based BeanShell "
			+ "(http://www.beanshell.org/) and thus understands the same "
			+ "commands as Java. Additionally all commands from java.lang.Math are available "
			+ "without prefix (i.e. you can just write 'sin(PI)').";

	/** The expression to evaluate. */
	private String expression;

	/** Map for storing variables, so we can add them each time. */
	private final Map<String, Double> variables = new HashMap<String, Double>();

	/** Set the expression evaluated. */
	@AConQATParameter(name = "calculate", minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "This defines the calculation to be performed.")
	public void setExpression(
			@AConQATAttribute(name = "expression", description = "the JEP expression to be calculated") String expression) {
		this.expression = expression;
	}

	/**
	 * Adds a variable to the BeanShell instance. If it already exists it will
	 * be overwritten.
	 */
	protected void addBshVariable(String name, double value) {
		variables.put(name, value);
	}

	/** Evaluates the current expression using the actual variable setting. */
	protected double evaluateExpression() throws ConQATException {
		Object bshResult = null;
		try {
			Interpreter interpreter = new Interpreter();
			for (Map.Entry<String, Double> variable : variables.entrySet()) {
				interpreter.set(variable.getKey(), variable.getValue()
						.doubleValue());
			}
			bshResult = interpreter.eval(SCRIPT_PREFIX + expression);
		} catch (EvalError e) {
			throw new ConQATException("Error performing calculation!", e);
		}

		if (bshResult instanceof Number) {
			return ((Number) bshResult).doubleValue();
		}

		throw new ConQATException("Script did not return a number!");
	}
}