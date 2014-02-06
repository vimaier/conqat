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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.conqat.engine.core.driver.error.BlockFileException;
import org.conqat.engine.core.driver.error.EDriverExceptionType;

/**
 * This is a common base class for processor and block specifications,
 * implementing functionality used by both of them. We use package visibility
 * here, as external classes do not need to access this one.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 53E59977751904C6B138F31C04AB34AB
 */
/* package */abstract class SpecificationBase<T extends ISpecificationParameter>
		implements ISpecification {

	/** The name of this specification. */
	private final String name;

	/** The parameters of this specification. */
	private final Map<String, T> parameters = new LinkedHashMap<String, T>();

	/** Create a new block specification. */
	public SpecificationBase(String name) {
		this.name = name;
	}

	/** Returns the name of this block specification. */
	@Override
	public String getName() {
		return name;
	}

	/** Add a parameter to this specification. */
	protected void addParam(T param) throws BlockFileException {
		if (parameters.containsKey(param.getName())) {
			throw new BlockFileException(
					EDriverExceptionType.DUPLICATE_PARAM_NAME,
					"Duplicate parameter name " + param.getName(), this);
		}
		parameters.put(param.getName(), param);
	}

	/** {@inheritDoc} */
	@Override
	public T getParameter(String name) {
		return parameters.get(name);
	}

	/** {@inheritDoc} */
	@Override
	public T[] getParameters() {
		return parameters.values()
				.toArray(newParameterArray(parameters.size()));
	}

	/** {@inheritDoc} */
	@Override
	public T[] getNonSyntheticParameters() {
		List<T> result = new ArrayList<T>();
		for (T parameter : getParameters()) {
			if (!parameter.isSynthetic()) {
				result.add(parameter);
			}
		}
		return result.toArray(newParameterArray(result.size()));
	}

	/**
	 * Template method to create a new array of the parameter type of given
	 * size.
	 */
	protected abstract T[] newParameterArray(int size);
}