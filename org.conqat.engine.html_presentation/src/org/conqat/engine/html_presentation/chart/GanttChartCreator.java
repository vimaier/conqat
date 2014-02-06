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

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.commons.collections.IdentityHashSet;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 02F01256D68835FC7BF5596BA69E4A96
 */
@AConQATProcessor(description = "Creates a Gantt chart from one or more task series.")
public class GanttChartCreator extends ChartCreatorBase {

	/** The task series' to chart. */
	private final IdentityHashSet<TaskSeries> taskSeries = new IdentityHashSet<TaskSeries>();

	/** Title of the diagram */
	private String title;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "title", minOccurrences = 1, maxOccurrences = 1, description = "The title displayed at the top of the chart")
	public void setTitle(
			@AConQATAttribute(name = "title", description = "Title.") String title) {
		this.title = title;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "task-series", minOccurrences = 1, description = "Task series to chart.")
	public void setData(
			@AConQATAttribute(name = "ref", description = "Reference to generating processor.") TaskSeries tasks) {
		taskSeries.add(tasks);
	}

	/** {@inheritDoc} */
	@Override
	protected JFreeChart createChart() {
		TaskSeriesCollection dataset = new TaskSeriesCollection();
		for (TaskSeries series : taskSeries) {
			dataset.add(series);
		}
		return ChartFactory.createGanttChart(title, null, null, dataset,
				drawLegend, false, false);
	}

}