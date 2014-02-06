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
package org.conqat.engine.sourcecode.analysis.findings;

import java.util.List;

import org.conqat.engine.commons.findings.EFindingKeys;
import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.findings.FindingCategory;
import org.conqat.engine.commons.findings.FindingGroup;
import org.conqat.engine.commons.findings.FindingReport;
import org.conqat.engine.commons.findings.util.FindingUtils;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.util.ResourceUtils;
import org.conqat.engine.sourcecode.analysis.Block;
import org.conqat.engine.sourcecode.analysis.BlockParser.BlockParserException;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.assessment.ETrafficLightColor;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.scanner.IToken;

/**
 * Base class for processors that operate on blocks with a specified nesting
 * depth, e.g. methods, and produce findings for them.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating GREEN Hash: 23A43F1F54DB5E86A5C29FC1A9BC188B
 */
public abstract class BlockAnalyzerBase extends BlockParserBase {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Key under which findings are stored", type = "org.conqat.engine.commons.findings.FindingsList")
	public static final String BLOCK_FINDINGS = "BlockFindings";

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "key", attribute = "name", description = "If set, largest value gets stored in key", optional = true)
	public String key;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "category", attribute = "name", description = "Name of category of created findingse.")
	public String categoryName;

	/** Finding group to which all findings are added */
	private FindingGroup group;

	/** Counts findings */
	private int findingCount;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "block-type", attribute = "name", description = "Name of block type (e.g. class, method) used to create finding message.")
	public String blockType;

	/** Inclusive thresholds for yellow findings */
	protected int thresholdYellow;

	/** Inclusive thresholds for red findings */
	protected int thresholdRed;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "threshold", minOccurrences = 1, maxOccurrences = 1, description = "Set thresholds for finding creation (inclusive).")
	public void setThresholds(
			@AConQATAttribute(name = "yellow", description = "Threshold for yellow findings") int thresholdYellow,
			@AConQATAttribute(name = "red", description = "Threshold for red findings") int thresholdRed) {
		this.thresholdYellow = thresholdYellow;
		this.thresholdRed = thresholdRed;
	}

	/** {@inheritDoc} */
	@Override
	protected void setUp(ITokenResource root) throws ConQATException {
		super.setUp(root);
		FindingReport report = NodeUtils.getFindingReport(root);
		FindingCategory category = report.getOrCreateCategory(categoryName);
		group = FindingUtils.getOrCreateFindingGroupAndSetRuleId(category,
				groupName(), ruleId());
	}

	/** Template method that deriving classes override to provide the group name */
	protected abstract String groupName();

	/** Template method that deriving classes override to provide the rule id */
	protected String ruleId() {
		return groupName();
	}

	/** {@inheritDoc} */
	@Override
	protected void analyzeTokens(List<IToken> tokens, ITokenElement element)
			throws ConQATException {
		List<Block> blocks;
		try {
			blocks = parseBlocks(tokens);
		} catch (BlockParserException e) {
			getLogger().warn(
					"Ill-formed nesting encountered in "
							+ element.getLocation());
			return;
		}

		int max = -1;
		for (Block block : blocks) {
			List<IToken> blockTokens = block.getTokens();
			max = Math.max(max, analyzeBlock(blockTokens, element));
		}

		if (!StringUtils.isEmpty(key) && max > -1) {
			element.setValue(key, max);
		}
	}

	/** Template method that deriving classes override to analyze block tokens */
	protected abstract int analyzeBlock(List<IToken> blockTokens,
			ITokenElement element) throws ConQATException;

	/** Creates a finding for a block */
	protected void createFinding(ITokenElement element,
			List<IToken> blockTokens, String message,
			ETrafficLightColor assessment) throws ConQATException {
		IToken firstToken = blockTokens.get(0);
		IToken lastToken = CollectionUtils.getLast(blockTokens);

		Finding finding = ResourceUtils
				.createAndAttachFindingForFilteredRegion(group, message,
						element, firstToken.getOffset(),
						lastToken.getEndOffset(), BLOCK_FINDINGS);
		annotateFinding(finding, message, assessment);
		findingCount++;
	}

	/** Store message and assessment in finding */
	private void annotateFinding(Finding finding, String message,
			ETrafficLightColor assessment) {
		finding.setValue(EFindingKeys.MESSAGE.toString(), message);
		finding.setValue(EFindingKeys.ASSESSMENT.toString(), assessment);
	}

	/** Creates a finding for a token. */
	protected void createFinding(ITokenElement element, IToken token,
			String message, ETrafficLightColor assessment)
			throws ConQATException {
		Finding finding = ResourceUtils
				.createAndAttachFindingForFilteredRegion(group, message,
						element, token.getOffset(), token.getEndOffset(),
						BLOCK_FINDINGS);
		annotateFinding(finding, message, assessment);
		findingCount++;
	}

	/** {@inheritDoc} */
	@Override
	protected void finish(ITokenResource root) {
		getLogger().info("Created " + findingCount + " findings");
	}

	/** {@inheritDoc} */
	@Override
	protected String[] getKeys() {
		if (!StringUtils.isEmpty(key)) {
			return new String[] { BLOCK_FINDINGS, key };
		}
		return new String[] { BLOCK_FINDINGS };
	}

	/** Determine assessment of finding for a value */
	protected ETrafficLightColor assessmentFor(int value) {
		CCSMAssert.isTrue(value >= thresholdYellow,
				"Nesting lower then threshold for yellow findings");
		if (value >= thresholdRed) {
			return ETrafficLightColor.RED;
		}
		return ETrafficLightColor.YELLOW;
	}
}