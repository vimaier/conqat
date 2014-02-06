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
package org.conqat.engine.core.conqatdoc.compare;

import java.util.Comparator;

import org.conqat.engine.core.conqatdoc.SpecUtils;
import org.conqat.engine.core.driver.specification.ISpecification;

/**
 * Comparator for comparing specifications by their short name (ignoring case).
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 86FED0C92D4E8E636090689F3D243356
 */
public class SpecificationShortNameComparator implements
		Comparator<ISpecification> {

	/** Compare two specifications by their short name ignoring case. */
	@Override
	public int compare(ISpecification specification1,
			ISpecification specification2) {
		return SpecUtils.getShortName(specification1).compareToIgnoreCase(
				SpecUtils.getShortName(specification2));
	}
}