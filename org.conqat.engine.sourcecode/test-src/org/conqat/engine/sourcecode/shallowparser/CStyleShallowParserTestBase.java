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

/**
 * Base class for tests of shallow parsers for C-style languages.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 7750FAEADAD1CCCAC2756D778F2B903E
 */
public abstract class CStyleShallowParserTestBase extends ShallowParserTestBase {

	/** Tests parsing of statement fragments. */
	public void testStatementFragments() {
		assertFragmentParsedTo(
				"for (a=5;a<3;++a) if (my_var) println();",
				"STATEMENT: for: null (lines 1-1)\n"
						+ "  STATEMENT: if: null (lines 1-1)\n"
						+ "    STATEMENT: simple statement: println (lines 1-1)\n");
	}

	/** Tests else-if. */
	public void testElseIf() {
		assertFragmentParsedTo("if(a) b (); else if (c) d();",
				"STATEMENT: if: null (lines 1-1)\n"
						+ "  STATEMENT: simple statement: b (lines 1-1)\n"
						+ "STATEMENT: else if: null (lines 1-1)\n"
						+ "  STATEMENT: simple statement: d (lines 1-1)\n");

	}

	/**
	 * Tests whether if-statements are nested correctly. Also see
	 * http://java.sun
	 * .com/docs/books/jls/third_edition/html/statements.html#14.5 for the
	 * "dangling else problem".
	 */
	public void testIfNesting() {
		String expected = "STATEMENT: if: null (lines 1-1)\n"
				+ "  STATEMENT: if: null (lines 1-1)\n"
				+ "    STATEMENT: simple statement: c (lines 1-1)\n"
				+ "  STATEMENT: else: null (lines 1-1)\n"
				+ "    STATEMENT: simple statement: d (lines 1-1)\n"
				+ "STATEMENT: else: null (lines 1-1)\n"
				+ "  STATEMENT: simple statement: e (lines 1-1)\n";

		// test both with and without braces
		assertFragmentParsedTo(
				"if (a) { if (b) { c(); } else { d(); } } else { e(); }",
				expected);
		assertFragmentParsedTo("if (a)  if (b)  c();  else d(); else e();",
				expected);
	}

	/** Tests parsing of incomplete fragments. */
	public void testBrokenFragments() {
		assertFragmentParsedTo(
				"for (a=5;a<3;++a) if (my_var) a += ",
				"STATEMENT: for: null (lines 1-1)\n"
						+ "  STATEMENT: if: null (lines 1-1)\n"
						+ "    STATEMENT: simple statement: a (lines 1-1) [incomplete]\n");

		assertFragmentParsedTo(
				"break; } } if (foo) { meth1(); a += 3;",
				"STATEMENT: simple statement: break (lines 1-1)\n"
						+ "META: dangling closing brace: null (lines 1-1) [incomplete]\n"
						+ "META: dangling closing brace: null (lines 1-1) [incomplete]\n"
						+ "STATEMENT: if: null (lines 1-1) [incomplete]\n"
						+ "  STATEMENT: simple statement: meth1 (lines 1-1)\n"
						+ "  STATEMENT: simple statement: a (lines 1-1)\n");
	}

	/** Tests array types. */
	public void testArrays() {
		assertFragmentParsedTo("class Foo { int[][][] a; }",
				"TYPE: class: Foo (lines 1-1)\n"
						+ "  ATTRIBUTE: attribute: a (lines 1-1)\n");
	}

}
