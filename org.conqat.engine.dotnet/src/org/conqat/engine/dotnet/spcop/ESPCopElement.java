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
 * Relevant XML elements within an SPCop report.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46294 $
 * @ConQAT.Rating GREEN Hash: AC60EE23D0CEBA14B42F5E44B6CE54E0
 */
/* package */enum ESPCopElement {

	/** Line that contains the finding (1-based). */
	LineNumber,

	/** Human-readable description of an SPCop finding. */
	Message,

	/** Represents a single SPCop finding. */
	Notification,

	/**
	 * Contains all findings for a specific quality attribute (e.g.,
	 * correctness, security, ...).
	 */
	VisitorGroup,

	/** Location of the affected file relative to the WSP file. */
	HiveLocation
}
