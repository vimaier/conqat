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
package org.conqat.engine.code_clones.detection.suffixtree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import junit.framework.TestCase;

/**
 * This is a JUnit test for the {@link SuffixTree} class.
 * 
 * @author Benjamin Hummel
 * @author $Author: juergens $
 * 
 * @version $Revision: 34670 $
 * @ConQAT.Rating GREEN Hash: 4E45D05BE72A56B6FDAD0FAD8983A91D
 */
public class SuffixTreeTest extends TestCase {

	/** Test basic behaviour of the suffix tree. */
	public void testBasic() {
		String s = "Test basic behaviour of the suffix tree Test basic behaviour of the suffix Creates a list of its characters for a given string tree abcabcabc";
		@SuppressWarnings({ "unchecked", "rawtypes" })
		List<Object> word = (List) stringToList(s);
		word.add(new SuffixTree.Sentinel());
		SuffixTree stree = new SuffixTree(word);

		for (int i = 0; i < s.length(); ++i) {
			for (int j = i + 1; j <= s.length(); ++j) {
				String substr = s.substring(i, j);
				assertTrue("Should contain " + substr,
						stree.containsWord(stringToList(substr)));
			}
		}

		for (String test : new String[] { "abd", "xyz", s + "a" }) {
			assertFalse("Should not contain " + test,
					stree.containsWord(stringToList(test)));
		}

	}

	/** Creates a list of its characters for a given string. */
	public static List<Character> stringToList(String s) {
		List<Character> result = new ArrayList<Character>();
		for (int i = 0; i < s.length(); ++i) {
			result.add(s.charAt(i));
		}
		return result;
	}

	/** Test using a larger string. */
	public void testBigString() {
		String alpha = "abcdefghijklmnopqrstuvwxyz";
		StringBuilder sb = new StringBuilder();
		Random r = new Random(42);
		for (int i = 0; i < 500000; ++i) {
			sb.append(alpha.charAt(r.nextInt(alpha.length())));
		}

		List<Character> word = stringToList(sb.toString());

		SuffixTree stree = new SuffixTree(word);

		List<Character> find = stringToList(sb.toString());
		assertTrue(stree.containsWord(find));
		find.add('x');
		assertFalse(stree.containsWord(find));
	}

}