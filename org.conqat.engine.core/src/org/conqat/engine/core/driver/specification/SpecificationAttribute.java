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

import java.util.List;

import org.conqat.engine.core.driver.error.DriverException;
import org.conqat.engine.core.driver.error.IErrorLocatable;
import org.conqat.engine.core.driver.util.IDocumented;
import org.conqat.lib.commons.reflect.ClassType;

/**
 * An attribute of a
 * {@link org.conqat.engine.core.driver.specification.ISpecificationParameter}.
 * Attributes basically consist of a name and an expected type, but also contain
 * additional information such as an (optional) default value, the outputs whose
 * types depend on this one (pipelines) and the relative index in the parameter.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 99C53E3EBDDA708F990C5BACD5D1FE00
 */
public abstract class SpecificationAttribute implements IDocumented,
		IErrorLocatable {

	/** The name of the parameter. */
	private final String name;

	/**
	 * Create a new attribute for a block specification.
	 * 
	 * @param name
	 *            name of the output channel.
	 */
	/* package */SpecificationAttribute(String name) {
		this.name = name;
	}

	/** Returns the name. */
	public String getName() {
		return name;
	}

	/** Returns the type required by this attribute. */
	public abstract ClassType getType();

	/**
	 * Returns an array of all outputs for which this attribute acts as a
	 * pipeline. So the type of these outputs actually is the same as that of
	 * the parameter used as input for this attribute.
	 */
	public abstract List<SpecificationOutput> getPipelineOutputs();

	/**
	 * Returns whether this attribute is a pipeline for any output. See
	 * {@link #getPipelineOutputs()} for details.
	 */
	public abstract boolean hasPipelineOutputs();

	/**
	 * Returns the default value of this attribute, or null if there is no
	 * default.
	 */
	public abstract Object getDefaultValue() throws DriverException;
}