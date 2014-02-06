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
package org.conqat.engine.io;

import java.util.HashMap;
import java.util.Map;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.reflect.ReflectionUtils;
import org.conqat.lib.commons.reflect.TypeConversionException;

/**
 * Converts strings into typed objects based on name-type associations.
 * 
 * @author juergens
 * @author $Author: juergens $
 * @version $Rev: 35195 $
 * @ConQAT.Rating GREEN Hash: DDA68816D5595D215A7BA08E99089ADB
 */
/* package */class TypeConverter {

	/** Maps from name to type */
	private final Map<String, Class<?>> types = new HashMap<String, Class<?>>();

	/** Default type */
	private Class<?> defaultType = String.class;

	/** Adds an association between a name and a type */
	public void addTypeAssociation(String name, Class<?> type) {
		types.put(name, type);
	}

	/**
	 * Casts the value string to the type associated with the name
	 * 
	 * @param name
	 *            Name of this value
	 * @param value
	 *            Value that is to be converted
	 */
	Object typedValueFor(String name, String value) throws ConQATException {
		Class<?> type = defaultType;
		if (types.containsKey(name)) {
			type = types.get(name);
		}

		try {
			return ReflectionUtils.convertString(value, type);
		} catch (TypeConversionException e) {
			throw new ConQATException("Could not convert " + value
					+ " to type " + type.getName() + ": " + e.getMessage());
		}
	}

	/** Sets the default type. */
	public void setDefaultType(Class<?> defaultType) {
		this.defaultType = defaultType;
	}
}