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
package org.conqat.engine.dotnet.coverage;

/**
 * XML Elements for processing XML coverage files.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43133 $
 * @ConQAT.Rating GREEN Hash: 986335DCE9F41C58D210869079643BAF
 */
public enum ECoverageXmlElement {
	/** Module element */
	Module,

	/** ModuleName element */
	ModuleName,

	/** LinesCovered element */
	LinesCovered,

	/** LinesPartiallyCovered element */
	LinesPartiallyCovered,

	/** LinesNotCovered element */
	LinesNotCovered,

	/** BlocksCovered element */
	BlocksCovered,

	/** BlocksNotCovered element */
	BlocksNotCovered,

	/** NamespaceTable element. Every Module has one NamespaceTable. */
	NamespaceTable,

	/** NamespaceName element. */
	NamespaceName,

	/** NamespaceKeyName element */
	NamespaceKeyName,

	/** Class element */
	Class,

	/** ClassName element. */
	ClassName,

	/** Method element. */
	Method,

	/** MethodName element. */
	MethodName

}