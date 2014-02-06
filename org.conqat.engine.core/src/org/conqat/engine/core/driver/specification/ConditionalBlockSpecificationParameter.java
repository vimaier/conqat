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
import org.conqat.engine.core.driver.util.Multiplicity;

/**
 * The {@link BlockSpecificationParameter} for the synthetic conditional
 * parameter.
 * 
 * @author $Author: juergens $
 * @version $Rev: 35194 $
 * @ConQAT.Rating GREEN Hash: A373D98FE456F63F00ECF2D01C083606
 */
public class ConditionalBlockSpecificationParameter extends
		BlockSpecificationParameter implements IConditionalParameter {

	/** Constructor. */
	public ConditionalBlockSpecificationParameter(
			BlockSpecification specification) throws BlockFileException {
		super(PARAMETER_NAME, specification);

		addAttribute(new ConditionalBlockSpecificationAttribute(
				VALUE_ATTRIBUTE, this));
		addAttribute(new ConditionalBlockSpecificationAttribute(
				INVERT_ATTRIBUTE, this));
	}

	/** {@inheritDoc} */
	@Override
	public Multiplicity getMultiplicity() {
		return new Multiplicity(1, 1);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isSynthetic() {
		return true;
	}
}