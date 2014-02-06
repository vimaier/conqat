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
package org.conqat.engine.commons.node;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.conqat.engine.commons.CommonUtils;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 42198 $
 * @ConQAT.Rating GREEN Hash: 2ED3A8B6DD75174147A6039A94E6FD1A
 */
@AConQATProcessor(description = "Allows to create a single node. Useful e.g. to create data for test runs.")
public class ListNodeDef extends ConQATProcessorBase {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "node", attribute = "id", optional = false, description = "ID of the node that gets created.")
	public String id;

	/** Stores values to be stored in the node */
	private final LinkedHashMap<String, Object> nodeValues = new LinkedHashMap<String, Object>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "store", minOccurrences = 0, maxOccurrences = -1, description = "Store value in node")
	public void setValue(
			@AConQATAttribute(name = ConQATParamDoc.WRITEKEY_KEY_NAME, description = ConQATParamDoc.WRITEKEY_KEY_DESC) String key,
			@AConQATAttribute(name = ConQATParamDoc.VALUE_KEY_NAME, description = ConQATParamDoc.STRING_VALUE_KEY_DESC) String valueString,
			@AConQATAttribute(name = ConQATParamDoc.TYPE_KEY_NAME, description = ConQATParamDoc.TYPE_KEY_DESC) String typeName)
			throws ConQATException {
		nodeValues.put(key, CommonUtils.convertTo(valueString, typeName));
	}

	/** {@inheritDoc} */
	@Override
	public ListNode process() {
		ListNode result = new ListNode(id);

		for (Entry<String, Object> entry : nodeValues.entrySet()) {
			NodeUtils.addToDisplayList(result, entry.getKey());
			result.setValue(entry.getKey(), entry.getValue());
		}

		return result;
	}
}