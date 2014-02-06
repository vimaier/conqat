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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.conqat.engine.commons.assessment.AssessmentRange;
import org.conqat.engine.commons.assessment.AssessmentRangeDefinition;
import org.conqat.engine.commons.assessment.AssessmentRangesDefinition;
import org.conqat.engine.commons.assessment.AssessmentRangesDefinitionBase;
import org.conqat.engine.commons.assessment.IAssessmentRangesDefinition;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.SetNode;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.clone.IDeepCloneable;
import org.conqat.lib.commons.color.ECCSMColor;
import org.conqat.lib.commons.enums.EnumUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Test is not a pure unit test but rather an integration test for
 * {@link RangeDistribution}, {@link AssessmentRangesDefinitionBase} and
 * {@link PercentageLessOrEqualRule}.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 52BFA943EA1DADC4BBBBE0B2BC5BC650
 */
public class RangeDistributionTest extends TestCase {

	/** Principal metric. */
	/* package */static final String P_METRIC = "principal_metric";

	/** Secondary metric. */
	/* package */static final String S_METRIC = "secondary_metric";

	/** Test with empty boundaries. */
	public void testNoBoundaries() throws ConQATException {
		assertDistTable(
				newDistTable(createEntities("a#2:30", "b#4:80", "c#7:100",
						"d#8:50")), "[2;8]red$260%1#a,b,c,d");
	}

	/** Test normal cases. */
	public void testNormal() throws ConQATException {
		assertDistTable(
				newDistTable(
						createEntities("a#2:30", "b#4:80", "c#7:100", "d#8:50"),
						"5:green", "7:yellow"), "[2;5]green$110%0.42#a,b",
				"]5;7]yellow$100%0.38#c", "]7;8]red$50%0.19#d");

		assertDistTable(
				newDistTable(
						createEntities("a#2.3:30", "b#4.1:80", "c#6.9:100",
								"d#8.5:50"), "5:green", "7:yellow"),
				"[2.3;5]green$110%0.42#a,b", "]5;7]yellow$100%0.38#c",
				"]7;8.5]red$50%0.19#d");

		assertDistTable(
				newDistTable(createEntities("a#0:30", "b#5:80", "c#7:100"),
						"5:green", "7:yellow"), "[0;5]green$110%0.52#a,b",
				"]5;7]yellow$100%0.47#c");
	}

	/**
	 * Test a case where all values are lower than then the specified
	 * boundaries.
	 */
	public void testAllValuesLowerThanBoundaries() throws ConQATException {
		assertDistTable(
				newDistTable(
						createEntities("a#2:30", "b#4:80", "c#7:100", "d#8:50"),
						"10:green", "14:yellow"), "[2;8]green$260%1#a,b,c,d");
	}

	/**
	 * Test a case where all values greater lower than then the specified
	 * boundaries.
	 */
	public void testAllValuesGreaterThanBoundaries() throws ConQATException {
		assertDistTable(
				newDistTable(createEntities("b#4:80", "c#7:100", "d#8:50"),
						"1:green", "3:yellow"), "[4;8]red$230%1#b,c,d");
	}

	/**
	 * Test values on boundaries. 
	 */
	public void testValuesOnBoundaries() throws ConQATException {
		assertDistTable(
				newDistTable(createEntities("b#5:100", "c#7:100"), "5:green"),
				"[5;5]green$100%0.5#b", "]5;7]red$100%0.5#c");

		assertDistTable(
				newDistTable(createEntities("b#5:100", "c#7:100"), "5:green",
						"7:yellow"), "[5;5]green$100%0.5#b",
				"]5;7]yellow$100%0.5#c");

		assertDistTable(
				newDistTable(createEntities("b#5:100", "c#7:100", "d#9:200"),
						"5:green", "7:yellow"), "[5;5]green$100%0.25#b",
				"]5;7]yellow$100%0.25#c", "]7;9]red$200%0.5#d");

		assertDistTable(
				newDistTable(createEntities("b#5:100", "c#6:100", "d#8:200"),
						"5:green", "7:yellow"), "[5;5]green$100%0.25#b",
				"]5;7]yellow$100%0.25#c", "]7;8]red$200%0.5#d");
	}

	/** Test one boundary. */
	public void testOneBoundary() throws ConQATException {
		assertDistTable(
				newDistTable(
						createEntities("a#2:30", "b#4:80", "c#7:100", "d#8:50"),
						"5:green"), "[2;5]green$110%0.42#a,b",
				"]5;8]red$150%0.57#c,d");
	}

	/** Test one entity. */
	public void testOneEntity() throws ConQATException {
		assertDistTable(
				newDistTable(createEntities("a#2:30"), "5:green", "7:yellow"),
				"[2;2]green$30%1#a");

		assertDistTable(
				newDistTable(createEntities("a#6:30"), "5:green", "7:yellow"),
				"[6;6]yellow$30%1#a");

		assertDistTable(
				newDistTable(createEntities("a#8:30"), "5:green", "7:yellow"),
				"[8;8]red$30%1#a");
	}

