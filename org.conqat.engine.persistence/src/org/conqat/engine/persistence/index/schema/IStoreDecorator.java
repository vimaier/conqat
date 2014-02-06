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
package org.conqat.engine.persistence.index.schema;

import org.conqat.engine.persistence.store.IStore;

/**
 * Interface that can be used to decorate the stores created by this schema.
 * 
 * @author $Author: heineman $
 * @version $Rev: 38851 $
 * @ConQAT.Rating GREEN Hash: C04226BF71C3377E9988BBA2F83D2240
 */
public interface IStoreDecorator {

	/** Template method used to decorate a store. */
	IStore decorate(IStore store);
}