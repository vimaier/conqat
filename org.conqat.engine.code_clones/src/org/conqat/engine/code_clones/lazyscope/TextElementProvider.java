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
package org.conqat.engine.code_clones.lazyscope;

import java.util.List;

import org.conqat.engine.resource.regions.RegionMarkerStrategyBase;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;

/**
 * Provider for {@link ITextResource}s.
 * <p>
 * Remark on implementation: this class only sets the generic parameter its base
 * class to {@link ITextResource}. This way, the ConQAT load time type checking
 * mechanism does not have to deal with generic types (which it cannot).
 * 
 * @author $Author: hummelb $
 * @version $Rev: 36296 $
 * @ConQAT.Rating GREEN Hash: 4848D495776CF7CEF423D6AD3D68F477
 */
public class TextElementProvider extends
		ElementProviderBase<ITextResource, ITextElement> {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** Constructor. */
	public TextElementProvider() {
		// nothing to do
	}

	/** Constructor. */
	public TextElementProvider(
			List<RegionMarkerStrategyBase<ITextElement>> strategies) {
		super(strategies);
	}

	/** {@inheritDoc} */
	@Override
	protected Class<ITextElement> getElementClass() {
		return ITextElement.class;
	}
}