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
package org.conqat.engine.persistence.store;

/**
 * Exception thrown in case of storage timeouts.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 46954 $
 * @ConQAT.Rating GREEN Hash: 9816A77C817167C24F6AA321120C075B
 */
public class TimeoutException extends StorageException {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** Constructor. */
	public TimeoutException(String message) {
		super(message);
	}

	/** Constructor. */
	public TimeoutException(String message, Throwable t) {
		super(message, t);
	}

	/** Constructor. */
	public TimeoutException(Throwable t) {
		super(t);
	}
}