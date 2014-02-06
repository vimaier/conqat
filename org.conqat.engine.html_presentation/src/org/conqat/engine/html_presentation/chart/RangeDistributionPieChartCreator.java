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

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.assessment.AssessmentRange;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 37E4A9E468B1E3F7109A2E3CDE6907E8
 */
@AConQATProcessor(description = "This layouter visualizes Range Distributions as stacked bar charts. Charts contain absolute and relative values")
public class RangeDistributionPieChartCreator extends
		RangeDistributionChartCreatorBase {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "secondary-metric", attribute = ConQATParamDoc.READKEY_KEY_NAME, description = "Secondary metrics to display.")
	public String secondaryMetric;

	/** {@inheritDoc} */
	@Override
	protected JFreeChart createChart() {
		PieDataset dataSet = createDataset();
		JFreeChart chart = ChartFactory.createPieChart(title, dataSet,
				drawLegend, true, false);

		PiePlot plot = (PiePlot) chart.getPlot();
		setColors(plot);
		plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
				"{0} = {1} ({2})"));
		PieChartCreator.stylePiePlot(plot);

		return chart;
	}

	/** Set colors for pie plot. */
	private void setColors(PiePlot plot) {
		for (AssessmentRange range : rangeDistribution.getRanges()) {
			plot.setSectionPaint(range, range.getColor());
		}
	}

	/** Create dataset. */
	private PieDataset createDataset() {
		DefaultPieDataset dataset = new DefaultPieDataset();
		for (AssessmentRange range : rangeDistribution.getRanges()) {
			if (range.isEmpty()) {
				continue;
			}

			dataset.setValue(range,
					rangeDistribution.getSum(range, secondaryMetric));
		}
		return dataset;
	}
}