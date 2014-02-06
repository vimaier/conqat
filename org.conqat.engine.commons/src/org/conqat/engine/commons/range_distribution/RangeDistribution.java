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
package org.conqat.engine.commons.range_distribution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

import org.conqat.engine.commons.assessment.AssessmentRange;
import org.conqat.engine.commons.assessment.IAssessmentRangesDefinition;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.clone.IDeepCloneable;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.ImmutablePair;
import org.conqat.lib.commons.collections.ListMap;
import org.conqat.lib.commons.collections.Pair;
import org.conqat.lib.commons.collections.UnmodifiableCollection;
import org.conqat.lib.commons.math.EAggregationStrategy;
import org.conqat.lib.commons.math.MathUtils;

/**
 * This class implements a 'Distribution Table'. {@value #DOC}.
 * 
 * Note: The design of this class was driven by two major forces: (1) The
 * interface should be as simple as possible. I found the
 * {@link AssessmentRange} as main data structure for accessing the distribution
 * table quite handy. (2) In this implementation readability beats efficiency. I
 * found the old implementation of distribution tables quite cumbersome and
 * tried to make this as readable as possible although I sacrificed efficiency
 * in multiple places.
 * 
 * This class is immutable. Note: If this class is modified to expose the actual
 * entities, immutability and, hence, deep cloning, must be reconsidered.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 142EC388E200871240E06B9E358B6D19
 */
public class RangeDistribution implements IDeepCloneable {

	/** Documentation, to be reused in processors. */
	public static final String DOC = "Such tables "
			+ "partition a set of entities w.r.t. to a selected metric (the 'principal metric') "
			+ "and a set of boundary values. Example: Given a set of entities where each entity "
			+ "is attributed with its Nesting Depth (ND), its LOC and its SLOC (secondary metrics). "
			+ "The principal metric is Nesting Depth and the specified boundaries are 3 and 5. "
			+ "The distribution table will have the following ranges: [y;3], ]3;5], ]5;x] "
			+ "where y is the minimum ND found for the entities and x is the maximum ND. If y>3 or x<=5,"
			+ "the affected ranges are ommitted. The actual ranges can be accessed via methog getRanges(). "
			+ "The entities belonging to a range can be accessed with getRanges(Range). To obtain the "
			+ "sum of LOC (or SLOC) for entities within a specific range use getSum(Range,String) or "
			+ "getTotal(String) to obtain the total sum of a secondary metric.";

	/** Maps from the range to the entities belonging to this range. */
	private final ListMap<AssessmentRange, IConQATNode> rangeMap = new ListMap<AssessmentRange, IConQATNode>(
			new TreeMap<AssessmentRange, List<IConQATNode>>());

	/** The assessment range definition. */
	private final IAssessmentRangesDefinition assessmentRangesDef;

	/**
	 * The default value used if the principal metric is undefined for an
	 * entity.
	 */
	private final double defaultPrincipalValue;

	/**
	 * Create new distribution table.
	 * 
	 * @param entities
	 *            the entities, may not be empty
	 * @param principalMetric
	 *            key of the principal metric
	 * @param defaultPrincipalValue
	 *            the default value used if the principal metric is undefined
	 *            for an entity.
	 * @param assessmentRangeDef
	 *            the range definition
	 * @throws ConQATException
	 *             if the provided assessment ranges overlap or no entities are
	 *             provided or the principal metric is NaN or infinite for one
	 *             or more entities
	 */
	public RangeDistribution(Collection<? extends IConQATNode> entities,
			String principalMetric, double defaultPrincipalValue,
			IAssessmentRangesDefinition assessmentRangeDef)
			throws ConQATException {

		if (entities.isEmpty()) {
			throw new ConQATException("No entities provided!");
		}

		this.defaultPrincipalValue = defaultPrincipalValue;

		Pair<Double, Double> extrema = determineExtrema(entities,
				principalMetric);
		Set<AssessmentRange> ranges = assessmentRangeDef.obtainRanges(
				extrema.getFirst(), extrema.getSecond());
		checkRanges(ranges);

		storeEntities(ranges, entities, principalMetric);
		this.assessmentRangesDef = assessmentRangeDef;
	}

	/** Check if the ranges overlap. */
	private void checkRanges(Set<AssessmentRange> ranges)
			throws ConQATException {

		List<ImmutablePair<AssessmentRange, AssessmentRange>> pairs = CollectionUtils
				.computeUnorderedPairs(ranges);
		for (ImmutablePair<AssessmentRange, AssessmentRange> pair : pairs) {
			if (pair.getFirst().overlaps(pair.getSecond())) {
				throw new ConQATException("Overlapping ranges "
						+ pair.getFirst() + " and " + pair.getSecond()
						+ " provided");
			}
		}
	}

