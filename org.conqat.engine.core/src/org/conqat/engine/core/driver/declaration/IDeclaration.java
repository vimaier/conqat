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

import java.util.List;

import org.conqat.engine.core.driver.error.DriverException;
import org.conqat.engine.core.driver.error.IErrorLocatable;
import org.conqat.engine.core.driver.instance.BlockInstance;
import org.conqat.engine.core.driver.instance.IInstance;
import org.conqat.engine.core.driver.specification.BlockSpecification;
import org.conqat.engine.core.driver.specification.ISpecification;

/**
 * Interface of declarations, which correspond to the block and processor
 * statements in XML. They reference specifications and contain details on which
 * parameters to set.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: CCD51D9B9C3B17147588EC3D3E509BAE
 */
public interface IDeclaration extends IErrorLocatable {

	/** Returns the name of the specification as specified in the config file. */
	public String getSpecificationName();

	/** Returns the name of this declaration. */
	public String getName();

	/** Returns the underlying specification. */
	public ISpecification getSpecification();

	/**
	 * This method links the declaration with the specification. It consists of
	 * the following steps:
	 * <ul>
	 * <li>Resolve the specification</li>
	 * <li>Build references from parameters and attributes to those defined in
	 * the specification.</li>
	 * <li>Include the implicitly declared outputs (from the specification).</li>
	 * <li>Check if all (declared) parameters contain all required attributes
	 * and add all omitted attributes with default values.</li>
	 * </ul>
	 */
	public void referenceSpecification() throws DriverException;

	/**
	 * Sets the list of parameters for this declaration. Any existing parameters
	 * will be discarded. No consistency checking is applied here!
	 * 
	 * @param parameters
	 *            the parameters to set.
	 */
	public void setParameters(List<DeclarationParameter> parameters);

	/** Returns the list of all parameters in the order found in the XML file. */
	public List<DeclarationParameter> getParameters();

	/**
	 * Returns the list of all non-synthetic parameters in the order found in
	 * the XML file.
	 */
	public List<DeclarationParameter> getNonSyntheticParameters();

	/** Returns the (ordered) list of outputs of this declaration. */
	public List<DeclarationOutput> getOutputs();

	/**
	 * Returns the specification which surrounds this declaration (i.e. its
	 * "scope").
	 */
	public BlockSpecification getSurroundingSpecification();

	/**
	 * Create a new instance based on this declaration. The instance is embedded
	 * into the given parent (block) instance, which also has instances of all
	 * elements which are referenced by this declaration.
	 * 
	 * @param parentInstance
	 *            the instance used as parent, or null if this is the root of
	 *            the instance tree.
	 * @return the created instance.
	 */
	public IInstance instantiate(BlockInstance parentInstance);
}