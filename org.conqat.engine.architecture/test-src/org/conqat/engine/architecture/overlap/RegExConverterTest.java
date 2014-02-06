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
package org.conqat.engine.architecture.overlap;

import org.conqat.lib.commons.test.CCSMTestCaseBase;

/**
 * Unit tests for {@link RegExConverter} class
 * 
 * @author $Author: Moritz Marc Beller$
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 304A800A93E21E20152288100E7A8FB6
 */
public class RegExConverterTest extends CCSMTestCaseBase {

	/**
	 * Method translating regEx to dk.brics.style and comparing with expected
	 * result
	 */
	private void assertReplacement(String regex, String expectedReplacement) {
		assertEquals(expectedReplacement,
				RegExConverter.translatePatternToAutomatonLibraryRegEx(regex));
	}

	/** Tests for if strings containing wrong syntax are replaced */
	public void testNoReplace() {
		assertReplacement("\\a\\b\\c", "\\a\\b\\c");
		assertReplacement("\\ d \\ D", "\\ d \\ D");
	}

	/** Tests standard replacement of \d, \w, and \s char classes */
	public void testStandardReplace() {
		assertReplacement("\\d", "[0-9]");
		assertReplacement("\\w", "[A-Za-z_0-9]");
		assertReplacement("\\s", "[ \\t\\n\\x0B\\f\\r]");
	}

	/**
	 * Tests replacement of negated-standard replacements \D, \W and \S char
	 * classes
	 */
	public void testNegateReplace() {
		assertReplacement("\\D", "[^0-9]");
		assertReplacement("\\W", "[^A-Za-z_0-9]");
		assertReplacement("\\S", "[^ \\t\\n\\x0B\\f\\r]");
	}

	/** Tests a real-world regex */
	public void testRealWorldRegEx() {
		// an example for matching a number in the form of 109,01 or 109
		assertReplacement("\\d\\d*(,\\d*)?\\s",
				"[0-9][0-9]*(,[0-9]*)?[ \\t\\n\\x0B\\f\\r]");
	}

	/** Tests for use of backreferences */
	public void testUnsupportedBackReference() {
		assertNotNull(RegExConverter.hasBackReference("(abc)\\1"));
		assertNotNull(RegExConverter.hasBackReference("(.*)\\145"));
		assertNull(RegExConverter.hasBackReference("(.*)\\d145"));
	}

	/** Tests for use of possessive quantifier (+) */
	public void testUnsupportedPossessiveQuantifier() {
		assertNotNull(RegExConverter.hasPossessiveRegEx("(.*){2,4}+"));
		assertNotNull(RegExConverter.hasPossessiveRegEx("4++"));
		assertNull(RegExConverter.hasPossessiveRegEx("(\\w)+"));
	}

	/** Tests for use of boundary matchers */
	public void testUnsupportedBoundaryMatchers() {
		assertNotNull(RegExConverter.hasBoundaryMatcher("asd\\bdfg"));
		assertNotNull(RegExConverter.hasBoundaryMatcher(".*{2}\\G.*"));
		assertNull(RegExConverter.hasBoundaryMatcher("\\\\g"));
	}

	/** Tests for use of char unions */
	public void testUnsupportedCharUnion() {
		assertNotNull(RegExConverter.hasCharClassUnion("[[abc]]"));
		assertNotNull(RegExConverter.hasCharClassUnion("[A-Z[abc]]"));
		assertNotNull(RegExConverter.hasCharClassUnion("edf.*[A-Z[abc]]"));
		assertNotNull(RegExConverter.hasCharClassUnion("edf.*[[abc]]"));
		assertNotNull(RegExConverter.hasCharClassUnion("edf.*[h\\\\f[abc]\\]"));

		assertNull(RegExConverter.hasCharClassUnion("x.*\\[[abc]\\]"));
		assertNull(RegExConverter.hasCharClassUnion("edf.*\\[hf[abc]\\]"));
		assertNull(RegExConverter.hasCharClassUnion("edf.*\\[h\\\\f[abc]\\]"));
		assertNull(RegExConverter.hasCharClassUnion("edf.*[h\\[abc]]"));
		assertNull(RegExConverter.hasCharClassUnion("edf.*[\\[abc]]"));
		assertNull(RegExConverter.hasCharClassUnion("edf.*[]\\[abc]"));
	}

	/** Tests for use of char intersections */
	public void testUnsupportedIntersection() {
		assertNotNull(RegExConverter.hasCharClassIntersection("[a&&bc]"));
		assertNotNull(RegExConverter.hasCharClassIntersection("[A-Z&&[abc]]"));

		assertNull(RegExConverter.hasCharClassIntersection("[a&b&c]"));
	}
}
