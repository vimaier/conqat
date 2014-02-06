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
 * Enumeration that defines rating states of clone classes.
 * 
 * @author juergens
 * @author $Author: steidl $
 * @version $Rev: 43358 $
 * @ConQAT.Rating GREEN Hash: F97FDC7A5D4D711278B06A31BA72165C
 */
public enum ECloneClassRating {

	/** The clone class has not been rated */
	UNRATED,

	/** A reviewer has tried to rate the clone class but couldn't decide */
	UNDECIDED,

	/** The clone class has been accepted */
	ACCEPTED,

	/** The clone class has been rejected */
	REJECTED,

	/**
	 * DS: added for parsing old clone reports: Intentionally coupled
	 * modification
	 */
	CONSISTENT,

	/**
	 * DS: added for parsing old clone reports
	 */
	INTERESTING

}