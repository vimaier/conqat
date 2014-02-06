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
package org.conqat.engine.commons.collections;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.commons.traversal.TargetExposedNodeTraversingProcessorBase;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 41376 $
 * @ConQAT.Rating GREEN Hash: FD0885E179D40075864F74F117C099CF
 */
@AConQATProcessor(description = "This processor filters the content of collections "
		+ "stored at a key. All collection entries whose string representation matches"
		+ "one of the given patterns are filtered. You can specify, which nodes are processed. "
		+ "By default, the processor operates on leafs. "
		+ "Nodes without a value stored at key are ignored. "
		+ "If a value is stored, it must be a collection. Otherwise, an exception is thrown.")
public class CollectionValueFilter extends
		TargetExposedNodeTraversingProcessorBase<IConQATNode> {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "key", attribute = "name", description = "Name of key that contains the collection.")
	public String key;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "filter", attribute = "patterns", description = "All collection entries whose string representation matches one of the patterns are filtered.")
	public PatternList filterPatterns;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.INVERT_NAME, attribute = ConQATParamDoc.INVERT_VALUE_NAME, optional = true, description = ConQATParamDoc.INVERT_PARAM_DOC)
	public boolean invert = false;

	/** {@inheritDoc} */
	@Override
	protected ETargetNodes getDefaultTargetNodes() {
		return ETargetNodes.LEAVES;
	}

	/** {@inheritDoc} */
	@Override
	public void visit(IConQATNode node) throws ConQATException {
		// we ignore, if no collection is stored
		Collection<?> collection = NodeUtils.getAndCheckCollection(node, key);
		if (collection == null) {
			return;
		}

		Set<Object> itemsToRemove = new HashSet<Object>();
		for (Object item : collection) {
			String s = item.toString();
			boolean remove = filterPatterns.matchesAny(s);
			if (invert) {
				remove = !remove;
			}

			if (remove) {
				itemsToRemove.add(item);
			}
		}

		collection.removeAll(itemsToRemove);
	}

}