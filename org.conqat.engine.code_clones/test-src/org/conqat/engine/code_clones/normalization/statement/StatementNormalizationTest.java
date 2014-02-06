/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 The ConQAT Project                                   |
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
package org.conqat.engine.code_clones.normalization.statement;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.conqat.engine.code_clones.core.CloneDetectionException;
import org.conqat.engine.code_clones.core.StatementUnit;
import org.conqat.engine.code_clones.core.Unit;
import org.conqat.engine.code_clones.normalization.NormalizationTestUtils;
import org.conqat.engine.code_clones.normalization.token.TokenNormalization;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.test.CCSMTestCaseBase;
import org.conqat.lib.scanner.ELanguage;
import org.conqat.lib.scanner.IToken;
import org.conqat.lib.scanner.ScannerException;

/**
 * Test case for {@link StatementNormalization}.
 * <p>
 * Methods
 * {@link NormalizationTestUtils#createStatementUnitsFor(String, ELanguage)} and
 * {@link NormalizationTestUtils#createStatementUnitsFor(List, TokenNormalization, ELanguage)}
 * can be used to create lists of {@link StatementUnit}s for test purposes.
 * 
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: FE25E5D63653F85F38CD3580E60FA80A
 */
public class StatementNormalizationTest extends CCSMTestCaseBase {

	/** Tests statement building on string ";" */
	public void testSingleSemiColonStatement() throws CloneDetectionException,
			IOException, ScannerException, ConQATException {
		List<StatementUnit> statements = NormalizationTestUtils
				.createStatementUnitsFor(";", ELanguage.JAVA);
		assertEquals(0, statements.size());
	}

	/** Tests statement building on string ";;;;" */
	public void testMultipleSemiColonStatement()
			throws CloneDetectionException, IOException, ScannerException,
			ConQATException {
		List<StatementUnit> statements = NormalizationTestUtils
				.createStatementUnitsFor(";;;;", ELanguage.JAVA);
		assertEquals(0, statements.size());
	}

	/** Test construction of typical statements */
	public void testNormalizeStatements() throws CloneDetectionException,
			IOException, ScannerException, ConQATException {
		List<StatementUnit> statements = NormalizationTestUtils
				.createStatementUnitsFor("int x = 5; int y = x + 3;",
						ELanguage.JAVA);
		assertEquals(2, statements.size());
	}

	/** Tests statement building on empty string */
	public void testNormalizeEmptyString() throws CloneDetectionException,
			IOException, ScannerException, ConQATException {
		List<StatementUnit> statements = NormalizationTestUtils
				.createStatementUnitsFor("", ELanguage.JAVA);
		assertEquals(0, statements.size());
	}

	/** Tests statement building on unclosed statements */
	public void testNormalizeUnclosedStatement()
			throws CloneDetectionException, IOException, ScannerException,
			ConQATException {
		List<StatementUnit> statements = NormalizationTestUtils
				.createStatementUnitsFor("x", ELanguage.JAVA);
		assertEquals(1, statements.size());

		statements = NormalizationTestUtils.createStatementUnitsFor("x;y;z",
				ELanguage.JAVA);
		assertEquals(3, statements.size());
	}

	/** Tests statement building on leading colons */
	public void testLeadingColons() throws CloneDetectionException,
			IOException, ScannerException, ConQATException {
		List<StatementUnit> statements = NormalizationTestUtils
				.createStatementUnitsFor(";;;;;x = y + 5;", ELanguage.JAVA);
		assertEquals(1, statements.size());
	}

	/** Test correct functionality of type keyword normalization */
	public void testTypeKeywordNormalization() throws CloneDetectionException,
			IOException, ScannerException, ConQATException {
		List<StatementUnit> statements1 = NormalizationTestUtils
				.createStatementUnitsFor("public string a;", ELanguage.CS);
		List<StatementUnit> statements2 = NormalizationTestUtils
				.createStatementUnitsFor("public decimal a;", ELanguage.CS);

		List<StatementUnit> statements3 = NormalizationTestUtils
				.createStatementUnitsFor("public int a;", ELanguage.JAVA);
		List<StatementUnit> statements4 = NormalizationTestUtils
				.createStatementUnitsFor("public boolean a;", ELanguage.JAVA);

		assertEquals(statements1, statements2);
		assertEquals(statements3, statements4);
	}

	/** Test whether statement indexes are correct. Included to reveal CR#2440 */
	public void testStatementIndexes() throws IOException, ScannerException,
			ConQATException {
		File file1 = useTestFile("statements.java");
		File file2 = useTestFile("statements2.java");
		ELanguage language = ELanguage.JAVA;
		List<IToken> tokens = NormalizationTestUtils.createTokens(file1,
				language);
		tokens.addAll(NormalizationTestUtils.createTokens(file2, language));

		List<StatementUnit> statements = createStatementUnitsFor(tokens,
				language);

		assertEquals("Unexpected number of statements", 10, statements.size());
		for (int expectedIndex = 0; expectedIndex < 5; expectedIndex++) {
			assertEquals("Index not as expected", expectedIndex, statements
					.get(expectedIndex).getIndexInElement());
		}

		for (int expectedIndex = 0; expectedIndex < 5; expectedIndex++) {
			assertEquals("Index not as expected", expectedIndex, statements
					.get(expectedIndex + 5).getIndexInElement());
		}
	}

	/**
	 * Another test for correctness of statement indexes. Included to reveal
	 * CR#2440
	 */
	public void testAbapStatementIndexes() throws IOException,
			ScannerException, ConQATException {
		File file1 = useTestFile("statements.abap");
		ELanguage language = ELanguage.ABAP;
		List<IToken> tokens = NormalizationTestUtils.createTokens(file1,
				language);

		List<StatementUnit> statements = createStatementUnitsFor(tokens,
				language);

		int lastIndex = -1;
		for (Unit statement : statements) {
			int currentIndex = statement.getIndexInElement();
			System.err.println(currentIndex);
			assertTrue("Index too small: " + currentIndex,
					currentIndex > lastIndex);
			lastIndex = currentIndex;
		}
	}

	/** Creates the statements units for given tokens and language. */
	private static List<StatementUnit> createStatementUnitsFor(
			List<IToken> tokens, ELanguage language) throws ConQATException {
		TokenNormalization normalization = NormalizationTestUtils
				.normalizationFor(tokens, language);
		return NormalizationTestUtils.createStatementUnitsFor(tokens,
				normalization, language);
	}
}