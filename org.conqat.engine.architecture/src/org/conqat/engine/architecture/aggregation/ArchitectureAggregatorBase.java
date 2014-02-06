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
package org.conqat.engine.architecture.aggregation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.conqat.engine.architecture.scope.AnnotatedArchitecture;
import org.conqat.engine.architecture.scope.ComponentNode;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.INodeVisitor;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.collections.CollectionUtils;

/**
 * Base class for architecture-drive aggregators.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 10ECD8DC26F8512FB5820EE91AF0ADCF
 */
public abstract class ArchitectureAggregatorBase<E> extends
		ConQATPipelineProcessorBase<AnnotatedArchitecture> implements
		INodeVisitor<ComponentNode, ConQATException> {

	/**
	 * Value used to indicate that the output key is the same as the input key.
	 * This is package visible to make it usable from the tests.
	 */
	/* package */static final String DEFAULT_TARGET_KEY = "__INPUT_KEY__";

	/** Mapping from input keys to output (write) keys. */
	private final Map<String, String> keys = new LinkedHashMap<String, String>();

	/** The architecture aggregation is performed on. */
	private AnnotatedArchitecture arch;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.READKEY_NAME, minOccurrences = 1, description = ""
			+ "The key to read the aggregation value from.")
	public void addKey(
			@AConQATAttribute(name = ConQATParamDoc.READKEY_KEY_NAME, description = ConQATParamDoc.READKEY_KEY_DESC) String readKey,
			@AConQATAttribute(name = "target", description = "This parameter can (optionally) give a key to which the aggregate "
					+ "values are written. If the value "
					+ DEFAULT_TARGET_KEY
					+ " is used, this is just the same as the input key.", defaultValue = DEFAULT_TARGET_KEY) String writeKey)
			throws ConQATException {
		if (DEFAULT_TARGET_KEY.equals(writeKey)) {
			writeKey = readKey;
		}
		if (keys.put(readKey, writeKey) != null) {
			throw new ConQATException("Duplicate read key: " + readKey);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void processInput(AnnotatedArchitecture arch)
			throws ConQATException {
		this.arch = arch;
		NodeUtils.addToDisplayList(arch,
				CollectionUtils.asUnmodifiable(keys.values()));
		TraversalUtils.visitAllDepthFirst(this, arch);
	}

	/**
	 * Perform aggregation for each key by calling
	 * {@link #aggregate(ComponentNode, String, String)}.
	 */
	@Override
	public void visit(ComponentNode node) throws ConQATException {
		for (String readKey : keys.keySet()) {
			aggregate(node, readKey, keys.get(readKey));
		}
	}

	/**
	 * This aggregates the values for a key at a component with the following
	 * steps
	 * <ol>
	 * <li>Initialize an empty value list.
	 * <li>Add the values stored for this key at all child components to the
	 * list.
	 * <li>Add the values stored for this key at all ConQAT node belonging to
	 * this component to the list
	 * <li>Call {@link #aggregate(List)} for the list
	 * </ol>
	 */
	private void aggregate(ComponentNode node, String readKey, String writeKey)
			throws ConQATException {
		List<E> values = new ArrayList<E>();

		if (node.hasChildren()) {
			for (ComponentNode child : node.getChildren()) {
				addValue(values, child, writeKey);
			}
		}

		for (IConQATNode type : arch.getMatchedTypes(node)) {
			addValue(values, type, readKey);
		}

		node.setValue(writeKey, aggregate(values));
	}

	/**
	 * This method uses {@link #obtainValue(IConQATNode, String)} to obtain a
	 * value from a node (may be a component node or a node from the scope). If
	 * the node is <code>null</code>, a warning is logged. Otherwise the value
	 * is added to the provided list.
	 */
	private void addValue(List<E> values, IConQATNode node, String readKey)
			throws ConQATException {
		E value = obtainValue(node, readKey);
		if (value == null) {
			getLogger().warn(
					"Could not determine value for key " + readKey
							+ " at node " + node.getId());
			return;
		}
		values.add(value);
	}

	/**
	 * Template method to obtain a value from a node stored at the specified
	 * key. This may return <code>null</code> if no value is stored or the value
	 * is not of the correct type. <code>null</code>-values are ignored.
	 */
	protected abstract E obtainValue(IConQATNode child, String readKey)
			throws ConQATException;

	/**
	 * Template method to aggregate list of values. If this returns
	 * <code>null</code> the behavior of following aggregations steps may be
	 * awkward.
	 */
	protected abstract E aggregate(List<E> values) throws ConQATException;

}