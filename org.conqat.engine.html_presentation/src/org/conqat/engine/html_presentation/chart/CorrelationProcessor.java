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

import org.apache.commons.math.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math.stat.correlation.SpearmansCorrelation;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.commons.node.ListNode;
import org.conqat.engine.commons.node.NodeConstants;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.jfree.data.xy.XYSeries;

/**
 * {@ConQAT.Doc}
 * 
 * @author juergens
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 913D4F32C3A3114D7173314EBFE3816C
 */
@AConQATProcessor(description = "Computes several correlation coefficients. "
		+ "For interpretation of results see for example Wikipedia.")
public class CorrelationProcessor extends ConQATProcessorBase {

	/** Key in which correlation is stored */
	public static final String CORRELATION = "Correlation";

	/** Data on which correlation is computed */
	private XYSeries data;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.INPUT_NAME, description = ConQATParamDoc.INPUT_DESC, minOccurrences = 1, maxOccurrences = 1)
	public void setData(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) XYSeries data) {
		this.data = data;
	}

	/** {@inheritDoc} */
	@Override
	public ListNode process() {
		ListNode root = new ListNode();
		root.setValue(NodeConstants.HIDE_ROOT, true);
		NodeUtils.addToDisplayList(root, CORRELATION);

		createCorrelationResultNode(root, "Pearson", pearsonCorrelation());
		createCorrelationResultNode(root, "Spearman", spearmanCorrelation());

		return root;
	}

	/** Create correlation node */
	private void createCorrelationResultNode(ListNode root, String caption,
			double correlation) {
		ListNode node = new ListNode(caption);
		node.setValue(CORRELATION, correlation);
		root.addChild(node);
	}

	/** Compute pearson correlation */
	private double pearsonCorrelation() {
		double[][] array = data.toArray();
		return new PearsonsCorrelation().correlation(array[0], array[1]);
	}

	/** Compute spearman correlation */
	private double spearmanCorrelation() {
		double[][] array = data.toArray();
		return new SpearmansCorrelation().correlation(array[0], array[1]);
	}
}