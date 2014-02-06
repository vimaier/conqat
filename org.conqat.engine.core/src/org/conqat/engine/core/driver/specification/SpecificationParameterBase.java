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

import java.util.LinkedHashMap;
import java.util.Map;

import org.conqat.engine.core.driver.error.BlockFileException;
import org.conqat.engine.core.driver.error.EDriverExceptionType;

/**
 * Base class for specification parameters. This class takes the type of
 * attribute as a generic parameter, in order to simplify type safety in its
 * subclasses.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: D49F4EBEA8FECE29D3DD4C19C78B78AB
 */
public abstract class SpecificationParameterBase<T extends SpecificationAttribute>
		implements ISpecificationParameter {

	/** The name of this parameter. */
	private final String name;

	/**
	 * The attributes for this parameter accessible by name and ordered in the
	 * sequence they occur. (For blocks: the sequence they occur in the xml
	 * file, for processors their sequence in java code.)
	 */
	private final Map<String, T> attributes = new LinkedHashMap<String, T>();

	/** Create a new instance. */
	/* package */SpecificationParameterBase(String name) {
		this.name = name;
	}

	/** Adds an attribute to this parameter. */
	public void addAttribute(T attr) throws BlockFileException {
		if (attributes.containsKey(attr.getName())) {
			throw new BlockFileException(
					EDriverExceptionType.DUPLICATE_ATTRIBUTE_NAME,
					"Duplicate attribute name '" + attr.getName() + " at "
							+ this, this);
		}
		attributes.put(attr.getName(), attr);
	}

	/** {@inheritDoc} */
	@Override
	public String getName() {
		return name;
	}

	/** {@inheritDoc} */
	@Override
	public T[] getAttributes() {
		return attributes.values().toArray(
				allocateAttributeArray(attributes.size()));
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "parameter '" + getName() + "'";
	}

	/**
	 * Returns a new array of the specification attribute type used as parameter
	 * for this class.
	 * <p>
	 * This is used to construct the array needed as a return value for
	 * {@link #getAttributes()}. Since it is not possible to construct arrays
	 * from generic type attributes, this template method delegates array
	 * instantiation to subclasses.
	 * 
	 * @param size
	 *            the required size of the array.
	 */
	protected abstract T[] allocateAttributeArray(int size);

	/**
	 * {@inheritDoc}.
	 * <p>
	 * Default implementation returns false.
	 */
	@Override
	public boolean isSynthetic() {
		return false;
	}
}