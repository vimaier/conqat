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
package org.conqat.engine.commons.collections;

import java.util.HashMap;
import java.util.Map;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;

/**
 * Provides a <code>HashMap&lt;String, String&gt;</code> to be used for
 * various purposes.
 * 
 * @author Tilman Seifert
 * @author Benjamin Hummel
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: 2C00D92EF4FB5A12D10173C853B35969
 */
@AConQATProcessor(description = "Defines a mapping from string to strings.")
public class StringMapDef extends ConQATProcessorBase {

	/** The map. */
	private final Map<String, String> map = new HashMap<String, String>();

	/** Take a (key, value) pair. Values defined once can't be overriden. */
	@AConQATParameter(name = "entry", description = "Definition of a "
			+ "(key, value) pair. Keys must be unique.")
	public void addKeyValuePair(
			@AConQATAttribute(name = "key", description = "The key.")
			String key,
			@AConQATAttribute(name = "value", description = "The value.")
			String value) throws ConQATException {

		if (map.containsKey(key)) {
			throw new ConQATException("Key '" + key + "' defined twice");
		}
		map.put(key, value);
	}

	/** Does not really process anything, but just returns the created map. */
	@Override
	public Map<String, String> process() {
		return map;
	}
}