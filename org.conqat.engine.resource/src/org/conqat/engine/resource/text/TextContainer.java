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
package org.conqat.engine.resource.text;

import org.conqat.engine.resource.base.ContainerBase;
import org.conqat.lib.commons.clone.DeepCloneException;

/**
 * Container for {@link ITextResource}s.
 * 
 * @author deissenb
 * @author $Author: juergens $
 * @version $Rev: 35198 $
 * @ConQAT.Rating GREEN Hash: E7D0BA90C322BCDD575F74C9EFDC44D2
 */
public class TextContainer extends ContainerBase<ITextResource> implements
		ITextResource {

	/** Constructor. */
	public TextContainer(String name) {
		super(name);
	}

	/** Copy constructor. */
	protected TextContainer(ContainerBase<ITextResource> other)
			throws DeepCloneException {
		super(other);
	}

	/** {@inheritDoc} */
	@Override
	protected ITextResource[] allocateArray(int size) {
		return new ITextResource[size];
	}

	/** {@inheritDoc} */
	@Override
	public TextContainer deepClone() throws DeepCloneException {
		return new TextContainer(this);
	}
}