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
package org.conqat.engine.graph.nodes;

import org.conqat.engine.commons.node.IRemovableConQATNode;

/**
 * This is the most general interface for ConQATNodes in the hierarchy of the
 * ConQAT graph. A {@link IConQATGraphNode} is either a
 * {@link IConQATGraphInnerNode} or an {@link IConQATGraphVertex}.
 * <p>
 * This does not introduce new methods but only makes some return values more
 * concrete.
 * 
 * @author Benjamin Hummel
 * @author $Author: deissenb $
 * @version $Rev: 35147 $
 * @ConQAT.Rating GREEN Hash: 66DBC59527EC03615075BBD7D2DCF5C4
 */
public interface IConQATGraphNode extends IRemovableConQATNode {

	/** {@inheritDoc} */
	@Override
	public IConQATGraphNode[] getChildren();

	/** {@inheritDoc} */
	@Override
	public IConQATGraphInnerNode getParent();
}