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
package org.conqat.engine.core.driver.util;

import org.conqat.engine.core.driver.declaration.DeclarationAttribute;
import org.conqat.engine.core.driver.specification.BlockSpecificationOutput;

/**
 * Interface for marking objects that reference generated data (outputs). As
 * these are either {@link BlockSpecificationOutput}s or
 * {@link DeclarationAttribute}s, there are two methods returning this object
 * casted to the corresponding class. Exacly one of these methods must return
 * <code>null</code>.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 31170064295A3C7F8C937406E8384425
 */
public interface IInputReferencer {

	/** Returns the object referenced from this one. */
	public IInputReferencable getReference();

	/**
	 * Returns this object cast to a {@link DeclarationAttribute} or
	 * <code>null</code> if this is not possible. If this returns
	 * <code>null</code>, then {@link #asBlockSpecificationOutput()} must return
	 * a non-<code>null</code> value.
	 */
	public DeclarationAttribute asDeclarationAttribute();

	/**
	 * Returns this object cast to a {@link BlockSpecificationOutput} or
	 * <code>null</code> if this is not possible. If this returns
	 * <code>null</code>, then {@link #asDeclarationAttribute()} must return a
	 * non-<code>null</code> value.
	 */
	public BlockSpecificationOutput asBlockSpecificationOutput();
}