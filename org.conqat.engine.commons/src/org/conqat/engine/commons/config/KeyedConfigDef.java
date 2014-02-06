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

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: goede $
 * @version $Rev: 38085 $
 * @ConQAT.Rating GREEN Hash: F6269BB8DE509A9EEB01C79DFEF083E3
 */
@AConQATProcessor(description = "Creates a keyed configuration based on existing configurations and keys provided as parameters. "
		+ "The order of the parameters is important, as they are applied in this order, i.e., later entries will override earlier ones.")
public class KeyedConfigDef extends ConQATProcessorBase {

	/** The resulting keyed config. */
	private KeyedConfig result = new KeyedConfig();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "set", description = "Sets an explicit key/value pair in the result.")
	public void addKeyValuePair(
			@AConQATAttribute(name = "key", description = "The key.") String key,
			@AConQATAttribute(name = "value", description = "The value.") String value) {
		result.set(key, value);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "config", description = "Adds all key/value pairs from the given config to the result.")
	public void addKeyedConfig(
			@AConQATAttribute(name = "ref", description = "The config.") KeyedConfig config) {
		for (String key : config.getKeys()) {
			result.set(key, config.get(key));
		}
	}

	/** {@inheritDoc} */
	@Override
	public KeyedConfig process() {
		return result;
	}
}
