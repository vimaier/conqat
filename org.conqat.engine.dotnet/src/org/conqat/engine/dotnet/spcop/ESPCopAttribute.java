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
package org.conqat.engine.dotnet.spcop;

/**
 * Relevant XML attributes within an SPCop report.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46153 $
 * @ConQAT.Rating GREEN Hash: C446E2EB5EFFC666800CF078E1884FF1
 */
/* package */ enum ESPCopAttribute {
	
	/** ID of a specific check. */
	CheckID,
	
	/** Name of an element. */
	Name,
	
	/** Severity of a finding as defined by SPCop. */
	Severity
}
