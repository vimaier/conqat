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

import org.conqat.engine.core.core.AConQATProcessor;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: F9A787BF32F917CD2F326B9A6A3CDEE3
 */
@AConQATProcessor(description = "This processor creates area charts from "
		+ "DateValueSeries-objects. "
		+ SeriesCreatorBase.SERIES_LAYOUTER_COMMENT)
public class AreaChartCreator extends SingleRangeSeriesCreator {

	/** {@inheritDoc} */
	@Override
	protected JFreeChart createChart() {
		JFreeChart chart = super.createChart();

		XYPlot plot = chart.getXYPlot();
		plot.setRenderer(new XYAreaRenderer(XYAreaRenderer.AREA));

		ValueAxis domainAxis = plot.getDomainAxis();
		domainAxis.setLowerMargin(0.0);
		domainAxis.setUpperMargin(0.0);

		return chart;
	}
}