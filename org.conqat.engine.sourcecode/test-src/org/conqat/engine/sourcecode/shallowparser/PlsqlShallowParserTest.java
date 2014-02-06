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

import org.conqat.lib.scanner.ELanguage;

/**
 * Tests the {@link PlsqlShallowParser}.
 * 
 * @author $Author: goede $
 * @version $Rev: 40435 $
 * @ConQAT.Rating GREEN Hash: ECC3DF2C9B1A752A7AC52AF8CB54F0B7
 */
public class PlsqlShallowParserTest extends ShallowParserTestBase {

	/** Tests parsing of fragments. */
	public void testFragments() {
		assertFragmentParsedTo("a integer; b integer;",
				"ATTRIBUTE: variable: a (lines 1-1)\n"
						+ "ATTRIBUTE: variable: b (lines 1-1)\n");

		assertFragmentParsedTo(
				"function foo (a integer, b integer) is begin if a > 3 then call(); elsif b < 4 then call2(); end if; end; ",
				"METHOD: function: foo (lines 1-1)\n"
						+ "  STATEMENT: if: null (lines 1-1)\n"
						+ "    STATEMENT: call: null (lines 1-1)\n"
						+ "  STATEMENT: elsif: null (lines 1-1)\n"
						+ "    STATEMENT: call2: null (lines 1-1)\n");
	}

	/** Tests parsing of incomplete fragments. */
	public void testBrokenFragments() {
		assertFragmentParsedTo(
				"call(); end; procedure p is b integer; begin a:= 15; if b > 5 then",
				"ATTRIBUTE: variable: call (lines 1-1)\n"
						+ "META: dangling end: null (lines 1-1) [incomplete]\n"
						+ "METHOD: procedure: p (lines 1-1) [incomplete]\n"
						+ "  ATTRIBUTE: variable: b (lines 1-1)\n"
						+ "  STATEMENT: a: null (lines 1-1)\n"
						+ "  STATEMENT: if: null (lines 1-1) [incomplete]\n");
	}

	/** Tests nested elsif construct. */
	public void testNestedElsif() {
		assertFragmentParsedTo(
				"if cond1 then if cond2 then func1(); elsif cond3 then func2(); end if; end if;",
				"STATEMENT: if: null (lines 1-1)\n"
						+ "  STATEMENT: if: null (lines 1-1)\n"
						+ "    STATEMENT: func1: null (lines 1-1)\n"
						+ "  STATEMENT: elsif: null (lines 1-1)\n"
						+ "    STATEMENT: func2: null (lines 1-1)\n");
	}

	/** {@inheritDoc} */
	@Override
	protected ELanguage getLanguage() {
		return ELanguage.PLSQL;
	}
}
