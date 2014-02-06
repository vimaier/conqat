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

import java.util.List;

import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;
import org.conqat.lib.scanner.IToken;

/**
 * Interface for a shallow parser. All shallow parsers are sufficiently robust
 * to support parsing even of non-compiling code and arbitrary code snippets.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 09D2DD2DE226CBE7956642CAC861DE8F
 */
public interface IShallowParser {

	/**
	 * Shallow parses the given list of tokens and returns all entities found.
	 * The tokens provided are expected to represent an entire top-level element
	 * of the language (typically a file).
	 */
	List<ShallowEntity> parseTopLevel(List<IToken> tokens);

	/**
	 * Shallow parses the given list of tokens and returns all entities found.
	 * The tokens provided may be from an arbitrary contiguous chunk of source
	 * code.
	 */
	List<ShallowEntity> parseFragment(List<IToken> tokens);
}
