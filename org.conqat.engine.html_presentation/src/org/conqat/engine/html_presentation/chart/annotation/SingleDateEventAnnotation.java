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

import java.util.Date;

import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;

/**
 * Annotation for event that refers to a single date.
 * 
 * @author ladmin
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 383C766A871342EAD8D571A83781F92A
 */
public class SingleDateEventAnnotation extends EventAnnotationBase {

	/** Date of the annotation */
	private final Date date;

	/** Constructor */
	public SingleDateEventAnnotation(String message, Date date) {
		super(message);
		this.date = date;
	}

	/** {@inheritDoc} */
	@Override
	public Date getDate() {
		return date;
	}

	/** {@inheritDoc} */
	@Override
	protected Marker doCreateMarker() {
		return new ValueMarker(date.getTime());
	}

}