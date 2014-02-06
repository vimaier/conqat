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
package org.conqat.lib.scanner;

import static org.conqat.lib.commons.collections.CollectionUtils.asHashSet;

import java.util.Set;

/**
 * Constants for dealing with languages. These constants cannot be declared in
 * {@link ELanguage}, since the literals have to be the first statements in an
 * enumeration.
 * 
 * @author herrmama
 * @author $Author: hummelb $
 * @version $Rev: 35769 $
 * @ConQAT.Rating GREEN Hash: 1BF8F56CDD840A9129E41E57C3DFA88D
 */
class ELanguageConstants {

	/** Delimiters for comments which are shared by a number of languages. */
	static final Set<String> STANDARD_COMMENT_DELIMITERS = asHashSet("/*", "*",
			"*/", "//");
}