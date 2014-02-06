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
 * @ConQAT.Rating GREEN Hash: 99864AE17F28059DB052090FE0A976EE
 */
@AConQATProcessor(description = KeyedConfigValueBase.DESCRIPTION_PREFIX
		+ "double" + KeyedConfigValueBase.DESCRIPTION_SUFFIX)
public class KeyedConfigDoubleValue extends KeyedConfigValueBase<Double> {

	/** {@inheritDoc} */
	@Override
	protected Class<Double> getValueClass() {
		return Double.class;
	}
}
