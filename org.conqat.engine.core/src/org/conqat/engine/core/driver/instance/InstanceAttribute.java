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

import org.conqat.engine.core.driver.declaration.DeclarationAttribute;
import org.conqat.engine.core.driver.specification.IConditionalParameter;
import org.conqat.lib.commons.clone.CloneUtils;
import org.conqat.lib.commons.clone.DeepCloneException;

/**
 * An attribute of an instance parameter.
 * 
 * @author Benjamin Hummel
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: D157E6E6D41B8A59CBA18A387BFED7A3
 */
public class InstanceAttribute extends ValueProviderBase {

	/** The declaration attribute this is based upon. */
	private final DeclarationAttribute declarationAttribute;

	/** The parameter this belongs to. */
	private final InstanceParameter instanceParameter;

	/** The value provider corresponding to the reference of this attribute. */
	private final IValueProvider referencedValueProvider;

	/**
	 * Create a new instance attribute.
	 * 
	 * @param declarationAttribute
	 *            the declaration this attribute is based upon.
	 * @param owningParameter
	 *            the parameter to which this attribute belongs.
	 * @param surroundingInstance
	 *            the block instance which surrounds the instance to which this
	 *            attribute belongs. This is used to resolve references (which
	 *            will always target elements in the scope of the given block
	 *            instance).
	 * @param referencedParameter
	 *            this is the parameter whose attribute should be used, if the
	 *            declaration attribute is a reference to a block specification
	 *            attribute. This parameter is necessary, since a single
	 *            specification attribute may be mapped to multiple instances,
	 *            so we have to know here which of these instance attributes to
	 *            reference. If either an immediate value or a reference to an
	 *            output is used, this may be null.
	 */
	public InstanceAttribute(DeclarationAttribute declarationAttribute,
			InstanceParameter owningParameter,
			BlockInstance surroundingInstance,
			InstanceParameter referencedParameter) {

		this.declarationAttribute = declarationAttribute;
		instanceParameter = owningParameter;

		if (declarationAttribute.isImmediateValue()) {
			referencedValueProvider = null;
		} else {
			if (declarationAttribute.getReference()
					.asBlockSpecificationAttribute() != null) {
				// this references the input of a block specification, which can
				// have multiple instances, so we pick the "right" attribute
				String attrName = declarationAttribute.getReference()
						.asBlockSpecificationAttribute().getName();
				referencedValueProvider = referencedParameter
						.getAttributeByName(attrName);
			} else {
				// this must be a DeclarationOutput (contract of
				// IInputReference), so link to its instance
				referencedValueProvider = surroundingInstance
						.getChildOutputInstance(declarationAttribute
								.getReference().asDeclarationOutput());
			}

			// the value provider needs no to know the number of consumers to
			// optimize cloning operations
			referencedValueProvider.addConsumer();
		}

		// allow additional consuming of the condition parameter
		if (owningParameter.getDeclaration().getName()
				.equals(IConditionalParameter.PARAMETER_NAME)) {
			addConsumer();
		}
	}

	/**
	 * Prepares the value to be provided by either using the immediate value or
	 * obtaining the result from a referenced value provider.
	 * 
	 * @throws DeepCloneException
	 *             if an immediate or default value could not be cloned.
	 */
	public void prepareValue() throws DeepCloneException {
		if (declarationAttribute.getValueObject() != null) {
			/*
			 * Clone the value object, as it might have resulted from a default
			 * value and thus could be the same for different processors. So to
			 * protect against changes at this object we clone.
			 */
			Object value = CloneUtils
					.cloneAsDeepAsPossible(declarationAttribute
							.getValueObject());
			setValue(value);
		} else if (referencedValueProvider != null
				&& referencedValueProvider.hasValue()) {
			setValue(referencedValueProvider.consumeValue());
		}
	}

	/** Returns the declaration attribute on which this is based. */
	public DeclarationAttribute getDeclarationAttribute() {
		return declarationAttribute;
	}

	/** Returns the object providing the value for this one. */
	public IValueProvider getValueProvider() {
		return referencedValueProvider;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return instanceParameter.toString() + ":"
				+ declarationAttribute.getName();
	}
}