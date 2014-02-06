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

import java.util.Comparator;
import java.util.Date;

import org.conqat.lib.commons.assertion.CCSMAssert;

/**
 * Compares annotations by their dates.
 * 
 * @author ladmin
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 5EABB9DEF10921CA30A95FA2F85B2B01
 */
public class AnnotationComparator implements Comparator<EventAnnotationBase> {

	/** Singleton instance */
	public static final AnnotationComparator INSTANCE = new AnnotationComparator();

	/** {@inheritDoc} */
	@Override
	public int compare(EventAnnotationBase a1, EventAnnotationBase a2) {
		CCSMAssert.isNotNull(a1);
		CCSMAssert.isNotNull(a2);
		Date d1 = a1.getDate();
		Date d2 = a2.getDate();

		CCSMAssert.isNotNull(d1);
		CCSMAssert.isNotNull(d2);
		return d1.compareTo(d2);
	}

}