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
package org.conqat.engine.resource.regions;

import org.conqat.engine.resource.text.ITextElement;

/**
 * This class sets type parameters to avoid generic as processor input or output
 * types.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 36299 $
 * @ConQAT.Rating GREEN Hash: 841140D809310BA0AC51E36E185ADBC4
 */
public abstract class TextElementRegionMarkerStrategyBase extends
		RegionMarkerStrategyBase<ITextElement> {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	// This method cannot be pulled up into the base class since we need
	// TextElementRegionMarkerStrategyBase as return type.
	/** {@inheritDoc} */
	@Override
	public TextElementRegionMarkerStrategyBase process() {
		return this;
	}
}