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
package org.conqat.engine.graph.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeConstants;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.graph.nodes.ConQATGraph;
import org.conqat.engine.graph.nodes.ConQATGraphInnerNode;
import org.conqat.engine.graph.nodes.ConQATGraphUtils;
import org.conqat.engine.graph.nodes.ConQATVertex;
import org.conqat.engine.graph.nodes.DeepCloneCopyAction;
import org.conqat.lib.commons.string.StringUtils;

import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;

/**
 * This processor creates a {@link ConQATGraph} from the results of a scope by
 * reading adjacency lists which are attached to the leaves.
 * 
 * @author $Author: pfaller $
 * @version $Rev: 37468 $
 * @ConQAT.Rating GREEN Hash: 34B98B62ABD646F5726BD8844FF41E52
 */
@AConQATProcessor(description = "This processor creates a ConQATGraph from adjacency lists attached "
		+ "to the leaves of a IConQATNode hierarchy. The keys under which these lists are found are "
		+ "given using the 'list-key' parameter.")
public class ScopeGraphCreator extends ConQATProcessorBase {

	/** Key to list edge sources. */
	@AConQATKey(description = "A list of those keys which were responsible for the creation of the edge. "
			+ "This is added to the edges (not the nodes).", type = "java.util.List<String>")
	public static final String SOURCE_KEY = "edge-source";

	/** the root node currently being analyzed. */
	private IConQATNode root;

	/** The dependency graph produced. */
	private final ConQATGraph result = new ConQATGraph();

	/** Whether to create nodes not already in the scope. */
	private boolean createMissingNodes = false;

	/**
	 * Creation of missing nodes that match one of the patterns in this list is
	 * not logged.
	 */
	private PatternList loggingIgnorePatterns = new PatternList();

	/** Set of keys storing the string lists. */
	private final Set<String> keys = new HashSet<String>();

	/** Lookup map for finding for a node ID the corresponding source node. */
	private final Map<String, IConQATNode> nodeLookup = new HashMap<String, IConQATNode>();

	/** Set the root element to work on. */
	@AConQATParameter(name = ConQATParamDoc.INPUT_NAME, minOccurrences = 1, maxOccurrences = 1, description = ConQATParamDoc.INPUT_DESC)
	public void setRoot(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) IConQATNode root) {
		this.root = root;
	}

	/** Set how to handle missing nodes. */
	@AConQATParameter(name = "missing-nodes", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Determines how to handle nodes occuring in the lists but not in the scope.")
	public void setCreateMissingNodes(
			@AConQATAttribute(name = "create", description = ""
					+ "If set to true, missing nodes are created and appended to the root of the graph. "
					+ "Otherwise they are not created but a message is logged. "
					+ "Default is to not create new nodes.") boolean create) {
		this.createMissingNodes = create;
	}

	/** Set a key to read from. */
	@AConQATParameter(name = "list-key", description = ""
			+ "Add a key to read a dependency list from. "
			+ "The same key is put into the edge summary lists of all edges created from these lists.")
	public void addListKey(
			@AConQATAttribute(name = "key", description = "The name of the key.") String key) {
		keys.add(key);
	}

	/** Set logging filter. */
	@AConQATParameter(name = "logging", maxOccurrences = 1, description = "Influence the logging behaviour, "
			+ "which is sometime useful for debugging a setup when lots of messages are shown.")
	public void setLoggingIgnorePatterns(
			@AConQATAttribute(name = "ignore-patterns", description = "Don't log creation of nodes that match this pattern") PatternList ignorePatterns) {
		this.loggingIgnorePatterns = ignorePatterns;
	}

	/** {@inheritDoc} */
	@Override
	public ConQATGraph process() throws ConQATException {
		result.setValue(NodeConstants.DISPLAY_LIST,
				NodeUtils.getDisplayList(root));
		rebuildHierarchy(root, result);
		insertEdges();
		getLogger().info(
				"Created graph has " + result.getVertices().size()
						+ " vertices and " + result.getEdges().size()
						+ " edges.");
		return result;
	}

	/** Duplicate the input hierarchy for the graph. */
	private void rebuildHierarchy(IConQATNode source,
			ConQATGraphInnerNode target) throws ConQATException {
		if (!source.hasChildren()) {
			return;
		}
		for (IConQATNode child : source.getChildren()) {
			if (!child.hasChildren()) {
				if (result.getVertexByID(child.getId()) != null) {
					getLogger().warn("Duplicate vertex: " + child.getId());
				} else {
					nodeLookup.put(child.getId(), child);
					result.createVertex(child.getId(), child.getId(), target);
				}
			} else {
				rebuildHierarchy(child,
						target.createChildNode(child.getId(), child.getName()));
			}
		}
	}

	/** Insert all edges implied by the adjacency lists. */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void insertEdges() throws ConQATException {
		for (ConQATVertex vertex : new ArrayList<ConQATVertex>(
				result.getVertices())) {
			IConQATNode originalNode = nodeLookup.get(vertex.getId());
			for (String key : keys) {
				Collection<String> targetCollection = (Collection<String>) originalNode
						.getValue(key);

				if (targetCollection == null) {
					continue;
				}

				assertNoEmptyDependency(targetCollection, originalNode.getId());

				for (String target : targetCollection) {
					ConQATVertex targetVertex = result.getVertexByID(target);
					if (targetVertex == null) {
						if (createMissingNodes) {
							targetVertex = result.createVertex(target, target,
									result);
							logMissingNodeCreation(target);
						} else {
							getLogger().warn(
									"No node of ID " + target + " found!");
							continue;
						}
					}
					DirectedSparseEdge edge = ConQATGraphUtils.getOrCreateEdge(
							result, vertex, targetVertex);

					Object collectionObject = edge.getUserDatum(SOURCE_KEY);
					Collection<String> collection;
					if (collectionObject instanceof Collection) {
						collection = (Collection) collectionObject;
					} else {
						collection = new ArrayList<String>();
						edge.setUserDatum(SOURCE_KEY, collection,
								DeepCloneCopyAction.getInstance());
					}
					collection.add(key);
				}
			}
		}
	}

	/**
	 * Checks whether the targetList contains an empty String. An empty string
	 * is considered as a bug.
	 * 
	 * @param nodeId
	 *            Id of node for which dependency list is checked
	 * 
	 * @throws ConQATException
	 *             If an empty dependency target is contained in the targetList
	 */
	private void assertNoEmptyDependency(Collection<String> targetList,
			String nodeId) throws ConQATException {
		for (String target : targetList) {
			if (StringUtils.isEmpty(target)) {
				throw new ConQATException(
						"Empty target node string in dependency list of node '"
								+ nodeId + "' found!");
			}
		}
	}

	/** Logs the creation of missing nodes */
	private void logMissingNodeCreation(String target) {
		if (!loggingIgnorePatterns.matchesAny(target)) {
			getLogger().info("Created missing node " + target + ".");
		}
	}
}