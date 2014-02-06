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
import java.util.List;
import java.util.Set;

import org.apache.bcel.classfile.Method;
import org.conqat.engine.java.resource.IJavaElement;
import org.conqat.lib.commons.math.EAggregationStrategy;
import org.conqat.lib.commons.math.MathUtils;

/**
 * Base class for processors that assign a number to a method.
 * 
 * @author Florian Deissenboeck
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 726021B7E7BBA7C5681ED1BA1614CEB4
 */
public abstract class MethodAnalyzerBase extends MethodProcessorBase {

	/**
	 * Forwards analysis to {@link #analyze(Method)}, add results and store sum
	 * at key defined by {@link #getKey()}.
	 */
	@Override
	protected void analyzeMethods(IJavaElement classElement, Set<Method> methods) {
		List<Double> values = new ArrayList<Double>();
		for (Method method : methods) {
			values.add(analyze(method));
		}
		if (!values.isEmpty()) {
			classElement.setValue(getKey(),
					MathUtils.aggregate(values, getStrategy()));
		}
	}

	/** Returns {@link #getKey()}. */
	@Override
	protected String[] getKeys() {
		return new String[] { getKey() };
	}

	/** Get key for value. */
	protected abstract String getKey();

	/**
	 * Template method for analyzing a method.
	 * 
	 * @param method
	 *            method to analyze.
	 * @return value associated with the method.
	 */
	protected abstract double analyze(Method method);

	/** Template method to obtain aggregation strategy. */
	protected abstract EAggregationStrategy getStrategy();
}