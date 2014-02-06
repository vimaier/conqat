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
package org.conqat.lib.simulink.targetlink;

import static org.conqat.lib.simulink.model.SimulinkConstants.NAME_Subsystem;
import static org.conqat.lib.simulink.model.SimulinkConstants.PARAM_BackgroundColor;
import static org.conqat.lib.simulink.model.SimulinkConstants.PARAM_Position;

import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.error.NeverThrownRuntimeException;
import org.conqat.lib.commons.visitor.IVisitor;
import org.conqat.lib.simulink.builder.SimulinkModelBuildingException;
import org.conqat.lib.simulink.model.SimulinkBlock;
import org.conqat.lib.simulink.model.SimulinkModel;
import org.conqat.lib.simulink.util.SimulinkUtils;

/**
 * Provides utility functions for target link models
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: AE4F15270EDD166E34235428CB5678D1
 */
public class TargetLinkUtils {

	/**
	 * Filter out the automatic generated blocks from TargetLink
	 * 
	 * @throws SimulinkModelBuildingException
	 *             if a synthetic block was found but could not be replaced.
	 * 
	 */
	public static void filterSyntheticBlocks(SimulinkModel model)
			throws SimulinkModelBuildingException {

		SimulinkBlock simFrameBlock = obtainSimFrameBlock(model);

		if (simFrameBlock == null) {
			return;
		}

		String name = simFrameBlock.getName();

		SimulinkBlock subsystem = simFrameBlock.getSubBlock(NAME_Subsystem);
		CCSMAssert.isNotNull(subsystem, String.format(
				"The SimFrame Block has no sub-block with name \"%s\"",
				NAME_Subsystem));

		SimulinkBlock block = subsystem.getSubBlock(name);
		CCSMAssert.isNotNull(block, String.format(
				"The SimFrame/Subsystem block has no sub-block with name %s",
				name));

		simFrameBlock.replace(block, PARAM_Position, PARAM_BackgroundColor);

	}

	/**
	 * Extracts the block with the TL_SimFrame property from the model
	 * 
	 * @param model
	 *            The model from which to extract the block
	 * @return If a suitable block is found in the model that block is returned,
	 *         otherwise {@code null}
	 */
	/* package */static SimulinkBlock obtainSimFrameBlock(SimulinkModel model) {
		SyntheticBlockFindingVisitor visitor = new SyntheticBlockFindingVisitor();
		SimulinkUtils.visitDepthFirst(model, visitor);
		return visitor.getSimFrameBlock();
	}

	/**
	 * Returns the synthetic block of a given Model.
	 */
	private static class SyntheticBlockFindingVisitor implements
			IVisitor<SimulinkBlock, NeverThrownRuntimeException> {

		/** the synthetic block */
		private SimulinkBlock simFrameBlock;

		/** {@inheritDoc} */
		@Override
		public void visit(SimulinkBlock element)
				throws NeverThrownRuntimeException {
			String maskType = element.getParameter("MaskType");
			if ("TL_SimFrame".equals(maskType)) {
				CCSMAssert
						.isTrue(simFrameBlock == null,
								"We assume that there's only one "
										+ "TL_Simframe block in a model. This assumption is violated.");
				simFrameBlock = element;
			}
		}

		/** Returns simFrameBlock. */
		public SimulinkBlock getSimFrameBlock() {
			return simFrameBlock;
		}
	}
}