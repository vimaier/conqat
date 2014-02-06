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
package org.conqat.engine.code_clones.normalization;

import java.io.Serializable;

import org.conqat.engine.code_clones.core.CloneDetectionException;
import org.conqat.engine.code_clones.core.Unit;
import org.conqat.engine.code_clones.normalization.provider.IUnitProvider;
import org.conqat.engine.code_clones.normalization.provider.ProviderBase;
import org.conqat.engine.resource.text.ITextResource;

/**
 * Base class for {@link Unit} providers.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 36299 $
 * @ConQAT.Rating GREEN Hash: 331F3614120401117B6D3B2D0773EE6A
 */
public abstract class UnitProviderBase<Element extends ITextResource, Data extends Unit>
		extends ProviderBase<Element, Data, CloneDetectionException> implements
		IUnitProvider<Element, Data>, Serializable {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;
	// All functionality present in base class.
}