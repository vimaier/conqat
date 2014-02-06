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
 * Tests the {@link CsShallowParser}.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 78FD2F2D6F302338001FDB8DDF6274B2
 */
public class CsShallowParserTest extends CStyleShallowParserTestBase {

	/** Tests parsing of method fragments. */
	public void testMethodFragments() {
		assertFragmentParsedTo(
				"public boolean foo (int my) { dosomething(); }",
				"METHOD: method: foo (lines 1-1)\n"
						+ "  STATEMENT: simple statement: dosomething (lines 1-1)\n");
	}

	/** Test case for CR#4318 */
	public void testExplicitInterfaceImplementation() {
		assertFragmentParsedTo(
				"public boolean Interface.foo (int my) { dosomething(); }",
				"METHOD: method: foo (lines 1-1)\n"
						+ "  STATEMENT: simple statement: dosomething (lines 1-1)\n");
	}

	/** Test case for CR#4318 */
	public void testExplicitGenericInterfaceImplementation() {
		assertFragmentParsedTo(
				"public boolean IList<String>.foo (int my) { dosomething(); }",
				"METHOD: method: foo (lines 1-1)\n"
						+ "  STATEMENT: simple statement: dosomething (lines 1-1)\n");
	}

	/** Test case for CR#4318 */
	public void testExplicitGetPropertyImplementation() {
		assertFragmentParsedTo(
				"public boolean Interface<String, String2>.Prop { get { return name; } }",
				"ATTRIBUTE: property: Prop (lines 1-1)\n"
						+ "  METHOD: get: null (lines 1-1)\n"
						+ "    STATEMENT: simple statement: return (lines 1-1)\n");
	}

	/** Test case for CR#4318 */
	public void testExplicitSetPropertyImplementation() {
		assertFragmentParsedTo(
				"public boolean Interface.Prop { set { return name; } }",
				"ATTRIBUTE: property: Prop (lines 1-1)\n"
						+ "  METHOD: set: null (lines 1-1)\n"
						+ "    STATEMENT: simple statement: return (lines 1-1)\n");
	}

	/** Tests operator overloading. */
	public void testOperatorOverloading() {
		assertFragmentParsedTo(
				"class Foo { public static bool operator ==( Foo f1, Foo f2 ) { return false; } }",
				"TYPE: class: Foo (lines 1-1)\n"
						+ "  METHOD: operator: == (lines 1-1)\n"
						+ "    STATEMENT: simple statement: return (lines 1-1)\n");

		// special case: indexer
		assertFragmentParsedTo(
				"class SampleCollection<T> { public T this[int i] { get { return 0; }}}",
				"TYPE: class: SampleCollection (lines 1-1)\n"
						+ "  ATTRIBUTE: indexer: this (lines 1-1)\n"
						+ "    METHOD: get: null (lines 1-1)\n"
						+ "      STATEMENT: simple statement: return (lines 1-1)\n");
	}

	/** {@inheritDoc} */
	@Override
	protected ELanguage getLanguage() {
		return ELanguage.CS;
	}
}
