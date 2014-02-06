/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
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
package org.conqat.engine.commons.defs;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATFieldParameter;

/**
 * Base class for processors defining a constant value. We need concrete
 * subclasses, as ConQAT needs the Java type information.
 * 
 * @param <T>
 *            the type of the constant.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 37510 $
 * @ConQAT.Rating GREEN Hash: C1578408850949C1BB45DD9B5274E9A0
 */
public class ConstantBase<T> extends ConQATProcessorBase {

	/** Documentation for the subclasses. */
	public static final String DOC = "Defines a constant value.";

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "constant", attribute = "value", description = "The constant value provided.")
	public T constant;

	/** {@inheritDoc} */
	@Override
	public T process() {
		return constant;
	}
}
