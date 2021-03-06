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
package org.conqat.engine.html_presentation.chart;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.jfree.data.xy.XYSeries;

/**
 * {@ConQAT.Doc}
 * 
 * @author Elmar Juergens
 * @author aswolins
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 3DEC44D6A7E9C1737DA6F41C941545D4
 */
@AConQATProcessor(description = "Creates a series of x/y values from IConQATNode leaves.")
public class XYSeriesCreator extends ConQATProcessorBase {

	/** Root node of input tree */
	private IConQATNode root;

	/** The key for the property which will be used as x */
	private String xKey;

	/** The key for the property which will be used as y */
	private String yKey;

	/** Value that is used if no value can be retrieved */
	private double defaultValue = 0;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "root", minOccurrences = 1, maxOccurrences = 1, description = "IConQATNode and the read keys")
	public void setInput(
			@AConQATAttribute(name = "ref", description = "IConQATNode") IConQATNode input,
			@AConQATAttribute(name = "xKey", description = "The key for the property which will be used as x") String xKey,
			@AConQATAttribute(name = "yKey", description = "The key for the property which will be used as y") String yKey) {

		root = input;
		this.xKey = xKey;
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
	@SuppressWarnings("unused")
	public XYSeries process() throws ConQATException {
		XYSeries series = new XYSeries(xKey);
		for (IConQATNode leaf : TraversalUtils.listLeavesDepthFirst(root)) {
			series.add(getNumber(xKey, leaf), getNumber(yKey, leaf));
		}
		return series;
	}

	/** Retrieves a number stored under a key in a ConQAT node. */
	private Number getNumber(String key, IConQATNode node) {
		try {
			return NodeUtils.getDoubleValue(node, key);
		} catch (ConQATException e) {
			getLogger().warn(
					"No value for key '" + key + "' found in node '"
							+ node.getId() + "'");
			return defaultValue;
		}
	}
}