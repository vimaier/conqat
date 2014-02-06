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
package org.conqat.engine.architecture.assessment.shared;

import org.conqat.engine.architecture.format.ECodeMappingType;

/**
 * Interface specifying an architecture code mapping
 * 
 * @author $Author: Moritz Marc Beller$
 * @version $Rev: 41263 $
 * @ConQAT.Rating GREEN Hash: B4295B5B4CE5CBFD5DCB8DBB618B638E
 */
public interface ICodeMapping {

	/** Returns the code mapping's RegEx */
	public String getRegex();

	/** Get code mapping type. */
	public ECodeMappingType getType();
}
