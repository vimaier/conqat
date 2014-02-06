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
package org.conqat.engine.resource.util;

import java.util.Comparator;

import org.conqat.engine.resource.IElement;

/**
 * Comparator for comparing elements by uniform path.
 * 
 * @author $Author: juergens $
 * @version $Rev: 35198 $
 * @ConQAT.Rating GREEN Hash: 17F488A51E43B0518C01401800166AB2
 */
public class ElementUniformPathComparator implements Comparator<IElement> {

	/** {@inheritDoc} */
	@Override
	public int compare(IElement e1, IElement e2) {
		return e1.getUniformPath().compareTo(e2.getUniformPath());
	}
}