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

import org.conqat.lib.commons.collections.CounterSet;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * This processor converts a counter set to a simple tree of
 * <code>IConQATNode</code>s.
 * 
 * @author Florian Deissenboeck
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: FA18BA590EDE89B46E659FCD740A9411
 */
@AConQATProcessor(description = "This processor converts a counter set to a simple tree of IConQATNodes. "
		+ "If a tree is specified, values will be added, otherwise a new tree is created.")
public class CounterSetConverter extends MapConverterBase {

	/** The counter set. */
	private CounterSet<Object> counterSet;

	/** The counter set defining the values. */
	@AConQATParameter(name = ConQATParamDoc.INPUT_NAME, minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "Counter set holding the values.")
	public void setCounterSet(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC)
			CounterSet<Object> counterArray) {
		this.counterSet = counterArray;
	}

	/** {@inheritDoc} */
	@Override
	protected Collection<?> getKeyElements() {
		return counterSet.getKeys();
	}

	/** {@inheritDoc} */
	@Override
	protected Integer getValue(Object key) {
		return counterSet.getValue(key);
	}
}