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
package org.conqat.engine.code_clones.core.report.enums;

/**
 * Categories of benchmark reference clone pairs.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43154 $
 * @ConQAT.Rating GREEN Hash: 702D0A2DF6ABE494D8A177978C3110C1
 */
public enum EReferenceCategory {

	/** Important true positives */
	TP,

	/** Not so important true positives */
	tp_small,

	/** Not so important false positives */
	fp_small,

	/** Important false positives */
	FP,

	/** Unrated category */
	UNRATED;

}
