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
package org.conqat.engine.code_clones.result.align;

/**
 * Enumeration of statement types used for AST alignment.
 * 
 * @author $Author: goede $
 * @version $Rev: 43378 $
 * @ConQAT.Rating GREEN Hash: 863635EDBD3415C3299A6164F4115C63
 */
public enum EAlignerStatementType {

	/** Primitive statements are leaves in the AST. */
	PRIMITIVE,

	/**
	 * The start of a compound statement (AST inner node), such as the head of a
	 * loop.
	 */
	COMPOUND_START,

	/**
	 * The end of a compound statement (AST inner node), such as the tail of a
	 * loop.
	 */
	COMPOUND_END
}