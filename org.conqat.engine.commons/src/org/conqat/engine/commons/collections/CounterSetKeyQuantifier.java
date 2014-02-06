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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.conqat.lib.commons.collections.CounterSet;
import org.conqat.lib.commons.collections.ImmutablePair;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.commons.traversal.TargetExposedNodeTraversingProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;

/**
 * {@ConQAT.Doc}
 * 
 * @author hummelb
 * @author $Author: hummelb $
 * @version $Rev: 36404 $
 * @ConQAT.Rating GREEN Hash: EDBBF24065293B3E8AFDD3176AF72BBB
 */
@AConQATProcessor(description = "This processors processes counter sets stored in keys "
		+ "and calculates a string describing a quantization of the set, i.e. tries "
		+ "to use a finite set of labels for describing the content of the counter set.")
public class CounterSetKeyQuantifier extends
		TargetExposedNodeTraversingProcessorBase<IConQATNode> {

	/** Input key. */
	private String inputKey;

	/** Output key. */
	private String outputKey;

	/** Set of keys to ignore. */
	private final Set<String> ignore = new HashSet<String>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.READKEY_NAME, minOccurrences = 1, description = ConQATParamDoc.READKEY_DESC)
	public void setReadKey(
			@AConQATAttribute(name = ConQATParamDoc.READKEY_KEY_NAME, description = ConQATParamDoc.READKEY_KEY_DESC) String key) {
		inputKey = key;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.WRITEKEY_NAME, minOccurrences = 1, maxOccurrences = 1, description = ConQATParamDoc.WRITEKEY_DESC)
	public void setWriteKey(
			@AConQATAttribute(name = ConQATParamDoc.WRITEKEY_KEY_NAME, description = ConQATParamDoc.WRITEKEY_KEY_DESC) String key) {
		outputKey = key;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "ignore", minOccurrences = 0, description = "Adds a counter set key to the set of ignored values.")
	public void addIgnore(
			@AConQATAttribute(name = "cskey", description = "The counter set key to be ignored.") String key) {
		ignore.add(key);
	}

	/** Add output key to display list. */
	@Override
	protected void setUp(IConQATNode root) throws ConQATException {
		super.setUp(root);
		NodeUtils.addToDisplayList(root, outputKey);
	}

	/** {@inheritDoc} */
	@Override
	@SuppressWarnings("unchecked")
	public void visit(IConQATNode node) throws ConQATException {
		// We use a List<Pair> instead of a PairList, as we want to sort the
		// list.
		List<ImmutablePair<Integer, String>> pairs = new ArrayList<ImmutablePair<Integer, String>>();
		int total = fillPairs(NodeUtils.getValue(node, inputKey,
				CounterSet.class), pairs);
		node.setValue(outputKey, getQuantifiedString(pairs, total));
	}

	/** Returns the string which summarizes the value. */
	private String getQuantifiedString(
			List<ImmutablePair<Integer, String>> pairs, int total) {
		if (pairs.size() == 0) {
			return "none";
		}
		if (pairs.size() == 1) {
			return "only " + pairs.get(0).getSecond();
		}

		// sort descending
		Collections.sort(pairs);
		Collections.reverse(pairs);

		if (pairs.get(0).getFirst() >= .9 * total) {
			return "mostly " + pairs.get(0).getSecond();
		}
		if (pairs.get(0).getFirst() > .5 * total) {
			return "majority of " + pairs.get(0).getSecond() + " followed by "
					+ pairs.get(1).getSecond();
		}

		return "mixed";
	}

	/**
	 * Fills a list of count/key pairs from a counter set, respecting the
	 * {@link #ignore} set. Returns the sum of values.
	 */
	private int fillPairs(CounterSet<Object> counterSet,
			List<ImmutablePair<Integer, String>> pairs) {
		int total = 0;
		for (Object key : counterSet.getKeys()) {
			if (!ignore.contains(key.toString())) {
				pairs.add(new ImmutablePair<Integer, String>(counterSet
						.getValue(key), key.toString()));
				total += counterSet.getValue(key);
			}
		}
		return total;
	}

	/** {@inheritDoc} */
	@Override
	protected ETargetNodes getDefaultTargetNodes() {
		return ETargetNodes.LEAVES;
	}

}