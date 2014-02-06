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

import java.io.IOException;
import java.util.List;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.resource.TokenTestCaseBase;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.scanner.ELanguage;
import org.conqat.lib.scanner.IToken;
import org.conqat.lib.scanner.ScannerFactory;
import org.conqat.lib.scanner.ScannerUtils;

/**
 * Base class for tests of shallow parsers.
 * 
 * @author $Author: goede $
 * @version $Rev: 39181 $
 * @ConQAT.Rating GREEN Hash: 9B4EF9F3B33915AB9C057BA9BEC097A1
 */
public abstract class ShallowParserTestBase extends TokenTestCaseBase {

	/** Template method defining the language. */
	protected abstract ELanguage getLanguage();

	/**
	 * Asserts that the fragments equals to the given parsed parameter after
	 * string serialization.
	 */
	protected void assertFragmentParsedTo(String code, String parsed) {
		List<ShallowEntity> entities = parse(code, getLanguage(), true);
		assertEquals(StringUtils.normalizeLineBreaks(parsed),
				normalizeParseResult(entities));
	}

	/** Normalizes the parsing result as a string. */
	protected static String normalizeParseResult(List<ShallowEntity> entities) {
		StringBuilder actual = new StringBuilder();
		for (ShallowEntity entity : entities) {
			actual.append(entity);
		}
		return StringUtils.normalizeLineBreaks(actual.toString());
	}

	/** Returns a suitable parser. */
	protected static IShallowParser getParser(ELanguage language) {
		try {
			return ShallowParserFactory.createParser(language);
		} catch (ConQATException e) {
			throw new AssertionError("Should not happen in the test!");
		}
	}

	/**
	 * Parses the given code.
	 * 
	 * @param isFragment
	 *            if this is true, the code is treated as arbitrary
	 *            fragment/snippet (no entire file).
	 */
	protected static List<ShallowEntity> parse(String code, ELanguage language,
			boolean isFragment) {
		List<IToken> tokens;
		try {
			tokens = ScannerUtils.readTokens(ScannerFactory.newLenientScanner(
					language, code, null));
		} catch (IOException e) {
			throw new AssertionError(
					"Should not happen for in-memory scanning!");
		}
		if (isFragment) {
			return getParser(language).parseFragment(tokens);
		}
		return getParser(language).parseTopLevel(tokens);
	}

}
