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
package org.conqat.engine.architecture.scope;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.conqat.engine.architecture.assessment.ArchitectureAnalyzer;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.lib.commons.clone.DeepCloneException;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableMap;

/**
 * An {@link AnnotatedArchitecture} is the combination of
 * {@link ArchitectureDefinition} and scope (described by {@link IConQATNode}s)
 * which the architecture definition is checked against.
 * <p>
 * Technically, {@link AnnotatedArchitecture} is just an extension of
 * {@link ArchitectureDefinition} that provides methods to navigate between the
 * types matched for a component (specified via key
 * {@link ArchitectureAnalyzer#MATCHED_TYPES_KEY}) and the types (represented by
 * {@link IConQATNode}) itself. We deliberately, chose this weak form of linking
 * via strings to keep deep cloning manageable.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 42918 $
 * @ConQAT.Rating GREEN Hash: 6793681F6E202ED03BA733E05C3B0E95
 */
public class AnnotatedArchitecture extends ArchitectureDefinition {

	/** The scope that belongs to this architecture. */
	private final IConQATNode scope;

	/**
	 * A mapping from ids to nodes for the above scope. This is initialized
	 * lazily.
	 */
	private Map<String, IConQATNode> idToNodeMap;

	/**
	 * Create new {@link AnnotatedArchitecture} from an architecture definition
	 * and a scope.
	 */
	public AnnotatedArchitecture(ArchitectureDefinition archDef,
			IConQATNode scope) throws DeepCloneException {
		super(archDef);
		this.scope = scope;
	}

	/** Copy constructor. */
	private AnnotatedArchitecture(AnnotatedArchitecture other)
			throws DeepCloneException {
		super(other);
		scope = other.scope.deepClone();
	}

	/** {@inheritDoc} */
	@Override
	public AnnotatedArchitecture deepClone() throws DeepCloneException {
		return new AnnotatedArchitecture(this);
	}

	/**
	 * Returns the types matched by a component. This only returns the nodes
	 * that are present in the associated scope. It can well be that the
	 * component references types via
	 * {@link ArchitectureAnalyzer#MATCHED_TYPES_KEY} that are not included in
	 * the scope and, hence, not returned here.
	 * 
	 * @return the matched types or an empty set if no types are matched (and
	 *         are present in the associated scope) by this component
	 */
	public Set<IConQATNode> getMatchedTypes(ComponentNode component) {
		HashSet<IConQATNode> result = new HashSet<IConQATNode>();

		Collection<String> matchedTypeNames = NodeUtils.getStringCollection(
				component, ArchitectureAnalyzer.MATCHED_TYPES_KEY);

		if (matchedTypeNames == null) {
			return result;
		}

		UnmodifiableMap<String, IConQATNode> map = getIdToNodeMap();
		for (String nodeName : matchedTypeNames) {
			IConQATNode node = map.get(nodeName);
			if (node != null) {
				result.add(node);
			}
		}
		return result;
	}

	/** Get the scope associated with this architecture. */
	public IConQATNode getScope() {
		return scope;
	}

	/**
	 * Get map that maps from node id to the nodes itself for the scope obtained
	 * via {@link #getScope()}.
	 */
	public UnmodifiableMap<String, IConQATNode> getIdToNodeMap() {
		if (idToNodeMap == null) {
			idToNodeMap = TraversalUtils.createIdToNodeMap(scope);
		}
		return CollectionUtils.asUnmodifiable(idToNodeMap);
	}
}