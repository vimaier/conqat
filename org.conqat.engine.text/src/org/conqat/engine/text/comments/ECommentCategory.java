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
package org.conqat.engine.text.comments;

/**
 * Enum for different comment categories.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 45696 $
 * @ConQAT.Rating GREEN Hash: CD140CA74AF77363EBF9C92C26D60153
 */
public enum ECommentCategory {

	/**
	 * A copyright comment containing information about copyright or license.
	 * Usually found at the beginning of a file.
	 */
	COPYRIGHT,

	/**
	 * A header comment is a class comment documenting the purpose of the class
	 * and possibly including information about author, revision, etc.
	 */
	HEADER,

	/**
	 * Interface comments describe a method or an attribute definition.
	 */
	INTERFACE,

	/** Inline comments are found within a method body. */
	INLINE,

	/** Section comments group several methods or attributes together. */
	SECTION,

	/** A note about a future todo, a bug, or a hack. */
	TASK,

	/** Commented out code. */
	CODE
}
