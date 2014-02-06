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
package org.conqat.engine.commons.assessment;

import static org.conqat.engine.commons.ConQATParamDoc.FINDING_CATEGORY_NAME;
import static org.conqat.engine.commons.ConQATParamDoc.FINDING_GROUP_NAME;
import static org.conqat.engine.commons.ConQATParamDoc.FINDING_MESSAGE_NAME;
import static org.conqat.engine.commons.ConQATParamDoc.FINDING_PARAM_NAME;
import static org.conqat.engine.commons.ConQATParamDoc.WRITEKEY_KEY_DESC;
import static org.conqat.engine.commons.ConQATParamDoc.WRITEKEY_KEY_NAME;

import java.util.EnumSet;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.findings.EFindingKeys;
import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.findings.FindingCategory;
import org.conqat.engine.commons.findings.FindingGroup;
import org.conqat.engine.commons.findings.location.ElementLocation;
import org.conqat.engine.commons.findings.util.FindingUtils;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.commons.traversal.TargetExposedNodeTraversingProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.assessment.Assessment;
import org.conqat.lib.commons.assessment.ETrafficLightColor;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Base class for processors creating an assessment based on local decisions
 * (i.e. without looking at other nodes).
 * 
 * @author Benjamin Hummel
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating GREEN Hash: 8F0C9B7C07AE6F872D4FB68EFA82A665
 * 
 * @param <E>
 *            the type expected by the assessment method.
 */
