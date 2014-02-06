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

import org.conqat.engine.core.driver.declaration.DeclarationOutput;
import org.conqat.engine.core.driver.specification.BlockSpecificationAttribute;
import org.conqat.lib.commons.reflect.ClassType;

/**
 * Interface for marking objects that can be referenced by an input (parameter
 * attribute). As these are either {@link BlockSpecificationAttribute}s or
 * {@link DeclarationOutput}s, there are two methods returning this object
 * casted to the corresponding class. Exacly one of these methods must return
 * <code>null</code>.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 00199811CF6ECD445847D3404800B6EF
 */
public interface IInputReferencable {

	/** Returns the type of the object provided by this referencable object. */
	public ClassType getType();

	/**
	 * Returns this object cast to a {@link DeclarationOutput} or
	 * <code>null</code> if this is not possible. If this returns
	 * <code>null</code>, then {@link #asBlockSpecificationAttribute()} must
	 * return a non-<code>null</code> value.
	 */
	public DeclarationOutput asDeclarationOutput();

	/**
	 * Returns this object cast to a {@link BlockSpecificationAttribute} or
	 * <code>null</code> if this is not possible. If this returns
	 * <code>null</code>, then {@link #asDeclarationOutput()} must return a non-
	 * <code>null</code> value.
	 */
	public BlockSpecificationAttribute asBlockSpecificationAttribute();
}