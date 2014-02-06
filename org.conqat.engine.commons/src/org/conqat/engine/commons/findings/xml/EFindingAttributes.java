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
package org.conqat.engine.commons.findings.xml;

/**
 * Names of attributes used in the finding XML format.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating GREEN Hash: 9E10931B82758781742D07F0241A2170
 */
public enum EFindingAttributes {

	/** Time attribute of the finding report. */
	TIME,

	/** Name attribute. */
	NAME,

	/** Description attribute. */
	DESCRIPTION,

	/** Uniform path attribute. */
	UNIFORM_PATH,
	
	/** Location hint attribute. */
	LOCATION_HINT,

	/** Line number attribute. */
	LINE_NUMBER,

	/** Start line number attribute. */
	START_LINE_NUMBER,

	/** End line number attribute. */
	END_LINE_NUMBER,

	/** Start position (in line) attribute. */
	START_POSITION,

	/** End position (in line) attribute. */
	END_POSITION,

	/** Key for key value pairs. */
	KEY,

	/** Attribute for namespace. */
	XMLNS
}