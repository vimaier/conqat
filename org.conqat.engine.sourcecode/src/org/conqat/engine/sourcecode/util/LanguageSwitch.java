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
package org.conqat.engine.sourcecode.util;

import org.conqat.engine.commons.dataflow.SwitchBase;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.scanner.ELanguage;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: deissenb $
 * @version $Rev: 37511 $
 * @ConQAT.Rating GREEN Hash: 58A241578A70460A2244F8103E9AAFD3
 */
@AConQATProcessor(description = SwitchBase.DOC)
public class LanguageSwitch extends SwitchBase<ELanguage> {
	// nothing to implement
}
