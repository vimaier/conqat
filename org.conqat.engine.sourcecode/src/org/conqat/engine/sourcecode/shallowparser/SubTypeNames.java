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
package org.conqat.engine.sourcecode.shallowparser;

import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;

/**
 * This class collects constants used for subtypes in a {@link ShallowEntity}.
 * The list of constants is not complete, as often the subtype is also taken
 * directly from the parsing context (e.g. a matched keyword). The purpose of
 * this class is only to eliminate redundancy between the parser and code
 * traversing the ASt.
 * 
 * Convention is to have the name of the constant and the content the same
 * (while respecting naming conventions). Hence, changes to a value should be
 * reflected in the name.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 47148 $
 * @ConQAT.Rating GREEN Hash: D2C9529FAFFB16625056458F9572BAC8
 */
public class SubTypeNames {

	/** Sub type. */
	public static final String CLASS_PUBLICATION = "class publication";

	/** Sub type. */
	public static final String CLASS_DEFINITION = "class definition";

	/** Sub type. */
	public static final String CLASS_IMPLEMENTATION = "class implementation";

	/** Sub type. */
	public static final String INTERFACE_PUBLICATION = "interface publication";

	/** Sub type. */
	public static final String INTERFACE_DEFINITION = "interface definition";

	/** Sub type. */
	public static final String METHOD_IMPLEMENTATION = "method implementation";

	/** Sub type. */
	public static final String METHOD_DECLARATION = "method declaration";

	/** Sub type. */
	public static final String FUNCTION = "function";

	/** Sub type. */
	public static final String ON_CHANGE = "on change";

	/** Sub type. */
	public static final String NATIVE_SQL = "native SQL";

	/** Sub type. */
	public static final String SELECT_BLOCK = "select block";

	/** Sub type. */
	public static final String MODULE = "module";

	/** Sub type. */
	public static final String FORM = "form";

	/** Sub type. */
	public static final String SINGLE_SELECT = "single select";

	/** Sub type. */
	public static final String MACRO = "macro";

	/** Sub type. */
	public static final String VISIBILITY = "Visibility";

	/** Sub type. */
	public static final String EMPTY_STATEMENT = "empty statement";

	/** Sub type. */
	public static final String LOCAL_VARIABLE = "local variable";
}
