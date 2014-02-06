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
package org.conqat.engine.code_clones.core.matching;

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.code_clones.core.TokenUnit;
import org.conqat.engine.core.logging.testutils.LoggerMock;
import org.conqat.engine.sourcecode.resource.TokenTestCaseBase;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.conqat.lib.scanner.IToken;

/**
 * Test case for {@link UnitListDiffer}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 36152 $
 * @ConQAT.Rating GREEN Hash: 6BFD16D6CAC6AAD3DA6BAF41089D537C
 */
public class UnitListDifferTest extends TokenTestCaseBase {

	/** Tests diff computation */
	public void testDiffThree() throws Exception {
		// assert equal
		assertDelta(computeDelta("a", "a", "a"), 1, 0, 0);

		// assert that result is independent of order; a new value that only
		// occurs once is treated as a difference
		assertDelta(computeDelta("a", "a", "b"), 0, 0, 1);
		assertDelta(computeDelta("a", "b", "a"), 0, 0, 1);
		assertDelta(computeDelta("b", "a", "a"), 0, 0, 1);
	}

	/** Tests diff computation */
	public void testDiffTwo() throws Exception {
		// assert equal tokens
		assertDelta(computeDelta("a", "a"), 1, 0, 0);
		assertDelta(computeDelta("a b a b", "a b a b"), 4, 0, 0);
		assertDelta(computeDelta("a a a a", "a a a a"), 4, 0, 0);

		// assert differences
		assertDelta(computeDelta("a b X b", "a b a b"), 3, 0, 1);
		assertDelta(computeDelta("X b a b", "a b a b"), 3, 0, 1);
		assertDelta(computeDelta("a b a b", "X b a b"), 3, 0, 1);

		// assert renames (deviating values that occur more than once)
		assertDelta(computeDelta("a a a a", "A A A A"), 0, 4, 0);
		assertDelta(computeDelta("a a c c", "A A C C"), 0, 4, 0);
		assertDelta(computeDelta("a a a a", "A A C C"), 0, 4, 0);

		// assert differences and renames
		assertDelta(computeDelta("a a a a", "A A A C"), 0, 3, 1);

		assertDelta(computeDelta("a b c d e a b c d e", "A b X d e A b c d e"),
				7, 2, 1);

		assertDelta(
				computeDelta("a b c d e a public b c d e",
						"A b X d e A b /* comment gets removed */ c d e"), 7,
				2, 1);
	}

	/** Assert expected count of equal, matches and differences in delta */
	private void assertDelta(UnitListDelta delta, int expectedEquals,
			int expectedMatches, int expectedDifferences) {
		assertEquals(expectedEquals, delta.getEquals());
		assertEquals(expectedMatches, delta.getRenames());
		assertEquals(expectedDifferences, delta.getDifferences());
	}

	/** Compute {@link UnitListDelta} between two strings */
	private UnitListDelta computeDelta(String... contents) throws Exception {
		List<List<String>> tokenLists = new ArrayList<List<String>>();
		for (String content : contents) {
			tokenLists.add(filter(tokensFor(content)));
		}
		return UnitListDiffer.computeDelta(tokenLists);
	}

	/** Remove all tokens that are not required for similarity computation */
	private List<String> filter(List<IToken> tokens) {
		List<String> filtered = new ArrayList<String>();

		for (IToken token : tokens) {
			// We filter all token types that cannot influence
			// over-normalization, such as comments or keywords
			if (TokenUnit.couldBeNormalized(token.getType())) {
				filtered.add(token.getText());
			}
		}

		return filtered;
	}

	/** Turn a string into a list of tokens */
	private UnmodifiableList<IToken> tokensFor(String content) throws Exception {
		return createTokenElement(content).getTokens(new LoggerMock());
	}
}
