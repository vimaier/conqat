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
package org.conqat.engine.simulink.output;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.commons.findings.EFindingKeys;
import org.conqat.engine.commons.findings.FindingCategory;
import org.conqat.engine.commons.findings.FindingGroup;
import org.conqat.engine.commons.findings.FindingReport;
import org.conqat.engine.commons.findings.location.ModelPartLocation;
import org.conqat.engine.commons.findings.typespec.EFindingTypeSpecType;
import org.conqat.engine.commons.findings.typespec.FindingTypeSpec;
import org.conqat.engine.commons.findings.typespec.FindingTypeSpecEntry;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.simulink.clones.result.SimulinkClone;
import org.conqat.engine.simulink.clones.result.SimulinkCloneResultNode;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.conqat.lib.simulink.model.SimulinkBlock;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating GREEN Hash: 89A06CD0A2E4185821C4D148F7FEF6A4
 */
@AConQATProcessor(description = "Processor for converting a Simulink clone result to a finding report.")
public class SimulinkClonesToFindingsConverter extends ConQATProcessorBase {

	/** The clones */
	private SimulinkCloneResultNode cloneResult;

	/** The category to add to. */
	private FindingCategory category;

	/** Counter for the clone groups. */
	private int numGroups = 0;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.INPUT_NAME, minOccurrences = 1, maxOccurrences = 1, description = "The simulink clone result.")
	public void setCloneResult(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) SimulinkCloneResultNode cloneResult) {
		this.cloneResult = cloneResult;
	}

	/** {@inheritDoc} */
	@Override
	public FindingReport process() throws ConQATException {
		FindingReport report = new FindingReport();
		category = report.getOrCreateCategory("Simulink Clones");
		for (SimulinkClone clone : cloneResult.getChildren()) {
			convertClone(clone);
		}
		return report;
	}

	/** Converts a clone to a finding group. */
	private void convertClone(SimulinkClone clone) throws ConQATException {
		FindingGroup group = category.createFindingGroup("Clone Group "
				+ ++numGroups);

		for (UnmodifiableList<SimulinkBlock> blocks : clone.getBlockLists()) {
			if (blocks.isEmpty()) {
				continue;
			}

			String uniformPath = blocks.get(0).getModel().getOriginId();
			if (uniformPath == null) {
				throw new ConQATException("Model doesn't have a origin id: "
						+ blocks.get(0));
			}

			ModelPartLocation location = new ModelPartLocation(uniformPath,
					uniformPath);

			for (SimulinkBlock block : blocks) {
				String id = block.getId();
				if (!uniformPath.equals(block.getModel().getOriginId())) {
					throw new ConQATException(
							"Blocks referenced by a single clone instance should be in the same model file!");
				}
				location.addElementId(id);
			}

			group.createFinding(location);
		}

		copyValues(clone, group);
	}

	/**
	 * Copies all numeric values from the clone to to the findings group.
	 */
	private void copyValues(SimulinkClone clone, FindingGroup group) {
		FindingTypeSpec format = new FindingTypeSpec();
		// just copy everything looking numeric
		for (String key : NodeUtils.getDisplayList(cloneResult)) {
			Object value = clone.getValue(key);
			if (value instanceof Number) {
				group.setValue(key, value);
				format.addEntry(new FindingTypeSpecEntry(key,
						EFindingTypeSpecType.DOUBLE));
			}
		}

		group.setValue(EFindingKeys.TYPESPEC.name(), format);
	}
}