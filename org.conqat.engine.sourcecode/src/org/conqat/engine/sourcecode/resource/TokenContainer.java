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
package org.conqat.engine.sourcecode.resource;

import org.conqat.engine.resource.base.ContainerBase;
import org.conqat.lib.commons.clone.DeepCloneException;

/**
 * Container for {@link ITokenResource}s.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: A83FFFC3D3499881F017A0F758C030D2
 */
public class TokenContainer extends ContainerBase<ITokenResource> implements
		ITokenResource {

	/** Constructor. */
	public TokenContainer(String name) {
		super(name);
	}

	/** Copy constructor. */
	public TokenContainer(ContainerBase<ITokenResource> other)
			throws DeepCloneException {
		super(other);
	}

	/** {@inheritDoc} */
	@Override
	protected ITokenResource[] allocateArray(int size) {
		return new ITokenResource[size];
	}

	/** {@inheritDoc} */
	@Override
	public TokenContainer deepClone() throws DeepCloneException {
		return new TokenContainer(this);
	}
}