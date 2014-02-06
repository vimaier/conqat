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

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.IConQATLogger;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;
import org.conqat.engine.sourcecode.shallowparser.preprocessor.IParserPreprocessor;
import org.conqat.lib.scanner.ELanguage;
import org.conqat.lib.scanner.IToken;

/**
 * Factory class for creation of shallow parsers and utility methods.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 44101 $
 * @ConQAT.Rating GREEN Hash: A1496E286F95D99A006730E93146AD3A
 */
public class ShallowParserFactory {

	/** The map of parsers (initialized lazily). */
	private static Map<ELanguage, IShallowParser> parsers;

	static {
		parsers = new EnumMap<ELanguage, IShallowParser>(ELanguage.class);
		parsers.put(ELanguage.JAVA, new JavaShallowParser());
		parsers.put(ELanguage.ADA, new AdaShallowParser());
		parsers.put(ELanguage.CS, new CsShallowParser());
		parsers.put(ELanguage.CPP, new CppShallowParser());
		parsers.put(ELanguage.PLSQL, new PlsqlShallowParser());
		parsers.put(ELanguage.ABAP, new AbapShallowParser());
		parsers.put(ELanguage.JAVASCRIPT, new JavaScriptShallowParser());
	}

	/**
	 * Returns a new parser for the given language.
	 * <p>
	 * While we call this method "create" for consistency with other factories,
	 * the parsers are actually created only once and then returned over and
	 * over again. The reason is that parser creation may be expensive,
	 * especially when many very small code fragments are to be parsed. Reusing
	 * parsers is possible as the parsers no not hold state of a specific parse
	 * and even can be used concurrently in multiple threads.
	 * 
	 * @throws ConQATException
	 *             if the language is not (yet) supported by our framework.
	 */
	public static IShallowParser createParser(ELanguage language)
			throws ConQATException {
		IShallowParser parser = parsers.get(language);
		if (parser == null) {
			throw new ConQATException("Shallow parsing for language "
					+ language + " not yet supported!");
		}
		return parser;
	}

	/**
	 * Returns whether the given language is supported by the parser factory.
	 */
	public static boolean supportsLanguage(ELanguage language) {
		return parsers.containsKey(language);
	}

	/**
	 * Shallow parses the given element and returns the entities parsed.
	 * Scanning errors are logged using the given logger.
	 * 
	 * @throws ConQATException
	 *             if access to the underlying element's text fails or if no
	 *             parser is available for the given language.
	 */
	public static List<ShallowEntity> parse(ITokenElement element,
			IConQATLogger logger) throws ConQATException {
		List<IToken> tokens = preprocess(element.getTokens(logger), element);
		return createParser(element.getLanguage()).parseTopLevel(tokens);
	}

	/**
	 * Performs preprocessing of the token sequence if a preprocessor is
	 * attached to the given element.
	 */
	private static List<IToken> preprocess(List<IToken> tokens,
			ITokenElement element) throws ConQATException {
		Object preprocessor = element.getValue(IParserPreprocessor.KEY);
		if (preprocessor == null) {
			return tokens;
		}

		if (preprocessor instanceof IParserPreprocessor) {
			return ((IParserPreprocessor) preprocessor).preprocess(tokens);
		}

		throw new ConQATException("Invalid object of type "
				+ preprocessor.getClass() + " found at preprocessor key.");
	}
}
