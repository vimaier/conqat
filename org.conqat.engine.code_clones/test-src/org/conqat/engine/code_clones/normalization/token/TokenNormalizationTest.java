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
package org.conqat.engine.code_clones.normalization.token;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.conqat.engine.code_clones.core.CloneDetectionException;
import org.conqat.engine.code_clones.core.TokenUnit;
import org.conqat.engine.code_clones.lazyscope.TokenElementProvider;
import org.conqat.engine.code_clones.normalization.NormalizationTestUtils;
import org.conqat.engine.code_clones.normalization.token.configuration.ITokenConfiguration;
import org.conqat.engine.code_clones.normalization.token.configuration.TokenConfigurationDef;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.testutils.LoggerMock;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.TokenTestCaseBase;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.scanner.ELanguage;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;
import org.conqat.lib.scanner.ScannerException;

/**
 * Test case for {@link TokenNormalization}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43764 $
 * @ConQAT.Rating GREEN Hash: A0D5681AAF17C4DF62AB14999839BD7E
 */
public class TokenNormalizationTest extends TokenTestCaseBase {

	/** Used as placeholder for numbers irrelevant for tests */
	public static final int UNKNOWN_NUMBER = -1;

	/** Uniform name of origin */
	private final String elementUniformName = "Uniform Name";

	/** Create unnormalized mock {@link TokenUnit}s for code fragment */
	public static List<TokenUnit> normalize(String code, ELanguage language)
			throws CloneDetectionException, IOException, ScannerException,
			ConQATException {
		return normalize(NormalizationTestUtils.createTokens(code, language),
				language);
	}

	/** Create unnormalized mock {@link TokenUnit}s for list of tokens */
	public static List<TokenUnit> normalize(ELanguage language,
			IToken... tokens) throws CloneDetectionException, ConQATException {
		return normalize(Arrays.asList(tokens), language);
	}

	/** Normalize list of tokens */
	public static List<TokenUnit> normalize(List<IToken> tokenList,
			ELanguage language) throws CloneDetectionException, ConQATException {
		TokenNormalization normalization = NormalizationTestUtils
				.normalizationFor(tokenList, language);

		// retrieve normalized tokens
		List<TokenUnit> normalizedTokens = new ArrayList<TokenUnit>();
		TokenUnit normalizedToken = normalization.getNext();
		while (normalizedToken != null) {
			normalizedTokens.add(normalizedToken);
			normalizedToken = normalization.getNext();
		}

		return normalizedTokens;
	}

	/** Compare expected token units against actual token units: compare texts */
	private void assertTokenTextsEqual(List<TokenUnit> expected,
			List<TokenUnit> actual) {
		assertNotNull(expected);
		assertNotNull(actual);

		assertEquals(expected.size(), actual.size());

		for (int i = 0; i < expected.size(); i++) {
			TokenUnit expectedToken = expected.get(i);
			TokenUnit actualToken = actual.get(i);

			assertEquals(expectedToken.getContent(), actualToken.getContent());
		}
	}

	/** Compare expected token units against actual token units: compare types */
	private void assertTokenTypesEqual(List<TokenUnit> expected,
			List<TokenUnit> actual) {
		assertNotNull(expected);
		assertNotNull(actual);

		assertEquals(expected.size(), actual.size());

		for (int i = 0; i < expected.size(); i++) {
			TokenUnit expectedToken = expected.get(i);
			TokenUnit actualToken = actual.get(i);

			assertEquals(expectedToken.getType(), actualToken.getType());
		}
	}

	/** Create id token */
	private IToken id(String content) {
		return new TokenMock(ETokenType.IDENTIFIER, content);
	}

	/** Create token for keyword private */
	private IToken privat() {
		return new TokenMock(ETokenType.PRIVATE, "private");
	}

	/** Create comment token */
	private IToken comment(String text) {
		return new TokenMock(ETokenType.TRADITIONAL_COMMENT, text);
	}

	/** Test normalization of fq names */
	public void testNormalizeFullyQualifiedNames()
			throws CloneDetectionException, IOException, ScannerException,
			ConQATException {
		List<TokenUnit> expected = NormalizationTestUtils.createTokenUnits(
				"id0", "id1");
		List<TokenUnit> actualJava = normalize(
				"edu.tum.cs.ClassA edu.tum.cs.ClassB", ELanguage.JAVA);
		assertTokenTextsEqual(expected, actualJava);

		List<TokenUnit> actualCpp = normalize(
				"edu::tum::cs::ClassA edu::tum::cs::ClassB", ELanguage.CPP);
		assertTokenTextsEqual(expected, actualCpp);
	}

	/** Tests ignore comments */
	public void testIgnoreComments() throws CloneDetectionException,
			ConQATException {
		List<TokenUnit> expected = NormalizationTestUtils.createTokenUnits(
				"id0", "id0");
		List<TokenUnit> actual = normalize(ELanguage.JAVA, id("x"),
				comment("Hi"), comment("Hi"), comment("Hi"), id("x"));

		assertTokenTextsEqual(expected, actual);
	}

	/** Test case for delimiter normalization */
	public void testIgnoreDelimiters() throws CloneDetectionException,
			IOException, ScannerException, ConQATException {
		List<TokenUnit> expected = NormalizationTestUtils.createTokenUnits(
				"id0", "id1");
		List<TokenUnit> actual = normalize("x ()[] y", ELanguage.JAVA);

		assertTokenTextsEqual(expected, actual);
	}

