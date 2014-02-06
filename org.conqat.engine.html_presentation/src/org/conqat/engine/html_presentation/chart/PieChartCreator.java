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
import java.util.List;
import java.util.Map;

import org.conqat.engine.commons.statistics.KeyedData;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.html_presentation.EHtmlPresentationFont;
import org.conqat.engine.html_presentation.color.AssessmentColorizer;
import org.conqat.lib.commons.assessment.ETrafficLightColor;
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
 * @ConQAT.Rating YELLOW Hash: 74A22419C85F8C368FA85A58C7669586
 */
@AConQATProcessor(description = "This processor creates pie charts from KeyedData-objects.")
public class PieChartCreator extends ChartCreatorBase {

	/** Default format used for labels */
	private static final String DEFAULT_LABEL_FORMAT = "{0}";

	/** Data to layout. */
	private KeyedData<Comparable<?>> data;

	/** The description. */
	private String dataDescription;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "label", attribute = "format", optional = true, description = ""
			+ "For the label format, use {0} where the pie section key should be inserted"
			+ "{1} for the absolute section value and {2} for the percent amount of the pie"
			+ "section, e.g. '{0} = {1} ({2})' will display as 'apple = 120 (5%)'. Default format is "
			+ DEFAULT_LABEL_FORMAT)
	public String labelFormat = DEFAULT_LABEL_FORMAT;

	/**
	 * Set chart data.
	 * 
	 * @param data
	 *            although this is called from non-generics-aware driver, we can
	 *            safely specify the lower bound here as per definition there
	 *            can be now KeyedData-instances which do not satisfy the
	 *            Comparable-interface.
	 */
	@AConQATParameter(name = "data", minOccurrences = 1, maxOccurrences = 1, description = "Data to chart.")
	public void setData(
			@AConQATAttribute(name = "ref", description = "Reference to series generating processor.") KeyedData<Comparable<?>> data,
			@AConQATAttribute(name = "description", description = "Data description.") String dataDescription) {
		this.dataDescription = dataDescription;
		this.data = data;
	}

	/** {@inheritDoc} */
	@Override
	protected JFreeChart createChart() {
		PieDataset dataSet = createDataset();
		JFreeChart chart = ChartFactory.createPieChart(dataDescription,
				dataSet, drawLegend, true, false);

		PiePlot plot = (PiePlot) chart.getPlot();
		setColors(plot, dataSet);
		plot.setLabelGenerator(new StandardPieSectionLabelGenerator(labelFormat));
		stylePiePlot(plot);

		return chart;
	}

	/** Set colors for traffic light colors properly. */
	@SuppressWarnings("unchecked")
	private void setColors(PiePlot plot, PieDataset dataSet) {
		List<Comparable<?>> keys = dataSet.getKeys();

		for (Comparable<?> key : keys) {
			if (key instanceof ETrafficLightColor) {
				plot.setSectionPaint(key, AssessmentColorizer
						.determineColor((ETrafficLightColor) key));
			}
		}
	}

	/** Create dataset. */
	private PieDataset createDataset() {
		DefaultPieDataset dataset = new DefaultPieDataset();
		Map<Comparable<?>, Double> values = data.getValues();

		for (Comparable<?> key : values.keySet()) {
			dataset.setValue(key, values.get(key));
		}

		return dataset;
	}

	/**
	 * Style the plot of a pie chart.
	 */
	public static void stylePiePlot(PiePlot plot) {
		plot.setLabelFont(EHtmlPresentationFont.SANS_CONDENSED.getFont());
		plot.setSectionOutlinesVisible(false);
		plot.setLabelGap(0.02);
		plot.setBackgroundPaint(Color.WHITE);
	}
}