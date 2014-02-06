/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
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
package org.conqat.engine.commons.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.node.IRemovableConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.commons.collections.IdentityHashSet;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 41146 $
<<<<<<< .mine
 * @ConQAT.Rating GREEN Hash: 7813A537F811CCE35BDA565ACEBB7EC8
=======
 * @ConQAT.Rating RED Hash: 3FE24219AC9B3C1309878269B6A50223
>>>>>>> .r41144
 */
@AConQATProcessor(description = "This filter keeps the leaves with the "
		+ "highest numerical values stored at one or more keys. The filter "
		+ "first selects the n leaves with the highest value for each "
		+ "individual key and then merges the result. As some nodes may be in "
		+ "the top nodes regarding different keys, the size of the result set "
		+ "is not known in advance. Its maximum size is keys * n, its minimum "
		+ "size is n (or less if there are less than n nodes in total).")
public class MultiTopFilter extends FilterBase<IRemovableConQATNode> {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "top", attribute = "n", optional = true, description = "The number of nodes retaiend for each ondividual key (default is 10).")
	public int n = 10;

	/** The list of keys used for comparison. */
	private List<String> keys = new ArrayList<String>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.READKEY_NAME, minOccurrences = 1, description = ConQATParamDoc.READKEY_DESC)
	public void addKey(
			@AConQATAttribute(name = ConQATParamDoc.READKEY_KEY_NAME, description = ConQATParamDoc.READKEY_KEY_DESC) String key) {
		keys.add(key);
	}

	/** Nodes which are kept (not filtered). */
	private Set<IRemovableConQATNode> retainedNodes =
	        new IdentityHashSet<IRemovableConQATNode>();
	
	/** {@inheritDoc} */
	@Override
	protected void preProcessInput(IRemovableConQATNode input) {
	    List<IRemovableConQATNode> nodes =
                TraversalUtils.listLeavesDepthFirst(input);
		for (String key : keys) {
			retainedNodes.addAll(getTopNodesForKey(nodes, key));
		}
	}

	/**
	 * Retrieves the top n nodes with the highest numerical value stored for the
	 * given key.
	 */
	private List<IRemovableConQATNode> getTopNodesForKey(
	        List<IRemovableConQATNode> nodes, final String key) {

		// Sort nodes according to the values stored for the given key.
		Collections.sort(nodes, new Comparator<IRemovableConQATNode>() {
			@Override
			public int compare(IRemovableConQATNode a, IRemovableConQATNode b) {
                return Double.compare(
                        NodeUtils.getDoubleValue(b, key, 0.0),
                        NodeUtils.getDoubleValue(a, key, 0.0));
			}
		});
		
		return nodes.subList(0, Math.min(n, nodes.size()));
	}

    /** {@inheritDoc} */
    @Override
    protected boolean isFiltered(IRemovableConQATNode node) {
        return !retainedNodes.contains(node);
    }
}
