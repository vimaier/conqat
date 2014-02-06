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
package org.conqat.engine.commons.statistics;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.conqat.lib.commons.clone.IDeepCloneable;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableMap;
import org.conqat.lib.commons.date.DateUtils;

/**
 * This class stores a series of date-value pairs. The order is not relevant as
 * the items are usually sorted by date.
 * 
 * @author Florian Deissenboeck
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: 043622C0D8EDB881E3B7543869BEFF99
 */
public class DateValueSeries implements IDeepCloneable {

	/** Series is map from date to double. */
	private final Map<Date, Double> values = new HashMap<Date, Double>();

	/** Create new series. */
	public DateValueSeries() {
		// nothing to do
	}

	/** Copy constructor. */
	private DateValueSeries(DateValueSeries series) {
		values.putAll(series.values);
	}

	/** Add a new date value pair. If a date already exists it is overwritten. */
	public void addValue(Date date, double value) {
		values.put(date, value);
	}

	/** Get values. */
	public UnmodifiableMap<Date, Double> getValues() {
		return CollectionUtils.asUnmodifiable(values);
	}

	/** {@inheritDoc} */
	@Override
	public DateValueSeries deepClone() {
		return new DateValueSeries(this);
	}

	/** Returns lowest date in series */
	public Date getEarliestDate() {
		return DateUtils.getEarliest(values.keySet());
	}

	/** Returns highest date in series */
	public Date getLatestDate() {
		return DateUtils.getLatest(values.keySet());
	}
}