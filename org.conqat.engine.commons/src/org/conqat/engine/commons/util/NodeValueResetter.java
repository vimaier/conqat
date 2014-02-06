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
package org.conqat.engine.commons.util;

import java.util.HashMap;
import java.util.Map;

import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.commons.traversal.INodeVisitor;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.commons.error.NeverThrownRuntimeException;

/**
 * Resets node values in nodes that don't match a specific pattern.
 * 
 * @author juergens
 * @author $Author: deissenb $
 * @version $Rev: 36200 $
 * @ConQAT.Rating GREEN Hash: 34483E561CF8AB81FE2A4ECBB7B95DB4
 */
@AConQATProcessor(description = "Clears node values in nodes that don't match a specific pattern")
public class NodeValueResetter extends ConQATPipelineProcessorBase<IConQATNode>
		implements INodeVisitor<IConQATNode, NeverThrownRuntimeException> {

	/** Matched nodes are not reseted */
	private PatternList preservedNodesPatterns = null;

	/** Map that stores keys and default values that are to be reset */
	private final Map<String, Object> resetKeys = new HashMap<String, Object>();

	/** ConQAT Parameter */
	@AConQATParameter(name = "node", minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "Patterns that determine in which nodes the values are reset. Matched nodes are left untouched.")
	public void setPreservedNodesPatterns(
			@AConQATAttribute(name = "patterns", description = "Preserved Nodes patterns") PatternList preservedNodesPatterns) {
		this.preservedNodesPatterns = preservedNodesPatterns;
	}

	/** ConQAT Parameter */
	@AConQATParameter(name = "reset", minOccurrences = 1, maxOccurrences = -1, description = "")
	public void setDefaultValue(
			@AConQATAttribute(name = "key", description = "Key that gets reset") String key,
			@AConQATAttribute(name = "value", description = "Value to which it gets reset. Use string 'null' to set a value to NULL") String value) {
		if ("null".equals(value.toLowerCase())) {
			value = null;
		}
		resetKeys.put(key, value);
	}

	/** {@inheritDoc} */
	@Override
	protected void processInput(IConQATNode root) {
		TraversalUtils.visitAllDepthFirst(this, root);
	}

	/** {@inheritDoc} */
	@Override
	public void visit(IConQATNode node) {
		if (!preservedNodesPatterns.matchesAny(node.getId())) {
			for (String key : resetKeys.keySet()) {
				node.setValue(key, resetKeys.get(key));
			}
		}
	}

}