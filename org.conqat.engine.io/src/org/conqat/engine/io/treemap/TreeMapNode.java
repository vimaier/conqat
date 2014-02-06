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
package org.conqat.engine.io.treemap;

import org.conqat.engine.commons.node.DelegatingConQATNodeBase;
import org.conqat.engine.commons.node.IConQATNode;

/**
 * This is a marker node type to mark output which should be displayed as a
 * treemap in QLaunch.
 * 
 * @author hummelb
 * @author $Author: juergens $
 * @version $Rev: 35195 $
 * @ConQAT.Rating GREEN Hash: 0832D48157953DBCF81BB5E6EDD58AC0
 */
public class TreeMapNode extends DelegatingConQATNodeBase {

	/** Constructor. */
	public TreeMapNode(IConQATNode inner) {
		super(inner);
	}

	/** {@inheritDoc} */
	@Override
	public TreeMapNode deepClone() {
		return new TreeMapNode(inner);
	}
}