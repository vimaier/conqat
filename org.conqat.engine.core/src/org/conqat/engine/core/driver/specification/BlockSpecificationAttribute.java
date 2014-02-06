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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.conqat.engine.core.driver.declaration.DeclarationOutput;
import org.conqat.engine.core.driver.error.BlockFileException;
import org.conqat.engine.core.driver.error.EDriverExceptionType;
import org.conqat.engine.core.driver.error.ErrorLocation;
import org.conqat.engine.core.driver.util.IDocumentable;
import org.conqat.engine.core.driver.util.IInputReferencable;
import org.conqat.lib.commons.reflect.ClassType;
import org.conqat.lib.commons.reflect.TypesNotMergableException;

/**
 * An attribute of a block specification parameter. Some of the data is taken
 * from the XML file, everything else is derived during the initialization of
 * the corresponding block specification.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: B54E4C0F932AA8029DC8503194BA6453
 */
public class BlockSpecificationAttribute extends SpecificationAttribute
		implements IDocumentable, IInputReferencable {

	/** The class type required for this attribute. */
	private ClassType type = new ClassType();

	/** The outputs for which this attribute is a pipeline. */
	private final Collection<BlockSpecificationOutput> pipelineOutputs = new HashSet<BlockSpecificationOutput>();

	/** The parameter this belongs to. */
	private final BlockSpecificationParameter parameter;

	/** Documentation for this element. */
	private String doc;

	/**
	 * Create a new attribute for a block specification parameter.
	 * 
	 * @param name
	 *            name of the attribute.
	 * @param parameter
	 *            the parameter this belongs to.
	 */
	public BlockSpecificationAttribute(String name,
			BlockSpecificationParameter parameter) {
		super(name);
		this.parameter = parameter;
	}

	/** {@inheritDoc} */
	@Override
	public ErrorLocation getErrorLocation() {
		return parameter.getErrorLocation();
	}

	/** {@inheritDoc} */
	@Override
	public String getDoc() {
		return doc;
	}

	/** {@inheritDoc} */
	@Override
	public void setDoc(String doc) {
		this.doc = doc;
	}

	/** {@inheritDoc} */
	@Override
	public Object getDefaultValue() {
		// attributes in block-specs _never_ have defaults (conflicts with type
		// inference)
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public List<SpecificationOutput> getPipelineOutputs() {
		return new ArrayList<SpecificationOutput>(pipelineOutputs);
	}

	/** {@inheritDoc} */
	@Override
	public boolean hasPipelineOutputs() {
		return !pipelineOutputs.isEmpty();
	}

	/** {@inheritDoc} */
	@Override
	public ClassType getType() {
		return type;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return parameter.toString() + ": attribute '" + getName() + "'";
	}

	/**
	 * Refines the type of this attribute using type merging. This is used by
	 * the {@link AttributeTypeInferer}.
	 */
	/* package */void refineType(ClassType newType) throws BlockFileException {
		try {
			type = type.merge(newType);
		} catch (TypesNotMergableException e) {
			throw new BlockFileException(
					EDriverExceptionType.INFERED_INCONSISTENT_TYPE,
					"Infered inconstructible type for " + this, this);
		}
	}

	/**
	 * Indicates that the type of an output of the enclosing specification
	 * pipeline-depends on this attribute.
	 */
	/* package */void addPipelineOutput(BlockSpecificationOutput output) {
		pipelineOutputs.add(output);
	}

	/** Returns the parameter this attribute belongs to. */
	public BlockSpecificationParameter getParameter() {
		return parameter;
	}

	/** {@inheritDoc} */
	@Override
	public BlockSpecificationAttribute asBlockSpecificationAttribute() {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public DeclarationOutput asDeclarationOutput() {
		return null;
	}

	/**
	 * Returns the complete name of this attribute as would be used to reference
	 * it (i.e. including the '@' and the parameter name). This is used to
	 * resolve references in {@link ReferenceResolver}.
	 */
	/* package */String getReferenceName() {
		return "@" + parameter.getName() + "." + getName();
	}

}