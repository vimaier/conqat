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
package org.conqat.engine.persistence.index;

import org.conqat.engine.persistence.store.IStore;
import org.conqat.engine.persistence.store.util.ConvenientStore;

/**
 * Abstract base class for indices. All index implementations are required to
 * have a public constructor that takes a single {@link IStore} parameter to
 * also allow reflective construction.
 * 
 * @author $Author: heineman $
 * @version $Rev: 37965 $
 * @ConQAT.Rating GREEN Hash: B2DB8D4FA7C6E7B585B8350AC051A2B5
 */
public abstract class IndexBase {

	/** The underlying store. */
	protected final ConvenientStore store;

	/** Constructor. */
	protected IndexBase(IStore store) {
		this.store = new ConvenientStore(store);
	}
}