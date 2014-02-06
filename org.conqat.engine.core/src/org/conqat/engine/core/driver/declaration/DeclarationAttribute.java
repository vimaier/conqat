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
package org.conqat.engine.core.driver.declaration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.conqat.engine.core.driver.error.BlockFileException;
import org.conqat.engine.core.driver.error.DriverException;
import org.conqat.engine.core.driver.error.EDriverExceptionType;
import org.conqat.engine.core.driver.error.EnvironmentException;
import org.conqat.engine.core.driver.error.ErrorLocation;
import org.conqat.engine.core.driver.error.IErrorLocatable;
import org.conqat.engine.core.driver.specification.BlockSpecificationAttribute;
import org.conqat.engine.core.driver.specification.BlockSpecificationOutput;
import org.conqat.engine.core.driver.specification.SpecificationAttribute;
import org.conqat.engine.core.driver.specification.SpecificationOutput;
import org.conqat.engine.core.driver.util.IInputReferencable;
import org.conqat.engine.core.driver.util.IInputReferencer;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.reflect.ClassType;
import org.conqat.lib.commons.reflect.ReflectionUtils;
import org.conqat.lib.commons.reflect.TypeConversionException;
import org.conqat.lib.commons.reflect.TypesNotMergableException;
import org.conqat.lib.commons.string.StringUtils;

/**
 * This is the attribute of a declaration parameter as found in the XML config
 * file or created as a default attribute. Basically, this is a key/value-pair
 * with the name of the attribute and its value. The latter can either be a
 * string (a so called immediate value), or a reference to the output of another
 * declaration or the input of a block specification.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 22AED54CE5D4E2B6D98ABD9B62D04219
 */
public class DeclarationAttribute implements IInputReferencer, IErrorLocatable {

	/** Name of the attribute */
	private final String name;

	/** Textual value, i.e. as given in the config file. */
	private final String valueText;

	/** The declaration parameter this belongs to. */
	private final DeclarationParameter parameter;

	/**
	 * The immediate value of this attribute. This is initialized in
	 * {@link #initConstant()} which is called from the ReferenceResolver. This
	 * may also stay null if the attribute holds a reference instead.
	 */
	private Object valueObject;

	/**
	 * The object referenced by this attribute. This is initialized in
	 * {@link #setReference(IInputReferencable)} which is called from the
	 * ReferenceResolver. This may also stay null if the attribute has an
	 * immediate value instead.
	 */
	private IInputReferencable reference;

	/** The specification for this attribute. */
	private SpecificationAttribute specificationAttribute;

	/**
	 * Stores the frozen type is this attribute is the source of a pipeline with
	 * fixed input type.
	 */
	private ClassType frozenType;

	/**
	 * The list of pipeline outputs for this attribute. This is populated during
	 * {@link #linkTo(SpecificationAttribute, Map)}.
	 */
	private final List<DeclarationOutput> pipelineOutputs = new ArrayList<DeclarationOutput>();

	/**
	 * Create a new declaration attribute.
	 * 
	 * @param name
	 *            name of the attribute.
	 * @param immediateValue
	 *            attribute value as found in the config file.
	 * @param parameter
	 *            the parameter this belongs to.
	 */
	public DeclarationAttribute(String name, String immediateValue,
			DeclarationParameter parameter) {
		this.name = name;
		valueText = immediateValue;
		this.parameter = parameter;
	}

	/**
	 * Create a new declaration attribute.
	 * 
	 * @param name
	 *            name of the attribute.
	 * @param value
	 *            value object used as immediate value, but not subject to
	 *            further parsin.
	 * @param parameter
	 *            the parameter this belongs to.
	 */
	public DeclarationAttribute(String name, Object value,
			DeclarationParameter parameter) {
		this.name = name;
		valueObject = value;
		this.parameter = parameter;

		// we have to store a non-null value here
		valueText = "OBJECT_IMMEDIATE";
	}

