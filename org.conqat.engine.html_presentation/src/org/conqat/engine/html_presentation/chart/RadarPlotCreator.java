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

import java.util.HashMap;
import java.util.Map;

import org.conqat.engine.commons.statistics.KeyedData;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: steidl $
 * @version $Rev: 43636 $
 * @ConQAT.Rating GREEN Hash: 561B26AFE7B64745CE2F81591CBE42C4
 */
@AConQATProcessor(description = "This layouter creates pie charts from KeyedData-objects.")
public class RadarPlotCreator extends ChartCreatorBase {

	/** Data to layout. */
	private final HashMap<String, KeyedData<Comparable<?>>> dataSets = new HashMap<String, KeyedData<Comparable<?>>>();

	/** The title. */
	private String title;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "description", minOccurrences = 1, maxOccurrences = 1, description = "Data description.")
	public void setDescription(
			@AConQATAttribute(name = "description", description = "Data description.") String title) {

		this.title = title;
	}

	/**
	 * {@ConQAT.Doc}
	 * 
	 * @param data
	 *            although this is called from non-generics-aware driver, we can
	 *            safely specify the lower bound here as per definition there
	 *            can be now KeyedData-instances which do not satisfy the
	 *            Comparable-interface.
	 */
	@AConQATParameter(name = "data", minOccurrences = 1, description = "Data to chart.")
	public void addData(
			@AConQATAttribute(name = "name", description = "Name of the data set") String name,
			@AConQATAttribute(name = "ref", description = "Reference to series generating processor.") KeyedData<Comparable<?>> data) {
		dataSets.put(name, data);
	}

	/** Create the chart. */
	@Override
	protected JFreeChart createChart() {
		return setupChart(createDataset());
	}

	/** Create dataset. */
	private DefaultCategoryDataset createDataset() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (String dataSetName : dataSets.keySet()) {
			KeyedData<Comparable<?>> data = dataSets.get(dataSetName);

			Map<Comparable<?>, Double> values = data.getValues();

			for (Comparable<?> key : values.keySet()) {
				dataset.setValue(values.get(key), dataSetName, key);
			}
		}
		return dataset;
	}

	/**
	 * Basic setup of a chart.
	 * 
	 * @param dataset
	 *            dataset for the chart
	 */
	private JFreeChart setupChart(DefaultCategoryDataset dataset) {
		SpiderWebPlot plot = new SpiderWebPlot(dataset);
		JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
				plot, drawLegend);
		return chart;
	}

}