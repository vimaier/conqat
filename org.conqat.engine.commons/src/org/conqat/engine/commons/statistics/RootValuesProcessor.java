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
package org.conqat.engine.commons.statistics;

import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.util.ConQATInputProcessorBase;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;

/**
 * {@ConQAT.Doc}
 * 
 * @author juergens
 * @author $Author: hummelb $
 * @version $Rev: 36404 $
 * @ConQAT.Rating GREEN Hash: 368148D4DE98C4FAC75D14C07D461169
 */
@AConQATProcessor(description = "Creates a KeyedData object from the keys and values stored in the "
		+ "display list of the root node. This KeyedData object can e.g. be used to create a pie chart.")
public class RootValuesProcessor extends ConQATInputProcessorBase<IConQATNode> {

	/** {@inheritDoc} */
	@Override
	public KeyedData<String> process() throws ConQATException {
		KeyedData<String> result = new KeyedData<String>();

		for (String key : NodeUtils.getDisplayList(input)) {
			Object value = input.getValue(key);
			try {
				Double numericValue = Double.parseDouble(value.toString());
				result.add(key, numericValue);
			} catch (NumberFormatException e) {
				throw new ConQATException("Cannot convert value" + value
						+ " stored in key " + key + " into a number: ", e);
			}
		}

		return result;
	}
}