	/** Test normalization of this reference */
	public void testIgnoreThisQualifier() throws CloneDetectionException,
			IOException, ScannerException, ConQATException {
		List<TokenUnit> expected = NormalizationTestUtils.createTokenUnits(
				"id0", "=", "id0", ";");

		List<TokenUnit> actualJava = normalize("this.x = x;", ELanguage.JAVA);
		assertTokenTextsEqual(expected, actualJava);

		List<TokenUnit> actualCpp = normalize("this->x = x;", ELanguage.CPP);
		assertTokenTextsEqual(expected, actualCpp);
	}

	/** Test normalization of this reference */
	public void testDontIgnoreSelfReference() throws CloneDetectionException,
			IOException, ScannerException, ConQATException {
		List<TokenUnit> expected = NormalizationTestUtils.createTokenUnits(
				"super", "this", ";");
		List<TokenUnit> actual = normalize("super(this);", ELanguage.JAVA);

		assertTokenTextsEqual(expected, actual);
	}

	/** Test normalization of literals */
	public void testNormalizeLiterals() throws CloneDetectionException,
			IOException, ScannerException, ConQATException {
		List<TokenUnit> expected = NormalizationTestUtils.createTokenUnits("",
				"char", "true", "0");
		List<TokenUnit> actual = normalize("\"lalala\" 'c' false 42",
				ELanguage.JAVA);

		assertTokenTextsEqual(expected, actual);
	}

	/** Tests ignore comments */
	public void testIgnoreVisibilityModifier() throws CloneDetectionException,
			ConQATException {
		List<TokenUnit> expected = NormalizationTestUtils
				.createTokenUnits("id0");
		List<TokenUnit> actual = normalize(ELanguage.JAVA, privat(), id("x"));

		assertTokenTextsEqual(expected, actual);
	}

	/** Test normalization of token type keywords */
	public void testNormalizeTypeKeyword() throws CloneDetectionException,
			IOException, ScannerException, ConQATException {
		List<TokenUnit> expected = new ArrayList<TokenUnit>();
		expected.add(new TokenUnit("int", UNKNOWN_NUMBER, UNKNOWN_NUMBER,
				elementUniformName, ETokenType.IDENTIFIER, 0));
		expected.add(new TokenUnit("double", UNKNOWN_NUMBER, UNKNOWN_NUMBER,
				elementUniformName, ETokenType.IDENTIFIER, 0));
		expected.add(new TokenUnit("boolean", UNKNOWN_NUMBER, UNKNOWN_NUMBER,
				elementUniformName, ETokenType.IDENTIFIER, 0));
		List<TokenUnit> actual = normalize("int double boolean", ELanguage.JAVA);

		assertTokenTypesEqual(expected, actual);
	}

	/**
	 * Test for CR#4043 that makes sure that keyword STRING is not normalized
	 * for COBOL
	 */
	public void testDontNormalizeStringForCobol()
			throws CloneDetectionException, IOException, ScannerException,
			ConQATException {
		List<TokenUnit> expected = new ArrayList<TokenUnit>();
		expected.add(new TokenUnit("string", UNKNOWN_NUMBER, UNKNOWN_NUMBER,
				elementUniformName, ETokenType.STRING, 0));
		List<TokenUnit> actual = normalize("string", ELanguage.COBOL);
		assertTokenTypesEqual(expected, actual);
	}

	/** Test ignore pragma option */
	public void testIgnorePreprocessorDirectives()
			throws CloneDetectionException, IOException, ScannerException,
			ConQATException {
		List<TokenUnit> expected = NormalizationTestUtils.createTokenUnits(
				"id0", "id1");
		List<TokenUnit> actual = normalize("x #region\n y", ELanguage.CS);

		assertTokenTextsEqual(expected, actual);
	}

	/** Tests creation of debug file content */
	public void testCreateDebugContent() throws IOException, ConQATException {
		// delete potentially lingering file from previous test run
		File oldResultFile = useTestFile("debugInput.java.tokendebug");
		if (oldResultFile.exists() && !oldResultFile.delete()) {
			throw new IOException("Could not delete old test file");
		}

		// set up root
		ITokenElement root = createTokenElement(
				useCanonicalTestFile("debugInput.java"), ELanguage.JAVA);

		// set up normalization chain
		TokenElementProvider elementProvider = new TokenElementProvider();
		TokenProvider tokenProvider = new TokenProvider(elementProvider);
		TokenConfigurationDef defaultConfig = new TokenConfigurationDef();
		defaultConfig.setNormalizeIdentifiers(true);
		TokenNormalization normalization = new TokenNormalization(
				tokenProvider, new ArrayList<ITokenConfiguration>(),
				defaultConfig, ".tokendebug");
		normalization.init(root, new LoggerMock());

		// run normalization to write debug file
		TokenUnit unit = null;
		do {
			unit = normalization.getNext();
		} while (unit != null);

		// assert expected debug file content
		String expected = FileSystemUtils
				.readFile(useTestFile("debugExpectedOutput.java"));
		String actual = FileSystemUtils
				.readFile(useTestFile("debugInput.java.tokendebug"));
		assertEquals(StringUtils.normalizeLineBreaks(expected),
				StringUtils.normalizeLineBreaks(actual));
	}
}