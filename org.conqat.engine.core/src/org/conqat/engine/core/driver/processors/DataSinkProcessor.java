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
package org.conqat.engine.core.driver.processors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.IConQATProcessor;
import org.conqat.engine.core.core.IConQATProcessorInfo;
import org.conqat.engine.core.driver.info.BlockInfo;

/**
 * A processor for storing the results of other processors. The data store is
 * static, so we can access its results later. Additionally the block info
 * provided to the processor is stored here.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: DFF5559B730CCAD74E7731175FEC958B
 */
@AConQATProcessor(description = "Data sink for testing purposes.")
public class DataSinkProcessor implements IConQATProcessor {

	/** Storage for data received. */
	private static Map<String, List<Object>> dataStore = new HashMap<String, List<Object>>();

	/** Store for the block info of the current execution. */
	public static BlockInfo blockInfo;

	/** Optional field; will be inserted during process(). */
	@AConQATFieldParameter(parameter = "set", attribute = "field", optional = true, description = "")
	public String field;

	/** Clear the store. */
	public static void resetDataStore() {
		dataStore = new HashMap<String, List<Object>>();
	}

	/** Access keyed data. */
	public static List<Object> accessData(String key) {
		return dataStore.get(key);
	}

	/** Append a value to the store. */
	@AConQATParameter(name = "append", description = "")
	public void appendResult(
			@AConQATAttribute(name = "name", description = "") String key,
			@AConQATAttribute(name = "value", description = "") Object value) {
		if (!dataStore.containsKey(key)) {
			dataStore.put(key, new ArrayList<Object>());
		}
		dataStore.get(key).add(value);
	}

	/** {@inheritDoc} */
	@Override
	public void init(IConQATProcessorInfo processorInfo) {
		blockInfo = processorInfo.getConfigurationInformation();
	}

	/** {@inheritDoc} */
	@Override
	public Object process() {
		// store contents of field
		if (field != null) {
			appendResult("field", field);
		}

		// always return null (useless)
		return null;
	}

}