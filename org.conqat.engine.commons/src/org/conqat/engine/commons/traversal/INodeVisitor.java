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

import org.conqat.lib.commons.error.NeverThrownRuntimeException;
import org.conqat.engine.commons.node.IConQATNode;

/**
 * Visitor for ConQAT nodes. This is typically used in conjunction with
 * {@link org.conqat.engine.commons.traversal.TraversalUtils}.
 * 
 * @param <E>
 *            the type of node this visitor visits.
 * @param <X>
 *            the exception type this visitor might throw. If this visitor does
 *            not throw any exceptions, use {@link NeverThrownRuntimeException}.
 * 
 * @author Florian Deissenboeck
 * @author Benjamin Hummel
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: 08134240900F0146D22318D18A893854
 */
public interface INodeVisitor<E extends IConQATNode, X extends Exception> {

	/**
	 * Called, whenever a node is visited.
	 * 
	 * @param node
	 *            the node visited.
	 * @throws X
	 *             thrown to signal an exception
	 */
	void visit(E node) throws X;

}