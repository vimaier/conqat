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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.jfree.data.time.DateRange;

import org.conqat.lib.commons.clone.IDeepCloneable;

/**
 * List of event annotations. This class is immutable.
 * 
 * @author ladmin
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 2471251A2A9DE58A0490D9186C9DF905
 */
public class AnnotationList implements IDeepCloneable {

	/** Id counter used to produce fresh ids */
	private static int idCounter = 1;

	/** List of contained annotations */
	private final List<EventAnnotationBase> annotations = new ArrayList<EventAnnotationBase>();

	/** Constructor */
	public AnnotationList(List<EventAnnotationBase> annotations) {
		this.annotations.addAll(annotations);

		// Creates a unique mnemonic for each annotation.
		Collections.sort(annotations, AnnotationComparator.INSTANCE);
		for (EventAnnotationBase annotation : annotations) {
			annotation.setMnemonic(Integer.toString(idCounter++));
		}
	}

	/** {@inheritDoc} */
	@Override
	public AnnotationList deepClone() {
		// no copy because this class is immutable.
		return this;
	}

	/** Adds the annotations that are visible inside a date range to the result */
	public List<EventAnnotationBase> projectTo(DateRange range) {
		List<EventAnnotationBase> result = new ArrayList<EventAnnotationBase>();
		for (EventAnnotationBase annotation : annotations) {
			Date date = annotation.getDate();
			if (!date.before(range.getLowerDate())
					&& !date.after(range.getUpperDate())) {
				result.add(annotation);
			}
		}
		return result;
	}

}