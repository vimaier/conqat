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
package org.conqat.engine.commons.keys;

import org.conqat.engine.core.core.AConQATKey;

/**
 * Interface defining a key for a dependency list.
 * 
 * @author $Author: streitel $
 * @version $Rev: 46300 $
 * @ConQAT.Rating YELLOW Hash: E6A7BADF958AB509DC749AADAA39A3D9
 */
public interface IDependencyListKey {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Key under which the dependencies are stored", type = "java.util.List<String>")
	public static final String DEPENDENCY_LIST_KEY = "Dependency List";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Key under which the supertype dependencies are stored", type = "java.util.List<String>")
	public static final String SUPER_TYPE_LIST_KEY = "Supertype List";

}
