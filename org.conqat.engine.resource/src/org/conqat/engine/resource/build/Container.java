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

import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.base.ContainerBase;
import org.conqat.lib.commons.clone.DeepCloneException;

/**
 * Default container implementation.
 * 
 * @author deissenb
 * @author $Author: juergens $
 * @version $Rev: 35198 $
 * @ConQAT.Rating GREEN Hash: 729B0D6A49566F2DB1588C9FB6C2F296
 */
public class Container extends ContainerBase<IResource> {

	/** Create container. */
	public Container(String name) {
		super(name);
	}

	/** Copy constructor. */
	public Container(ContainerBase<IResource> other) throws DeepCloneException {
		super(other);
	}

	/** {@inheritDoc} */
	@Override
	protected IResource[] allocateArray(int size) {
		return new IResource[size];
	}

	/** {@inheritDoc} */
	@Override
	public Container deepClone() throws DeepCloneException {
		return new Container(this);
	}
}