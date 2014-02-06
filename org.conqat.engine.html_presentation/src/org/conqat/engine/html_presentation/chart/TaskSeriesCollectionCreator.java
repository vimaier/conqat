/*-----------------------------------------------------------------------+
 | eu.cqse.conqat.engine.incubator
 |                                                                       |
   $Id: TaskSeriesCollectionCreator.java 42198 2012-10-11 13:59:07Z poehlmann $            
 |                                                                       |
 | Copyright (c)  2009-2012 CQSE GmbH                                 |
 +-----------------------------------------------------------------------*/
package org.conqat.engine.html_presentation.chart;

import java.util.Date;

import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.commons.util.ConQATInputProcessorBase;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.collections.ListMap;
import org.conqat.lib.commons.date.DateUtils;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 42198 $
 * @ConQAT.Rating GREEN Hash: A2BF9E5BDE11DF72655813ECBFC0084B
 */
@AConQATProcessor(description = "Creates a task series collection from the leaf nodes of a ConQAT node tree. A task is created for each leaf.")
public class TaskSeriesCollectionCreator extends
		ConQATInputProcessorBase<IConQATNode> {

	/** Maps from task series names to lists of corresponding tasks. */
	protected final ListMap<String, Task> taskSeries = new ListMap<String, Task>();

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "series-name", attribute = "key", optional = false, description = ""
			+ "Key under which series name is stored. If stored object is no string, string representation is used.")
	public String seriesKey;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "date", attribute = "key", optional = false, description = ""
			+ "Key under which task date is stored.")
	public String dateKey;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "task-message", attribute = "key", optional = true, description = ""
			+ "Key under which task message is stored. This key is optional. If not set, empty strings are used as messages.")
	public String messageKey;

	/** {@inheritDoc} */
	@Override
	public TaskSeriesCollection process() throws ConQATException {
		for (IConQATNode child : TraversalUtils.listLeavesDepthFirst(input)) {
			String seriesName = "NULL";
			Object value = child.getValue(seriesKey);
			if (value != null) {
				seriesName = value.toString();
			}

			Task task = createTaskForNode(child);
			taskSeries.add(seriesName, task);
		}

		return createTaskSeriesCollection();
	}

	/** Creates a task from the information stored in a node */
	private Task createTaskForNode(IConQATNode child) throws ConQATException {
		Date taskDate = NodeUtils.getDateValue(child, dateKey);
		String taskMessage = NodeUtils.getStringValue(child, messageKey, "");
		taskDate = DateUtils.truncateToBeginOfDay(taskDate);

		return new Task(taskMessage, taskDate,
				DateUtils.incrementByOneDay(taskDate));
	}

	/**
	 * Creates the final task series collection from the calculated
	 * {@link #taskSeries}.
	 */
	protected TaskSeriesCollection createTaskSeriesCollection() {
		TaskSeriesCollection result = new TaskSeriesCollection();

		for (String seriesName : taskSeries.getKeys()) {
			TaskSeries series = new TaskSeries(seriesName);
			series.setDescription(seriesName);
			result.add(series);

			for (Task task : taskSeries.getCollection(seriesName)) {
				series.add(task);
			}
		}

		return result;
	}
}