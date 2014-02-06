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
 * Some of the elements used in the bug collection XML file written by FindBugs.
 * Spelling is the same as in the XML.
 * 
 * @author hummelb
 * @author $Author: juergens $
 * @version $Rev: 35196 $
 * @ConQAT.Rating GREEN Hash: C458481FD16F4A01D21DDAED94AB7ECF
 */
public enum EBugCollectionElements {

	/** Bug collection. */
	BugCollection,

	/** Project (contains {@link #SrcDir}s). */
	Project,

	/** Source directories for code lookup. */
	SrcDir,

	/** Bug instances. */
	BugInstance,

	/** Location class. */
	Class,

	/** Location method. */
	Method,

	/** Location field. */
	Field,

	/** Location source line. */
	SourceLine
}