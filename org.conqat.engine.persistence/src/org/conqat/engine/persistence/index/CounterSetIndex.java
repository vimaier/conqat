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
package org.conqat.engine.persistence.index;

import org.conqat.engine.persistence.store.IStore;
import org.conqat.lib.commons.collections.CounterSet;

/**
 * An index holding {@link CounterSet}s.
 * 
 * @author $Author: $
 * @version $Rev: $
 * @ConQAT.Rating GREEN Hash: 5C8B1C03D2753574320E86170F6BCD67
 */
@SuppressWarnings("rawtypes")
public class CounterSetIndex extends
		SerializationBasedValueIndexBase<CounterSet> {

	/** Constructor */
	public CounterSetIndex(IStore store) {
		super(store);
	}
}
