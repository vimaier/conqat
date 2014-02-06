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

import org.conqat.engine.core.driver.error.ErrorLocation;
import org.conqat.lib.commons.reflect.ClassType;

/**
 * An attribute of a processor specification parameter. All information is taken
 * from annotations.
 * <p>
 * Although this class is abstract, it does not end in "Base". The reason is
 * that this class is exposed externally, while the concrete implementations are
 * only visible within this package.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: FA28FDB296FC5BD75D21F1FDE6A69B78
 */
public abstract class ProcessorSpecificationAttribute extends
		SpecificationAttribute {

	/** The parameter this belongs to. */
	protected final ProcessorSpecificationParameter parameter;

	/** The type of this attribute. */
	private final ClassType attributeType;

	/**
	 * Create a new attribute for a processor specification.
	 * 
	 * @param name
	 *            the name of the attribute. belongs to.
	 * @param parameter
	 *            the parameter this belongs to.
	 */
	protected ProcessorSpecificationAttribute(String name,
			ClassType attributeType, ProcessorSpecificationParameter parameter) {
		super(name);
		this.parameter = parameter;
		this.attributeType = attributeType;
	}

	/** {@inheritDoc} */
	@Override
	public ErrorLocation getErrorLocation() {
		return parameter.getErrorLocation();
	}

	/** {@inheritDoc} */
	@Override
	public ClassType getType() {
		return attributeType;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return parameter.toString() + ": attribute '" + getName() + "'";
	}

}