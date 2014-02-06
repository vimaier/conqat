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
package org.conqat.engine.sourcecode.shallowparser.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.shallowparser.ShallowParserFactory;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntityTraversalUtils;
import org.conqat.engine.sourcecode.shallowparser.util.EntitySelectionExpressionParsingException.EParsingExceptionMessage;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.predicate.IPredicate;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.commons.test.CCSMTestCaseBase;
import org.conqat.lib.scanner.ELanguage;
import org.conqat.lib.scanner.IToken;
import org.conqat.lib.scanner.ScannerUtils;

import static org.conqat.engine.sourcecode.shallowparser.util.EntitySelectionExpressionParsingException.EParsingExceptionMessage.*;

/**
 * Tests the {@link EntitySelectionExpressionParser} and indirectly the
 * {@link EntitySelectionPredicates} as well.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46254 $
 * @ConQAT.Rating GREEN Hash: A711BDD01BA7AA7B54B7E46A21D7E81F
 */
public class EntitySelectionExpressionParserTest extends CCSMTestCaseBase {

	/** The parsed test file. */
	private List<ShallowEntity> entities;

	/** {@inheritDoc} */
	@Override
	protected void setUp() throws Exception {
		String content = FileSystemUtils
				.readFile(useTestFile("Expression.java"));
		List<IToken> tokens = ScannerUtils.getTokens(content, ELanguage.JAVA);
		entities = ShallowParserFactory.createParser(ELanguage.JAVA)
				.parseFragment(tokens);
	}

	/** Tests predicates in isolation. */
	public void testSinglePredicates() throws ConQATException {
		assertExpressionResult("module");
		assertExpressionResult("type", "Expression");
		assertExpressionResult("attribute", "CONST", "foo", "bar", "baz");
		assertExpressionResult("method", "setX", "setComplex", "getFoo",
				"getBarUpdated");

		assertExpressionResult("public", "Expression", "foo", "setX",
				"setComplex");
		assertExpressionResult("protected", "bar", "getBarUpdated");
		assertExpressionResult("private", "CONST", "getFoo");

		assertExpressionResult("static", "CONST");
		assertExpressionResult("final", "CONST");

		assertExpressionResult("name(foo)", "foo");

		assertExpressionResult("annotated(X)");
		assertExpressionResult("annotated(Override)", "setComplex",
				"SomeOtherAnnotation", "getBarUpdated");

		assertExpressionResult("simpleGetter", "getFoo");
		assertExpressionResult("simpleSetter", "setX");
	}

	/**
	 * Tests different ways of writing predicates. This has no asserts but
	 * checks if any exceptions are thrown.
	 */
	public void testWritingVariations() throws ConQATException {
		EntitySelectionExpressionParser.parse("public");
		EntitySelectionExpressionParser.parse("PUBLIC");
		EntitySelectionExpressionParser.parse("Pub_Lic");
		EntitySelectionExpressionParser.parse("pub-lic");
		EntitySelectionExpressionParser.parse("public()");
		EntitySelectionExpressionParser.parse("((public))");
	}

	/**
	 * Tests more complex expressions (both in terms of parsing and evaluation).
	 */
	public void testComplexExpressions() throws ConQATException {
		assertExpressionResult("default & attribute", "baz");
		assertExpressionResult("method & !(public | private)", "getBarUpdated");
		assertExpressionResult("!(!method | !(!public & !private))",
				"getBarUpdated");
		assertExpressionResult("public & method & !(simpleGetter | simpleSetter | annotated(Override))");
	}

	/** Tests the handling of various parsing errors. */
	public void testParsingErrors() throws ConQATException {
		assertParsingError("foo", PREDICATE_NOT_FOUND);
		assertParsingError("public & .", UNEXPECTED_CHARACTER);
		assertParsingError("public private", EXPECTED_BINARY_OPERATOR);
		assertParsingError("public(abc)", UNSUPPORTED_PARAMETER);
		assertParsingError("annotated & public", PARAMETER_MISSING);
		assertParsingError("public &", EXPECTED_EXPRESSION);
		assertParsingError("| public", EXPECTED_EXPRESSION);
		assertParsingError("!", EXPECTED_EXPRESSION);
		assertParsingError("(public & private", MISSING_CLOSING_PARENTHESIS);
		assertParsingError("public & private)", MISPLACED_CLOSING_PARENTHESIS);
		assertParsingError("(public &) private", EXPECTED_EXPRESSION);
		assertParsingError("()", EXPECTED_EXPRESSION);
	}

	/**
	 * Checks that the given expression runs into a parsing error whose message
	 * starts with given prefix.
	 */
	private void assertParsingError(String expression,
			EParsingExceptionMessage expectedMessage) throws ConQATException {
		try {
			EntitySelectionExpressionParser.parse(expression);
			fail("Expected parsing exception!");
		} catch (EntitySelectionExpressionParsingException e) {
			assertEquals(expectedMessage, e.getMessageIdentifier());
		}
	}

	/**
	 * Asserts that the given expression returns exactly the entities with given
	 * names.
	 */
	private void assertExpressionResult(String expression,
			String... entityNames) throws ConQATException {
		IPredicate<ShallowEntity> predicate = EntitySelectionExpressionParser
				.parse(expression);

		String expectedNames = StringUtils.concat(
				CollectionUtils.sort(Arrays.asList(entityNames)), ", ");
		List<String> actualNames = new ArrayList<String>();
		for (ShallowEntity entity : ShallowEntityTraversalUtils.selectEntities(
				entities, predicate)) {
			actualNames.add(entity.getName());
		}

		assertEquals(expectedNames,
				StringUtils.concat(CollectionUtils.sort(actualNames), ", "));
	}
}
