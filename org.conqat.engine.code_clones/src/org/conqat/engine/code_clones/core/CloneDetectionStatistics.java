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
package org.conqat.engine.code_clones.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableIterator;
import org.conqat.lib.commons.collections.UnmodifiableList;

/**
 * Stores statistical data during clone detection.
 * 
 * @author juergens
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: 507D9AF90CE73D49FD668508D5D0982F
 */
public class CloneDetectionStatistics implements Iterable<String> {

	/** Map that stores all primitive statistic values */
	Map<String, Object> statisticsMap = new HashMap<String, Object>();

	/** Sets a statistic value */
	public void setStatistic(String name, Object value) {
		statisticsMap.put(name, value);
	}

	/** Gets a statistic value */
	public void setStatistic(ECloneDetectionStatistic name, Object value) {
		setStatistic(name.toString(), value);
	}

	/**
	 * Returns an iterator that iterates over a name-value paired list of the
	 * statistics. Useful to display the statistics via a TableLayout.
	 */
	@Override
	public UnmodifiableIterator<String> iterator() {
		return getStatistics().iterator();
	}

	/** Gets the sorted list of statistic names */
	public UnmodifiableList<String> getStatistics() {
		List<String> names = new ArrayList<String>(statisticsMap.keySet());
		return CollectionUtils.asUnmodifiable(CollectionUtils.sort(names));
	}

	/** Gets the value if the statistic with this name */
	public String get(String name) {
		return statisticsMap.get(name).toString();
	}

}