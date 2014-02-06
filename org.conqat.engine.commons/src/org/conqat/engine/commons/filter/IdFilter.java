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
package org.conqat.engine.commons.filter;

import org.conqat.engine.commons.node.IRemovableConQATNode;
import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * Filters out nodes that match an ignore pattern.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: 07363BBC0F3811618D143EF9081E4FDF
 */
@AConQATProcessor(description = "Filters out nodes that match an ignore pattern.")
public class IdFilter extends FilterBase<IRemovableConQATNode> {

	/** Target nodes this filter operates on. */
	@AConQATFieldParameter(parameter = "target", attribute = "nodes", optional = true, description = ""
			+ "The target nodes to operate on.")
	public ETargetNodes targetNodes;

	/** List of ignore patterns */
	private PatternList ignorePatterns;
	
	/** List of include patterns */
	private final PatternList includePatterns = new PatternList();

	/** ConQAT Parameter */
	@AConQATParameter(name = "ignore", description = "List of ignore patterns", minOccurrences = 1, maxOccurrences = 1)
	public void setIgnorePatterns(
			@AConQATAttribute(name = "patterns", description = "List of ignore patterns") PatternList ignorePatterns) {
		this.ignorePatterns = ignorePatterns;
	}

	/** ConQAT Parameter */
	@AConQATParameter(name = "include", description = "List of include patterns. Include patterns override ignore patterns", minOccurrences = 0, maxOccurrences = 1)
	public void setIncludePatterns(
			@AConQATAttribute(name = "patterns", description = "List of ignore patterns") PatternList includePatterns) {
		this.includePatterns.addAll(includePatterns);
	}
	
	
	/** {@inheritDoc} */
	@Override
	protected ETargetNodes getTargetNodes() {
		if (targetNodes == null) {
			return super.getTargetNodes();
		}
		return targetNodes;
	}

	/** {@inheritDoc} */
	@Override
	protected boolean isFiltered(IRemovableConQATNode node) {
		String nodeId = node.getId();
		return ignorePatterns.matchesAny(nodeId)
				&& !includePatterns.matchesAny(nodeId);
	}

}