	/**
	 * Test with a metric where higher values means better quality, e.g. comment
	 * ratio.
	 */
	public void testCommentRatio() throws ConQATException {
		assertDistTable(
				newDistTable(
						createEntities("a#0.5:10", "b#0.1:10", "c#0.4:10",
								"d#0.7:10"), ECCSMColor.GREEN, "0.3:red",
						"0.5:yellow"), "[0.1;0.3]red$10%0.25#b",
				"]0.3;0.5]yellow$20%0.5#a,c", "].5;0.7]green$10%0.25#d");

	}

	/**
	 * We introduced this as we had strange behavior due to a buggy
	 * implementation of {@link AssessmentRange#compareTo(org.conqat.lib.commons.math.Range)}
	 */
	public void testAssessmentRange() {
		NavigableMap<AssessmentRange, String> rangeMap = new TreeMap<AssessmentRange, String>();

		AssessmentRange range1 = new AssessmentRange(0.1, true,
				new AssessmentRangeDefinition(0.3, ECCSMColor.BLUE.getColor(),
						ECCSMColor.BLUE.name()));
		rangeMap.put(range1, range1.toString());

		AssessmentRange range2 = new AssessmentRange(0.3, false,
				new AssessmentRangeDefinition(0.5, ECCSMColor.BLUE.getColor(),
						ECCSMColor.BLUE.name()));

		assertTrue(rangeMap.containsKey(range1));
		assertFalse(rangeMap.containsKey(range2));
	}
	
	/** Test if the default value is used if a principal metric is missing. */
	public void testMissingPrincipalMetric() throws ConQATException {
		assertDistTable(
				newDistTable(
						createEntities("a#:30", "b#4:80", "c#7:100", "d#8:50"),
						"5:green", "7:yellow"), "[0;5]green$110%0.42#a,b",
				"]5;7]yellow$100%0.38#c", "]7;8]red$50%0.19#d");
	}

	/**
	 * Test if corrupt range definitions are rejected
	 * 
	 * @throws ConQATException
	 */
	public void testCorruptRangeDefinitions() throws ConQATException {
		IAssessmentRangesDefinition assessmentRangeDef = new CorruptAssessmentRangesDefinition();
		try {
			new RangeDistribution(createEntities("a#0.5:10", "b#0.1:10",
					"c#0.4:10", "d#0.7:10"), P_METRIC, 0, assessmentRangeDef);
			fail();
		} catch (Exception ex) {
			// expected
		}
	}

	/** Assert distribution table. */
	private void assertDistTable(RangeDistribution actual,
			String... expectedRangeDescriptors) {

		int i = 0;
		for (AssessmentRange range : actual.getRanges()) {
			assertRange(actual, range, expectedRangeDescriptors[i]);
			i += 1;
		}
	}

	/** Assert a range. */
	private void assertRange(RangeDistribution table, AssessmentRange range,
			String descriptor) {
		ExpectedRange expectedRange = new ExpectedRange(descriptor);

		assertEquals(expectedRange, range);
		assertEquals(expectedRange.getColor(), range.getColor());
		assertEquals(expectedRange.getName().toUpperCase(), range.getName());
		assertEquals(expectedRange.secondarySum, table.getSum(range, S_METRIC));
		double delta = Math.abs(expectedRange.secondaryPercent
				- table.getPercentage(range, S_METRIC));
		assertTrue("Delta is " + delta, delta < 0.01);

		Set<String> actualEntityIds = new HashSet<String>();

		for (IConQATNode entity : table.getEntities(range)) {
			actualEntityIds.add(entity.getId());
		}

		assertEquals(expectedRange.entityIds, actualEntityIds);

	}

	/** Create a distribution table with default color red. */
	private RangeDistribution newDistTable(Collection<IConQATNode> entities,
			String... boundaryDescriptors) throws ConQATException {

		return newDistTable(entities, ECCSMColor.RED, boundaryDescriptors);
	}
	
	
	/**
	 * Create a distribution table.
	 */
	private RangeDistribution newDistTable(Collection<IConQATNode> entities,
			ECCSMColor defaultColor, String... boundaryDescriptors)
			throws ConQATException {

		AssessmentRangesDefinition assessmentDef = createAssessmentRangesDefinition(
				defaultColor, boundaryDescriptors);

		return new RangeDistribution(entities, P_METRIC, 0, assessmentDef);
	}

	/**
	 * Create assessment ranges.
	 */
	/* package */static AssessmentRangesDefinition createAssessmentRangesDefinition(
			ECCSMColor defaultColor, String... boundaryDescriptors) throws ConQATException {
		List<AssessmentRangeDefinition> rangeDefs = new ArrayList<AssessmentRangeDefinition>();

		for (String descriptor : boundaryDescriptors) {
			String[] parts = descriptor.split(":");
			ECCSMColor color = EnumUtils.valueOfIgnoreCase(ECCSMColor.class,
					parts[1]);
			AssessmentRangeDefinition rangeDef = new AssessmentRangeDefinition(
					Double.parseDouble(parts[0]), color.getColor(),
					color.name());
			rangeDefs.add(rangeDef);
		}

		AssessmentRangesDefinition assessmentDef = new AssessmentRangesDefinition(
				defaultColor.getColor(), defaultColor.name(), rangeDefs);
		return assessmentDef;
	}

