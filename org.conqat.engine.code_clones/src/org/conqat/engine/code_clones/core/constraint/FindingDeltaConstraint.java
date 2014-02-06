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
package org.conqat.engine.code_clones.core.constraint;

import java.util.HashSet;
import java.util.Set;

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.findings.EFindingDeltaState;
import org.conqat.engine.commons.findings.EFindingKeys;
import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.findings.FindingReport;
import org.conqat.engine.commons.findings.util.FindingUtils;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: deissenb $
 * @version $Rev: 42085 $
 * @ConQAT.Rating GREEN Hash: E6C64A98C9A8C232FF3C4CA8460E332D
 */
@AConQATProcessor(description = "Filters clones based on finding tracking delta states. Only works, "
		+ "if the finding report was created from a clone report. Does not work for findings reports read from disk.")
public class FindingDeltaConstraint extends ConstraintBase {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.FINDING_PARAM_NAME, attribute = "report", description = "Finding report that contains delta information")
	public FindingReport deltaFindings;

	/** Set of ids of those clone classes that are kept */
	private final Set<Long> keepCloneClasses = new HashSet<Long>();

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "finding-state", attribute = "keep", description = "All clones with this stored finding delta state are kept")
	public EFindingDeltaState state;

	/** {@inheritDoc} */
	@Override
	protected void setup() {
		for (Finding finding : FindingUtils.getAllFindings(deltaFindings)) {
			Clone clone = NodeUtils.getValue(finding,
					EFindingKeys.REFERENCE.name(), Clone.class, null);
			if (clone != null && EFindingDeltaState.isInState(finding, state)) {
				keepCloneClasses.add(clone.getCloneClass().getId());
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean satisfied(CloneClass cloneClass) {
		return keepCloneClasses.contains(cloneClass.getId());
	}

}
