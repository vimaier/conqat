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

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;

/**
 * Often two value series should be plotted in the same graph but one series
 * contains way more (older) values than the other and the resulting graph is
 * not suitable for the interpretation of relations between the two series'.
 * This processor time-trims a series in order to compare it to another series.
 * 
 * @author Florian Deissenboeck
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: FF47906C36EC4893A160E52A0E985A64
 */
@AConQATProcessor(description = "Often two value series should be plotted in the "
		+ "same graph but one series contains way more (older) values than the "
		+ "other and the resulting graph is not suitable for the "
		+ "interpretation of relations between the two series'. This "
		+ "processor time-trims a series in order to compare it to another "
		+ "series.")
public class ValueSeriesTrimmer extends ConQATProcessorBase {

	/** Series to be trimmed, the series that carries the data. */
	private DateValueSeries targetSeries;

	/** The trimming policy. */
	private ETrimPolicy trimPolicy;

	/** The lower bound defined by the source series. */
	private Date lowerBound;

	/** The upper bound defined by the source series. */
	private Date upperBound;

	/** Enumeration to described the trimming policy. */
	public enum ETrimPolicy {
		/** Create matching start point. */
		UPPER,
		/** Create matching end point. */
		LOWER,
		/** Create matching start and end point. */
		BOTH
	}

	/** Set target series. */
	@AConQATParameter(name = "target-series", minOccurrences = 1, maxOccurrences = 1, description = "The series to be trimmed "
			+ "(the one that carries the data).")
	public void setTargetSeries(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC)
			DateValueSeries series) {
		this.targetSeries = series;
	}

	/**
	 * Set source series.
	 * 
	 * @throws ConQATException
	 *             if the series is empty
	 */
	@AConQATParameter(name = "source-series", minOccurrences = 1, maxOccurrences = 1, description = "The series that defines the trimming points.")
	public void setSourceSeries(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC)
			DateValueSeries sourceSeries) throws ConQATException {

		Collection<Date> values = sourceSeries.getValues().keySet();
		if (values.isEmpty()) {
			throw new ConQATException("Source series is empty.");
		}
		lowerBound = Collections.min(values);
		upperBound = Collections.max(values);
	}

	/** Set policy. */
	@AConQATParameter(name = "trim", minOccurrences = 1, maxOccurrences = 1, description = "Trimming policy is either 'LOWER' "
			+ "(trim lower end of time interval), 'UPPER' (trim upper end) or 'BOTH' (trim both ends).")
	public void setTrimPolicy(
			@AConQATAttribute(name = "policy", description = "Reference to the generating processor.")
			ETrimPolicy policy) {
		this.trimPolicy = policy;
	}

	/** {@inheritDoc} */
	@Override
	public DateValueSeries process() {
		DateValueSeries result = new DateValueSeries();
		for (Map.Entry<Date, Double> e : targetSeries.getValues().entrySet()) {
			if (isIncluded(e.getKey())) {
				result.addValue(e.getKey(), e.getValue());
			}
		}
		return result;
	}

	/** Test if a point in time is included in the new series. */
	private boolean isIncluded(Date time) {
		switch (trimPolicy) {
		case UPPER:
			return time.compareTo(upperBound) <= 0;
		case LOWER:
			return time.compareTo(lowerBound) >= 0;
		case BOTH:
			return time.compareTo(upperBound) <= 0
					&& time.compareTo(lowerBound) >= 0;
		default:
			throw new IllegalStateException("Unknown enum element.");
		}
	}
}