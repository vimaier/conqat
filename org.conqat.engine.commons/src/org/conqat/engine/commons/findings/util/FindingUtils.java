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
package org.conqat.engine.commons.findings.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.conqat.engine.commons.findings.EFindingKeys;
import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.findings.FindingCategory;
import org.conqat.engine.commons.findings.FindingGroup;
import org.conqat.engine.commons.findings.FindingReport;
import org.conqat.engine.commons.findings.location.ElementLocation;
import org.conqat.engine.commons.findings.location.QualifiedNameLocation;
import org.conqat.engine.commons.findings.location.TextRegionLocation;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeConstants;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.assessment.ETrafficLightColor;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.ListMap;
import org.conqat.lib.commons.region.Region;

/**
 * Utility methods for findings.
 * 
 * @author $Author: pfaller $
 * @version $Rev: 47078 $
 * @ConQAT.Rating YELLOW Hash: 10C0CC2590BB8757E5511E4FAB5945B6
 */
public class FindingUtils {

	/** The expression used to split the prefix of a qualified name. */
	public static final String QUALIFIEDNAME_SPLIT_EXPRESSION = "["
			+ QualifiedNameLocation.SEPARATOR_CHARS + "]";

	/**
	 * Returns findings grouped by the element they are referencing. The key of
	 * the returned map is the uniform path of the element.
	 */
	public static ListMap<String, Finding> getFindingsByElement(
			FindingReport report) {
		ListMap<String, Finding> result = new ListMap<String, Finding>();
		for (FindingCategory category : report.getChildren()) {
			for (FindingGroup group : category.getChildren()) {
				for (Finding finding : group.getChildren()) {
					result.add(finding.getLocation().getUniformPath(), finding);
				}
			}
		}
		return result;
	}

	/**
	 * Returns findings grouped by the qualified name they are referencing
	 * (possibly just a prefix is used). Findings with a location different from
	 * qualified name are ignored. To determine the prefix, the
	 * {@link QualifiedNameLocation#SEPARATOR_CHARS} are interpreted as
	 * separator chars.
	 */
	public static ListMap<String, Finding> getFindingsByQualifiedName(
			FindingReport report) {
		return getFindingsByQualifiedName(report,
				QUALIFIEDNAME_SPLIT_EXPRESSION);
	}

	/**
	 * Returns findings grouped by the qualified name they are referencing
	 * (possibly just a prefix is used). Findings with a location different from
	 * qualified name are ignored.
	 * 
	 * @param separatorExpression
	 *            a regular expression which is used to split the prefix from
	 *            the remainder. Also see {link String#split(String)}. If this
	 *            is null, the entire qualified name will be used.
	 */
	private static ListMap<String, Finding> getFindingsByQualifiedName(
			FindingReport report, String separatorExpression) {
		ListMap<String, Finding> result = new ListMap<String, Finding>();
		for (FindingCategory category : report.getChildren()) {
			for (FindingGroup group : category.getChildren()) {
				for (Finding finding : group.getChildren()) {
					String qualifiedName = getQualifiedName(finding,
							separatorExpression);
					if (qualifiedName != null) {
						result.add(qualifiedName, finding);
					}
				}
			}
		}
		return result;
	}

	/**
	 * Returns the qualified name (resp. the prefixes) which is affected by a
	 * finding (or null). Also see
	 * {@link #getFindingsByQualifiedName(FindingReport, String)}.
	 */
	private static String getQualifiedName(Finding finding,
			String separatorExpression) {
		ElementLocation location = finding.getLocation();
		if (!(location instanceof QualifiedNameLocation)) {
			return null;
		}

		String prefix = ((QualifiedNameLocation) location).getQualifiedName();
		if (separatorExpression != null) {
			prefix = prefix.split(separatorExpression, 2)[0];
		}
		return prefix;
	}

	/**
	 * Adopts (i.e. clones) the given findings for the provided target report.
	 * So the findings will be copied into the given report.
	 * 
	 * @throws ConQATException
	 *             in case of deep cloning problems.
	 */
	public static List<Finding> adoptFindings(FindingReport targetReport,
			Collection<Finding> findings) throws ConQATException {
		List<Finding> result = new ArrayList<Finding>();
		for (Finding finding : findings) {
			result.add(adoptFinding(targetReport, finding));
		}
		return result;
	}

