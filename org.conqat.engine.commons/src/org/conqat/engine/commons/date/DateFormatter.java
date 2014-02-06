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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.conqat.engine.commons.CommonUtils;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.commons.traversal.TargetExposedNodeTraversingProcessorBase;
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
 * @ConQAT.Rating GREEN Hash: DA31EECF2FF3BBEE101D96B0C8BDFADD
 */
@AConQATProcessor(description = "Converts date objects to date strings with the specified format. "
		+ "If no date object is found at the node, nothing is done.")
public class DateFormatter extends
		TargetExposedNodeTraversingProcessorBase<IConQATNode> {

	/** Date format. */
	private SimpleDateFormat dateFormat;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.READKEY_NAME, attribute = ConQATParamDoc.READKEY_KEY_NAME, description = ConQATParamDoc.READKEY_KEY_DESC)
	public String readKey;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.WRITEKEY_NAME, attribute = ConQATParamDoc.WRITEKEY_KEY_NAME, description = "Key out write output to. "
			+ "If not set, the read key is used.", optional = true)
	public String writeKey;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "date-format", minOccurrences = 1, maxOccurrences = 1, description = "Date format")
	public void addKey(
			@AConQATAttribute(name = "pattern", defaultValue = DEFAULT_DATE_FORMAT_PATTERN, description = ConQATParamDoc.DATE_PATTERN_DESC
					+ " [" + DEFAULT_DATE_FORMAT_PATTERN + "]") String pattern)
			throws ConQATException {
		dateFormat = CommonUtils.createDateFormat(pattern);
	}

	/** {@inheritDoc} */
	@Override
	protected void setUp(IConQATNode root) {
		if (writeKey == null) {
			writeKey = readKey;
		}
	}

	/** {@inheritDoc} */
	@Override
	protected ETargetNodes getDefaultTargetNodes() {
		return ETargetNodes.LEAVES;
	}

	/** {@inheritDoc} */
	@Override
	public void visit(IConQATNode node) {
		Date date = NodeUtils.getValue(node, readKey, Date.class, null);
		if (date == null) {
			return;
		}
		node.setValue(writeKey, dateFormat.format(date));
	}
}