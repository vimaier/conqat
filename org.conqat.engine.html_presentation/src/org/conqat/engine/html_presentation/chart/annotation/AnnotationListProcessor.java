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
package org.conqat.engine.html_presentation.chart.annotation;

import static org.conqat.engine.commons.CommonUtils.DEFAULT_DATE_FORMAT_PATTERN;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.conqat.engine.commons.CommonUtils;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;

/**
 * {@ConQAT.Doc}
 * 
 * @author ladmin
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 3227698CD7A8705004E19D4CE7D4D0FC
 */
@AConQATProcessor(description = "Creates message annotations for charts.")
public class AnnotationListProcessor extends ConQATProcessorBase {

	/** List of annotations added to processor */
	private final List<EventAnnotationBase> annotations = new ArrayList<EventAnnotationBase>();

	/** Date format string used to parse dates */
	@AConQATFieldParameter(parameter = "date", attribute = "format", description = "Set date format", optional = true)
	public String formatString = DEFAULT_DATE_FORMAT_PATTERN;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "annotate-event", description = "Creates a message annotation for a chart group that refers to a single date.", minOccurrences = 0, maxOccurrences = -1)
	public void addEventAnnotation(
			@AConQATAttribute(name = "message", description = "Message of the annotation") String message,
			@AConQATAttribute(name = "date", description = "Annotation date") String dateString)
			throws ConQATException {

		Date date = parseDate(dateString);
		annotations.add(new SingleDateEventAnnotation(message, date));
		getLogger().debug("Annotated event at: " + date);
	}

	/** Create date from date string */
	private Date parseDate(String dateString) throws ConQATException {
		return CommonUtils.parseDate(dateString, formatString);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "annotate-interval", description = "Creates a message annotation for a chart group that refers to a date interval.", minOccurrences = 0, maxOccurrences = -1)
	public void addIntervalAnnotation(
			@AConQATAttribute(name = "message", description = "Message of the annotation") String message,
			@AConQATAttribute(name = "start-date", description = "Start date of interval") String startDateString,
			@AConQATAttribute(name = "end-date", description = "Start date of interval") String endDateString)
			throws ConQATException {

		Date startDate = parseDate(startDateString);
		Date endDate = parseDate(endDateString);
		annotations
				.add(new IntervalEventAnnotation(message, startDate, endDate));
		getLogger().debug(
				"Annotated interval from " + startDate + " to " + endDate);
	}

	/** {@inheritDoc} */
	@Override
	public AnnotationList process() {
		return new AnnotationList(annotations);
	}

}