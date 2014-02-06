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
package org.conqat.engine.resource;

import org.conqat.engine.commons.node.IRemovableConQATNode;

/**
 * This interface describes a generic resource. Resources may be manifested in a
 * file system but may also be of virtual nature. Resources are further refined
 * into {@link IContainer} (inner nodes) and {@link IElement} (usually leafs).
 * 
 * @author deissenb
 * @author $Author: juergens $
 * @version $Rev: 35198 $
 * @ConQAT.Rating GREEN Hash: 399D475DBBD7E863AA62349D1B0A3434
 */
public interface IResource extends IRemovableConQATNode {

	/** {@inheritDoc} */
	@Override
	IContainer getParent();

	/** Set parent of this resource. */
	void setParent(IContainer parent);

	/** {@inheritDoc} */
	@Override
	IResource[] getChildren();
}