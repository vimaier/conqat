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
package org.conqat.engine.sourcecode.analysis;

import org.conqat.engine.resource.regions.RegionMarkerStrategyBase;
import org.conqat.engine.sourcecode.resource.ITokenElement;

/**
 * This class sets type parameters to avoid generic as processor input or output
 * types.
 * 
 * @author juergens
 * @author $Author: hummelb $
 * @version $Rev: 36299 $
 * @ConQAT.Rating GREEN Hash: 80F3F00DABB95CB9E21BC5F813001E0B
 */
public abstract class SourceCodeElementRegionMarkerStrategyBase extends
		RegionMarkerStrategyBase<ITokenElement> {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	// This method cannot be pulled up into the base class since we need
	// FileSystemElementRegionMarkerStrategyBase as return type.
	/** {@inheritDoc} */
	@Override
	public SourceCodeElementRegionMarkerStrategyBase process() {
		return this;
	}
}