	/**
	 * Create a new attribute from a default value. This is used from
	 * {@link DeclarationParameter#linkTo(org.conqat.engine.core.driver.specification.ISpecificationParameter, Map)}
	 * to create any attributes missing from their default value. This does not
	 * call {@link #linkTo(SpecificationAttribute, Map)}!
	 * 
	 * @param specificationAttribute
	 *            the specification attribute to create the attribute from.
	 * @param parameter
	 *            the parameter this belongs to.
	 * @throws DriverException
	 *             if the default value could not be cloned.
	 */
	/* package */DeclarationAttribute(
			SpecificationAttribute specificationAttribute,
			DeclarationParameter parameter) throws DriverException {
		name = specificationAttribute.getName();
		valueText = "";
		valueObject = specificationAttribute.getDefaultValue();
		this.parameter = parameter;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Returns null, if attribute contains an immediate value.
	 */
	@Override
	public IInputReferencable getReference() {
		return reference;
	}

	/**
	 * Return true, if the value of this attribute is a reference (and no
	 * immediate value)
	 */
	public boolean isReference() {
		return (reference != null);
	}

	/**
	 * Returns true, if the value of this attribute is an immediate value (and
	 * no reference)
	 */
	public boolean isImmediateValue() {
		return !isReference();
	}

	/** Returns the name. */
	public String getName() {
		return name;
	}

	/** Returns the specification for this attribute. */
	public SpecificationAttribute getSpecificationAttribute() {
		return specificationAttribute;
	}

	/** {@inheritDoc} */
	@Override
	public ErrorLocation getErrorLocation() {
		return parameter.getErrorLocation();
	}

	/**
	 * Get the attribute's textual value. This is the value defined in the
	 * config file or an empty string for attributes created from default
	 * values.
	 */
	public String getValueText() {
		return valueText;
	}

	/** Returns the type of this attribute. */
	public ClassType getType() {
		if (frozenType != null) {
			return frozenType;
		}

		return specificationAttribute.getType();
	}

	/** Returns the parameter this attribute belongs to. */
	public DeclarationParameter getParameter() {
		return parameter;
	}

	/**
	 * Returns the list of outputs whose type depends on the input of this
	 * attribute (pipeline outputs).
	 */
	public List<DeclarationOutput> getPipelineOutputs() {
		return pipelineOutputs;
	}

	/**
	 * Freezes a pipeline attribute if it is fed with a concrete value. This
	 * causes other outputs to get a fixed value as well. This is called from
	 * DeclarationTypeChecker.
	 */
	public void freezePipeline(ClassType type) {
		// avoid freezing loops
		if (frozenType != null && type.isAssignableFrom(frozenType)) {
			return;
		}

		// must fulfill both existing type and frozen one
		try {
			frozenType = type.merge(getType());
		} catch (TypesNotMergableException e) {
			CCSMAssert.fail("This should not happen due to earlier checks!");
		}

		List<DeclarationOutput> outputs = new ArrayList<DeclarationOutput>(
				getPipelineOutputs());
		pipelineOutputs.clear();
		for (DeclarationOutput output : outputs) {
			output.freezePipeline(type);
		}
	}

	/**
	 * Link this declaration attribute to the given specification attribute.
	 * 
	 * @param specificationAttribute
	 *            the specification attribute to link to
	 * @param outputLookup
	 *            a mapping from specification outputs to declaration outputs to
	 *            complete the list of pipeline outputs.
	 */
	public void linkTo(SpecificationAttribute specificationAttribute,
			Map<SpecificationOutput, DeclarationOutput> outputLookup)
			throws EnvironmentException {

		if (this.specificationAttribute != null) {
			throw new IllegalStateException("may only be called once!");
		}

		this.specificationAttribute = specificationAttribute;

		if (valueObject != null
				&& !getType().isAssignableFrom(
						new ClassType(valueObject.getClass()))) {
			throw new EnvironmentException(
					EDriverExceptionType.INVALID_IMMEDIATE_OBJECT_TYPE,
					"Invalid valueObject: expected type: " + getType()
							+ ", actual type: " + valueObject.getClass(), this);
		}

		for (SpecificationOutput specOutput : specificationAttribute
				.getPipelineOutputs()) {
			DeclarationOutput declOutput = outputLookup.get(specOutput);
			pipelineOutputs.add(declOutput);
			declOutput.addPipelineAttribute(this);
		}
	}

	/**
	 * Deep-clone this attribute but replace a value of <code>oldValue</code>
	 * with <code>newValue</code>. This is used to implement
	 * {@link DeclarationParameter#cloneWithAttributeSubstitution(String, String)}
	 * .
	 * 
	 * @param oldValue
	 *            the value which should be replaced if used as a value.
	 * @param newValue
	 *            the value which should be instead for a value.
	 * @param newParameter
	 *            the parameter to use as new parent.
	 */
	/* package */DeclarationAttribute cloneWithAttributeSubstitution(
			String oldValue, String newValue, DeclarationParameter newParameter) {

		// We should not clone, if the reference has been set, because the
		// substitution might have affected the reference. Note that this is
		// not a problem with the valueObject, as the substitution is only used
		// for star references which never are constants (by the way a constant
		// should only be present for attributes constructed from default
		// values).
		if (reference != null) {
			throw new IllegalStateException(
					"May not clone attribute after reference has been assigned!");
		}

		String value = valueText;
		if (oldValue.equals(value)) {
			value = newValue;
		}

		DeclarationAttribute newAttribute = new DeclarationAttribute(name,
				value, newParameter);
		newAttribute.specificationAttribute = specificationAttribute;
		newAttribute.valueObject = valueObject;

		newAttribute.pipelineOutputs.addAll(pipelineOutputs);

		// This is only a problem when more than one parameter is created due to
		// star-operator expansion. But as parameters with pipeline attributes
		// must have multiplicity one, this does not occur (or raises and error
		// later).
		for (DeclarationOutput declOutput : pipelineOutputs) {
			declOutput.addPipelineAttribute(newAttribute);
		}

		return newAttribute;
	}

	/**
	 * Set the object referenced by this attribute. This is called from
	 * ReferenceResolver which in turn inspects the valueText.
	 */
	public void setReference(IInputReferencable reference) {
		checkNotInitialized();
		this.reference = reference;
	}

	/**
	 * Initialize the constant value from the valueText. This is called from
	 * ReferenceResolver.
	 */
	public void initConstant() throws BlockFileException {
		checkNotInitialized();

		try {
			ClassType type = specificationAttribute.getType();
			if (type.hasInterfaces()) {
				String message;
				if (StringUtils.isEmpty(valueText)) {
					message = "Empty attribute value for " + this + ".";
				} else {
					message = "Immediate value '" + valueText
							+ "' is not allowed for " + this
							+ ". Forgot @-sign?";
				}
				throw new BlockFileException(
						EDriverExceptionType.INCONSTRUCTIBLE_CLASS, message,
						this);
			}
			valueObject = ReflectionUtils.convertString(valueText,
					type.getBaseClass());

		} catch (TypeConversionException e) {
			throw new BlockFileException(
					EDriverExceptionType.ILLEGAL_IMMEDIATE_VALUE,
					"Illegal immediate value '" + valueText
							+ "' for attribute " + this, e, this);
		}

	}

	/**
	 * If this has has been initialized by either
	 * {@link #setReference(IInputReferencable)} or {@link #initConstant()} and
	 * exception is thrown.
	 */
	private void checkNotInitialized() {
		if (reference != null || valueObject != null) {
			throw new IllegalStateException(
					"May not both use ref and value object!");
		}
	}

	/**
	 * Returns the immediate value of this attribute. If this has a reference
	 * instead or the constant has not yet been initialized, null is returned.
	 */
	public Object getValueObject() {
		return valueObject;
	}

	/** {@inheritDoc} */
	@Override
	public BlockSpecificationOutput asBlockSpecificationOutput() {
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public DeclarationAttribute asDeclarationAttribute() {
		return this;
	}

	/**
	 * Returns the referenced block specification attribute or null if this has
	 * an immediate value or does not reference the attribute of the enclosing
	 * block specification (but rather the output of another declaration).
	 */
	public BlockSpecificationAttribute getReferencedBlockSpecAttr() {
		if (!isReference()) {
			return null;
		}
		return reference.asBlockSpecificationAttribute();
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return parameter.toString() + ": attribute '" + getName() + "'";
	}
}