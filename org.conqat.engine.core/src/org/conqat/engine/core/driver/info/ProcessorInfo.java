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

import java.util.EnumMap;

import org.conqat.engine.core.driver.instance.EInstanceState;
import org.conqat.engine.core.driver.instance.ProcessorInstance;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableMap;

/**
 * Information on {@link ProcessorInstance}s.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: F592879DF35366D541C7C60E73538890
 */
public class ProcessorInfo extends InfoBase {

	/** The underlying {@link ProcessorInstance} */
	private final ProcessorInstance instance;

	/** Create a new processor info. */
	/* package */ProcessorInfo(ProcessorInstance instance, BlockInfo parent) {
		super(instance, parent);
		this.instance = instance;
	}

	/** {@inheritDoc} */
	@Override
	public UnmodifiableMap<EInstanceState, Integer> getProcessorStateDistribution() {
		EnumMap<EInstanceState, Integer> result = new EnumMap<EInstanceState, Integer>(
				EInstanceState.class);
		result.put(getState(), 1);
		return CollectionUtils.asUnmodifiable(result);
	}

	/** {@inheritDoc} */
	@Override
	public EInstanceState getState() {
		return instance.getState();
	}

	/** {@inheritDoc} */
	@Override
	public long getExecutionTime() {
		return instance.getExecutionTime();
	}
}