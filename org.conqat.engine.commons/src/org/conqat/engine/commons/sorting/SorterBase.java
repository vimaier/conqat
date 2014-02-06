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
package org.conqat.engine.commons.sorting;

import java.util.Comparator;

import org.conqat.lib.commons.collections.InvertingComparator;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeConstants;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.commons.traversal.NodeTraversingProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;

/**
 * Base class for sorting processors. These are based on setting the comparator
 * for all inner nodes of a ConQATNode tree. The actual sorting happens in the
 * presentation.
 * 
 * @author Florian Deissenboeck
 * @author Benjamin Hummel
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: 1ADCDCCE237110F3E74AB4129C39F29A
 */
public abstract class SorterBase extends
		NodeTraversingProcessorBase<IConQATNode> {

	/** Whether to sort ascending or descending. */
	private boolean descending = false;

	/** {@inheritDoc} */
	@Override
	protected ETargetNodes getTargetNodes() {
		return ETargetNodes.INNER;
	}

	/** Set whether to sort ascending or descending. */
	@AConQATParameter(name = "descending", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "If this is set to true, the sorting is descending. Default is ascending (false).")
	public void setDescending(
			@AConQATAttribute(name = "value", description = "true or false")
			boolean descending) {
		this.descending = descending;
	}

	/** Set the comparator (only called for inner nodes). */
	@Override
	public void visit(IConQATNode node) throws ConQATException {
		Comparator<IConQATNode> comp = getComparator(node);
		if (descending) {
			comp = new InvertingComparator<IConQATNode>(comp);
		}
		node.setValue(NodeConstants.COMPARATOR, comp);
	}

	/**
	 * Returns the comparator to be used for the given node. This is called
	 * exactly once for each node. The comparator should sort nodes in ascending
	 * order, as descending ordering is handled transparently in this class
	 * using an enclosing comparator.
	 */
	protected abstract Comparator<IConQATNode> getComparator(IConQATNode node)
			throws ConQATException;

}