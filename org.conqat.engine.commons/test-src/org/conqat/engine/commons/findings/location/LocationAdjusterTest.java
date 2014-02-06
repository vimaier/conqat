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
package org.conqat.engine.commons.findings.location;

import java.util.Random;

import org.conqat.lib.commons.region.Region;
import org.conqat.lib.commons.string.LineOffsetConverter;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.commons.test.CCSMTestCaseBase;

/**
 * Tests the {@link LocationAdjuster}.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46933 $
 * @ConQAT.Rating GREEN Hash: E1F14DC5474B5F30B20C5092EF931171
 */
public class LocationAdjusterTest extends CCSMTestCaseBase {

	/** Tests for unchanged strings. */
	public void testNoChange() {
		assertInvalidAdjustement("abc def ghi", "abc def ghi", -4, -3);
		assertInvalidAdjustement("abc def ghi", "abc def ghi", 5, 5);

		assertAdjustement("abc def ghi", "abc def ghi", 4, 6, 4, 6);
		assertAdjustement("abc def ghi", "abc def ghi", 3, 6, 4, 6);
		assertAdjustement("abc def ghi", "abc def ghi", 4, 7, 4, 6);

		assertAdjustement("abc def ghi", "abc def ghi", 7, 700, 8, 10);
		assertAdjustement("abc def ghi", "abc def ghi", -700, 700, 0, 10);
	}

	/** Tests for insertions in strings. */
	public void testInsertions() {
		assertAdjustement("012 345 678", "012 ABC 345 678", 0, 2, 0, 2);
		assertAdjustement("012 345 678", "012 ABC 345 678", 3, 6, 8, 10);
		assertAdjustement("012 345 678", "012 ABC 345 678", 0, 7, 0, 10);

		assertAdjustement("012 345 678", "012 A B C D E F 345 678", 0, 7, 0, 18);
	}

	/** Tests for deletions from strings. */
	public void testDeletions() {
		assertAdjustement("012 345 678", "012 678", 0, 2, 0, 2);
		assertAdjustement("012 345 678", "012 678", 8, 10, 4, 6);
		assertInvalidAdjustement("012 345 678", "012 678", 4, 6);
		assertAdjustement("012 345 678", "012 678", 0, 10, 0, 6);

		// too many discarded tokens make the adjustment invalid
		assertInvalidAdjustement("012 3 a 4 a 5 678", "012 678", 0, 20);
	}

	/** Tests for replacements in strings. */
	public void testReplacements() {
		assertAdjustement("012 ABC 678", "012 DEF 678", 0, 2, 0, 2);
		assertAdjustement("012 ABC 678", "012 DEF 678", 8, 10, 8, 10);
		assertInvalidAdjustement("012 ABC 678", "012 DEF 678", 4, 6);

		assertAdjustement("012 ABC 678", "012 DEF 678", 0, 10, 0, 10);
		assertAdjustement("012 ABC 678", "012 DEF 678", 0, 6, 0, 2);
	}

	/**
	 * Asserts that the given adjustment is invalid, i.e. adjusting returns
	 * null.
	 */
	private void assertInvalidAdjustement(String originalString,
			String adjustedString, int originalStart, int originalEnd) {
		Region adjustedOffset = new LocationAdjuster(originalString,
				adjustedString).getAdjustedRegion(originalStart, originalEnd);
		assertNull(adjustedOffset);
	}

	/** Asserts that offset adjustment returns the expected offset */
	private void assertAdjustement(String originalString,
			String adjustedString, int originalStart, int originalEnd,
			int expectedAdjustedStart, int expectedAdjustedEnd) {
		Region adjustedOffset = new LocationAdjuster(originalString,
				adjustedString).getAdjustedRegion(originalStart, originalEnd);
		assertNotNull(adjustedOffset);
		assertEquals(
				new Region(expectedAdjustedStart, expectedAdjustedEnd)
						.toString(),
				adjustedOffset.toString());
	}

	/** Tests location adjustment with large input text. */
	public void testLargeInput() {
		StringBuilder original = new StringBuilder();
		StringBuilder adjusted = new StringBuilder();

		Random r = new Random(42);
		for (int line = 0; line < 20000; ++line) {
			for (int token = 0; token < 10; ++token) {
				String text = StringUtils.randomString(10, r);
				original.append(text + " ");
				if (line != 4242) {
					adjusted.append(text + " ");
				}
			}

			original.append("\n");
			adjusted.append("\r\n");
		}

		String originalText = original.toString();
		LineOffsetConverter originalLineOffsetConverter = new LineOffsetConverter(
				originalText);

		String adjustedText = adjusted.toString();
		LineOffsetConverter adjustedLineOffsetConverter = new LineOffsetConverter(
				adjustedText);

		int checkedLine = 13456;
		assertAdjustement(originalText, adjustedText,
				originalLineOffsetConverter.getOffset(checkedLine),
				originalLineOffsetConverter.getOffset(checkedLine + 1) - 1,
				adjustedLineOffsetConverter.getOffset(checkedLine),
				adjustedLineOffsetConverter.getOffset(checkedLine + 1) - 4);
	}

	/** Tests line conversion on multiline strings. */
	public void testLineConversion() {
		String originalString = "abc\ndef\nghi\njkl";
		TextRegionLocation location = new TextRegionLocation("", "", 4, 10, 2,
				3);

		TextRegionLocation windowsLocation = new LocationAdjuster(
				originalString, "abc\r\ndef\r\nghi\r\njkl")
				.adjustLocation(location);
		assertEquals(5, windowsLocation.getRawStartOffset());
		assertEquals(12, windowsLocation.getRawEndOffset());
		assertEquals(2, windowsLocation.getRawStartLine());
		assertEquals(3, windowsLocation.getRawEndLine());
	}

}
