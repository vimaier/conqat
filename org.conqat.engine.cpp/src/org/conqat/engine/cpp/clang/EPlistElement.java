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
 * The elements found in a Plist XML file.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46893 $
 * @ConQAT.Rating GREEN Hash: B709D19571B1095ABE81CB4345F9E4AB
 */
public enum EPlistElement {

	/** The Plist root element. */
	PLIST,

	/** Dictionary (i.e. map) type. */
	DICT,

	/** Array type. */
	ARRAY,

	/** Key in a map/dict. */
	KEY,

	/** String value. */
	STRING,

	/** Int value. */
	INTEGER,

	/** Used for all other elements that we do not know about. */
	OTHER
}
