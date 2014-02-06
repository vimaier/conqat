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
package org.conqat.engine.sourcecode.shallowparser.preprocessor;

import java.util.List;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.clone.IDeepCloneable;
import org.conqat.lib.scanner.IToken;

/**
 * A preprocessor for the parser framework. This can be used to modify
 * the source code (i.e. the token stream) prior to passing it to the parser.
 * This way, the parser can be adjusted to dialects of a language or to heavy
 * usage of macros in languages like C.
 * <p>
 * The preprocessor is stored at the element it should be applied to. As these
 * values are potentially cloned, we should implement {@link IDeepCloneable} to
 * allow for clean cloning.
 * 
 * @author $Author: pfaller $
 * @version $Rev: 40706 $
 * @ConQAT.Rating GREEN Hash: B5A8AD5A893558AFC8F94DF0833C0F9E
 */
public interface IParserPreprocessor extends IDeepCloneable {

	/** The key used to store preprocessors in ConQAT nodes. */
	public static final String KEY = IParserPreprocessor.class.getSimpleName();

	/**
	 * Preprocesses the given token stream. Implementations must be able to deal
	 * with unmodifiable input lists, i.e. the returned list should typically be
	 * a new list.
	 */
	List<IToken> preprocess(List<IToken> tokens) throws ConQATException;
}
