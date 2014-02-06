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

import static org.conqat.engine.commons.ConQATParamDoc.INPUT_DESC;
import static org.conqat.engine.commons.ConQATParamDoc.INPUT_NAME;
import static org.conqat.engine.commons.ConQATParamDoc.INPUT_REF_DESC;
import static org.conqat.engine.commons.ConQATParamDoc.INPUT_REF_NAME;

import java.util.Date;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.date.DateUtils;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 3138B6603F377BDD03020A314E67A2CE
 */
@AConQATProcessor(description = "This processor creates a task series from the leave nodes of the input tree. "
		+ "Every leaf is converted to one task where start date, end date and completion are defined by values "
		+ "stored at the leaf.")
public class TaskSeriesCreator extends ConQATProcessorBase {

	/** Dummy value for attributes. */
	private static final String DUMMY_VALUE = "#dummy#";

	/** Root node of input tree. */
	private IConQATNode root;

	/** The name of the task series. */
	private String seriesName;

	/** Key used for descriptions. */
	private String descriptionKey;

	/** Key used for task start date. */
	private String startDateKey;

	/** Key used for task end date. */
	private String endDateKey;

	/** Key used for completion information. */
	private String completionKey;

	/** Default task start date. */
	private Date defaultStartDate = DateUtils.getNow();

	/** Default task end date. */
	private Date defaultEndDate = DateUtils.getNow();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = INPUT_NAME, minOccurrences = 1, maxOccurrences = 1, description = INPUT_DESC)
	public void setInput(
			@AConQATAttribute(name = INPUT_REF_NAME, description = INPUT_REF_DESC) IConQATNode input,
			@AConQATAttribute(name = "start-date-key", description = "The key the task start date is stored at.") String startDateKey,
			@AConQATAttribute(name = "end-date-key", description = "The key the task end date is stored at.") String endDateKey,
			@AConQATAttribute(name = "description-key", description = "The key the task description is stored at. If not specified the node id is used.", defaultValue = DUMMY_VALUE) String descriptonKey,
			@AConQATAttribute(name = "percent-completed-key", description = "The key the completion information is stored at. "
					+ "Completion must be specified by a numeric value [0..1]. Completion information is not required.", defaultValue = DUMMY_VALUE) String completionKey) {

		this.root = input;
		this.startDateKey = startDateKey;
		this.endDateKey = endDateKey;
		this.completionKey = completionKey;
		this.descriptionKey = descriptonKey;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "series-name", minOccurrences = 1, maxOccurrences = 1, description = "Name of the series")
	public void setSeriesName(
			@AConQATAttribute(name = "value", description = "Series name") String seriesName) {
		this.seriesName = seriesName;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "default-start-date", minOccurrences = 0, maxOccurrences = 1, description = "Default date for task start date. "
			+ "This is used if start date for a task cannot be determined. If not specified, today's date is used.")
	public void setDefaultStartDate(
			@AConQATAttribute(name = "value", description = "Date") Date date) {
		defaultStartDate = date;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "default-end-date", minOccurrences = 0, maxOccurrences = 1, description = "Default date for task start date"
			+ "This is used if end date for a task cannot be determined. If not specified, today's date is used.")
	public void setDefaultEndDate(
			@AConQATAttribute(name = "value", description = "Date") Date date) {
		defaultEndDate = date;
	}

	/** {@inheritDoc} */
	@Override
	public TaskSeries process() {
		TaskSeries series = new TaskSeries(seriesName);
		for (IConQATNode leaf : TraversalUtils.listLeavesDepthFirst(root)) {
			Task task = createTask(leaf);
			if (task != null) {
				series.add(task);
			}
		}
		return series;
	}

	/**
	 * Create the task based on the start and end dates stored at a node. This
	 * returns <code>null</code> if end date is before start date.
	 */
	private Task createTask(IConQATNode node) {
		String description = NodeUtils.getStringValue(node, descriptionKey,
				node.getId());

		Date startDate = getDate(node, startDateKey, defaultStartDate);

		Date endDate = getDate(node, endDateKey, defaultEndDate);

		if (endDate.compareTo(startDate) < 0) {
			getLogger().info(
					"For task " + node.getId()
							+ " end date is before start date. Ignoring task.");
			return null;
		}

		Task task = new Task(description, startDate, endDate);

		if (!DUMMY_VALUE.equals(completionKey)) {
			setCompletion(node, task);
		}

		return task;
	}

	/** Get date if defined, otherwise log message and return default. */
	private Date getDate(IConQATNode node, String key, Date defaultValue) {

		try {
			return NodeUtils.getValue(node, key, Date.class);
		} catch (ConQATException e) {
			getLogger().warn(
					"Value stored at node " + node.getId() + ": "
							+ node.getValue(key) + " is not a date.");
			return defaultValue;
		}

	}

	/** Set completion of task based on completion key. */
	private void setCompletion(IConQATNode node, Task task) {
		double percentCompleted;
		try {
			percentCompleted = NodeUtils.getDoubleValue(node, completionKey);
		} catch (ConQATException e) {
			getLogger().warn(
					"Value stored at node '" + node.getId() + "' for key '"
							+ completionKey + "': "
							+ node.getValue(completionKey)
							+ " cannot be converted a double.");
			return;
		}

		if (percentCompleted < 0 || percentCompleted > 1) {
			getLogger().warn(
					"Value stored at node '" + node.getId() + "' for key '"
							+ completionKey + "': " + percentCompleted
							+ " is not in the interval [0..1].");
			return;
		}

		task.setPercentComplete(percentCompleted);
	}

}