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
package org.conqat.engine.simulink.clones.normalize;

import org.conqat.lib.commons.clone.IDeepCloneable;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.simulink.model.SimulinkBlock;
import org.conqat.lib.simulink.model.SimulinkLine;

/**
 * Interface for a Simulink normalization which maps both blocks and lines to
 * plain strings. It is also responsible for determining the weight of a block.
 * 
 * @author hummelb
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: 1885C9F8DC394B1327C31F37FB65D5DE
 */
public interface ISimulinkNormalizer extends IDeepCloneable {

	/** Returns a normalized representation of the block. */
	public String normalizeBlock(SimulinkBlock block) throws ConQATException;

	/** Returns the weight for the given block. */
	public int determineWeight(SimulinkBlock block) throws ConQATException;

	/** Returns a normalized representation of the line. */
	public String normalizeLine(SimulinkLine line) throws ConQATException;
}