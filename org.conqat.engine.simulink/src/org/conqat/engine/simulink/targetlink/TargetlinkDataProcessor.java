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
package org.conqat.engine.simulink.targetlink;

import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.simulink.analyzers.SimulinkBlockTraversingProcessorBase;
import org.conqat.engine.simulink.scope.ISimulinkElement;
import org.conqat.lib.simulink.builder.SimulinkModelBuildingException;
import org.conqat.lib.simulink.model.SimulinkBlock;
import org.conqat.lib.simulink.targetlink.TargetLinkDataResolver;

/**
 * Resolves the TargetLink parameters of all blocks in the Simulink node tree,
 * by introducing additional parameters.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @levd.rating GREEN Hash: 3E9AFAE9680234C73C95C93E58141260
 */
@AConQATProcessor(description = "Resolves the TargetLink parameters of all "
		+ "blocks in the Simulink node tree, by introducing additional parameters.")
public class TargetlinkDataProcessor extends SimulinkBlockTraversingProcessorBase {

	/** {@inheritDoc} */
	@Override
	public void visitBlock(SimulinkBlock block, ISimulinkElement element)
			throws ConQATException {
		try {
			new TargetLinkDataResolver().visit(block);
		} catch (SimulinkModelBuildingException e) {
			throw new ConQATException(e);
		}
	}
}