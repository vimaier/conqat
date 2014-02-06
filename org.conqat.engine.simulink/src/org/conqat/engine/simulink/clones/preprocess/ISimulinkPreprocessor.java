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
package org.conqat.engine.simulink.clones.preprocess;

import java.util.Set;

import org.conqat.lib.commons.clone.IDeepCloneable;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.model_clones.detection.util.ICloneReporter;
import org.conqat.engine.simulink.clones.normalize.ISimulinkNormalizer;
import org.conqat.engine.simulink.scope.ISimulinkResource;
import org.conqat.lib.simulink.model.SimulinkBlock;

/**
 * Interface of strategies which may preprocess a Simulink model prior to clone
 * detection. The preprocessing phase may also report clones.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: 654D67BB95005FC0D2A482F08FED6489
 */
public interface ISimulinkPreprocessor extends IDeepCloneable {

	/**
	 * Performs preprocessing.
	 * 
	 * @param model
	 *            the model to preprocess. The model may be modified as a
	 *            result, however instead of deletions, it is better to add
	 *            blocks to the ignore list.
	 * @param normalizer
	 *            the normalization applied.
	 * @param ignoredBlocks
	 *            the set of blocks which should be ignored during processing
	 *            the file. This is the preferred way to exclude blocks, as the
	 *            model will still be intact. This set serves as both input and
	 *            output.
	 * @param reporter
	 *            a reporter through which any clones found may be reported.
	 */
	void preprocess(ISimulinkResource model, ISimulinkNormalizer normalizer,
			Set<SimulinkBlock> ignoredBlocks, ICloneReporter reporter)
			throws ConQATException;
}