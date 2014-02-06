/*--------------------------------------------------------------------------+
$Id: SyntheticTargetLinkBlockFilter.java 44603 2013-04-22 14:42:26Z deissenb $
|                                                                          |
| Copyright 2005-2010 by the ConQAT Project                                |
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
+--------------------------------------------------------------------------*/
package org.conqat.engine.simulink.targetlink;

import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.simulink.analyzers.SimulinkModelAnalyzerBase;
import org.conqat.engine.simulink.scope.ISimulinkElement;
import org.conqat.lib.simulink.builder.SimulinkModelBuildingException;
import org.conqat.lib.simulink.targetlink.TargetLinkUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: deissenb $
 * @version $Rev: 44603 $
 * @ConQAT.Rating GREEN Hash: 21B8119301D2E141A7C8952C80E34EB4
 */
@AConQATProcessor(description = "This processor filters the synthetic blocks from "
		+ "TargetLink models.")
public class SyntheticTargetLinkBlockFilter extends SimulinkModelAnalyzerBase {

	/** {@inheritDoc} */
	@Override
	protected void analyzeModel(ISimulinkElement element)
			throws ConQATException {
		try {
			TargetLinkUtils.filterSyntheticBlocks(element.getModel());
		} catch (SimulinkModelBuildingException e) {
			throw new ConQATException(e);
		}
	}

}
