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
package org.conqat.engine.commons.traversal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.ConQATException;

/**
 * Base class for processors which work by traversing the tree of ConQATNodes
 * provided (using DFS) and possibly changing the values attached to these
 * nodes. This processor also deals with progress.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 42193 $
 * @ConQAT.Rating GREEN Hash: B81918B82BFFFF267F530C02E43E0B31
 */
public abstract class NodeTraversingProcessorBase<E extends IConQATNode>
		extends ConQATPipelineProcessorBase<E> implements
		INodeVisitor<E, ConQATException> {

	/** Returns the targets for the visitor (template method). */
	protected abstract ETargetNodes getTargetNodes();

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.INCLUSION_PREDICATE_PARAM, attribute = ConQATParamDoc.INCLUSION_PREDICATE_ATTRIBUTE, optional = true, description = ConQATParamDoc.INCLUSION_PREDICATE_DESC)
	public ConQATNodePredicateBase inclusionPredicate;

	/** {@inheritDoc} */
	@Override
	protected void processInput(E root) throws ConQATException {
		setUp(root);
		List<E> nodes = TraversalUtils.listDepthFirst(root, getTargetNodes(),
				inclusionPredicate);
		setOverallWork(Math.max(1, nodes.size()));

		// we expect this (or similar) code to appear wherever multi-thread
		// processors, that do no use this base class, are implemented. However,
		// as we currently cannot tell how the multi-threading support will be
		// used in the future, we don't built any abstractions right now. The
		// first person who's about to copy and modify this code, is required to
		// implement such an abstraction. For example, we could provide a method
		// that takes a list of calleables and ProcessorInfo objects and
		// executes them.
		try {
			for (Future<ConQATException> result : getProcessorInfo()
					.getExecutorService().invokeAll(createCallables(nodes))) {
				if (result.get() != null) {
					throw result.get();
				}
			}
		} catch (InterruptedException e) {
			throw new ConQATException(
					"Encountered spurious InterruptedException: "
							+ e.getMessage());
		} catch (ExecutionException e) {
			if (e.getCause() instanceof RuntimeException) {
				throw (RuntimeException) e.getCause();
			}
			throw new RuntimeException(e.getCause());
		}

		finish(root);
	}

	/** Creates the callables for process traversal. */
	private List<Callable<ConQATException>> createCallables(List<E> nodes) {
		List<Callable<ConQATException>> tasks = new ArrayList<Callable<ConQATException>>();
		for (final E node : nodes) {
			tasks.add(new Callable<ConQATException>() {
				@Override
				public ConQATException call() {
					// workDone(1) appears on both branches to make it be called
					// after the call to visit()
					try {
						visit(node);
					} catch (ConQATException e) {
						workDone(1);
						return e;
					}
					workDone(1);
					return null;
				}
			});
		}
		return tasks;
	}

	/**
	 * This method is called before any visiting method is called, so subclasses
	 * can check values, setup data structures or perform manipulations on the
	 * root. This is an empty implementation, so subclasses do not have to
	 * implement it themselves.
	 */
	@SuppressWarnings("unused")
	protected void setUp(E root) throws ConQATException {
		// nothing to do here
	}

	/**
	 * This method is called after visiting has been completed, so subclasses
	 * can perform final manipulations on the root. This is an empty
	 * implementation, so subclasses do not have to implement it themselves.
	 */
	@SuppressWarnings("unused")
	protected void finish(E root) throws ConQATException {
		// nothing to do here
	}
}