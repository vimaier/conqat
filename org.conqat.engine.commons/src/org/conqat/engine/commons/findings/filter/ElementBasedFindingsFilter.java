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
package org.conqat.engine.commons.findings.filter;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 37136 $
 * @ConQAT.Rating GREEN Hash: 69495A61899C541F066571BB2B78BE87
 */
@AConQATProcessor(description = "Filters findings based on boolean values stored under a given key in their elements. "
		+ "It can e.g. be used to filter findings from ignored elements. "
		+ "If the value is not found, the findings for an element are not filtered. "
		+ "If a non-boolean value is stored under the key, a ConQATException is thrown.")
public class ElementBasedFindingsFilter extends FindingsFilterBase {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.READKEY_NAME, attribute = ConQATParamDoc.READKEY_KEY_NAME, optional = false, description = ConQATParamDoc.READKEY_DESC)
	public String key;

	/** {@inheritDoc} */
	@Override
	protected boolean isFiltered(IConQATNode node, Finding finding)
			throws ConQATException {
		return NodeUtils.getBooleanValue(node, key);
	}

}
