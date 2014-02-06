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
package org.conqat.engine.code_clones.core.report.enums;

/**
 * Enumeration for clone change types occurring during evolution.
 * 
 * @author juergens
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: C2E605ECBFC0917B5BFCC6E002FE7042
 */
public enum EChangeType {

	/** Intentionally coupled modification */
	CONSISTENT,

	/** Unintentionally uncoupled modification */
	INCONSISTENT,

	/** Intentionally uncoupled modification */
	INDEPENDENT,

	/** Don't know */
	DONT_KNOW,

	/** Not yet decided */
	UNRATED
}