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
package org.conqat.engine.dotnet.coverage;

import org.conqat.engine.commons.node.ListNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.util.ConQATInputProcessorBase;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.engine.resource.util.ResourceTraversalUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43133 $
 * @ConQAT.Rating GREEN Hash: 9811FCD9AFA9DACA4090937F8C954163
 */
@AConQATProcessor(description = "Processor that imports coverage xml reports")
public class CoverageReportReader extends
		ConQATInputProcessorBase<ITextResource> {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Number of covered lines", type = "java.lang.String")
	public static final String LINES_COVERED = ECoverageXmlElement.LinesCovered
			.name();

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Number of partially covered lines", type = "java.lang.String")
	public static final String LINES_PARTIALLY_COVERED = ECoverageXmlElement.LinesPartiallyCovered
			.name();

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Number of lines not covered", type = "java.lang.String")
	public static final String LINES_NOT_COVERED = ECoverageXmlElement.LinesNotCovered
			.name();

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Number of covered blocks", type = "java.lang.String")
	public static final String BLOCKS_COVERED = ECoverageXmlElement.BlocksCovered
			.name();

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Number of blocks not covered", type = "java.lang.String")
	public static final String BLOCKS_NOT_COVERED = ECoverageXmlElement.BlocksNotCovered
			.name();

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "method-level", attribute = "analyze", optional = true, description = "Whether to read "
			+ "method-level coverage information.")
	public boolean methodLevel = false;

	/** {@inheritDoc} */
	@Override
	public ListNode process() throws ConQATException {
		ListNode root = new ListNode();
		NodeUtils.addToDisplayList(root, LINES_COVERED,
				LINES_PARTIALLY_COVERED, LINES_NOT_COVERED, BLOCKS_COVERED,
				BLOCKS_NOT_COVERED);

		for (ITextElement coverageReport : ResourceTraversalUtils
				.listTextElements(input)) {
			CoverageXmlReader reader = new CoverageXmlReader(coverageReport,
					root, methodLevel);
			reader.parse();
			getLogger().debug(
					"Parsed coverage report: " + coverageReport.getLocation());
		}

		return root;
	}

}