	/**
	 * Adopts (i.e. clones) the given finding for the provided target report.
	 * 
	 * @throws ConQATException
	 *             in case of deep cloning problems.
	 */
	public static Finding adoptFinding(FindingReport targetReport,
			Finding finding) throws ConQATException {
		FindingGroup sourceGroup = finding.getParent();
		FindingCategory targetCategory = targetReport
				.getOrCreateCategory(sourceGroup.getParent().getName());
		FindingGroup targetGroup = targetCategory.getGroupByName(sourceGroup
				.getName());
		if (targetGroup == null) {
			targetGroup = targetCategory.createFindingGroup(sourceGroup
					.getName());
			NodeUtils.copyValues(sourceGroup.getKeys(), sourceGroup,
					targetGroup);
		}

		Finding result = targetGroup.createFinding(finding.getLocation());
		NodeUtils.copyValues(finding.getKeys(), finding, result);
		return result;
	}

	/**
	 * This gets or creates a finding group with the specified name. This also
	 * stores the rule id under key {@link NodeConstants#RULE_IDENTIFIER_KEY}.
	 * 
	 * @throws AssertionError
	 *             on attempt to overwrite a rule id with different one or if a
	 *             rule identifier which is not a string is stored. We use
	 *             assertions here as we assume a programming problem in one of
	 *             the processors.
	 */
	public static FindingGroup getOrCreateFindingGroupAndSetRuleId(
			FindingCategory findingCategory, String groupName, String ruleId) {

		FindingGroup group = findingCategory.getGroupByName(groupName);

		if (group == null) {
			group = findingCategory.createFindingGroup(groupName);
		}

		Object currentRuleId = group
				.getValue(NodeConstants.RULE_IDENTIFIER_KEY);

		if (currentRuleId == null) {
			group.setValue(NodeConstants.RULE_IDENTIFIER_KEY, ruleId);
		} else {
			CCSMAssert.isInstanceOf(currentRuleId, String.class);
			CCSMAssert.isTrue(currentRuleId.equals(ruleId),
					"Attempt to overwrite rule id " + currentRuleId + " with "
							+ ruleId);
		}

		return group;
	}

	/**
	 * Create a finding and attach it to an element.
	 * 
	 * @param group
	 *            the finding group the finding belongs to
	 * @param message
	 *            the message
	 * @param element
	 *            the affected element
	 * @param location
	 *            the location of the finding
	 * @param key
	 *            the key used to store the finding
	 * @return the created finding
	 */
	public static Finding createAndAttachFinding(FindingGroup group,
			String message, IConQATNode element, ElementLocation location,
			String key) {
		Finding finding = group.createFinding(location);
		finding.setValue(EFindingKeys.MESSAGE.toString(), message);
		NodeUtils.getOrCreateFindingsList(element, key).add(finding);
		return finding;
	}

	/** Retrieve color stored in finding */
	public static ETrafficLightColor getFindingColor(Finding finding) {
		Object colorObject = finding.getValue(EFindingKeys.ASSESSMENT
				.toString());
		if (colorObject != null) {
			CCSMAssert.isTrue(
					colorObject instanceof ETrafficLightColor,
					"Object stored under key " + EFindingKeys.ASSESSMENT
							+ " must be an "
							+ ETrafficLightColor.class.getSimpleName()
							+ " but was "
							+ colorObject.getClass().getSimpleName());
			return (ETrafficLightColor) colorObject;
		}

		return ETrafficLightColor.RED;
	}

	/**
	 * Determines whether a finding overlaps with a {@link Region}. The region
	 * must be based on raw line numbers.
	 */
	public static boolean overlapsRawLineRegion(Finding finding,
			Region rawLineRegion) {
		ElementLocation location = finding.getLocation();
		if (location instanceof TextRegionLocation) {
			TextRegionLocation regionLocation = (TextRegionLocation) location;
			if (rawLineRegion.overlaps(new Region(regionLocation
					.getRawStartLine(), regionLocation.getRawEndLine()))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Retrieves a list of all findings from the given categories. If no
	 * category name is specified, findings for all categories are returned.
	 */
	public static List<Finding> getAllFindings(FindingReport report,
			String... categoryNames) {
		List<Finding> result = new ArrayList<Finding>();
		HashSet<String> includeCategoryNames = CollectionUtils
				.asHashSet(categoryNames);

		for (FindingCategory category : report.getChildren()) {
			if (includeCategoryNames.isEmpty()
					|| includeCategoryNames.contains(category.getName())) {
				for (FindingGroup group : category.getChildren()) {
					for (Finding finding : group.getChildren()) {
						result.add(finding);
					}
				}
			}
		}

		return result;
	}

	/**
	 * Sets the color for a finding. The color will be stored under the key
	 * {@link EFindingKeys#ASSESSMENT}.
	 */
	public static void setFindingColor(Finding finding, ETrafficLightColor color) {
		if (finding != null) {
			finding.setValue(EFindingKeys.ASSESSMENT.toString(), color);
		}
	}
}