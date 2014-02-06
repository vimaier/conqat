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
package org.conqat.engine.core.driver.info;

import org.conqat.engine.core.driver.instance.InstanceAttribute;
import org.conqat.lib.commons.reflect.ClassType;

/**
 * Info on {@link InstanceAttribute}s.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 855FE588A53D4A2109A77FC5DAA68943
 */
public class InfoAttribute extends InfoRefNode {

	/** The parameter this belongs to. */
	private final InfoParameter parameter;

	/** The underlying {@link InstanceAttribute} */
	private final InstanceAttribute instanceAttribute;

	/** Create a new info attribute. */
	/* package */InfoAttribute(InstanceAttribute instanceAttribute,
			InfoParameter parameter) {
		this.instanceAttribute = instanceAttribute;
		this.parameter = parameter;
	}

	/** Returns the instance attribute wrapped by this info. */
	/* package */InstanceAttribute getInstanceAttribute() {
		return instanceAttribute;
	}

	/** Returns the parameter this belongs to. */
	public InfoParameter getParameter() {
		return parameter;
	}

	/** Returns the name of this attribute. */
	public String getName() {
		return instanceAttribute.getDeclarationAttribute().getName();
	}

	/** Returns the type of this attribute. */
	public ClassType getType() {
		return instanceAttribute.getDeclarationAttribute().getType();
	}

	/**
	 * Returns true if this attribute uses an immediate value instead of a
	 * reference.
	 */
	public boolean isImmediateValue() {
		return instanceAttribute.getDeclarationAttribute().getValueObject() != null;
	}

	/**
	 * Returns the immediate value used in the configuration (properties and
	 * default values are resolved before), or null if this has no immediate
	 * value.
	 */
	public String getImmediateValue() {
		Object valueObject = instanceAttribute.getDeclarationAttribute()
				.getValueObject();
		if (valueObject == null) {
			return null;
		}
		return valueObject.toString();
	}
}