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
package org.conqat.engine.dotnet.resource.parser;

/**
 * Enumeration of XML attributes required to read VS.NET project files.
 * <p>
 * Consult package documentation to understand where they occur in VS.NET
 * project files.
 * 
 * @author $Author: juergens $
 * @version $Rev: 38092 $
 * @ConQAT.Rating GREEN Hash: 94FEB925FEEE33B27A2C59455F166EAD
 */
/* package */enum EProjectXmlAttribute {

	/** Include attribute */
	Include,

	/** RelPath attribute */
	RelPath,

	/** BuildAction attribute */
	BuildAction,

	/**
	 * The condition used for the decision which PropertyGroup Element is used
	 * in a build run
	 */
	Condition,

	/** AssemblyName attribute in VS 2003 project files */
	AssemblyName,

	/** OutputType attribute in VS 2003 project files */
	OutputType,

	/** OutputPath attribute in VS 2003 project files */
	OutputPath,

	/** Name attribute in VS 2003 project files (of Config tag) */
	Name,

	/** Link attribute used for files that are linked into the workspace */
	Link,
	
	/** NoWarn IDs in VS 2003 project files */
	NoWarn,
	
	/** Warning level setting in VS 2003 project files */
	WarningLevel;
}