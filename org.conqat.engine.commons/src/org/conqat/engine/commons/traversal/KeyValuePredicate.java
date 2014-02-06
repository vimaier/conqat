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
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.collections.PairList;
import org.conqat.lib.commons.string.StringUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: $
 * @version $Rev: $
 * @ConQAT.Rating YELLOW Hash: 46C945F5F4E5052EB70A435C67F03769
 */
@AConQATProcessor(description = "Predicate that is satisfied for all nodes that contain all given key-value pairs. "
		+ "Matching of values is performed on their string representation.")
public class KeyValuePredicate extends ConQATNodePredicateBase {

	/** List that stores pairs of key value pairs used for filtering */
	private final PairList<String, String> keyValuePairs = new PairList<String, String>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "match", description = "If present, only nodes annotated with given "
			+ "key and value are processed. Values are matched by their "
			+ "toString()-representation. If set multple times, only nodes that satisfy all criteria are processed.")
	public void addMatchedPair(
			@AConQATAttribute(name = ConQATParamDoc.READKEY_KEY_NAME, description = ConQATParamDoc.READKEY_KEY_DESC) String key,
			@AConQATAttribute(name = ConQATParamDoc.ATTRIBUTE_VALUE_NAME, description = "String representation of value ") String value)
			throws ConQATException {
		if (StringUtils.isEmpty(key)) {
			throw new ConQATException("Key must not be empty");
		}
		keyValuePairs.add(key, value);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isContained(IConQATNode node) {
		return NodeUtils.containsValues(node, keyValuePairs);
	}
}
