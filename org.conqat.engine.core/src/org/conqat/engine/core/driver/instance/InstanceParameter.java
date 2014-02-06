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
package org.conqat.engine.core.driver.instance;

import java.util.LinkedHashMap;
import java.util.Map;

import org.conqat.engine.core.driver.declaration.DeclarationAttribute;
import org.conqat.engine.core.driver.declaration.DeclarationParameter;
import org.conqat.lib.commons.clone.DeepCloneException;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableCollection;

/**
 * A parameter of an instance.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: B1B03F93C2F8602D458F9FA262CBE77E
 */
public class InstanceParameter {

	/** The declaration parameter this is based on. */
	private final DeclarationParameter declarationParameter;

	/** The instance this parameter belongs to. */
	private final InstanceBase<?> instance;

	/** The ordered list of attributes, also accessible by name. */
	private final Map<String, InstanceAttribute> attributes = new LinkedHashMap<String, InstanceAttribute>();

	/**
	 * Creates a new instance parameter.
	 * 
	 * @param declarationParameter
	 *            the declaration parameter on which this is based.
	 * @param instance
	 *            the instance this parameter belongs to.
	 * @param referencedParameters
	 *            maps the attribute names to the block specification parameter
	 *            referenced by the attribute of this parameter. This is
	 *            required, because a single block specification parameter might
	 *            be mapped to multiple instance parameters and we have to know
	 *            which one to use. Attributes that do not reference instance
	 *            parameters are not required in this map.
	 */
	/* package */InstanceParameter(DeclarationParameter declarationParameter,
			InstanceBase<?> instance,
			Map<String, InstanceParameter> referencedParameters) {
		this.declarationParameter = declarationParameter;
		this.instance = instance;

		instantiateAttributes(referencedParameters);
	}

	/**
	 * Creates instances for all attributes.
	 * 
	 * @param referencedParameters
	 *            the same parameter as specified in
	 *            {@link #InstanceParameter(DeclarationParameter, InstanceBase, Map)}
	 *            .
	 */
	private void instantiateAttributes(
			Map<String, InstanceParameter> referencedParameters) {
		for (DeclarationAttribute declAttr : declarationParameter
				.getAttributes()) {
			attributes.put(declAttr.getName(),
					new InstanceAttribute(declAttr, this, instance.getParent(),
							referencedParameters.get(declAttr.getName())));
		}
	}

	/** Returns the underlying declaration parameter. */
	public DeclarationParameter getDeclarationParameter() {
		return declarationParameter;
	}

	/** Returns whether this parameter is synthetic. */
	public boolean isSynthetic() {
		return declarationParameter.isSynthetic();
	}

	/** Returns the list of all attributes for this parameter. */
	public UnmodifiableCollection<InstanceAttribute> getAttributes() {
		return CollectionUtils.asUnmodifiable(attributes.values());
	}

	/**
	 * Returns an array containing the values of all attributes in the correct
	 * order.
	 */
	/* package */Object[] getValueArray() throws DeepCloneException {
		Object[] result = new Object[attributes.size()];
		int index = 0;
		for (InstanceAttribute attr : attributes.values()) {
			result[index++] = attr.consumeValue();
		}
		return result;
	}

	/** Returns the declaration parameter this is based upon. */
	/* package */DeclarationParameter getDeclaration() {
		return declarationParameter;
	}

	/** Returns the attribute of the given name (or null if none exists). */
	/* package */InstanceAttribute getAttributeByName(String name) {
		return attributes.get(name);
	}

	/**
	 * Prepares the values of all attributes of this parameter. If any of them
	 * is not available, the remaining attributes are not prepared.
	 * 
	 * @return true if all values are available, false otherwise.
	 */
	/* package */boolean prepareAttributes() throws DeepCloneException {
		for (InstanceAttribute attr : getAttributes()) {
			attr.prepareValue();
			if (!attr.hasValue()) {
				return false;
			}
		}
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return instance.getName() + "." + declarationParameter.getName();
	}
}