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
package org.conqat.engine.commons.statistics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: goeb $
 * @version $Rev: 46968 $
 * @ConQAT.Rating YELLOW Hash: 77638CA44944883B692054D029CBF09D
 */
@AConQATProcessor(description = "Removes items from a KeyedData structure to meet the given length threshold. "
		+ "The items from the beginning of the structure (determined by the iteration order) are kept.")
public class KeyedDataTopFilter extends
		ConQATPipelineProcessorBase<KeyedData<Comparable<?>>> {

	/** Number of items to include. */
	private int numberOfItems;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "top", minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "The maximaum number of items to pass on.")
	public void setMaxLength(
			@AConQATAttribute(name = "value", description = "The number of items.") int numberOfItems) {

		this.numberOfItems = numberOfItems;
	}

	/** {@inheritDoc} */
	@Override
	protected void processInput(KeyedData<Comparable<?>> keyedData) {
		Set<Comparable<?>> keys = keyedData.getValues().keySet();
		keys.retainAll(getFirstElements(keys, numberOfItems));
	}

	/** Returns a List containing the first n elements of the original set. */
	public static <K> List<K> getFirstElements(Collection<K> input, int n) {
		List<K> result = new ArrayList<K>();

		Iterator<K> it = input.iterator();
		for (int i = 0; i < n && it.hasNext(); i++) {
			result.add(it.next());
		}
		return result;
	}

}
