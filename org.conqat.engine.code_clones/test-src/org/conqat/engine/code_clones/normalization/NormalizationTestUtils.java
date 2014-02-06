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
package org.conqat.engine.code_clones.normalization;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.conqat.engine.code_clones.core.StatementUnit;
import org.conqat.engine.code_clones.core.TokenUnit;
import org.conqat.engine.code_clones.core.Unit;
import org.conqat.engine.code_clones.normalization.provider.ListBasedTokenProvider;
import org.conqat.engine.code_clones.normalization.statement.StatementNormalization;
import org.conqat.engine.code_clones.normalization.token.ITokenProvider;
import org.conqat.engine.code_clones.normalization.token.TokenNormalization;
import org.conqat.engine.code_clones.normalization.token.TokenNormalizationTest;
import org.conqat.engine.code_clones.normalization.token.configuration.ITokenConfiguration;
import org.conqat.engine.code_clones.normalization.token.configuration.TokenConfigurationDef;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.testutils.ProcessorInfoMock;
import org.conqat.engine.resource.scope.memory.InMemoryContentAccessor;
import org.conqat.engine.sourcecode.resource.TokenContainer;
import org.conqat.engine.sourcecode.resource.TokenElement;
import org.conqat.lib.scanner.ELanguage;
import org.conqat.lib.scanner.IScanner;
import org.conqat.lib.scanner.IToken;
import org.conqat.lib.scanner.ScannerException;
import org.conqat.lib.scanner.ScannerFactory;
import org.conqat.lib.scanner.ScannerUtils;

/**
 * Offers methods for convenient test data construction for
 * normalization-related tests. All methods that create test data that is used
 * by more than one test case are to be placed here.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43764 $
 * @ConQAT.Rating GREEN Hash: 1339720606CF8A21FBAF85CA7372464D
 */
public class NormalizationTestUtils {

	/** String used as origin for test data */
	public static final String UNKNOWN_FILE = "unknown";

	/** Reads a list of tokens from a file */
	public static List<IToken> createTokens(File file, ELanguage language)
			throws IOException, ScannerException {
		IScanner scanner = ScannerFactory.newScanner(language, file);
		return readTokens(scanner);
	}

	/** Reads a list of tokens from a code fragment */
	public static List<IToken> createTokens(String code, ELanguage language)
			throws IOException, ScannerException {
		IScanner scanner = ScannerFactory.newScanner(language, code,
				UNKNOWN_FILE);
		return readTokens(scanner);
	}

	/** Read tokens from scanner into list */
	private static List<IToken> readTokens(IScanner scanner)
			throws IOException, ScannerException {
		ArrayList<ScannerException> exceptions = new ArrayList<ScannerException>();

		List<IToken> tokens = new ArrayList<IToken>();
		ScannerUtils.readTokens(scanner, tokens, exceptions);
		if (exceptions.size() > 0) {
			throw exceptions.get(0);
		}
		return tokens;
	}

	/** Create list of token units from texts */
	public static List<TokenUnit> createTokenUnits(String... texts) {
		List<TokenUnit> tokenUnits = new ArrayList<TokenUnit>();

		for (String text : texts) {
			TokenUnit tokenUnit = new TokenUnit(text,
					TokenNormalizationTest.UNKNOWN_NUMBER,
					TokenNormalizationTest.UNKNOWN_NUMBER, UNKNOWN_FILE, null,
					0);
			tokenUnits.add(tokenUnit);
		}

		return tokenUnits;
	}

	/** Returns a normalization for the code fragment */
	public static TokenNormalization createTokenNormalizationFor(String code,
			ELanguage language) throws IOException, ScannerException,
			ConQATException {
		return NormalizationTestUtils.normalizationFor(
				createTokens(code, language), language);
	}

	/** Returns a normalization for the token list */
	public static TokenNormalization normalizationFor(List<IToken> tokenList,
			ELanguage language) throws ConQATException {
		ITokenProvider tokenProvider = new ListBasedTokenProvider(tokenList);
		TokenConfigurationDef defaultConfiguration = new TokenConfigurationDef();
		defaultConfiguration.setAll();
		TokenNormalization normalization = new TokenNormalization(
				tokenProvider, new ArrayList<ITokenConfiguration>(),
				defaultConfiguration);

		normalization.init(createTokenResource(tokenList, language),
				new ProcessorInfoMock().getLogger());
		return normalization;
	}

	/** Creates a token resource for a list of tokens. */
	private static TokenContainer createTokenResource(List<IToken> tokenList,
			ELanguage language) {
		Set<String> origins = new HashSet<String>();
		for (IToken token : tokenList) {
			origins.add(token.getOriginId());
		}
		TokenContainer container = new TokenContainer("root");
		for (String origin : origins) {
			container.addChild(new TokenElement(new InMemoryContentAccessor(
					origin, new byte[0]), Charset.defaultCharset(), language));
		}
		return container;
	}

	/** Creates statements from code fragment */
	public static List<StatementUnit> createStatementUnitsFor(String code,
			ELanguage language) throws IOException, ScannerException,
			ConQATException {
		List<IToken> tokens = createTokens(code, language);
		return NormalizationTestUtils.createStatementUnitsFor(tokens,
				normalizationFor(tokens, language), ELanguage.JAVA);
	}

	/**
	 * Utility method: runs a {@link StatementNormalization} on a
	 * {@link TokenNormalization} and returns a list with the created statements
	 */
	public static List<StatementUnit> createStatementUnitsFor(
			List<IToken> tokenList, TokenNormalization normalization,
			ELanguage language) throws ConQATException {

		StatementNormalization statementBuilder = new StatementNormalization(
				normalization, true);
		statementBuilder.init(createTokenResource(tokenList, language),
				new ProcessorInfoMock().getLogger());

		List<StatementUnit> statements = new ArrayList<StatementUnit>();
		Unit statement = statementBuilder.getNext();
		while (statement != null) {
			if (statement instanceof StatementUnit) {
				statements.add((StatementUnit) statement);
			}
			statement = statementBuilder.getNext();
		}
		return statements;
	}

}