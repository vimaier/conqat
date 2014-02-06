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
package org.conqat.engine.code_clones.normalization.repetition;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.code_clones.core.CloneDetectionException;
import org.conqat.engine.code_clones.core.StatementUnit;
import org.conqat.engine.code_clones.normalization.statement.StatementNormalization;
import org.conqat.engine.code_clones.normalization.token.TokenNormalization;
import org.conqat.engine.code_clones.normalization.token.configuration.ITokenConfiguration;
import org.conqat.engine.code_clones.normalization.token.configuration.TokenConfigurationDef;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.testutils.ProcessorInfoMock;
import org.conqat.engine.resource.scope.memory.InMemoryContentAccessor;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.TokenElement;
import org.conqat.engine.sourcecode.resource.TokenTestCaseBase;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.scanner.ELanguage;
import org.conqat.lib.scanner.IScanner;
import org.conqat.lib.scanner.ScannerFactory;

/**
 * Test case for {@link StatementEquator}
 * 
 * @author juergens
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: 373AFB0606D1D7A274910C7AC07D9DC2
 */
public class StatementEquatorTest extends TokenTestCaseBase {

	/** Name of file that contains statements with equal token types */
	public static final String STATEMENTS_WITH_EQUAL_TOKEN_TYPES = "StatementsWithEqualTokenTypes.cs";

	/** Name of file that contains statements with unequal token types */
	public static final String STATEMENTS_WITH_DIFFERENT_TOKEN_TYPES = "StatementsWithDifferentTokenTypes.cs";

	/** {@link StatementEquator} under test */
	private StatementEquator comparer;

	/** {@inheritDoc} */
	@Override
	protected void setUp() {
		comparer = new StatementEquator();
	}

	/**
	 * Assert that {@link StatementEquator} recognizes statements with equal
	 * token types as equal
	 */
	public void testEqualSequences() throws IOException, ConQATException {
		List<StatementUnit> sequences = readStatementsFrom(useTestFile(STATEMENTS_WITH_EQUAL_TOKEN_TYPES));

		StatementUnit reference = sequences.get(0);
		for (StatementUnit sequence : sequences) {
			assertTrue(comparer.equals(reference, sequence));
		}
	}

	/**
	 * Assert that {@link StatementEquator} recognizes statements with unequal
	 * token types as unequal
	 */
	public void testDifferentSequences() throws IOException, ConQATException {
		List<StatementUnit> sequences = readStatementsFrom(useTestFile(STATEMENTS_WITH_DIFFERENT_TOKEN_TYPES));

		StatementUnit reference = sequences.get(0);
		for (StatementUnit sequence : sequences) {
			if (reference == sequence) {
				continue; // skip first one
			}
			assertFalse(comparer.equals(reference, sequence));
		}
	}

	/**
	 * Helper method that reads the content of a file into a list of
	 * {@link StatementUnit}s
	 */
	public static List<StatementUnit> readStatementsFrom(File file)
			throws IOException, ConQATException {
		return readStatementsFrom(file, ELanguage.CS);
	}

	/**
	 * Helper method that reads the content of a file into a list of
	 * {@link StatementUnit}s
	 */
	public static List<StatementUnit> readStatementsFrom(File file,
			ELanguage language) throws IOException, ConQATException,
			CloneDetectionException {
		// Set up statement normalization on input file; "foo" is the unique
		// path used for the dummy element created below
		IScanner scanner = ScannerFactory.newScanner(language,
				FileSystemUtils.readFile(file), "foo");
		TokenConfigurationDef configuration = new TokenConfigurationDef();
		configuration.setAll();
		configuration.setIgnoreEndOfStatementTokens(false);
		TokenNormalization transformation = new TokenNormalization(
				new ScannerAdapter(scanner),
				new ArrayList<ITokenConfiguration>(), configuration);
		StatementNormalization sequencer = new StatementNormalization(
				transformation, true);
		ITokenElement element = new TokenElement(new InMemoryContentAccessor(
				"foo", new byte[0]), Charset.defaultCharset(), language);
		sequencer.init(element, new ProcessorInfoMock().getLogger());

		return RepetitionUtils.drainStatementsOnly(sequencer);
	}
}