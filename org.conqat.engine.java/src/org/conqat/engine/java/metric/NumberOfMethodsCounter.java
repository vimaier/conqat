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

import org.apache.bcel.classfile.Method;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.java.base.MethodAnalyzerBase;
import org.conqat.lib.commons.math.EAggregationStrategy;

/**
 * This analyzer counts the methods of class. Visibility of methods to include
 * must be specified.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 5E36AA2895098EDBEADCB05822207A6E
 */
@AConQATProcessor(description = "This analyzer counts the methods of the classes. "
		+ "Visibility of methods to include must be specified. "
		+ "By default no visibility is specified.")
public class NumberOfMethodsCounter extends MethodAnalyzerBase {

	/** The key to use for saving the number of the methods. */
	@AConQATKey(description = "Number of Methods", type = "java.lang.Integer")
	public static final String KEY = "#Methods";

	/** Returns 1. */
	@Override
	protected double analyze(Method method) {
		return 1;
	}

	/** Returns {@value #KEY}. */
	@Override
	protected String getKey() {
		return KEY;
	}

	/** Returns {@link EAggregationStrategy#SUM} */
	@Override
	protected EAggregationStrategy getStrategy() {
		return EAggregationStrategy.SUM;
	}
}