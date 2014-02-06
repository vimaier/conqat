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
package org.conqat.engine.code_clones.core.report;

/**
 * XML elements for the clone report
 * 
 * @author juergens
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: BC06E155C537D7A73041FCFFDBA2988C
 */
public enum ECloneReportElement {

	/** Start element of clone report */
	cloneReport,

	/** The &lt;{@linkplain #cloneClass}&gt; element. */
	cloneClass,

	/** The &lt;{@linkplain #clone}&gt; element. */
	clone,

	/** The &lt;{@linkplain #sourceFile}&gt; element. */
	sourceFile,

	/** The &lt;{@linkplain #values}&gt; element. */
	values,

	/** The &lt;{@linkplain #value}&gt; element. */
	value,

}