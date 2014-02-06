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
package org.conqat.engine.cpp.clang;

/**
 * Keys used in Plist format. These are encoded as text in a key element.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46894 $
 * @ConQAT.Rating GREEN Hash: E9FC11BF231EB3558BDEF59529FE04B5
 */
public enum EPlistKey {

	/** Key for the list of files. */
	FILES,

	/** Key for the list of findings. */
	DIAGNOSTICS,

	/** Description of a finding. */
	DESCRIPTION,

	/** Category of a finding. */
	CATEGORY,

	/** Type of a finding. */
	TYPE,

	/** Location information. */
	LOCATION,

	/** The line number (1-based). */
	LINE,

	/** The file: 0-based index into files list. */
	FILE,

	/** Other keys we do not care about. */
	OTHER;
}
