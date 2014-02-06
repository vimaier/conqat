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
package org.conqat.engine.commons.aggregation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.collections.CollectionUtils;

/**
 * Base class to simplify building new aggregators. The output key is added to
 * the display list if it is not already present.
 * <p>
 * Aggregation is only performed for inner nodes. The tree is traversed depth
 * first, with child nodes visited first.
 * 
 * @author $Author: juergens $
 * @version $Rev: 41415 $
 * @ConQAT.Rating GREEN Hash: 77F4811EB50461B9E7B556C68FF9B64C
 * 
 * @param <VALUE>
 *            the value type stored in the nodes.
 * @param <AGGREGATOR>
 *            the type used internally for aggregation.
 */
public abstract class AggregatorBase<VALUE, AGGREGATOR> extends
		ConQATPipelineProcessorBase<IConQATNode> {

	/**
	 * Value used to indicate that the output key is the same as the input key.
	 * This is package visible to make it usable from the tests.
	 */
	/* package */static final String DEFAULT_TARGET_KEY = "__INPUT_KEY__";

	/** The class type for the value, used for type checking. */
	private final Class<VALUE> valueClass;

	/** Mapping from input keys to output (write) keys. */
	private final Map<String, String> keys = new LinkedHashMap<String, String>();

	/** Flag that determines whether missing values are logged */
	private boolean logMissingInputValue = true;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "include-inner-nodes", attribute = "value", optional = true, description = "If set, values stored at inner nodes are included into the aggregation.")
	public boolean includeInnerNodes = false;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.IGNORE_NAME, attribute = ConQATParamDoc.IGNORE_KEY_NAME, optional = true, description = ConQATParamDoc.IGNORE_DESC
			+ " " + ConQATParamDoc.IGNORE_KEY_DESC)
	public String ignoreKey = null;

	/** Constructor. */
	protected AggregatorBase(Class<VALUE> valueClass) {
		this.valueClass = valueClass;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.READKEY_NAME, minOccurrences = 1, description = ""
			+ "The key to read the aggregation value from.")
	public void setReadKey(
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

	/** ConQAT Parameter */
	@AConQATParameter(name = "missing", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Logging behaviour")
	public void setLogMissingInputValue(
			@AConQATAttribute(name = "log", description = "Flag that determines whether missing values are logged", defaultValue = "true") boolean logMissingValue) {
		this.logMissingInputValue = logMissingValue;
	}

	/** Returns the name of the first output key. */
	protected String getFirstOutputKey() {
		CCSMAssert.isFalse(keys.isEmpty(),
				"Multiplicity should ensure non-empty keys!");
		return keys.values().iterator().next();
	}

	/** {@inheritDoc} */
	@Override
	protected void processInput(IConQATNode input) throws ConQATException {
		// add all new output keys.
		// next method ignores all keys which are already added
		NodeUtils.addToDisplayList(input,
				CollectionUtils.asUnmodifiable(keys.values()));

		for (Map.Entry<String, String> entry : keys.entrySet()) {
			performAggregation(input, entry.getKey(), entry.getValue());
		}
	}

	/** Traverses the given node depth first and performs aggregation. */
	private AGGREGATOR performAggregation(IConQATNode node, String inputKey,
			String outputKey) throws ConQATException {
		if (ignoreKey != null && NodeUtils.isIgnored(node, ignoreKey)) {
			return null;
		}

		if (!node.hasChildren()) {
			return getAggregatorForNode(node, inputKey);
		}

		List<AGGREGATOR> aggregators = new ArrayList<AGGREGATOR>();
		if (includeInnerNodes) {
			aggregators.add(getAggregatorForNode(node, inputKey));
		}
		for (IConQATNode child : node.getChildren()) {
			AGGREGATOR childAggregator = performAggregation(child, inputKey,
					outputKey);
			if (childAggregator != null) {
				aggregators.add(childAggregator);
			}
		}

		if (aggregators.isEmpty()) {
			return null;
		}

		AGGREGATOR result = aggregate(aggregators);
		node.setValue(outputKey, fromAggregator(result));
		return result;
	}

	/** Returns the aggregator obtained from a single node. */
	private AGGREGATOR getAggregatorForNode(IConQATNode node, String inputKey)
			throws ConQATException {
		Object valueObject = node.getValue(inputKey);
		if (valueObject != null) {
			if (!valueClass.isInstance(valueObject)) {
				throw new ConQATException("Invalid value encountered at key "
						+ inputKey + " of node " + node.getId() + "! "
						+ valueObject + " is not of type "
						+ valueClass.getSimpleName());
			}
			VALUE value = valueClass.cast(valueObject);
			return toAggregator(value);
		}

		if (logMissingInputValue) {
			getLogger().info(
					"No value for key '" + inputKey + "' at node: "
							+ node.getId());
		}
		return null;
	}

	/**
	 * This method should calculate the aggregated value from the provided
	 * values. The provided list is guaranteed to have at least one entry.
	 */
	protected abstract AGGREGATOR aggregate(List<AGGREGATOR> values);

	/**
	 * Template method for converting the value type to the aggregator type.
	 * Default implementation simply casts.
	 */
	@SuppressWarnings("unchecked")
	protected AGGREGATOR toAggregator(VALUE value) {
		return (AGGREGATOR) value;
	}

	/**
	 * Template method for converting the aggregator type to the value type.
	 * Default implementation simply casts.
	 */
	@SuppressWarnings("unchecked")
	protected VALUE fromAggregator(AGGREGATOR aggregator) {
		return (VALUE) aggregator;
	}
}