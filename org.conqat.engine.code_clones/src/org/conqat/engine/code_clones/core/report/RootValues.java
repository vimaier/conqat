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
package org.conqat.engine.code_clones.core.report;

import org.conqat.engine.code_clones.core.KeyValueStoreBase;

/**
 * Key value store used to store values at the root of a clone report.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43764 $
 * @ConQAT.Rating GREEN Hash: 7CA9636573DF11E81A3ED329EC0DF878
 */
public class RootValues extends KeyValueStoreBase {

	/** Constructor. */
	public RootValues() {
		super(0);
	}
}