	/**
	 * Determine the extrema.
	 * 
	 * @return a pair where 1st is the minimum and second the maximum. Both
	 *         values are guaranteed to be not null, not NaN and no infinite.
	 * @throws ConQATException
	 *             if the principal metric is NaN or infinite for one (or more)
	 *             of the entities
	 */
	private Pair<Double, Double> determineExtrema(
			Collection<? extends IConQATNode> entities, String principalMetric)
			throws ConQATException {
		CCSMAssert.isFalse(entities.isEmpty(), "No entities provided");

		double max = Double.NEGATIVE_INFINITY;
		double min = Double.POSITIVE_INFINITY;

		for (IConQATNode entity : entities) {
			double value = NodeUtils.getDoubleValue(entity, principalMetric,
					defaultPrincipalValue);

			if (Double.isNaN(value) || Double.isInfinite(value)) {
				throw new ConQATException("Metric " + principalMetric
						+ " of entity " + entity.getId()
						+ " is NaN or infinite.");
			}

			min = Math.min(min, value);
			max = Math.max(max, value);
		}
		return new Pair<Double, Double>(min, max);
	}

	/** Store the entities in the ranges. */
	private void storeEntities(Set<AssessmentRange> ranges,
			Collection<? extends IConQATNode> entities, String principalMetric) {

		// this is used to determine the correct range for each entity
		NavigableMap<Double, AssessmentRange> map = new TreeMap<Double, AssessmentRange>();

		for (AssessmentRange range : ranges) {
			map.put(range.getUpper(), range);
		}

		for (IConQATNode entity : entities) {
			double value = NodeUtils.getDoubleValue(entity, principalMetric,
					defaultPrincipalValue);
			Entry<Double, AssessmentRange> ceilingEntry = map
					.ceilingEntry(value);
			rangeMap.add(ceilingEntry.getValue(), entity);
		}
	}

	/** Get the ranges of this distribution table. */
	public List<AssessmentRange> getRanges() {
		return new ArrayList<AssessmentRange>(rangeMap.getKeys());
	}

	/**
	 * Get entities belonging to a specific range. This returns an empty
	 * collection if the table holds no entities for the specified range.
	 */
	/* package */UnmodifiableCollection<IConQATNode> getEntities(
			AssessmentRange range) {
		Collection<IConQATNode> entities = rangeMap.getCollection(range);
		CCSMAssert.isNotNull(entities);
		return CollectionUtils.asUnmodifiable(entities);
	}

	/*** Get all entities. */
	private List<IConQATNode> getEntities() {
		List<IConQATNode> result = new ArrayList<IConQATNode>();
		for (AssessmentRange range : rangeMap.getKeys()) {
			result.addAll(getEntities(range));
		}
		return result;
	}

	/**
	 * Aggregate the values of the secondary metric for all entities with the
	 * specified aggregation strategy. Entities that do not have a metric value
	 * for the specified metric are ignored.
	 */
	public double aggregate(EAggregationStrategy strategy,
			String secondaryMetric) {
		return MathUtils.aggregate(
				obtainValues(getEntities(), secondaryMetric), strategy);
	}

	/**
	 * Aggregate the values of the secondary metric for the entities in the
	 * specified range with the specified aggregation strategy. Entities that do
	 * not have a metric value for the specified metric are ignored.
	 */
	public double aggregate(AssessmentRange range,
			EAggregationStrategy strategy, String secondaryMetric) {
		return MathUtils.aggregate(
				obtainValues(getEntities(range), secondaryMetric), strategy);
	}

	/**
	 * Get the sum of the secondary metrics for all entities in the specified
	 * range. This returns zero if the table holds no entities for the specified
	 * range.
	 */
	public double getSum(AssessmentRange range, String secondaryMetric) {
		return aggregate(range, EAggregationStrategy.SUM, secondaryMetric);
	}

	/**
	 * Get the sum of the secondary metric for all entities. This returns zero
	 * collection if the table holds no entities.
	 */
	public double getTotal(String secondaryMetric) {
		return aggregate(EAggregationStrategy.SUM, secondaryMetric);
	}

	/**
	 * Obtain the value of the entities for the specified secondary metric.
	 * Entities that do not have a metric value for the specified metric are
	 * ignored.
	 */
	private Collection<Double> obtainValues(
			Collection<? extends IConQATNode> entities, String secondaryMetric) {
		Collection<Double> values = new ArrayList<Double>();
		for (IConQATNode entity : entities) {
			try {
				values.add(NodeUtils.getDoubleValue(entity, secondaryMetric));
			} catch (ConQATException e) {
				// ignore
			}
		}
		return values;
	}

	/**
	 * Get the relative amount of the sum of the secondary metrics for all
	 * entities in the specified range w.r.t. to the sum of the secondary metric
	 * for all entities. This returns {@link Double#NaN} if sum of the secondary
	 * metric values is zero.
	 */
	public double getPercentage(AssessmentRange range, String secondaryMetric) {
		double total = getTotal(secondaryMetric);
		if (total == 0) {
			return Double.NaN;
		}
		return getSum(range, secondaryMetric) / total;
	}

	/**
	 * Checks if this range distribution has a range definition with the
	 * specified name. Note that this does not automatically imply that
	 * {@link #getRanges()} will return a range with this name, as the range
	 * could be excluded as no measurement data was found for it.
	 */
	public boolean hasRangeDefinition(String rangeName) {
		return assessmentRangesDef.hasRangeDefinition(rangeName);
	}

	/**
	 * Returns the range with the specified name. Returns null if the specified
	 * range is not defined or empty.
	 */
	public AssessmentRange getRange(String rangeName) {
		for (AssessmentRange range : getRanges()) {
			if (range.getName().equals(rangeName)) {
				return range;
			}
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public RangeDistribution deepClone() {
		return this;
	}
}
