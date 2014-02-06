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
package org.conqat.engine.code_clones.index.store.mem;

import java.io.Serializable;
import java.util.HashMap;

import org.conqat.engine.code_clones.index.store.ICloneIndexStore;

/**
 * Base class for clone index stores residing in memory. This class deals with
 * options handling and provides an empty close operation.
 * 
 * @author hummelb
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: 14730207B990411738B1D72243FFB5DC
 */
public abstract class MemoryStoreBase implements ICloneIndexStore {

	/** Map for storing options. */
	protected final HashMap<String, Serializable> options = new HashMap<String, Serializable>();

	/** {@inheritDoc} */
	@Override
	public Serializable getOption(String key) {
		return options.get(key);
	}

	/** {@inheritDoc} */
	@Override
	public void setOption(String key, Serializable value) {
		options.put(key, value);
	}

	/** {@inheritDoc} */
	@Override
	public void close() {
		// does nothing
	}
}