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
package org.conqat.engine.core.driver.instance;

import org.conqat.lib.commons.clone.DeepCloneException;

/**
 * Interface for objects providing a value. In this context these are either
 * {@link InstanceOutput}s or {@link InstanceAttribute}s.
 * <p>
 * To avoid interference of different consumers in their usage of the returned
 * value object, the value object is usually cloned before returning it.
 * <p>
 * However, as this is potentially expensive, the cloning is only performed if
 * we know for sure that the value will be requested again. Thus, each potential
 * consumer has to register during its initialization, so the exact number of
 * {@link #consumeValue()} calls is known at execution time (and thus the
 * necessary number of clone operations).
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 4C5AB0F97F3F486FB40E1EEBBDD52319
 */
public interface IValueProvider {

	/**
	 * Returns whether a value is available from this provider. False can mean
	 * either that the associated object has not yet been executed or that there
	 * were errors during execution preventing the generation of the value.
	 */
	public boolean hasValue();

	/**
	 * Increases the consumer counter by one. Each call to this method allows
	 * exactly one call to the {@link #consumeValue()} method later. This method
	 * must be called before the actual value was calculated.
	 */
	public void addConsumer();

	/**
	 * Decreases the consumer counter by one. This can be used instead of
	 * {@link #consumeValue()}.
	 */
	public void removeConsumer();

	/**
	 * Returns the value (or a clone of the value) available from this provider,
	 * or null if no value is present (also see {@link #hasValue()}). A null
	 * value could also indicate that the provided value is null (thus the
	 * underlying processor returned null for some reason). This method may only
	 * be called as often as {@link #addConsumer()} was called before (taking
	 * calls to {@link #removeConsumer()} into account, too).
	 * 
	 * @throws DeepCloneException
	 *             if the cloning process caused any problems.
	 */
	public Object consumeValue() throws DeepCloneException;
}