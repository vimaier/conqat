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
import java.util.List;

import org.conqat.engine.code_clones.core.StatementUnit;
import org.conqat.engine.code_clones.normalization.NormalizationTestUtils;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.equals.IEquator;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.test.CCSMTestCaseBase;
import org.conqat.lib.scanner.ELanguage;
import org.conqat.lib.scanner.ScannerException;

/**
 * Test cases for the {@link RepetitionFinder} class.
 * 
 * @author $Author: juergens $
 * @version $Revision: 34670 $
 * @ConQAT.Rating GREEN Hash: AEF2E95618248BC62EAE51346B2A8050
 */
public class RepetitionFinderTest extends CCSMTestCaseBase {

	/** Tests repetition detection on a list of 10 C# constant declarations */
	public void testConstantDecls() throws IOException, ScannerException,
			ConQATException {
		String code = FileSystemUtils.readFile(useTestFile("constants.cs"));
		List<StatementUnit> units = NormalizationTestUtils
				.createStatementUnitsFor(code, ELanguage.JAVA);
		StatementUnit[] input = units.toArray(new StatementUnit[] {});
		assertEquals(10, input.length);

		RepetitionFinder<StatementUnit> detector = new RepetitionFinder<StatementUnit>(
				input, StatementEquator.getInstance(), 10, 2);
		List<Repetition<StatementUnit>> repetitions = detector
				.findRepetitionFor(1);

		assertEquals(1, repetitions.size());
	}

	/**
	 * Tests repetition detection on a list of alternating java getters and
	 * setters
	 */
	public void testGetterSetters() throws IOException, ScannerException,
			ConQATException {
		String code = FileSystemUtils.readFile(useTestFile("getters.java"));
		List<StatementUnit> units = NormalizationTestUtils
				.createStatementUnitsFor(code, ELanguage.JAVA);
		StatementUnit[] input = units.toArray(new StatementUnit[] {});

		RepetitionFinder<StatementUnit> detector = new RepetitionFinder<StatementUnit>(
				input, StatementEquator.getInstance(), 10, 2);
		List<Repetition<StatementUnit>> repetitions = detector
				.findRepetitionFor(4);

		assertTrue("Should find repetitions", repetitions.size() > 0);
	}

	/** Tests detection of simple repetition */
	public void testDetectSimpleSequenceLength2() {
		String[] input = splitIntoLetters("1212xx121212");

		RepetitionFinder<String> detector = new RepetitionFinder<String>(input,
				new StringEquator(), 2, 2);

		assertEquals(2, detector.findRepetitionFor(2).size());
	}

	/** Tests detection of simple repetition */
	public void testDetectSimpleSequenceLength5() {
		String[] input = splitIntoLetters("1234512345xx12345xx1234512345");

		RepetitionFinder<String> detector = new RepetitionFinder<String>(input,
				new StringEquator(), 5, 2);

		assertEquals(2, detector.findRepetitionFor(5).size());
	}

	/** Tests whether only repetitions that are long enough are found */
	public void testMinLength() {
		String[] input = splitIntoLetters("1212xxx1234512345");

		RepetitionFinder<String> detector = new RepetitionFinder<String>(input,
				new StringEquator(), 5, 2);

		assertEquals(1, detector.findRepetitions(1, 5).size());
	}

	/** Tests whether only repetitions that are long enough are found */
	public void testMinLength2() {
		String[] input = splitIntoLetters("xxxxxxxxxx");

		RepetitionFinder<String> detector = new RepetitionFinder<String>(input,
				new StringEquator(), 10, 2);

		assertEquals(1, detector.findRepetitions(1, 5).size());
	}

	/** Tests whether only repetitions that are long enough are found */
	public void testMinLength3() {
		String[] input = splitIntoLetters("xxxxxxxxxx");

		RepetitionFinder<String> detector = new RepetitionFinder<String>(input,
				new StringEquator(), 11, 2);

		assertEquals(0, detector.findRepetitions(1, 5).size());
	}

	/**
	 * Tests that no long repetitions are found in regions that already contain
	 * repetitions with a shorter motiv
	 */
	public void testNonOverlapping() {
		String[] input = splitIntoLetters("xx1111111111xx");

		RepetitionFinder<String> detector = new RepetitionFinder<String>(input,
				new StringEquator(), 3, 2);

		assertEquals(1, detector.findRepetitions(1, 10).size());
	}

	/**
	 * Runs the repetition detector on the a file that contains two repetitions
	 * of declarations
	 */
	public void testDeclarations() throws IOException, ConQATException {
		List<StatementUnit> tokenSequences = StatementEquatorTest
				.readStatementsFrom(useTestFile("declarations.cs"));

		StatementEquator equator = new StatementEquator();
		RepetitionFinder<StatementUnit> detector = new RepetitionFinder<StatementUnit>(
				tokenSequences.toArray(new StatementUnit[] {}), equator, 2, 2);

		assertEquals(2, detector.findRepetitionFor(1).size());
	}

	/**
	 * Runs the repetition detector on the a file that contains two repetitions
	 * of assignments
	 */
	public void testAbapAssignments() throws IOException, ConQATException {
		File file = useTestFile("assignments.abap");
		List<StatementUnit> tokenSequences = StatementEquatorTest
				.readStatementsFrom(file, ELanguage.ABAP);

		StatementEquator equator = new StatementEquator();
		RepetitionFinder<StatementUnit> detector = new RepetitionFinder<StatementUnit>(
				tokenSequences.toArray(new StatementUnit[] {}), equator, 2, 2);

		assertTrue("Expecting at least one repetition", detector
				.findRepetitionFor(1).size() > 0);
	}

	/** Test computation of repetition length */
	public void testLength() {
		String[] input = splitIntoLetters("xx");

		RepetitionFinder<String> detector = new RepetitionFinder<String>(input,
				new StringEquator(), 1, 2);

		assertEquals(2, detector.findRepetitionFor(1).get(0).getLength());
	}

	/** Cuts a string into an array of single letters */
	private String[] splitIntoLetters(String s) {
		String[] array = new String[s.length()];
		for (int i = 0; i < s.length(); i++) {
			array[i] = s.substring(i, i + 1);
		}
		return array;
	}

	/** Sequence element equator for test purposes. */
	private class StringEquator implements IEquator<String> {

		/** {@inheritDoc} */
		@Override
		public boolean equals(String element1, String element2) {
			if (element1 == null) {
				return false;
			}
			return element1.equals(element2);
		}
	}

}