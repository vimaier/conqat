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

import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.commons.traversal.INodeVisitor;
import org.conqat.engine.commons.traversal.NodeTraversingProcessorBase;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.IResource;

/**
 * Base class for processors which work by traversing the tree of resources
 * provided (using DFS) and possibly changing the values attached to these
 * nodes. This processor also deals with progress.
 * 
 * @param <R>
 *            the type of resources to be traversed.
 * 
 * @param <E>
 *            the type of element this works on. The element class should
 *            implement R and must match with the class returned from
 *            {@link ElementTraversingProcessorBase#getElementClass()}
 * 
 * 
 * @author $Author: juergens $
 * @version $Rev: 35198 $
 * @ConQAT.Rating GREEN Hash: 2B23D9027FC8A059F01438F9ADE702A3
 */
public abstract class ElementTraversingProcessorBase<R extends IResource, E extends IElement>
		extends NodeTraversingProcessorBase<R> implements
		INodeVisitor<R, ConQATException> {

	/** {@inheritDoc} */
	@Override
	protected ETargetNodes getTargetNodes() {
		return ETargetNodes.ALL;
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public void visit(R node) throws ConQATException {
		if (getElementClass().isAssignableFrom(node.getClass())) {
			processElement((E) node);
		}
	}

	/** Template method. Returns class for parameter E. */
	@SuppressWarnings("unchecked")
	protected Class<E> getElementClass() {
		return (Class<E>) IElement.class;
	}

	/** Template method that deriving classes override to process element */
	protected abstract void processElement(E element) throws ConQATException;

}