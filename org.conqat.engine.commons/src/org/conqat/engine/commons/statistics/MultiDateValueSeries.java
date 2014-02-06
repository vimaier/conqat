/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
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

import org.conqat.lib.commons.collections.PairList;
import org.conqat.lib.commons.collections.UnmodifiableMap;

/**
 * This class manages multiple named date value series.
 * <p>
 * Overridden methods from DateValueSeries work on the first series only.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 40420 $
 * @ConQAT.Rating GREEN Hash: 0564270FB004B6D3505D86913C5218B1
 */
public class MultiDateValueSeries extends DateValueSeries {

	/** The series. */
	private final PairList<String, DateValueSeries> series = new PairList<String, DateValueSeries>();

	/** Create new series. */
	public MultiDateValueSeries() {
		// nothing to do
	}

	/** Copy constructor. */
	private MultiDateValueSeries(MultiDateValueSeries other) {
		for (int i = 0; i < other.series.size(); ++i) {
			series.add(other.series.getFirst(i), other.series.getSecond(i)
					.deepClone());
		}
	}

	/** Adds a series to this one. */
	public void addSeries(String name, DateValueSeries series) {
		this.series.add(name, series);
	}

	/** Returns the size, i.e. the number of series. */
	public int getSize() {
		return series.size();
	}

	/** Returns the name of the series with the given index. */
	public String getName(int index) {
		return series.getFirst(index);
	}

	/** Returns the series with the given index. */
	public DateValueSeries getSeries(int index) {
		return series.getSecond(index);
	}

	/** {@inheritDoc} */
	@Override
	public void addValue(Date date, double value) {
		first().addValue(date, value);
	}

	/** {@inheritDoc} */
	@Override
	public UnmodifiableMap<Date, Double> getValues() {
		return first().getValues();
	}

	/** {@inheritDoc} */
	@Override
	public Date getEarliestDate() {
		return first().getEarliestDate();
	}

	/** {@inheritDoc} */
	@Override
	public Date getLatestDate() {
		return first().getLatestDate();
	}

	/** Returns the first series (or creates one if empty). */
	private DateValueSeries first() {
		if (series.isEmpty()) {
			series.add("default", new DateValueSeries());
		}
		return series.getSecond(0);
	}

	/** {@inheritDoc} */
	@Override
	public MultiDateValueSeries deepClone() {
		return new MultiDateValueSeries(this);
	}

}
