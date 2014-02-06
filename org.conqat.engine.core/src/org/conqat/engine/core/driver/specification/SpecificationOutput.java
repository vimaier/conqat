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

import org.conqat.engine.core.driver.error.ErrorLocation;
import org.conqat.engine.core.driver.error.IErrorLocatable;
import org.conqat.engine.core.driver.util.IDocumented;
import org.conqat.lib.commons.reflect.ClassType;

/**
 * An output of a specification. It basically consists of a name and a type.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: A7E3AAE43007739738A6DFD12F53EA6D
 */
public abstract class SpecificationOutput implements IDocumented,
		IErrorLocatable {

	/** The name of the output. */
	private final String name;

	/** Location of the specification */
	private final ErrorLocation location;

	/** Create a new output. */
	/* package */SpecificationOutput(String name, ErrorLocation location) {
		this.name = name;
		this.location = location;
	}

	/** Returns the name of this output. */
	public String getName() {
		return name;
	}

	/** Returns the type provided by this output. */
	public abstract ClassType getType();

	/** {@inheritDoc} */
	@Override
	public ErrorLocation getErrorLocation() {
		return location;
	}
}