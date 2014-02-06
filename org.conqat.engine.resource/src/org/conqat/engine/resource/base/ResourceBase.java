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
package org.conqat.engine.resource.base;

import org.conqat.engine.commons.node.ConQATNodeBase;
import org.conqat.engine.resource.IContainer;
import org.conqat.engine.resource.IResource;
import org.conqat.lib.commons.clone.DeepCloneException;

/**
 * Base class for {@link ElementBase} and {@link ContainerBase}.
 * 
 * @author hummelb
 * @author $Author: juergens $
 * @version $Rev: 35198 $
 * @ConQAT.Rating GREEN Hash: BD7BDC365B9837A491EF8622CC5CDCDC
 */
public abstract class ResourceBase extends ConQATNodeBase implements IResource {

	/** The parent. */
	private IContainer parent;

	/** Constructor. */
	protected ResourceBase() {
		// nothing to do
	}

	/** Copy constructor. */
	protected ResourceBase(ResourceBase other) throws DeepCloneException {
		super(other);
	}

	/** {@inheritDoc} */
	@Override
	public final IContainer getParent() {
		return parent;
	}

	/** {@inheritDoc} */
	@Override
	public void setParent(IContainer parent) {
		this.parent = parent;
	}

	/** {@inheritDoc} */
	@Override
	public void remove() {
		if (parent != null) {
			parent.removeChild(this);
		}
	}
}