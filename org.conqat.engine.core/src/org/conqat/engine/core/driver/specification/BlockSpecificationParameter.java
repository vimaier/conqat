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

import org.conqat.engine.core.driver.error.BlockFileException;
import org.conqat.engine.core.driver.error.EDriverExceptionType;
import org.conqat.engine.core.driver.error.ErrorLocation;
import org.conqat.engine.core.driver.util.IDocumentable;
import org.conqat.engine.core.driver.util.Multiplicity;

/**
 * An parameter for a block specification. The information is partially taken
 * from the XML file, the rest is calculated during the initialization of the
 * block specification containing this parameter.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: C59761DDACE85BE097D24BF17113D98F
 */
public class BlockSpecificationParameter extends
		SpecificationParameterBase<BlockSpecificationAttribute> implements
		IDocumentable {

	/** The allowed multiplicity of this parameter. */
	private Multiplicity multiplicity = new Multiplicity();

	/** The block specification containing this parameter. */
	private final BlockSpecification specification;

	/** Documentation for this element. */
	private String doc;

	/**
	 * Create a new parameter for a block specification.
	 * 
	 * @param name
	 *            the name of this parameter.
	 * @param specification
	 *            the block specification this parameter will belong to.
	 */
	public BlockSpecificationParameter(String name,
			BlockSpecification specification) {
		super(name);
		this.specification = specification;
	}

	/** {@inheritDoc} */
	@Override
	public ErrorLocation getErrorLocation() {
		return specification.getErrorLocation();
	}

	/** {@inheritDoc} */
	@Override
	public Multiplicity getMultiplicity() {
		return multiplicity;
	}

	/**
	 * Refine the multiplicity of this parameter by intersecting it with another
	 * interval. This is used from the {@link AttributeTypeInferer}.
	 */
	/* package */void refineMultiplicity(Multiplicity newMultiplicity)
			throws BlockFileException {
		multiplicity = multiplicity.intersect(newMultiplicity);
		if (multiplicity.isEmpty() || multiplicity.getUpper() == 0) {
			throw new BlockFileException(
					EDriverExceptionType.EMPTY_INFERED_PARAMETER_INTERVAL,
					"Infered empty parameter interval for " + this, this);
		}
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
	protected BlockSpecificationAttribute[] allocateAttributeArray(int size) {
		return new BlockSpecificationAttribute[size];
	}

}