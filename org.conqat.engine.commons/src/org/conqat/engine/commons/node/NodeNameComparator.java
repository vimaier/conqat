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
package org.conqat.engine.commons.node;

import java.util.Comparator;

/**
 * Comparator for comparing elements by node names.
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 45761 $
 * @ConQAT.Rating YELLOW Hash: 9311806373DD67A62017F44011F1EBCE
 */
public class NodeNameComparator implements Comparator<IConQATNode> {

	/** {@inheritDoc} */
	@Override
	public int compare(IConQATNode n1, IConQATNode n2) {
		return n1.getName().compareTo(n2.getName());
	}
}