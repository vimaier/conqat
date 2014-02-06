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

import org.conqat.lib.commons.clone.CloneUtils;
import org.conqat.lib.commons.clone.DeepCloneException;

/**
 * Implementation of the {@link IValueProvider} interface.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: DFA1296EF387EA2FD53504B985676FC6
 */
public abstract class ValueProviderBase implements IValueProvider {

	/** Stores whether a value is available. */
	private boolean containsValue = false;

	/** The actual value */
	private Object value = null;

	/** The number of reads expected. */
	private int expectedReads = 0;

	/** {@inheritDoc} */
	@Override
	public void addConsumer() {
		if (containsValue) {
			throw new IllegalStateException(
					"May only add consumers before value assignment!");
		}
		expectedReads += 1;
	}

	/** {@inheritDoc} */
	@Override
	public void removeConsumer() {
		expectedReads -= 1;

		// allow memory to be freed
		if (expectedReads <= 0) {
			value = null;
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean hasValue() {
		return containsValue;
	}

	/** Assigns the value to be provided to this object. */
	protected void setValue(Object value) {
		if (containsValue) {
			throw new IllegalStateException("Should only be assigned once!");
		}

		containsValue = true;
		if (expectedReads > 0) {
			this.value = value;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws DeepCloneException
	 *             if the cloning process caused any problems.
	 */
	@Override
	public Object consumeValue() throws DeepCloneException {
		if (!containsValue) {
			throw new IllegalStateException(
					"This does not have a value to consume!");
		}

		if (expectedReads == 0) {
			throw new IllegalStateException("No more reads expected for "
					+ toString());
		}
		expectedReads -= 1;

		// clone if more reads are expected
		if (expectedReads > 0) {
			return CloneUtils.cloneAsDeepAsPossible(value);
		}

		// free value to support garbage collector
		Object result = value;
		value = null;
		return result;
	}
}