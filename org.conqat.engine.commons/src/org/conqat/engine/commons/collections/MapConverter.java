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

import java.util.Collection;
import java.util.Map;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * This processor converts a map to simple tree of <code>IConQATNode</code>s.
 * 
 * @author Florian Deissenboeck
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: 5F71AEF514CA34994B25A891036ABF68
 */
@AConQATProcessor(description = "This processor converts a map to a simple tree of IConQATNodes. "
		+ "If a tree is specified, values will be added, otherwise a new tree is created.")
public class MapConverter extends MapConverterBase {

	/** The map. */
	private Map<?, ?> map;

	/**
	 * The map defining the values.
	 */
	@AConQATParameter(name = "map", minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "Map holding the values.")
	public void setMap(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC)
			Map<?, ?> map) {
		this.map = map;
	}

	/** {@inheritDoc} */
	@Override
	protected Collection<?> getKeyElements() {
		return map.keySet();
	}

	/** {@inheritDoc} */
	@Override
	protected Object getValue(Object key) {
		return map.get(key);
	}
}