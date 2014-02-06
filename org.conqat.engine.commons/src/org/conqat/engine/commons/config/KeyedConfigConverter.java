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

import java.util.Collection;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.collections.MapConverterBase;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.commons.assertion.CCSMAssert;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: goede $
 * @version $Rev: 38085 $
 * @ConQAT.Rating GREEN Hash: AE8C5D05CCF3CF69E6BA34E89588197B
 */
@AConQATProcessor(description = "This processor converts a keyed config to a simple tree of IConQATNodes. "
		+ "If a tree is specified, values will be added, otherwise a new tree is created.")
public class KeyedConfigConverter extends MapConverterBase {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "config", attribute = ConQATParamDoc.INPUT_REF_NAME, description = "The keyed config to convert.")
	public KeyedConfig input;

	/** {@inheritDoc} */
	@Override
	protected Collection<?> getKeyElements() {
		return input.getKeys();
	}

	/** {@inheritDoc} */
	@Override
	protected Object getValue(Object key) {
		CCSMAssert.isInstanceOf(key, String.class);
		return input.get((String) key);
	}

}
