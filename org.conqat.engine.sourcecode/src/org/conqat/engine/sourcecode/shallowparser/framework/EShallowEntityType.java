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
package org.conqat.engine.sourcecode.shallowparser.framework;

/**
 * The possible types of {@link ShallowEntity}s.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 0DB8C87AD7AD95C7289935B7160B7A75
 */
public enum EShallowEntityType {

	/** A module (package, namespace, etc.). */
	MODULE,

	/** A type (class, record, etc.). */
	TYPE,

	/** A method (procedure, function, etc.). */
	METHOD,

	/** An attribute (parameter, variable declaration, etc.). */
	ATTRIBUTE,

	/** A statement (both simple and block). */
	STATEMENT,

	/** Meta information, such as annotations, pragmas, etc. */
	META;
}
