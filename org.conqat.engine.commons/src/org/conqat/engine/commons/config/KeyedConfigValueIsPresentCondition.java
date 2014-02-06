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
package org.conqat.engine.commons.config;

import static org.conqat.engine.commons.config.KeyedConfigValueBase.CONFIG_DESCRIPTION;
import static org.conqat.engine.commons.config.KeyedConfigValueBase.CONFIG_PARAM_NAME;
import static org.conqat.engine.commons.config.KeyedConfigValueBase.KEY_ATTR_NAME;
import static org.conqat.engine.commons.config.KeyedConfigValueBase.KEY_DESCRIPTION;
import static org.conqat.engine.commons.config.KeyedConfigValueBase.KEY_PARAM_NAME;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.bool.ConditionBase;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 5153DACF2580AC831974C18FFCD29E39
 */
@AConQATProcessor(description = "This processor checks if a specific key is present in the provided configuration.")
public class KeyedConfigValueIsPresentCondition extends ConditionBase {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = CONFIG_PARAM_NAME, attribute = ConQATParamDoc.INPUT_REF_NAME, description = CONFIG_DESCRIPTION)
	public KeyedConfig config;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = KEY_PARAM_NAME, attribute = KEY_ATTR_NAME, description = KEY_DESCRIPTION)
	public String keyName;

	/** {@inheritDoc} */
	@Override
	protected boolean evaluateCondition() {
		// we do not log this as we expect the value to be used later (and then
		// logged anyway)
		return config.get(keyName) != null;
	}
}
