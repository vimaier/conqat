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

import java.awt.Color;
import java.text.NumberFormat;

import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.commons.color.ECCSMColor;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.DialShape;
import org.jfree.chart.plot.MeterInterval;
import org.jfree.chart.plot.MeterPlot;
import org.jfree.data.Range;
import org.jfree.data.general.DefaultValueDataset;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: EACFD94E36FD5A5E1D012AA1B9FA37A6
 */
@AConQATProcessor(description = "Creates a MeterChart that displays a single "
		+ "double value. A meter chart divides its range into three contiguous "
		+ "sections: GREEN, YELLOW and RED. The value that is displayed is "
		+ "depicted via a needle that points to a location in one of the "
		+ "three sections.")
public class MeterChartCreator extends AssessmentChartCreatorBase {

	/** {@inheritDoc} */
	@Override
	protected JFreeChart createChart() {
		MeterPlot plot = createMeterPlot();

		plot.addInterval(new MeterInterval("Green", new Range(0,
				greenThreshold), null, null, ECCSMColor.GREEN.getColor()));
		plot.addInterval(new MeterInterval("Yellow", new Range(greenThreshold,
				yellowThreshold), null, null, ECCSMColor.YELLOW.getColor()));
		plot.addInterval(new MeterInterval("Red", new Range(yellowThreshold,
				redThreshold), null, null, ECCSMColor.RED.getColor()));

		JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
				plot, drawLegend);

		return chart;
	}

	/** Creates a meter plot and sets color values */
	private MeterPlot createMeterPlot() {
		MeterPlot plot = new MeterPlot(new DefaultValueDataset(value));

		plot.setUnits(unit);
		plot.setDialShape(DialShape.CHORD);
		plot.setDialBackgroundPaint(Color.WHITE);
		plot.setRange(new Range(0, redThreshold));
		plot.setDialOutlinePaint(Color.GRAY);
		plot.setNeedlePaint(Color.BLACK);
		plot.setTickLabelsVisible(true);
		plot.setTickLabelPaint(Color.BLACK);
		plot.setTickPaint(Color.GRAY);
		plot.setTickLabelFormat(NumberFormat.getNumberInstance());
		plot.setTickSize(10);
		plot.setValuePaint(Color.BLACK);

		return plot;
	}

}