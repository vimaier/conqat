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
package org.conqat.engine.persistence.rollback;

import org.conqat.engine.persistence.store.StorageException;

/**
 * Interface for indexes that implement their own rollback strategy.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46880 $
 * @ConQAT.Rating GREEN Hash: 5AA8952482BC87CFA4B978A126C61F13
 */
public interface IRollbackableIndex {

	/**
	 * Resets the content of the underlying state back to the version of the
	 * given timestamp.
	 */
	void performRollback(long timestamp) throws StorageException;
}
