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
package org.conqat.engine.commons.node;

import java.util.Map;

import org.conqat.lib.commons.clone.CloneUtils;
import org.conqat.lib.commons.clone.DeepCloneException;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.MemoryEfficientStringMap;
import org.conqat.lib.commons.collections.UnmodifiableCollection;
import org.conqat.engine.commons.findings.FindingsList;
import org.conqat.engine.commons.sorting.NameSorter;

/**
 * This is a base class to simplify the creation of new IConQATNode
 * implementations. Key value pairs are handled here and a copy constructor to
 * ease cloning exists.
 * 
 * @author Benjamin Hummel
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: A0432A4CBEC6071B14C55717B466E98A
 */
public abstract class ConQATNodeBase implements IConQATNode {

	/** value storage. */
	private final Map<String, Object> values = new MemoryEfficientStringMap<Object>();

	/** Constructor, sets comparator to name comparator. */
	protected ConQATNodeBase() {
		setValue(NodeConstants.COMPARATOR, NameSorter.NameComparator
				.getInstance());
	}

	/** Copy constructor. */
	protected ConQATNodeBase(ConQATNodeBase node) throws DeepCloneException {
		Map<String, Object> source = node.values;
		for (String key : source.keySet()) {
			Object value = source.get(key);
			if (value instanceof FindingsList) {
				value = new FindingsList((FindingsList) value, this);
			} else {
				value = CloneUtils.cloneAsDeepAsPossible(value);
			}
			values.put(key, value);
		}
	}

	/** Returns the ID of this node. */
	@Override
	public String toString() {
		return getId();
	}

	/** {@inheritDoc} */
	@Override
	public Object getValue(String key) {
		return values.get(key);
	}

	/** {@inheritDoc} */
	@Override
	public void setValue(String key, Object value) {
		values.put(key, value);
	}

	/** Returns the keys which are used for this node. */
	public UnmodifiableCollection<String> getKeys() {
		return CollectionUtils.asUnmodifiable(values.keySet());
	}
}