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
package org.conqat.engine.code_clones.normalization.provider;

import java.io.Serializable;

import org.conqat.engine.code_clones.core.CloneDetectionException;
import org.conqat.engine.code_clones.core.Unit;
import org.conqat.engine.resource.text.ITextResource;

/**
 * General interface for components that provide units.
 * 
 * @author $Author: juergens $
 * @version $Revision: 34670 $
 * @ConQAT.Rating GREEN Hash: 2249ED6E9894912799B14FA4761D4652
 */
public interface IUnitProvider<E extends ITextResource, Data extends Unit>
		extends IProvider<E, Data, CloneDetectionException>, Serializable {
	// nothing to do
}