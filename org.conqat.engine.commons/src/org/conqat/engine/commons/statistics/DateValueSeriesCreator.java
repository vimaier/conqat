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
package org.conqat.engine.commons.statistics;

import java.util.Date;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;

/**
 * {@ConQAT.Doc}
 * 
 * @author Elmar Juergens
 * @author $Author: hummelb $
 * @version $Rev: 36404 $
 * @ConQAT.Rating GREEN Hash: 1F4FD3E2314EC38C11BB36EBC6C6A489
 */
@AConQATProcessor(description = "Creates a DateValueSeries of date/y values from IConQATNode leaves.")
public class DateValueSeriesCreator extends ConQATProcessorBase {

	/** Root node of input tree */
	private IConQATNode root;

	/** The key for the property which will be used as x */
	private String dateKey;

	/** The key for the property which will be used as y */
	private String yKey;

	/** Value that is used if no value can be retrieved */
	private double defaultValue = 0;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "root", minOccurrences = 1, maxOccurrences = 1, description = "IConQATNode and the read keys")
	public void setInput(
			@AConQATAttribute(name = "ref", description = "IConQATNode") IConQATNode input,
			@AConQATAttribute(name = "dateKey", description = "The key for the property which will be used as date") String dateKey,
			@AConQATAttribute(name = "yKey", description = "The key for the property which will be used as y") String yKey) {

		root = input;
		this.dateKey = dateKey;
		this.yKey = yKey;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "default", minOccurrences = 0, maxOccurrences = 1, description = "Value that is used if no value can be retrieved")
	public void setDefaultValue(
			@AConQATAttribute(name = "value", description = "If not set, default value is 0.") double defaultValue) {
		this.defaultValue = defaultValue;
	}

	/** {@inheritDoc} */
	@Override
	public DateValueSeries process() throws ConQATException {
		DateValueSeries series = new DateValueSeries();

		for (IConQATNode leaf : TraversalUtils.listLeavesDepthFirst(root)) {
			Date date = NodeUtils.getDateValue(leaf, dateKey);
			double value = NodeUtils
					.getDoubleValue(leaf, yKey, defaultValue);
			series.addValue(date, value);
		}

		return series;
	}

}