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
 * Tests the {@link CppShallowParser}.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 44364 $
 * @ConQAT.Rating GREEN Hash: 4FB6A1E5F39C8658D48B4CD76EE61271
 */
public class CppShallowParserTest extends CStyleShallowParserTestBase {

	/** Tests parsing of method fragments. */
	public void testMethodFragments() {
		assertFragmentParsedTo(
				"public: bool foo (int my) { dosomething(); }",
				"METHOD: function: foo (lines 1-1)\n"
						+ "  STATEMENT: simple statement: dosomething (lines 1-1)\n");
	}

	/** Tests operator overloading. */
	public void testOperatorOverloading() {
		assertFragmentParsedTo(
				"class Foo { int a; inline void operator= (const Foo &f) { a = f.a; } };",
				"TYPE: class: Foo (lines 1-1)\n"
						+ "  ATTRIBUTE: attribute: a (lines 1-1)\n"
						+ "  METHOD: operator: operator= (lines 1-1)\n"
						+ "    STATEMENT: simple statement: a (lines 1-1)\n");
	}

	/** Tests extraction of names. */
	public void testNameExtraction() {
		assertFragmentParsedTo(
				"static MyClass *MyClass::instance = new MyClass ();",
				"ATTRIBUTE: attribute: instance (lines 1-1)\n");

		assertFragmentParsedTo("namespace editor::model;",
				"META: namespace: editor::model (lines 1-1)\n");
	}

	/** {@inheritDoc} */
	@Override
	protected ELanguage getLanguage() {
		return ELanguage.CPP;
	}
}
