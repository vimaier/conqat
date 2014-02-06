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
package org.conqat.engine.code_clones.core.constraint;

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IElement;
import org.conqat.lib.commons.collections.PairList;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 6E2D606D3BA16C81AFBA807F659B5071
 */
@AConQATProcessor(description = ""
		+ "This constraint checks a boolean value stored in a key for the element containing the clone. "
		+ "The constraint is only satisfied if at least one of the keys holds the value true. "
		+ "This can be used, e.g., when reducing a clone detection result to changed files.")
public class BooleanElementKeyConstraint extends ElementConstraintBase {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.READKEY_NAME, attribute = ConQATParamDoc.READKEY_KEY_NAME, description = ConQATParamDoc.READKEY_KEY_DESC)
	public String key;

	/** {@inheritDoc} */
	@Override
	protected boolean satisfied(CloneClass cloneClass,
			PairList<Clone, IElement> clonesAndElements) throws ConQATException {
		for (int i = 0; i < clonesAndElements.size(); ++i) {
			IElement element = clonesAndElements.getSecond(i);
			if (NodeUtils.getBooleanValue(element, key)) {
				return true;
			}
		}
		return false;
	}

}
