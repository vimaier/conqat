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
package org.conqat.engine.model_clones.detection;

import org.conqat.lib.commons.clone.IDeepCloneable;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;

/**
 * Base class for model clone detection algorithm factories.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 36615 $
 * @ConQAT.Rating GREEN Hash: B54670B02CAE87AF931DC8830604CB89
 */
public abstract class ModelCloneDetectorFactoryBase extends ConQATProcessorBase
		implements IModelCloneDetector, IDeepCloneable {

	/** The minimum size a clone should have to be reported. */
	protected int minSize;

	/** The minimum weight a clone should have to be reported. */
	protected int minWeight;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "min", minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "Sets size constraints for the clones found.")
	public void setMinSize(
			@AConQATAttribute(name = "size", description = "The minimum size of a clone (number of nodes).") int minSize,
			@AConQATAttribute(name = "weight", description = ""
					+ "The minimum weight of a clone (often it is sensible to use the same value as for the size).") int minWeight) {
		this.minSize = minSize;
		this.minWeight = minWeight;
	}

	/** {@inheritDoc} */
	@Override
	public IModelCloneDetector process() {
		return this;
	}

	/**
	 * Returns this, as there is no interesting state to clone, since this class
	 * is not modifiable
	 */
	@Override
	public IDeepCloneable deepClone() {
		return this;
	}
}