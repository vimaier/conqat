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
import org.conqat.engine.core.logging.IConQATLogger;

/**
 * Processor for testing purposes.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 36897 $
 * @ConQAT.Rating GREEN Hash: 672EC210482F9AE67E5A44E191FF9EE6
 */
@SuppressWarnings("unused")
@AConQATProcessor(description = "bla")
public class AbstractGenericProcessorInstance extends
		AbstractGenericProcessorBase<IConQATLogger> {
	// empty
}