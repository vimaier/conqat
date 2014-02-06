/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2012 The ConQAT Project                                   |
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
 * Enumeration of XML elements required to read VS.NET project files.
 * <p>
 * Consult package documentation to understand where they occur in VS.NET
 * project files.
 * 
 * @author $Author: juergens $
 * @version $Rev: 38016 $
 * @ConQAT.Rating GREEN Hash: F57C14DF3AE9DC761B65D43AF9D7E7F3
 */
/* package */enum EProjectXmlElement {

	/** ItemGroup element */
	ItemGroup,

	/** Compile element */
	Compile,

	/** CSHARP element */
	CSHARP,

	/** Files */
	Files,

	/** Include */
	Include,

	/** Assembly name */
	AssemblyName,

	/** Output type */
	OutputType,

	/** Properties group */
	PropertyGroup,

	/** The relative output path */
	OutputPath,

	/** File */
	File,

	/** Settings tag in a VS 2003 project file */
	Settings,

	/** Config tag in a VS 2003 project file */
	Config,
	
	/** WarnLevel */
	WarningLevel,
	
	/** NoWarn */
	NoWarn
}