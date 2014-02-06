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
package org.conqat.engine.dotnet.ila.xml;

/**
 * Enumeration of the xml elements the IL-XML format comprises.
 * 
 * @author Elmar Juergens
 * @author $Author: juergens $
 * 
 * @version $Revision: 35167 $
 * @ConQAT.Rating GREEN Hash: 5B251637653E86F9B089EC4FCB02C745
 */
public enum EIlaXmlElement {

	/** TypeElement represents types such as classes, enums, ... */
	TypeElement,

	/** Expresses implementation inheritance relationship between types */
	Extends,

	/** Represents interface inheritance relationship between types */
	Implements,

	/**
	 * Represents dependencies that do not originate in inheritance
	 * relationships
	 */
	Depends,

	/** Represens a member of a type */
	Member
}