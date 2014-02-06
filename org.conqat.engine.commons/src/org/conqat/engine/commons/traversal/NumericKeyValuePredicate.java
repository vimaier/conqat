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
package org.conqat.engine.commons.traversal;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.commons.collections.PairList;
import org.conqat.lib.commons.predicate.IPredicate;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 45910 $
 * @ConQAT.Rating GREEN Hash: 16701064D7DF5A1D852A401E90C985A1
 */
@AConQATProcessor(description = "Predicate that is satisfied for all nodes that contain all key-value pairs."
		+ "Matching of values is performed on their numeric representation.")
public class NumericKeyValuePredicate extends ConQATNodePredicateBase {

	/** List that stores pairs of keys and predicates used for filtering */
	private final PairList<String, IPredicate<Number>> keyPredicatePairs = new PairList<String, IPredicate<Number>>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "equals", description = ("If present, only nodes annotated with the same numeric value for the given key are processed."))
	public void setEquals(
			@AConQATAttribute(name = ConQATParamDoc.READKEY_KEY_NAME, description = ConQATParamDoc.READKEY_KEY_DESC) String key,
			@AConQATAttribute(name = ConQATParamDoc.ATTRIBUTE_VALUE_NAME, description = "Numeric representation of value") final double expected) {
		keyPredicatePairs.add(key, new IPredicate<Number>() {
			@Override
			public boolean isContained(Number value) {
				return value.doubleValue() == expected;
			}
		});
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "less", description = ("If present, only nodes annotated with the a less (or equal) numeric value for the given key are processed."))
	public void setLess(
			@AConQATAttribute(name = ConQATParamDoc.READKEY_KEY_NAME, description = ConQATParamDoc.READKEY_KEY_DESC) String key,
			@AConQATAttribute(name = ConQATParamDoc.ATTRIBUTE_VALUE_NAME, description = "Numeric representation of value") final double expected,
			@AConQATAttribute(name = "equals", description = "Include equal value", defaultValue = "false") final boolean equals) {
		keyPredicatePairs.add(key, new IPredicate<Number>() {
			@Override
			public boolean isContained(Number value) {
				double actual = value.doubleValue();
				if (equals && actual == expected) {
					return true;
				}

				return actual < expected;
			}
		});
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "greater", description = ("If present, only nodes annotated with the a greater (or equal) numeric value for the given key are processed."))
	public void setGreater(
			@AConQATAttribute(name = ConQATParamDoc.READKEY_KEY_NAME, description = ConQATParamDoc.READKEY_KEY_DESC) String key,
			@AConQATAttribute(name = ConQATParamDoc.ATTRIBUTE_VALUE_NAME, description = "Numeric representation of value") final double expected,
			@AConQATAttribute(name = "equals", description = "Include equal value", defaultValue = "false") final boolean equals) {
		keyPredicatePairs.add(key, new IPredicate<Number>() {
			@Override
			public boolean isContained(Number value) {
				double actual = value.doubleValue();
				if (equals && actual == expected) {
					return true;
				}

				return actual > expected;
			}
		});
	}

	/** {@inheritDoc} */
	@Override
	public boolean isContained(IConQATNode node) {
		for (int i = 0; i < keyPredicatePairs.size(); i++) {
			String key = keyPredicatePairs.getFirst(i);
			IPredicate<Number> predicate = keyPredicatePairs.getSecond(i);
			Number number = NodeUtils.getValue(node, key, Number.class, null);
			if (number == null || !predicate.isContained(number)) {
				return false;
			}
		}
		return true;
	}
}
