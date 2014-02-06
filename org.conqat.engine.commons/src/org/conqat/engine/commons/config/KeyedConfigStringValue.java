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

import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: goede $
 * @version $Rev: 38085 $
 * @ConQAT.Rating GREEN Hash: 0EE09B09C9C5DC8D2C0A2EFC315CE194
 */
@AConQATProcessor(description = KeyedConfigValueBase.DESCRIPTION_PREFIX
		+ "string" + KeyedConfigValueBase.DESCRIPTION_SUFFIX)
public class KeyedConfigStringValue extends KeyedConfigValueBase<String> {

	/** {@inheritDoc} */
	@Override
	protected Class<String> getValueClass() {
		return String.class;
	}
}