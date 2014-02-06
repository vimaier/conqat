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
package org.conqat.engine.java.metric;

import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.Method;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.java.base.MethodAnalyzerBase;
import org.conqat.lib.commons.math.EAggregationStrategy;

/**
 * This analyzer counts the number of statements in the byte code of the
 * classes.
 * 
 * @author Tilman Seifert
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 27E1521CCA87FECADA9B2AC088B9820C
 */
@AConQATProcessor(description = "Determines number of statements "
		+ "of each method and aggregates them with the specified "
		+ "aggregation strategy.")
public class NumberOfStatementsCounter extends MethodAnalyzerBase {

	/** Aggregation strategy. */
	private EAggregationStrategy strategy = EAggregationStrategy.SUM;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "aggregation-strategy", description = "Set aggregation strategy "
			+ "[default is summation]", minOccurrences = 0, maxOccurrences = 1)
	public void setAggregationStrategy(
			@AConQATAttribute(name = "value", description = "Strategy name") EAggregationStrategy strategy) {
		this.strategy = strategy;
	}

	/** The key to use for saving the number of the methods. */
	@AConQATKey(description = "Number of bytecode statements", type = "java.lang.Integer")
	public static final String KEY = "#Statements";

	/** Returns the number of statements in this method. */
	@Override
	protected double analyze(Method method) {
		Code code = method.getCode();
		if (code == null) {
			getLogger().warn(
					"Could not analyze method " + method.getName() + ".");
			return 0;
		}
		return code.getLength();
	}

	/** Returns {@value #KEY}. */
	@Override
	protected String getKey() {
		return KEY;
	}

	/** {@inheritDoc} */
	@Override
	protected EAggregationStrategy getStrategy() {
		return strategy;
	}
}