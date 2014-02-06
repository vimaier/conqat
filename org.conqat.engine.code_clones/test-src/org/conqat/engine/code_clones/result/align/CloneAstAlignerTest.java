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
package org.conqat.engine.code_clones.result.align;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.code_clones.core.utils.StableCloneClassComparator;
import org.conqat.engine.code_clones.core.utils.StableCloneComparator;
import org.conqat.engine.code_clones.detection.CloneDetectionResultElement;
import org.conqat.engine.commons.findings.location.TextRegionLocation;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.engine.sourcecode.resource.TokenTestCaseBase;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.LineOffsetConverter;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.scanner.ELanguage;

/**
 * Tests the {@link CloneAstAligner}. Note that the clone regions found are not
 * necessarily the correct/best clones a detector could find, but rather chosen
 * for testing certain features. The test uses 1-based line numbers (although
 * the clone classes internally use 0-based lines), as they are easier available
 * in the editor.
 * 
 * @author $Author: goede $
 * @version $Rev: 44842 $
 * @ConQAT.Rating GREEN Hash: C427A84544FC822E0783914E858D23E2
 */
@SuppressWarnings("unchecked")
public class CloneAstAlignerTest extends TokenTestCaseBase {

	/**
	 * Tests whether extension of clones (e.g. including closing braces) works
	 * as expected.
	 */
	public void testExtension() throws Exception {
		List<CloneClass> alignedClasses = runAlignment("Extend.java", 7, 13,
				19, 25);
		assertEquals(1, alignedClasses.size());
		assertEquals("7-15, 19-27",
				toClonePositionString(alignedClasses.get(0)));
	}

	/**
	 * Tests whether trimming of clones (e.g. removing partial for-loop headers)
	 * works.
	 */
	public void testTrimming() throws Exception {
		List<CloneClass> alignedClasses = runAlignment("Trim.java", 7, 14, 22,
				29);
		assertEquals(1, alignedClasses.size());
		assertEquals("8-12, 23-27",
				toClonePositionString(alignedClasses.get(0)));
	}

	/** Tests whether splitting of clones along the AST works. */
	public void testSplitting() throws Exception {
		List<CloneClass> alignedClasses = runAlignment("Split.java", 8, 20, 29,
				42);
		assertEquals(2, alignedClasses.size());

		// The order or clones is stable, but determined by fingerprint. Thus,
		// the order might change for non-obvious reasons
		assertEquals("8-14, 29-35",
				toClonePositionString(alignedClasses.get(0)));
		assertEquals("17-21, 38-42",
				toClonePositionString(alignedClasses.get(1)));
	}

	/**
	 * Tests with multiple cloned methods (based on a bug during initial
	 * development).
	 */
	public void testMethodLevelClones() throws Exception {
		List<CloneClass> alignedClasses = runAlignment("CCSMPre.java", 74, 103,
				74, 103);
		assertEquals(1, alignedClasses.size());

		// as we input the same clone twice, we get only a single clone in
		// response (set semantics)
		assertEquals("74-105", toClonePositionString(alignedClasses.get(0)));
	}

	/**
	 * Tests whether merging of clone classes works. We expect clone classes
	 * that are the same after alignment to be merged to one.
	 */
	public void testMerging() throws Exception {
		List<CloneClass> alignedClasses = runAlignment("Trim.java",
				Arrays.asList(8, 17, 23, 32), Arrays.asList(8, 18, 23, 33));
		assertEquals(1, alignedClasses.size());
		assertEquals("8-17, 23-32",
				toClonePositionString(alignedClasses.get(0)));
	}

	/**
	 * Runs alignment using the given code file and clone positions. All lines
	 * are 1-based and inclusive. Returns the clone classes after alignment
	 * (sorted by {@link StableCloneClassComparator}).
	 */
	private List<CloneClass> runAlignment(String testFileName,
			int clone1FirstLine, int clone1LastLine, int clone2FirstLine,
			int clone2LastLine) throws Exception {
		return runAlignment(testFileName, Arrays.asList(clone1FirstLine,
				clone1LastLine, clone2FirstLine, clone2LastLine));
	}

	/**
	 * Runs alignment using the given code file and clone positions. All lines
	 * are 1-based and inclusive. Returns the clone classes after alignment
	 * (sorted by {@link StableCloneClassComparator}).
	 * 
	 * @param cloneClassDescriptions
	 *            each list corresponds to a clone class. The indexes of the
	 *            list are interpreted in pairs, where each pair is a start and
	 *            end line (1-based, inclusive).
	 */
	private List<CloneClass> runAlignment(String testFileName,
			List<Integer>... cloneClassDescriptions) throws Exception {
		String normalizedContent = StringUtils.replaceLineBreaks(
				FileSystemUtils.readFileUTF8(useTestFile(testFileName)), "\n");
		LineOffsetConverter lineOffsetConverter = new LineOffsetConverter(
				normalizedContent);

		List<CloneClass> cloneClasses = new ArrayList<CloneClass>();
		for (List<Integer> cloneClassDescription : cloneClassDescriptions) {
			assertTrue(cloneClassDescription.size() > 0
					&& cloneClassDescription.size() % 2 == 0);

			CloneClass cloneClass = new CloneClass(cloneClassDescription.get(1)
					- cloneClassDescription.get(0) + 1, 1);
			for (int i = 0; i < cloneClassDescription.size(); i += 2) {
				createClone(cloneClass, testFileName, lineOffsetConverter,
						cloneClassDescription.get(i),
						cloneClassDescription.get(i + 1));
			}
			cloneClasses.add(cloneClass);
		}

		ITokenResource tokenScope = createTokenScope(useTestFile("."),
				ELanguage.JAVA, new String[] { "**.java" },
				new String[] { "**/.svn/**" });
		CloneDetectionResultElement input = new CloneDetectionResultElement(
				new Date(), tokenScope, cloneClasses);

		CloneDetectionResultElement output = (CloneDetectionResultElement) executeProcessor(
				CloneAstAligner.class, "('detection-result'=(ref=", input,
				"), clonelength=(min=3))");

		return CollectionUtils.sort(output.getList(),
				StableCloneClassComparator.INSTANCE);
	}

	/** Creates a clone in the given clone class. All lines are 1-based. */
	private void createClone(CloneClass cloneClass, String testFileName,
			LineOffsetConverter lineOffsetConverter, int firstLine, int lastLine) {

		int startOffset = lineOffsetConverter.getOffset(firstLine);
		int endOffset = lineOffsetConverter.getOffset(lastLine + 1) - 1;

		String uniformPath = "TEST/" + testFileName;
		new Clone(2, cloneClass, new TextRegionLocation(testFileName,
				uniformPath, startOffset, endOffset, firstLine, lastLine),
				firstLine, lastLine, "fingerprint");
	}

	/**
	 * Returns a string encoding the positions of the contained clones.
	 * Positions are 1-based lines.
	 */
	private static String toClonePositionString(CloneClass cloneClass) {
		StringBuilder builder = new StringBuilder();
		for (Clone clone : CollectionUtils.sort(cloneClass.getClones(),
				StableCloneComparator.INSTANCE)) {
			if (builder.length() > 0) {
				builder.append(", ");
			}
			builder.append(clone.getLocation().getRawStartLine());
			builder.append("-");
			builder.append(clone.getLocation().getRawEndLine());
		}
		return builder.toString();
	}
}
