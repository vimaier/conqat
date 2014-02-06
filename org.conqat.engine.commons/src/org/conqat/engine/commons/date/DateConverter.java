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
package org.conqat.engine.commons.date;

import static org.conqat.engine.commons.CommonUtils.DEFAULT_DATE_FORMAT_PATTERN;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.conqat.engine.commons.CommonUtils;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.commons.traversal.TargetExposedNodeTraversingProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;

/**
 * {@ConQAT.Doc}
 * 
 * @author deissenb
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: ACDD6DE0EE555C265F4EEAF77CFA1A49
 */
@AConQATProcessor(description = "This processor converts string values "
		+ "to objects of type java.util.Date.")
public class DateConverter extends
		TargetExposedNodeTraversingProcessorBase<IConQATNode> {

	/** Keys that are converted. */
	private final HashMap<String, SimpleDateFormat> keys = new HashMap<String, SimpleDateFormat>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "key", minOccurrences = 1, description = "Key to convert.")
	public void addKey(
			@AConQATAttribute(name = "value", description = "The key.") String value,
			@AConQATAttribute(name = "pattern", defaultValue = DEFAULT_DATE_FORMAT_PATTERN, description = ConQATParamDoc.DATE_PATTERN_DESC
					+ " [" + DEFAULT_DATE_FORMAT_PATTERN + "]") String pattern)
			throws ConQATException {
		keys.put(value, CommonUtils.createDateFormat(pattern));
	}

	/** Returns {@link ETargetNodes#LEAVES}. */
	@Override
	protected ETargetNodes getDefaultTargetNodes() {
		return ETargetNodes.LEAVES;
	}

	/** {@inheritDoc} */
	@Override
	public void visit(IConQATNode node) {
		for (String key : keys.keySet()) {
			convert(node, key);
		}
	}

	/**
	 * Convert the value stored at a key to a date object. This method logs
	 * warnings if value is not found or does not match the date format.
	 */
	private void convert(IConQATNode node, String key) {
		Object value = node.getValue(key);
		if (value instanceof Date) {
			return;
		}

		if (value == null) {
			getLogger().warn(
					"No value stored at node '" + node.getId() + "' for key '"
							+ key + "'");
			return;
		}

		String dateString = value.toString();
		SimpleDateFormat dateFormat = keys.get(key);

		try {
			node.setValue(key, dateFormat.parse(dateString));
		} catch (ParseException e) {
			getLogger().warn(
					"Value stored at node '" + node.getId() + "' for key '"
							+ key + "': " + dateString
							+ " cannot be converted by pattern "
							+ dateFormat.toPattern());
		}

	}

}