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
package org.conqat.engine.resource.mark;

import org.conqat.engine.commons.mark.MarkerBase;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.IResource;

/**
 * Base class for processors that mark resources.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 55509EBFF7A061B7D955BF6C3AAFACFA
 */
public abstract class ResourceMarkerBase<R extends IResource, E extends IElement>
		extends MarkerBase<R> {

	/** {@inheritDoc} */
	@Override
	protected boolean skip(R node) {
		return !getElementClass().isAssignableFrom(node.getClass());
	}

	/** Template method. Returns class for parameter E. */
	@SuppressWarnings("unchecked")
	protected Class<E> getElementClass() {
		return (Class<E>) IElement.class;
	}

	/** {@inheritDoc} */
	@Override
	@SuppressWarnings("unchecked")
	protected String getNodeStringToMatch(R element)
			throws ConQATException {
		return getElementStringToMatch((E) element);
	}
	
	/** Template method that deriving classes override */
	protected abstract String getElementStringToMatch(E element)
			throws ConQATException;
	
	
	/** {@inheritDoc} */
	@Override
	@SuppressWarnings("unchecked")
	protected String logDetail(R element) {
		return ((E) element).getUniformPath();
	}

}