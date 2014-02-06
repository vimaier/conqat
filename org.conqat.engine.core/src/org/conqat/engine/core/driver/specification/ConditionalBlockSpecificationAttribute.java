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
package org.conqat.engine.core.driver.specification;

import org.conqat.engine.core.driver.error.BlockFileException;
import org.conqat.lib.commons.reflect.ClassType;

/**
 * The {@link BlockSpecificationAttribute} for the synthetic conditional
 * parameter.
 * 
 * @author $Author: juergens $
 * @version $Rev: 35194 $
 * @ConQAT.Rating GREEN Hash: 5DEA7A6D8FCAE5C0471D661431C2CF19
 */
public class ConditionalBlockSpecificationAttribute extends
		BlockSpecificationAttribute {

	/** Constructor. */
	public ConditionalBlockSpecificationAttribute(String name,
			BlockSpecificationParameter parameter) throws BlockFileException {
		super(name, parameter);

		refineType(getType());
	}

	/** {@inheritDoc} */
	@Override
	public ClassType getType() {
		return new ClassType(Boolean.class);
	}
}