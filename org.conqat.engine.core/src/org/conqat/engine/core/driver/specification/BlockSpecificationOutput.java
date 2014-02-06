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

import org.conqat.engine.core.driver.declaration.DeclarationAttribute;
import org.conqat.engine.core.driver.util.IDocumentable;
import org.conqat.engine.core.driver.util.IInputReferencable;
import org.conqat.engine.core.driver.util.IInputReferencer;
import org.conqat.lib.commons.reflect.ClassType;

/**
 * An output for a block specification. As the outputs of a block are internally
 * "wired" to other declarations and inputs, this also implements the
 * {@link IInputReferencer} interface.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 329F717DB5DF90875EF96F5B914B5C80
 */
public class BlockSpecificationOutput extends SpecificationOutput implements
		IDocumentable, IInputReferencer {

	/**
	 * The contents of the ref attribute (using at-notation). This is resolved
	 * to the actual reference during the initialization of the block
	 * specification.
	 */
	private final String referenceText;

	/**
	 * The reference this output takes the result from. This is derived from the
	 * referenceText during the initialization of the block specification.
	 */
	private IInputReferencable reference;

	/**
	 * The type of this output. This is left uninitialized and set during
	 * infering the output types when initializing the enclosing specification.
	 */
	private ClassType type;

	/** The specification this belongs to. */
	private final BlockSpecification specification;

	/** Documentation for this element. */
	private String doc;

	/**
	 * Create a new output for a block specification.
	 * 
	 * @param name
	 *            name of the output.
	 * @param referenceText
	 *            referenced output (using at-notation).
	 * @param specification
	 *            the enclosing specification
	 */
	public BlockSpecificationOutput(String name, String referenceText,
			BlockSpecification specification) {
		super(name, specification.getErrorLocation());
		this.referenceText = referenceText;
		this.specification = specification;
	}

	/** Returns the reference string. */
	public String getReferenceText() {
		return referenceText;
	}

	/** Returns the actual reference. */
	@Override
	public IInputReferencable getReference() {
		if (reference == null) {
			throw new IllegalStateException(
					"This may not be called before initialization!");
		}
		return reference;
	}

	/**
	 * Set the "real" output referenced by this output. This happens during
	 * initialization ({@link ReferenceResolver}).
	 */
	/* package */void setReference(IInputReferencable reference) {
		if (this.reference != null) {
			throw new IllegalStateException("This may not be set twice!");
		}
		this.reference = reference;
	}

	/** {@inheritDoc} */
	@Override
	public ClassType getType() {
		if (type == null) {
			throw new IllegalStateException(
					"This may not be called before initialization!");
		}
		return type;
	}

	/**
	 * Set the type of this output. This happens during initialization (
	 * {@link OutputTypeInferer}).
	 */
	/* package */void setType(ClassType newType) {
		if (type != null) {
			throw new IllegalStateException("This may not be set twice!");
		}
		type = newType;
	}

	/** {@inheritDoc} */
	@Override
	public BlockSpecificationOutput asBlockSpecificationOutput() {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public DeclarationAttribute asDeclarationAttribute() {
		return null;
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
	public String toString() {
		return specification.toString() + ": output " + getName();
	}
}