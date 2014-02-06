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
package org.conqat.engine.persistence.store.util;

import org.conqat.engine.persistence.store.IKeyValueCallback;
import org.conqat.engine.persistence.store.StorageException;

/**
 * Base class for {@link IKeyValueCallback} that can deal with exceptions by
 * storing them and allowing to throw them later on.
 * 
 * @author $Author: heineman $
 * @version $Rev: 37980 $
 * @ConQAT.Rating GREEN Hash: 5F88CADEAC0E816870216E3ADA4860D0
 */
public abstract class ExceptionHandlingKeyValueCallbackBase implements
		IKeyValueCallback {

	/** The exception (if any). */
	private StorageException exception;

	/** {@inheritDoc} */
	@Override
	public void callback(byte[] key, byte[] value) {
		// skip all further processing in case of an exception
		if (exception != null) {
			return;
		}

		try {
			callbackWithException(key, value);
		} catch (StorageException e) {
			exception = e;
		}
	}

	/** Template method for the callback that may throw exceptions. */
	protected abstract void callbackWithException(byte[] key, byte[] value)
			throws StorageException;

	/**
	 * Returns the exception caught during callback processing or null if none
	 * occurred.
	 */
	public StorageException getException() {
		return exception;
	}

	/**
	 * Throws the exception caught during callback processing. If no exception
	 * occurred during this phase, nothing happens.
	 */
	public void throwCaughtException() throws StorageException {
		if (exception != null) {
			throw exception;
		}
	}
}