	/** Create entities. "a#2:30", "b#4:80", "c#7:100", "d#8:55" */
	/* package */static Collection<IConQATNode> createEntities(
			String... descriptors) {
		List<IConQATNode> result = new ArrayList<IConQATNode>();

		for (String descriptor : descriptors) {

			String id = parse("^(.*)#", descriptor).trim();

			SetNode<String> node = new SetNode<String>(id);

			if (!StringUtils.isEmpty(parse("#(.*):", descriptor))) {
				node.setValue(P_METRIC, parseDouble("#(.*):", descriptor));
			}

			node.setValue(S_METRIC, parseDouble(":(.*)$", descriptor));

			result.add(node);
		}

		return result;
	}

	/**
	 * Extract a string from another string. The pattern must have exactly one
	 * capturing group.
	 */
	private static String parse(String patternString, String descriptor) {
		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(descriptor);
		matcher.find();
		return matcher.group(1);
	}

	/**
	 * Extract a string from another string and convert it to a double. The
	 * pattern must have exactly one capturing group.
	 */
	private static double parseDouble(String pattern, String descriptor) {
		return Double.parseDouble(parse(pattern, descriptor));
	}

	/** This class is used to describe expected results. */
	private static class ExpectedRange extends AssessmentRange {

		/** Sum of secondary metric. */
		private final double secondarySum;

		/** Secondary metric in percent. */
		private final double secondaryPercent;

		/** Ids of entities. */
		private final Set<String> entityIds;

		/**
		 * Create new expected range.
		 * 
		 * @param descriptor
		 *            the format is
		 * 
		 *            <pre>
		 * ]<lower>;<upper>]<color_name>$<secondary_sum>%<secondary_percent>#<entity_ids>
		 * </pre>
		 * 
		 *            Example: [3;5]color$35.0%0.35#a,b,c .
		 */
		public ExpectedRange(String descriptor) {
			super(getLower(descriptor), isLowerInclusive(descriptor),
					getRangeDefinition(descriptor));
			entityIds = getEntityIds(descriptor);
			secondarySum = getSecondarySum(descriptor);
			secondaryPercent = getSecondaryPercent(descriptor);
		}

		/** Parse secondary percent. */
		private static double getSecondaryPercent(String descriptor) {
			return parseDouble("\\%(.*)#", descriptor);
		}

		/** Parse secondary sum. */
		private static double getSecondarySum(String descriptor) {
			return parseDouble("\\$(.*)%", descriptor);
		}

		/** Parse entity ids. */
		private static Set<String> getEntityIds(String descriptor) {
			String entityNames = parse("#(.*)$", descriptor);

			Set<String> result = new HashSet<String>();

			for (String name : entityNames.split(",")) {
				result.add(name.trim());
			}

			return result;
		}

		/** Parse color. */
		private static AssessmentRangeDefinition getRangeDefinition(
				String descriptor) {
			// pattern inserts arbitrary characters to match second bracket
			String colorName = parse(".+\\](.*)\\$", descriptor);
			Color color = EnumUtils.valueOfIgnoreCase(ECCSMColor.class,
					colorName).getColor();
			return new AssessmentRangeDefinition(getUpper(descriptor), color,
					colorName);
		}

		/** Parse upper. */
		private static double getUpper(String descriptor) {
			return parseDouble(";(.*)\\]", descriptor);
		}

		/** Parse lower inclusion. */
		private static boolean isLowerInclusive(String descriptor) {
			return descriptor.charAt(0) == '[';
		}

		/** Parse upper. */
		private static double getLower(String descriptor) {
			return parseDouble("[\\[\\]](.*);", descriptor);
		}

	}

	/** A corrupt implementation of {@link IAssessmentRangesDefinition} */
	private class CorruptAssessmentRangesDefinition implements
			IAssessmentRangesDefinition {

		/** {@inheritDoc} */
		@Override
		public Set<AssessmentRange> obtainRanges(double minValue,
				double maxValue) {

			Set<AssessmentRange> result = new HashSet<AssessmentRange>();

			result.add(new AssessmentRange(0, true,
					new AssessmentRangeDefinition(5, Color.blue, "default")));

			result.add(new AssessmentRange(0, true,
					new AssessmentRangeDefinition(15, Color.blue, "default")));

			return result;
		}

		/** {@inheritDoc} */
		@Override
		public boolean hasRangeDefinition(String rangeName) {
			return true;
		}

		/** {@inheritDoc} */
		@Override
		public AssessmentRangeDefinition obtainRangeDefinition(double value) {
			return null;
		}
		
		/** {@inheritDoc} */
		@Override
		public IDeepCloneable deepClone() {
			return this;
		}

	}
}
