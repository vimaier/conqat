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
package org.conqat.engine.core.driver.specification.processors;

import java.util.List;

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * Processor for testing purposes.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 36897 $
 * @ConQAT.Rating GREEN Hash: 77F64805E080EAFDC7E7064384AAFD92
 */
@SuppressWarnings("unused")
@AConQATProcessor(description = "bla")
public class GenericProcessorInstance extends GenericProcessorBase<Double> {

	/** test method */
	@AConQATParameter(name = "pls", description = "")
	public void pls(
			@AConQATAttribute(name = "a", description = "") List<String> l) {
		// nothing to do here
	}

	/** test method */
	@AConQATParameter(name = "plq", description = "")
	public void plq(@AConQATAttribute(name = "a", description = "") List<?> l) {
		// nothing to do here
	}

	/** test method */
	@SuppressWarnings("rawtypes")
	@AConQATParameter(name = "plx", description = "")
	public void plx(@AConQATAttribute(name = "a", description = "") List l) {
		// nothing to do here
	}
}