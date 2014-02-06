package org.conqat.engine.html_presentation.listing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.conqat.lib.commons.algo.Diff;
import org.conqat.lib.commons.algo.Diff.Delta;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.commons.test.CCSMTestCaseBase;

/**
 * Test case for {@link LineDiffColorProvider}
 * 
 * TODO (BH): Please resolve warnings.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 44692 $
 * @ConQAT.Rating RED Hash: 543E24E3C2DE3F537E1F4B5F110D5A25
 */
public class LineDiffColorProviderTest extends CCSMTestCaseBase {

	public void testSingleChar() throws Exception {
		assertInsertions("a", "a", asList());
	}

	/** Returns a list that contains the passed integers */
	private List<Integer> asList(int... numbers) {
		List<Integer> list = new ArrayList<Integer>();
		for (int number : numbers) {
			list.add(number);
		}
		return list;
	}

	public void testEmpty() throws Exception {
		assertInsertions("", "", asList());
		assertDeletions("", "", asList());
	}

	public void testLargeIdentical() throws Exception {
		String word1 = "abcd";
		assertInsertions(word1, word1, asList());
		assertDeletions(word1, word1, asList());
	}

	public void testInsertInMiddle() throws Exception {
		String word1 = "abcd";
		String word2 = "abXcd";
		assertInsertions(word1, word2, asList(2));
		assertDeletions(word1, word2, asList());
	}

	public void testInsertHead() throws Exception {
		String word1 = "abcd";
		String word2 = "Xabcd";
		assertInsertions(word1, word2, asList(0));
		assertDeletions(word1, word2, asList());
	}

	public void testInsertTail() throws Exception {
		String word1 = "abcd";
		String word2 = "abcdX";
		assertInsertions(word1, word2, asList(4));
		assertDeletions(word1, word2, asList());
	}

	public void testModify() throws Exception {
		String word1 = "a";
		String word2 = "b";
		assertInsertions(word1, word2, asList(0));
		assertDeletions(word1, word2, asList());
	}

	public void testModifyInMiddle() throws Exception {
		String word1 = "aXb";
		String word2 = "aYb";
		assertInsertions(word1, word2, asList(1));
		assertDeletions(word1, word2, asList(2));
	}

	public void testDeleteInMiddle() throws Exception {
		String word1 = "aXc";
		String word2 = "ac";
		assertInsertions(word1, word2, asList());
		assertDeletions(word1, word2, asList(1));
	}

	public void testDeleteLargeInMiddle() throws Exception {
		String word1 = "aXYGFDc";
		String word2 = "ac";
		assertInsertions(word1, word2, asList());
		assertDeletions(word1, word2, asList(1));
	}

	public void testDeleteAndModify() throws Exception {
		String word1 = "aXc";
		String word2 = "af";
		assertInsertions(word1, word2, asList(1));
		assertDeletions(word1, word2, asList());
	}

	public void testHead() throws Exception {
		String word1 = "aXcde";
		String word2 = "cde";
		assertInsertions(word1, word2, asList());
		assertDeletions(word1, word2, asList(0));
	}

	/** Make sure that line colors are as expected */
	private void assertInsertions(String word1, String word2,
			List<Integer> expected) {
		Delta<Character> delta = Diff.computeDelta(asCharArray(word1),
				asCharArray(word2));

		List<Integer> actual = new LineDiffColorProvider(delta).getInsertions();
		assertEquals("Expected: " + StringUtils.concat(expected, ",")
				+ " but was " + StringUtils.concat(actual, ","), expected,
				actual);
	}

	/** Make sure that line colors are as expected */
	private void assertDeletions(String word1, String word2,
			List<Integer> expected) {
		Delta<Character> delta = Diff.computeDelta(asCharArray(word1),
				asCharArray(word2));

		List<Integer> actual = new LineDiffColorProvider(delta).getDeletions();
		String expectedString = StringUtils.concat(expected, ",");
		String actualString = StringUtils.concat(actual, ",");
		assertEquals(
				"Expected: " + expectedString + " but was " + actualString,
				expectedString, actualString);
	}

	/** Split chars and turn into array */
	private List<Character> asCharArray(String word) {
		return Arrays.asList(StringUtils.splitChars(word));
	}

}