public abstract class LocalAssessorBase<E> extends
		TargetExposedNodeTraversingProcessorBase<IConQATNode> {

	/** {@inheritDoc} */
	@Override
	protected ETargetNodes getDefaultTargetNodes() {
		return ETargetNodes.ALL;
	}

	/** The key to read from. */
	private String readKey;

	/** The key to write the result into. */
	private String outputKey;

	/** Color used, if the key does not exist. */
	private ETrafficLightColor errorColor = ETrafficLightColor.UNKNOWN;

	/** Whether to replace an assessment or to merge it with an old one. */
	private boolean replace = false;

	/** Whether to invert the assessment (i.e. exchange red and green) */
	private boolean invertAssessment = false;

	/** Colors for which to create findings */
	private final EnumSet<ETrafficLightColor> findingColors = EnumSet
			.noneOf(ETrafficLightColor.class);

	/** Name of finding group */
	private String findingGroupName;

	/** Name of finding category */
	private String findingCategoryName;

	/** Key under which findings are stored */
	private String findingKey;

	/** Message of the findings */
	private String findingMessage;

	/** Holds the category of identified findings */
	private FindingCategory findingCategory = null;

	/** Holds the group of identified findings */
	private FindingGroup findingGroup = null;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.READKEY_NAME, minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "Set the key to read the assessed value from.")
	public void setReadKey(
			@AConQATAttribute(name = ConQATParamDoc.READKEY_KEY_NAME, description = ConQATParamDoc.READKEY_KEY_DESC) String readKey) {
		this.readKey = readKey;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.WRITEKEY_NAME, minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "The key to write the assessment into.")
	public void setWriteKey(
			@AConQATAttribute(name = ConQATParamDoc.WRITEKEY_KEY_NAME, description = ConQATParamDoc.WRITEKEY_KEY_DESC) String outputKey) {
		this.outputKey = outputKey;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "error", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Set the assessment color returned if the read key is not present or of the wrong type."
			+ " Default is UNKNOWN.")
	public void setErrorColor(
			@AConQATAttribute(name = "color", description = "traffic light color") ETrafficLightColor errorColor) {
		this.errorColor = errorColor;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "replace", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "This controls what happens if the key already holds an assessment. "
			+ "Setting it true will replace the old one, while false will merge both."
			+ "Default is false (i.e. merge).")
	public void setReplace(
			@AConQATAttribute(name = "value", description = "true (replace) or false (merge)") boolean replace) {
		this.replace = replace;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "invert", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "If this is set to true, the assessments added will be inverted, which basically results in the "
			+ "exchange of red and green. This sometimes simplifies the definition of an assessment."
			+ "Default is false.")
	public void setInvertAssessment(
			@AConQATAttribute(name = "value", description = "true (invert) or false") boolean invertAssessment) {
		this.invertAssessment = invertAssessment;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "finding-for", minOccurrences = 0, maxOccurrences = -1, description = ""
			+ "Color for which a finding will be created.")
	public void addFindingColor(
			@AConQATAttribute(name = "color", description = "traffic light color, multiple colors may be used") ETrafficLightColor findingColor) {
		this.findingColors.add(findingColor);
	}

	/**
	 * {@ConQAT.Doc}
	 * 
	 * @throws ConQATException
	 *             if any of the attributes is <code>null</code>
	 */
	@AConQATParameter(name = FINDING_PARAM_NAME, minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Attributes of the findings which are created according to the finding-for parameter. Requires that all attributes are given.")
	public void setFinding(
			@AConQATAttribute(name = WRITEKEY_KEY_NAME, description = WRITEKEY_KEY_DESC) String findingKey,
			@AConQATAttribute(name = FINDING_GROUP_NAME, description = "name identifier for the finding group") String findingGroupName,
			@AConQATAttribute(name = "category", description = "name identifier for the finding category") String findingCategoryName,
			@AConQATAttribute(name = "message", description = "finding message text") String findingMessage)
			throws ConQATException {

		checkFindingParamNotEmpty(findingKey, WRITEKEY_KEY_NAME);
		checkFindingParamNotEmpty(findingGroupName, FINDING_GROUP_NAME);
		checkFindingParamNotEmpty(findingCategoryName, FINDING_CATEGORY_NAME);
		checkFindingParamNotEmpty(findingMessage, FINDING_MESSAGE_NAME);

		this.findingGroupName = findingGroupName;
		this.findingCategoryName = findingCategoryName;
		this.findingMessage = findingMessage;
		this.findingKey = findingKey;
	}

	/** Checks that the parameter is not empty. Otherwise raises an exception */
	private void checkFindingParamNotEmpty(String parameter, String name)
			throws ConQATException {
		if (StringUtils.isEmpty(parameter)) {
			throw new ConQATException("Parameter " + FINDING_PARAM_NAME + "."
					+ name + " must not be empty");
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void setUp(IConQATNode root) throws ConQATException {
		super.setUp(root);
		NodeUtils.addToDisplayList(root, outputKey);

		checkValidFindingConfiguration();

		if (isConfiguredForFindings()) {
			findingCategory = NodeUtils.getFindingReport(root)
					.getOrCreateCategory(findingCategoryName);
			findingGroup = FindingUtils.getOrCreateFindingGroupAndSetRuleId(
					findingCategory, findingGroupName, findingGroupName);
		}
	}

	/**
	 * Checks if the creation of finding is required by the configuration.
	 * 
	 * @return <code>true</code> if configured for finding creation,
	 *         <code>false</code> otherwise.
	 */
	private boolean isConfiguredForFindings() {
		if (findingKey == null) {
			return false;
		}
		return true;
	}

	/**
	 * Checks if finding configuration valid. That is all or none of the
	 * required parameters is set.
	 * 
	 * @throws ConQATException
	 *             if configuration is invalid.
	 */
	private void checkValidFindingConfiguration() throws ConQATException {
		// if findingKey is set all other attributes of finding parameter must
		// be set, see setFinding(..). Thus only check for findingKey and
		// findingColors.
		if (findingKey == null && !findingColors.isEmpty()) {
			throw new ConQATException(
					"Missconfigured finding parameters: no findings key specified");
		}
		if (findingKey != null && findingColors.isEmpty()) {
			throw new ConQATException(
					"Missconfigured finding parameters: no color for finding creation specified");
		}
	}

	/** {@inheritDoc} */
	@Override
	public void visit(IConQATNode node) {
		Assessment assessment = null;

		try {
			Object o = node.getValue(readKey);
			if (o != null) {
				@SuppressWarnings("unchecked")
				E value = (E) o;
				assessment = assessValue(value);
				if (invertAssessment && assessment != null) {
					assessment = doInvert(assessment);
				}
				if (shouldCreateFinding(assessment)) {
					createFinding(assessment, node);
				}
			}
		} catch (ClassCastException e) {
			// warn here and use the error color instead below
			getLogger().warn(e);
		}

		if (assessment == null) {
			assessment = new Assessment(errorColor);
		}

		if (replace) {
			node.setValue(outputKey, assessment);
		} else {
			NodeUtils.getOrCreateAssessment(node, outputKey).add(assessment);
		}
	}

	/**
	 * Checks if a {@link Finding} should be created for the given
	 * {@link Assessment}.
	 * 
	 */
	private boolean shouldCreateFinding(Assessment assessment) {
		if (isConfiguredForFindings()) {
			return findingColors.contains(assessment.getDominantColor());
		}
		return false;
	}

	/**
	 * Creates a new {@link Finding} for the given {@link Assessment} at the
	 * given node.
	 */
	private void createFinding(Assessment assessment, IConQATNode node) {
		ElementLocation location = new ElementLocation(node.getId(),
				node.getId());
		String message = getFindingMessage(assessment);
		Finding finding = FindingUtils.createAndAttachFinding(findingGroup,
				message, node, location, findingKey);
		finding.setValue(EFindingKeys.ASSESSMENT.toString(),
				assessment.getDominantColor());
	}

	/**
	 * Gets the finding message for the given {@link Assessment}. Sub-classes of
	 * {@link LocalAssessorBase} may overwrite this method for specific message.
	 */
	protected String getFindingMessage(
			@SuppressWarnings("unused") Assessment assessment) {
		return findingMessage;
	}

	/** Inverts the given assessment by exchanging the value for RED and GREEN. */
	private Assessment doInvert(Assessment oldAssessment) {
		Assessment assessment = new Assessment();
		for (ETrafficLightColor target : ETrafficLightColor.values()) {
			ETrafficLightColor source = target;
			if (target == ETrafficLightColor.GREEN) {
				source = ETrafficLightColor.RED;
			}
			if (target == ETrafficLightColor.RED) {
				source = ETrafficLightColor.GREEN;
			}
			int frequency = oldAssessment.getColorFrequency(source);
			if (frequency > 0) {
				assessment.add(target, frequency);
			}
		}
		return assessment;
	}

	/**
	 * Calculates the assessment for a given value. In case of an error
	 * <code>null</code> should be returned.
	 * 
	 * @param value
	 *            the value to be assessed.
	 */
	protected abstract Assessment assessValue(E value);
}