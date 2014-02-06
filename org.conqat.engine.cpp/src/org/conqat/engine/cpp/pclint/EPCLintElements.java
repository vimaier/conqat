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
package org.conqat.engine.cpp.pclint;

/**
 * Some of the elements used in the XML reports written by PCLint. Spelling is
 * the same as in the XML.
 * 
 * @author hummelb
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: CA3C4A972D65D76BD8ADDA08D7683F04
 */
/* package */enum EPCLintElements {

	/** Doc element. Root element of PCLint report. */
	doc,

	/** Message element. Root element for each finding. */
	message,

	/** Line element. Contains line number of finding. */
	line,

	/** File element. Contains filename. */
	file,

	/** Type element. E.g. warning */
	type,

	/** Code element. Contains number of PCLint rule. */
	code,

	/** Desc element. Contains human readable description of the finding. */
	desc

}