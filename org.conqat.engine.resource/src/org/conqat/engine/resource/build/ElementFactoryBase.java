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
package org.conqat.engine.resource.build;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.lib.commons.clone.IDeepCloneable;

/**
 * Base class for {@link IElementFactory} implementations that are ConQAT
 * processors at the same time. Implementations should be immutable.
 * 
 * @author hummelb
 * @author $Author: juergens $
 * @version $Rev: 35198 $
 * @ConQAT.Rating GREEN Hash: 06A07F543BA027962BC55E13F90C1C8D
 */
public abstract class ElementFactoryBase extends ConQATProcessorBase implements
		IElementFactory, IDeepCloneable {

	/** {@inheritDoc} */
	@Override
	public IElementFactory process() {
		return this;
	}

	/** Returns this. */
	@Override
	public IDeepCloneable deepClone() {
		return this;
	}
}