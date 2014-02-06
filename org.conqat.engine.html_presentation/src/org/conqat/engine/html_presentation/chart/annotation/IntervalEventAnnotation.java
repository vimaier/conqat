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

import java.awt.Color;
import java.util.Date;

import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.Marker;

import org.conqat.engine.core.core.ConQATException;

/**
 * Annotation for events that belong to a date interval with a begin and an end.
 * 
 * @author ladmin
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 535154AC5171FF1C84851BC6F6DB48A0
 */
public class IntervalEventAnnotation extends EventAnnotationBase {

	/** Color used to draw the marker */
	private static final Color color = new Color(150, 150, 255);

	/** Start date of interval */
	private final Date startDate;

	/** End date of interval */
	private final Date endDate;

	/** Constructor */
	protected IntervalEventAnnotation(String message, Date startDate,
			Date endDate) throws ConQATException {
		super(message);

		if (endDate.before(startDate)) {
			throw new ConQATException(
					"Annotation end date must not be before start date");
		}

		this.startDate = startDate;
		this.endDate = endDate;
	}

	/** {@inheritDoc} */
	@Override
	public Date getDate() {
		return startDate;
	}

	/** {@inheritDoc} */
	@Override
	protected Marker doCreateMarker() {
		IntervalMarker marker = new IntervalMarker(startDate.getTime(), endDate
				.getTime());
		marker.setPaint(color);
		return marker;
	}

}