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
package org.conqat.engine.dotnet.gendarme;

/**
 * XML element of the Gendarme report.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 8C06F6272ACF887017DBA096DC1FCD4D
 */
/* package */enum EGendarmeElement {

	/** This element contains the actual result. */
	results,

	/**
	 * Results are grouped by rules. Caution, there is another element of the
	 * same name below element 'rules' that we do not consider here.
	 */
	rule,

	/** This element contains a description of the rule. */
	problem,

	/** This elements groups finding for single target. */
	target,

	/** This element describes a single finding. */
	defect
}