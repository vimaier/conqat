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
package org.conqat.engine.text.comments.classification.features;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.conqat.engine.text.comments.Comment;
import org.conqat.engine.text.comments.utils.CommentUtils;
import org.conqat.engine.text.comments.utils.PowerSetIterable;
import org.conqat.lib.commons.collections.CollectionUtils;

/**
 * Helper class to calculate the coherence between a comment and a method name
 * 
 * @author $Author: steidl $
 * @version $Rev: 46304 $
 * @ConQAT.Rating YELLOW Hash: D527C7D585721031C02C0CF3AB55F711
 */
public class CoherenceUtils {
	/**
	 * Code from http://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/
	 * Levenshtein_distance
	 */
	private static int minimum(int a, int b, int c) {
		return Math.min(Math.min(a, b), c);
	}

	/**
	 * Code from http://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/
	 * Levenshtein_distance.
	 */
	private static int getLevenshteinDistance(CharSequence str1,
			CharSequence str2) {

		String s1 = str1.toString();
		str1 = s1.toLowerCase();

		String s2 = str2.toString();
		str2 = s2.toLowerCase();

		int[][] distance = new int[str1.length() + 1][str2.length() + 1];

		for (int i = 0; i <= str1.length(); i++)
			distance[i][0] = i;
		for (int j = 0; j <= str2.length(); j++)
			distance[0][j] = j;

		for (int i = 1; i <= str1.length(); i++)
			for (int j = 1; j <= str2.length(); j++)
				distance[i][j] = minimum(
						distance[i - 1][j] + 1,
						distance[i][j - 1] + 1,
						distance[i - 1][j - 1]
								+ ((str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0
										: 1));

		return distance[str1.length()][str2.length()];
	}

	/**
	 * Returns the minimal levensthein distance between a word in the comment
	 * and the method name. Words are separated by white space
	 */
	public static int correspond(String methodName, String comment) {
		List<String> words = getCommentWords(comment);
		int minDis = Integer.MAX_VALUE;
		for (String part : getMethodNameParts(methodName)) {
			part = part.toLowerCase();
			for (String word : words) {
				word = word.toLowerCase();
				int dis = CoherenceUtils.getLevenshteinDistance(part, word);
				minDis = Math.min(minDis, dis);
			}
		}

		return minDis;
	}

	/**
	 * Returns the number of words in the comment that have a Levensthein
	 * distance to the method name which is smaller than the given minimum
	 * distance Words are separated by white space
	 * 
	 */
	public static int correspond(String methodName, String comment,
			int minDistance) {
		List<String> words = getCommentWords(comment);
		int count = 0;
		for (String part : getMethodNameParts(methodName)) {
			for (String word : words) {
				int dis = getLevenshteinDistance(part, word);

				if (dis < minDistance) {
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * Returns the power set of all words in the method name.
	 */
	private static List<String> getMethodNameParts(String methodName) {

		List<String> methodNameParts = new ArrayList<String>();
		if (methodName.contains("_")) {
			methodNameParts = Arrays.asList(methodName.split("_"));
		} else {
			methodNameParts = camelCasing(methodName);
		}

		String[] methodNameParsArray = CollectionUtils.toArray(methodNameParts,
				String.class);

		methodNameParts = new ArrayList<String>();
		PowerSetIterable<String> psi = new PowerSetIterable<String>(
				methodNameParsArray);
		for (String[] s : psi) {
			String result = "";
			for (String string : s) {
				result += string;
			}
			methodNameParts.add(result);
		}

		methodNameParts.remove("");
		return methodNameParts;
	}

	/**
	 * Splits the method name according to camel casing.
	 */
	private static List<String> camelCasing(String methodName) {
		String currentword = "";
		List<String> parts = new ArrayList<String>();
		boolean lastLetterWasLowerCase = true;
		for (int i = 0; i < methodName.length(); i++) {
			char letter = methodName.charAt(i);
			if (lastLetterWasLowerCase) {
				if (Character.isUpperCase(letter)) {
					// switch from lower case to Upper case
					lastLetterWasLowerCase = false;
					parts.add(currentword);
					currentword = String.valueOf(letter);
				} else {
					// lower case followed by lower case
					currentword = currentword + letter;
				}
			} else {
				if (Character.isUpperCase(letter)) {
					// upper case followed by upper case
					currentword = currentword + letter;
				} else {
					// upper case followed by lower case
					currentword = currentword + letter;
					lastLetterWasLowerCase = true;
				}
			}
		}
		parts.add(currentword);
		parts.remove("");
		return parts;
	}

	/**
	 * Returns the words in the comment.
	 */
	public static List<String> getCommentWords(String comment) {
		comment = comment.replaceAll("\\.", "");
		comment = comment.replaceAll("(\\r|\\n)", "");

		// replace everything that is not a letter or a number
		comment = comment.replaceAll("[^\\p{L}\\p{Z}]", " ");

		String[] parts = comment.split(" ");
		List<String> wordsImmutable = Arrays.asList(parts);
		List<String> words = new ArrayList<String>();
		words.addAll(wordsImmutable);
		List<String> toRemove = new ArrayList<String>();
		for (String word : words) {
			if (word.length() < 2 || word.contains("*")) {
				toRemove.add(word);
			}
		}
		words.removeAll(toRemove);

		return words;
	}

	/**
	 * Returns true if the comment has a context relation to its method. Returns
	 * true for default comments and comments that only contain java doc.
	 */
	public static boolean hasContextCorrelation(Comment classificationObject) {
		String comment = classificationObject.getCommentString();
		if (CommentUtils.isDefaultComment(comment))
			return true;

		if (CommentUtils.hasOnlyJavaDoc(comment)) {
			return true;
		}
		return (CoherenceUtils.correspond(
				classificationObject.getMethodFinder().getNextDefinition(
						classificationObject.getPosition()), comment) < 2);
	}

}
