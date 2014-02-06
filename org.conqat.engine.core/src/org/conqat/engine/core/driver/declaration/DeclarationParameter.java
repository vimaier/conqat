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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.conqat.engine.core.driver.BlockFileReader;
import org.conqat.engine.core.driver.error.BlockFileException;
import org.conqat.engine.core.driver.error.DriverException;
import org.conqat.engine.core.driver.error.EDriverExceptionType;
import org.conqat.engine.core.driver.error.ErrorLocation;
import org.conqat.engine.core.driver.error.IErrorLocatable;
import org.conqat.engine.core.driver.specification.BlockSpecificationAttribute;
import org.conqat.engine.core.driver.specification.BlockSpecificationParameter;
import org.conqat.engine.core.driver.specification.ISpecificationParameter;
import org.conqat.engine.core.driver.specification.SpecificationAttribute;
import org.conqat.engine.core.driver.specification.SpecificationOutput;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableList;

/**
 * This is the parameter for a declaration and represents a parameter as defined
 * in the config file.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: FABD97D0D3055E0EB156E4535824A898
 */
public class DeclarationParameter implements IErrorLocatable {

	/** Name of the parameter as specified in the config file. */
	private final String name;

	/** The declaration this parameter belongs to. */
	private final IDeclaration declaration;

	/**
	 * List of attributes for this parameter. Before referencing the
	 * specification information this is kept in the order used in the XML file.
	 * After that (and adding missing optional attributes) the order is the same
	 * as that of the specification attributes.
	 */
	private final List<DeclarationAttribute> attributes = new ArrayList<DeclarationAttribute>();

	/** The specification for this parameter. */
	private ISpecificationParameter specificationParameter;

	/**
	 * Create a new parameter.
	 * 
	 * @param name
	 *            name of the parameter
	 * @param declaration
	 *            the declaration this belongs to.
	 */
	public DeclarationParameter(String name, IDeclaration declaration) {
		this.name = name;
		this.declaration = declaration;
	}

	/** Get the name of this parameter. */
	public String getName() {
		return name;
	}

	/** {@inheritDoc} */
	@Override
	public ErrorLocation getErrorLocation() {
		return declaration.getErrorLocation();
	}

	/** Get list of attributes. */
	public UnmodifiableList<DeclarationAttribute> getAttributes() {
		return CollectionUtils.asUnmodifiable(attributes);
	}

	/**
	 * Returns the referenced parameters of the surrounding block specification
	 * as a mapping from (declaration) attribute name to referenced block
	 * specification parameter. Attributes that do not reference a block input
	 * (such as immediate values) are not included in the returned map.
	 */
	public Map<String, BlockSpecificationParameter> getReferencedSpecificationParameters() {
		Map<String, BlockSpecificationParameter> result = new HashMap<String, BlockSpecificationParameter>();
		for (DeclarationAttribute attr : getAttributes()) {
			BlockSpecificationAttribute specAttr = attr
					.getReferencedBlockSpecAttr();
			if (specAttr != null) {
				result.put(attr.getName(), specAttr.getParameter());
			}
		}
		return result;
	}

	/** Returns the specification parameter referenced by this object. */
	public ISpecificationParameter getSpecificationParameter() {
		return specificationParameter;
	}

	/**
	 * Link this declaration to the provided specification.
	 * 
	 * @param specificationParameter
	 *            the specification to link to.
	 * @param outputLookup
	 *            a mapping from outputs of the specification to outputs of the
	 *            declaration, which is used to setup the attributes.
	 */
	public void linkTo(ISpecificationParameter specificationParameter,
			Map<SpecificationOutput, DeclarationOutput> outputLookup)
			throws DriverException {

		if (this.specificationParameter != null) {
			throw new IllegalStateException("may only be called once!");
		}

		this.specificationParameter = specificationParameter;

		Map<String, DeclarationAttribute> knownAttributes = makeAttributeMap();
		orderAndCompleteAttributes(knownAttributes, outputLookup);

		if (!knownAttributes.isEmpty()) {
			throw new BlockFileException(
					EDriverExceptionType.UNSUPPORTED_ATTRIBUTE, "Parameter '"
							+ this
							+ "' does not support attribute '"
							+ knownAttributes.values().iterator().next()
									.getName() + "'.", this);
		}
	}

	/** Returns a pretty map from all attributes indexed by attribute name. */
	private Map<String, DeclarationAttribute> makeAttributeMap() {
		Map<String, DeclarationAttribute> knownAttributes = new HashMap<String, DeclarationAttribute>();
		for (DeclarationAttribute attribute : attributes) {
			knownAttributes.put(attribute.getName(), attribute);
		}
		return knownAttributes;
	}

	/**
	 * Recreates the attributes list using the order implied by the
	 * specification of this parameter. If an attribute is missing we try to
	 * create one using the default value.
	 * 
	 * @param knownAttributes
	 *            a mapping from attribute names to those attributes already
	 *            known. The attributes used by this method are removed from the
	 *            map.
	 * @param outputLookup
	 *            a mapping from outputs of the specification to outputs of the
	 *            declaration, which is used to setup the attributes.
	 */
	private void orderAndCompleteAttributes(
			Map<String, DeclarationAttribute> knownAttributes,
			Map<SpecificationOutput, DeclarationOutput> outputLookup)
			throws DriverException {

		attributes.clear();
		for (SpecificationAttribute specAttribute : specificationParameter
				.getAttributes()) {

			DeclarationAttribute declAttribute = null;
			if (knownAttributes.containsKey(specAttribute.getName())) {
				declAttribute = knownAttributes.get(specAttribute.getName());
				knownAttributes.remove(specAttribute.getName());
			} else if (specAttribute.getDefaultValue() != null) {
				declAttribute = new DeclarationAttribute(specAttribute, this);
			} else {
				throw new BlockFileException(
						EDriverExceptionType.MISSING_ATTRIBUTE, "Attribute '"
								+ specAttribute.getName()
								+ "' is missing at parameter '" + this + "'.",
						this);
			}

			attributes.add(declAttribute);
			declAttribute.linkTo(specAttribute, outputLookup);
		}
	}

	/**
	 * Deep-clone this parameter but replace in all attributes a value of
	 * <code>oldValue</code> with <code>newValue</code>. This is used by the
	 * StarReferenceResolver.
	 */
	public DeclarationParameter cloneWithAttributeSubstitution(String oldValue,
			String newValue) {

		DeclarationParameter newParameter = new DeclarationParameter(name,
				declaration);
		newParameter.specificationParameter = specificationParameter;

		for (DeclarationAttribute attribute : attributes) {
			newParameter.addAttribute(attribute.cloneWithAttributeSubstitution(
					oldValue, newValue, newParameter));
		}

		return newParameter;
	}

	/**
	 * Add an attribute. This is used by the {@link BlockFileReader} to add
	 * attributes. As XML does not allow duplicate attributes, we do not have to
	 * check anything here.
	 */
	public void addAttribute(DeclarationAttribute attribute) {
		attributes.add(attribute);
	}

	/**
	 * @return The declaration this parameter belongs to.
	 */
	public IDeclaration getDeclaration() {
		return declaration;
	}

	/** Returns whether this parameter is synthetic. */
	public boolean isSynthetic() {
		return specificationParameter.isSynthetic();
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return declaration.toString() + ": parameter '" + name + "'";
	}

}