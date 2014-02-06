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
package org.conqat.engine.core.build;

/**
 * This enumeration describes the XML elements used in ANT file generation.
 * 
 * @author Florian Deissenboeck
 * @author $Author: hummelb $
 * @version $Rev: 47069 $
 * @ConQAT.Rating GREEN Hash: A8B8157FA4C974B218C08A1077059F8D
 */
/* package */enum EANTElement {
	/** Element &lt;{@linkplain #ant}&gt; */
	ant,

	/** Element &lt;{@linkplain #copy}&gt; */
	copy,

	/** Element &lt;{@linkplain #delete}&gt; */
	delete,

	/** Element &lt;{@linkplain #fileset}&gt; */
	fileset,

	/** Element &lt;{@linkplain #zipfileset}&gt; */
	zipfileset,

	/** Element &lt;{@linkplain #include}&gt; */
	include,

	/** Element &lt;{@linkplain #exclude}&gt; */
	exclude,

	/** Element &lt;{@linkplain #mkdir}&gt; */
	mkdir,

	/** Element &lt;{@linkplain #project}&gt; */
	project,

	/** Element &lt;{@linkplain #target}&gt; */
	target,

	/** Element &lt;{@linkplain #zip}&gt; */
	zip,

	/** Element &lt;{@linkplain #jar}&gt; */
	jar,

	/** Element &lt;{@linkplain #manifest}&gt; */
	manifest,

	/** Element &lt;{@linkplain #attribute}&gt; */
	attribute,

	/** Element &lt;{@linkplain #exec}&gt; */
	exec,

	/** Element &lt;{@linkplain #arg}&gt; */
	arg,

	/** Element &lt;{@linkplain #property}&gt; */
	property,
	
	/** Element &lt;{@linkplain #parallel}&gt; */
	parallel
}