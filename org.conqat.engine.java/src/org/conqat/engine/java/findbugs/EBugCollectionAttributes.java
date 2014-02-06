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
package org.conqat.engine.java.findbugs;

/**
 * Some of the attributes used in the bug collection XML file written by
 * FindBugs. Spelling is the same as in the XML.
 * 
 * @author hummelb
 * @author $Author: juergens $
 * @version $Rev: 35196 $
 * @ConQAT.Rating GREEN Hash: 729A623CEFF4EE916CFA8FB7043DC751
 */
public enum EBugCollectionAttributes {

	/** Bug type. */
	type,

	/** Abbreviated bug type (kind of bug class). */
	abbrev,

	/** Bug category. */
	category,

	/** Class name (location). */
	classname,

	/** Role of location element (when set we usually ignore this). */
	role,

	/** Source file path (in location). */
	sourcepath,

	/** Start line in file. */
	start,

	/** End line in file (inclusive). */
	end,

	/** Name of method/field. */
	name
}