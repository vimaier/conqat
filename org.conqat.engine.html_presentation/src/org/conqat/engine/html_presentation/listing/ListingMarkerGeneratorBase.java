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
package org.conqat.engine.html_presentation.listing;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.clone.IDeepCloneable;

/**
 * Base class for marker generators.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 36282 $
 * @ConQAT.Rating GREEN Hash: 98A78E412017724C68836AB75474E0E0
 */
public abstract class ListingMarkerGeneratorBase extends ConQATProcessorBase
		implements IDeepCloneable, IListingMarkerGenerator {

	/** {@inheritDoc} */
	@Override
	public IListingMarkerGenerator process() throws ConQATException {
		setup();
		return this;
	}

	/** Template method for performing setup work in {@link #process()}. */
	@SuppressWarnings("unused")
	protected void setup() throws ConQATException {
		// empty default implementation
	}

	/** {@inheritDoc} */
	@Override
	public IDeepCloneable deepClone() {
		return this;
	}
}
