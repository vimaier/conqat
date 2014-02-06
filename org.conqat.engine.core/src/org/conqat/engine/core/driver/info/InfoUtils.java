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
package org.conqat.engine.core.driver.info;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility code for dealing with the info classes.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: D9689E8FCC0B85CBFAF9B9F939A691C3
 */
public class InfoUtils {

	/**
	 * Returns for a given block info all processor infos (also from all sub
	 * blocks).
	 */
	public static List<ProcessorInfo> getProcessorInfos(BlockInfo blockInfo) {
		List<ProcessorInfo> result = new ArrayList<ProcessorInfo>();
		for (IInfo info : blockInfo.getChildren()) {
			if (info instanceof ProcessorInfo) {
				result.add((ProcessorInfo) info);
			} else if (info instanceof BlockInfo) {
				result.addAll(getProcessorInfos((BlockInfo) info));
			} else {
				throw new IllegalStateException(
						"Only two implementations of IInfo known.");
			}
		}
		return result;
	}

}