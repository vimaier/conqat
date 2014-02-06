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
package org.conqat.lib.commons.string;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;

import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.filesystem.FileSystemUtils;

/**
 * A utility class providing some advanced string functionality.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 47067 $
 * @ConQAT.Rating GREEN Hash: 1BB5DE7BBBE1825138696920297BA591
 */
public class StringUtils {

	/** Line break. */
	public static final String CR = System.getProperty("line.separator");

	/** The empty string. */
	public static final String EMPTY_STRING = "";

	/** A symbol representing the line terminator. */
	public static final String LINE_TERMINATOR_SYMBOL = "\\n";

	/** A space. */
	public static final String SPACE = " ";

	/** A space character. */
	public static final char SPACE_CHAR = ' ';

	/** A tab character. */
	public static final String TAB = "\t";

	/** Two spaces. */
	public static final String TWO_SPACES = "  ";

	/** Number formatter. */
	private static NumberFormat numberFormat = NumberFormat.getInstance();

	/** Number formatter for percentages. */
	private static NumberFormat percentageFormat = NumberFormat
			.getPercentInstance();

	/** Random number generator. */
	private static final Random random = new Random();

	/**
	 * Adds random line breaks to a string.
	 * 
	 * @param text
	 *            the original string.
	 * @param count
	 *            the number of line breaks to add.
	 * @return the string with line breaks.
	 */
	public static String addRandomLineBreaks(String text, int count) {
		StringBuilder result = new StringBuilder(text);
		int len = text.length();
		for (int i = 0; i < count; i++) {
			int pos = random.nextInt(len);
			result.insert(pos, CR);
		}
		return result.toString();
	}

	/**
	 * Create a sting of the given length and center the given string within it.
	 * Left and right areas are filled by the character provided.
	 * 
	 * @param string
	 *            The input string.
	 * @param length
	 *            The length of the string to be returned.
	 * @param c
	 *            The character to surround the input string with.
	 * @return the new string or, if the string is longer than the specified
	 *         length, the original string.
	 * @see #flushLeft(String, int, char)
	 * @see #flushRight(String, int, char)
	 */
	public static String center(String string, int length, char c) {
		if (string.length() >= length) {
			return string;
		}
		int strLen = string.length();
		int fillLen = (length - strLen) / 2;
		String leftFiller = fillString(fillLen, c);

		if ((length - strLen) % 2 != 0) {
			fillLen++;
		}

		String rightFiller = fillString(fillLen, c);

		return leftFiller + string + rightFiller;
	}

	/**
	 * Compares two strings both of which may be <code>null</code>. A string
	 * which is <code>null</code> is always smaller than the other string,
	 * except for both strings being <code>null</code>.
	 * 
	 * @param a
	 *            The string which is compared to the second string.
	 * @param b
	 *            The string which is compared to the first string.
	 * @return Returns 0 if both strings are <code>null</code>, -1 if only the
	 *         first string is <code>null</code>, and 1 if only the second
	 *         string is <code>null</code>. If both strings are not
	 *         <code>null</code>, returns the result of the usual string
	 *         comparison.
	 */
	public static int compare(String a, String b) {
		if (a == b) {
			return 0;
		}

		if (a == null) {
			return -1;
		}

		if (b == null) {
			return 1;
		}

		return a.compareTo(b);
	}

	/**
	 * Concatenates all elements of an iterable using the
	 * <code>toString()</code>-method.
	 * 
	 * @param iterable
	 *            the iterable
	 * @return a concatenation, separated by spaces
	 */
	public static String concat(Iterable<?> iterable) {
		return concat(iterable, SPACE);
	}

	/**
	 * Concatenates all elements of an iterable using the
	 * <code>toString()</code>-method, separating them with the given
	 * <code>separator</code>.
	 * 
	 * @param iterable
	 *            the iterable containing the strings
	 * @param separator
	 *            the separator to place between the strings, may be
	 *            <code>null</code>
	 * @return a concatenation of the string in the array or <code>null</code>
	 *         if array was <code>null</code>. If array is of length 0 the empty
	 *         string is returned.
	 */
	public static String concat(Iterable<?> iterable, String separator) {
		if (iterable == null) {
			return null;
		}

		Iterator<?> iterator = iterable.iterator();

		if (!iterator.hasNext()) {
			return EMPTY_STRING;
		}

		if (separator == null) {
			separator = EMPTY_STRING;
		}

		StringBuilder builder = new StringBuilder();

		while (iterator.hasNext()) {
			builder.append(iterator.next());
			if (iterator.hasNext()) {
				builder.append(separator);
			}
		}

		return builder.toString();
	}

	/**
	 * Concatenates all elements of an array using the <code>toString()</code>
	 * -method.
	 * 
	 * @param array
	 *            the array containing the strings
	 * @return a concatenation of the string separated by spaces
	 */
	public static String concat(Object[] array) {
		return concat(array, SPACE);
	}

	/**
	 * Concatenates all elements of an array using the <code>toString()</code>
	 * -method, separating them with the given <code>separator</code>.
	 * 
	 * @param array
	 *            the array
	 * @param separator
	 *            the separator to place between the strings, may be
	 *            <code>null</code>
	 * @return a concatenation of the string in the array or <code>null</code>
	 *         if array was <code>null</code>. If array is of length 0 the empty
	 *         string is returned.
	 */
	public static String concat(Object[] array, String separator) {
		if (array == null) {
			return null;
		}
		return concat(Arrays.asList(array), separator);
	}

	/**
	 * Concatenate two string arrays.
	 * 
	 * @param array1
	 * @param array2
	 * @return the concatenation
	 */
	public static String[] concat(String[] array1, String[] array2) {
		String[] result = new String[array1.length + array2.length];
		System.arraycopy(array1, 0, result, 0, array1.length);
		System.arraycopy(array2, 0, result, array1.length, array2.length);
		return result;
	}

	/**
	 * Build a string with a specified length from a character.
	 * 
	 * @param length
	 *            The length of the string.
	 * @param c
	 *            The character.
	 * @return The string.
	 */
	public static String fillString(int length, char c) {
		char[] characters = new char[length];

		Arrays.fill(characters, c);

		return new String(characters);
	}

	/**
	 * Create a sting of the given length starting with the provided string.
	 * Remaining characters are filled with the provided character.
	 * 
	 * @param string
	 *            The input string.
	 * @param length
	 *            The length of the string to be returned.
	 * @param c
	 *            The character to fill the string.
	 * @return the new string or, if the string is longer than the specified
	 *         length, the original string.
	 * @see #flushRight(String, int, char)
	 * @see #center(String, int, char)
	 */
	public static String flushLeft(String string, int length, char c) {
		int gap = length - string.length();
		if (gap <= 0) {
			return string;
		}
		return string + StringUtils.fillString(gap, c);
	}

	/**
	 * Create a sting of the given length ending with the provided string.
	 * Remaining characters are filled with the provided character.
	 * 
	 * @param string
	 *            The input string.
	 * @param length
	 *            The length of the string to be returned.
	 * @param c
	 *            The character to fill the string.
	 * @return the new string or, if the string is longer than the specified
	 *         length, the original string.
	 * @see #flushLeft(String, int, char)
	 * @see #center(String, int, char)
	 */
	public static String flushRight(String string, int length, char c) {
		int gap = length - string.length();
		if (gap <= 0) {
			return string;
		}
		return StringUtils.fillString(gap, c) + string;
	}

	/**
	 * Format number
	 */
	public static String format(Number number) {
		return numberFormat.format(number);
	}

	/**
	 * Format as percentage.
	 */
	public static String formatAsPercentage(Number number) {
		return percentageFormat.format(number);
	}

	/** Returns the first line of a string. */
	public static String getFirstLine(String string) {
		LineSplitter lineSplitter = new LineSplitter(string);
		return lineSplitter.next();
	}

	/**
	 * Returns the first n part of a string, separated by the given character.
	 * 
	 * E.g., getStringParts("edu.tum.cs", 2, '.') gives: "edu.tum".
	 * 
	 * @param string
	 *            the base string
	 * @param partNumber
	 *            number of parts
	 * @param separator
	 *            the separator character
	 */
	public static String getFirstParts(String string, int partNumber,
			char separator) {

		if (partNumber < 0 || string == null) {
			return string;
		}

		int idx = 0;

		for (int i = 0; i < partNumber; i++) {
			idx = string.indexOf(separator, idx + 1);
			if (idx == -1) {
				return string;
			}
		}

		return string.substring(0, idx);
	}

	/**
	 * Splits a key-value string and stores it in a hash map. The string must
	 * have the following format:
	 * <p>
	 * <code>key=value[,key=value]*</code>
	 * </p>
	 * If the string is <code>null</code> <code>null</code> is returned.
	 * 
	 * @param keyValueString
	 *            with format described above
	 * @return a hash map containing the key-values-pairs.
	 */
	public static HashMap<String, String> getKeyValuePairs(String keyValueString) {
		if (keyValueString == null) {
			return null;
		}
		HashMap<String, String> result = new HashMap<String, String>();
		if (keyValueString.trim().equals(EMPTY_STRING)) {
			return result;
		}

		String[] pairs = keyValueString.split(",");

		for (String pair : pairs) {
			int index = pair.indexOf('=');
			if (index < 0) {
				result.put(pair.trim(), null);
			} else {
				String key = pair.substring(0, index).trim();
				String value = pair.substring(index + 1).trim();
				result.put(key, value);
			}
		}
		return result;
	}

	/**
	 * Return the last part of a String which is separated by the given
	 * character.
	 * 
	 * E.g., getLastPart("org.conqat.lib.commons.string.StringUtils", '.') gives
	 * "StringUtils".
	 * 
	 * @param string
	 *            the String
	 * @param separator
	 *            separation character
	 * @return the last part of the String, or the original String if the
	 *         separation character is not found.
	 */
	public static String getLastPart(String string, char separator) {
		int idx = string.lastIndexOf(separator);
		if (idx >= 0) {
			return string.substring(idx + 1);
		}
		return string;

	}

	/**
	 * Searches the elements of a string array for a string. Strings are
	 * trimmed.
	 * 
	 * @param array
	 *            the array to search
	 * @param string
	 *            the search string
	 * @return the index of the element where the string was found or
	 *         <code>-1</code> if string wasn't found.
	 */
	public static int indexOf(String[] array, String string) {
		for (int i = 0; i < array.length; i++) {
			if (array[i].trim().equals(string.trim())) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Checks if a string is empty (after trimming).
	 * 
	 * @param text
	 *            the string to check.
	 * @return <code>true</code> if string is empty or <code>null</code>,
	 *         <code>false</code> otherwise.
	 */
	public static boolean isEmpty(String text) {
		if (text == null) {
			return true;
		}
		return "".equals(text.trim());
	}

	/**
	 * Checks if the given string contains at least one letter (checked with
	 * {@link Character#isLetter(char)}).
	 */
	public static boolean containsLetter(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (Character.isLetter(s.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Generates a random string with a certain length. The string consists of
	 * characters with ASCII code between 33 and 126.
	 * 
	 * @param length
	 *            the length of the random string
	 * @return the random string
	 */
	public static String randomString(int length) {
		return randomString(length, random);
	}

	/**
	 * Performs the actal creation of the random string using the given
	 * randomizer.
	 */
	public static String randomString(int length, Random random) {
		char[] characters = new char[length];
		for (int i = 0; i < length; i++) {
			characters[i] = (char) (random.nextInt(93) + 33);
		}
		return new String(characters);
	}

	/**
	 * Generates an array of random strings.
	 * 
	 * @param length
	 *            number of strings
	 * @param stringLength
	 *            length of each string
	 * @return the randomly generated array.
	 */
	public static String[] randomStringArray(int length, int stringLength) {
		String[] array = new String[length];
		for (int i = 0; i < length; i++) {
			array[i] = randomString(stringLength);
		}
		return array;
	}

	/**
	 * Generates a pseudo random string with a certain length in a
	 * deterministic, reproducable fashion.
	 * 
	 * 
	 * @param length
	 *            the length of the pseudo-random string
	 * @param seed
	 *            seed value for the random number generator used for the
	 *            generation of the pseudo-random string. If the same seed value
	 *            is used, the same pseudo-random string is generated.
	 */
	public static String generateString(int length, int seed) {
		Random seededRandomizer = new Random(seed);
		return randomString(length, seededRandomizer);
	}

	/**
	 * Generates an array of pseudo-random strings in a deterministic,
	 * reproducable fashion.
	 * 
	 * @param length
	 *            number of strings
	 * @param stringLength
	 *            length of each string
	 * @param seed
	 *            seed value for the random number generator used for the
	 *            generation of the pseudo-random string. If the same seed value
	 *            is used, the same pseudo-random string array is generated.
	 * @return the randomly generated array.
	 */
	public static String[] generateStringArray(int length, int stringLength,
			int seed) {
		String[] array = new String[length];
		for (int i = 0; i < length; i++) {
			array[i] = generateString(stringLength, seed + i);
		}
		return array;
	}

	/**
	 * Returns the beginning of a String, cutting off the last part which is
	 * separated by the given character.
	 * 
	 * E.g., removeLastPart("org.conqat.lib.commons.string.StringUtils", '.')
	 * gives "org.conqat.lib.commons.string".
	 * 
	 * @param string
	 *            the String
	 * @param separator
	 *            separation character
	 * @return the String without the last part, or the original string if the
	 *         separation character is not found.
	 */
	public static String removeLastPart(String string, char separator) {
		int idx = string.lastIndexOf(separator);
		if (idx == -1) {
			return string;
		}

		return string.substring(0, idx);
	}

	/**
	 * Replace all linebreaks in string with {@link #LINE_TERMINATOR_SYMBOL}.
	 * 
	 * @return a string without linebreaks
	 */
	public static String replaceLineBreaks(String string) {
		return replaceLineBreaks(string, LINE_TERMINATOR_SYMBOL);
	}

	/**
	 * Replace all linebreaks in string with the platform-specific line
	 * separator.
	 * 
	 * @return a string without linebreaks
	 */
	public static String normalizeLineBreaks(String string) {
		return replaceLineBreaks(string, CR);
	}

	/**
	 * Replace all linebreaks in string by a specified symbol.
	 * 
	 * @return a string with line breaks replaced.
	 */
	public static String replaceLineBreaks(String string, String symbol) {
		StringBuilder builder = new StringBuilder();

		LineSplitter lineSplitter = new LineSplitter(string);
		lineSplitter.setIncludeTrailingEmptyLine(true);

		for (String line : lineSplitter) {
			builder.append(line);
			if (lineSplitter.hasNext()) {
				builder.append(symbol);
			}
		}

		return builder.toString();
	}

	/**
	 * Split string in lines. For the the empty string and <code>null</code> an
	 * array of length zero is returned.
	 * 
	 * @see #splitLinesAsList(String)
	 */
	public static String[] splitLines(String content) {
		List<String> lineList = splitLinesAsList(content);
		String[] result = new String[lineList.size()];
		lineList.toArray(result);
		return result;
	}

	/**
	 * Returns the number of occurrences of the given character in the given
	 * string.
	 */
	public static int countCharacter(String content, char character) {
		int count = 0;
		for (char c : content.toCharArray()) {
			if (c == character) {
				count++;
			}
		}
		return count;
	}

	/** Return number of lines in a string. */
	public static int countLines(String content) {
		LineSplitter lineSplitter = new LineSplitter(content);
		int count = 0;
		while (lineSplitter.next() != null) {
			count++;
		}
		return count;
	}

	/**
	 * Split string in lines. For the the empty string and <code>null</code> an
	 * empty list is returned.
	 * 
	 * @see #splitLines(String)
	 */
	public static List<String> splitLinesAsList(String content) {
		List<String> result = new ArrayList<String>();
		LineSplitter lineSplitter = new LineSplitter(content);
		for (String line : lineSplitter) {
			result.add(line);
		}
		return result;
	}

	/**
	 * Remove prefix from a string.
	 * 
	 * @param prefix
	 *            the prefix
	 * @param string
	 *            the string
	 * @return the string without the prefix or the original string if it does
	 *         not start with the prefix.
	 */
	public static String stripPrefix(String prefix, String string) {
		if (string.startsWith(prefix)) {
			return string.substring(prefix.length());
		}
		return string;
	}

	/**
	 * Remove suffix from a string.
	 * 
	 * @param suffix
	 *            the suffix
	 * @param string
	 *            the string
	 * @return the string without the suffix or the original string if it does
	 *         not end with the suffix.
	 */
	public static String stripSuffix(String suffix, String string) {
		if (string.endsWith(suffix)) {
			return string.substring(0, string.length() - suffix.length());
		}
		return string;
	}

	/** Strips all digits from the given String. */
	public static String stripDigits(String string) {
		return string.replaceAll("[0-9]", EMPTY_STRING);
	}

	/** Strips all trailing digits from the end of the given String. */
	public static String stripTrailingDigits(String string) {
		return string.replaceAll("\\d+$", EMPTY_STRING);
	}

	/**
	 * Create string representation of a map.
	 */
	public static String toString(Map<?, ?> map) {
		return toString(map, EMPTY_STRING);
	}

	/**
	 * Create string representation of a map.
	 * 
	 * @param map
	 *            the map
	 * @param indent
	 *            a line indent
	 */
	public static String toString(Map<?, ?> map, String indent) {
		StringBuilder result = new StringBuilder();
		Iterator<?> keyIterator = map.keySet().iterator();

		while (keyIterator.hasNext()) {
			result.append(indent);
			Object key = keyIterator.next();
			result.append(key);
			result.append(" = ");
			result.append(map.get(key));
			if (keyIterator.hasNext()) {
				result.append(CR);
			}
		}

		return result.toString();
	}

	/**
	 * Convert stack trace of a {@link Throwable} to a string.
	 */
	public static String obtainStackTrace(Throwable throwable) {
		StringWriter result = new StringWriter();
		PrintWriter printWriter = new PrintWriter(result);
		throwable.printStackTrace(printWriter);
		return result.toString();
	}

	/**
	 * Test if a string starts with one of the provided prefixes. Returns
	 * <code>false</code> if the list of prefixes is empty. This should only be
	 * used for short lists of prefixes.
	 */
	public static boolean startsWithOneOf(String string, String... prefixes) {
		for (String prefix : prefixes) {
			if (string.startsWith(prefix)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Test if a string contains of the provided strings. Returns
	 * <code>false</code> if the list of strings is empty. This should only be
	 * used for short lists of strings.
	 */
	public static boolean containsOneOf(String text, String... strings) {
		for (String prefix : strings) {
			if (text.contains(prefix)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Test if a string ends with one of the provided suffixes. Returns
	 * <code>false</code> if the list of prefixes is empty. This should only be
	 * used for short lists of suffixes.
	 */
	public static boolean endsWithOneOf(String string, String... suffixes) {
		for (String suffix : suffixes) {
			if (string.endsWith(suffix)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Prefix all lines of a string. This also replaces line breaks with the
	 * platform-specific line-separator.
	 * 
	 * @param string
	 *            the string to prefix
	 * @param prefix
	 *            the prefix to add
	 * @param prefixFirstLine
	 *            a flag that indicates if the first line should be prefixed or
	 *            not.
	 */
	public static String prefixLines(String string, String prefix,
			boolean prefixFirstLine) {
		String[] lines = StringUtils.splitLines(string.trim());
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < lines.length; i++) {
			if (i > 0 || prefixFirstLine) {
				result.append(prefix);
			}
			result.append(lines[i]);
			if (i < lines.length - 1) {
				result.append(CR);
			}
		}
		return result.toString();
	}

	/**
	 * Splits the given string into an array of {@link Character}s. This is
	 * mostly used for testing purposes, if an array of certain objects is
	 * needed.
	 */
	public static Character[] splitChars(String s) {
		Character[] result = new Character[s.length()];
		for (int i = 0; i < result.length; ++i) {
			result[i] = s.charAt(i);
		}
		return result;
	}

	/** Capitalize string. */
	public static String capitalize(String string) {
		if (StringUtils.isEmpty(string)) {
			return string;
		}
		return string.substring(0, 1).toUpperCase() + string.substring(1);
	}

	/**
	 * This method splits the input string into words (delimited by whitespace)
	 * and returns a string whose words are separated by single spaces and whose
	 * lines are not longer than the given length (unless a very long word
	 * occurs)).
	 */
	public static String wrapLongLines(String s, int maxLineLength) {
		String[] words = s.split("\\s+");

		StringBuilder sb = new StringBuilder();
		int lineLength = 0;
		for (String word : words) {
			if (word.length() == 0) {
				continue;
			}

			if (lineLength > 0) {
				if (lineLength + 1 + word.length() > maxLineLength) {
					sb.append(CR);
					lineLength = 0;
				} else {
					sb.append(SPACE);
					lineLength += 1;
				}
			}
			sb.append(word);
			lineLength += word.length();
		}

		return sb.toString();
	}

	/** Returns the longest common prefix of s and t */
	public static String longestCommonPrefix(String s, String t) {
		int n = Math.min(s.length(), t.length());
		for (int i = 0; i < n; i++) {
			if (s.charAt(i) != t.charAt(i)) {
				return s.substring(0, i);
			}
		}
		return s.substring(0, n);
	}

	/** Returns the longest common suffix of s and t */
	public static String longestCommonSuffix(String s, String t) {
		return reverse(StringUtils.longestCommonPrefix(reverse(s), reverse(t)));
	}

	/** Reverse a string */
	public static String reverse(String s) {
		return new StringBuilder(s).reverse().toString();
	}

	/**
	 * Returns the longest common prefix of the strings in the list or the empty
	 * string if no common prefix exists.
	 */
	public static String longestCommonPrefix(Iterable<String> strings) {
		Iterator<String> iterator = strings.iterator();
		CCSMAssert
				.isTrue(iterator.hasNext(), "Expected are at least 2 strings");
		String commonPrefix = iterator.next();
		CCSMAssert
				.isTrue(iterator.hasNext(), "Expected are at least 2 strings");

		while (iterator.hasNext()) {
			commonPrefix = longestCommonPrefix(commonPrefix, iterator.next());
			if (commonPrefix.length() == 0) {
				break;
			}
		}

		return commonPrefix;
	}

	/** Removes whitespace from a string. */
	public static String removeWhitespace(String content) {
		return content.replaceAll("\\s", StringUtils.EMPTY_STRING);
	}

	/**
	 * Creates a unique name which is not contained in the given set of names.
	 * If possible the given base name is directly returned, otherwise it is
	 * extended by a number.
	 */
	public static String createUniqueName(String baseName, Set<String> usedNames) {
		String name = baseName;
		int i = 1;
		while (usedNames.contains(name)) {
			name = baseName + ++i;
		}
		return name;
	}

	/** Transforms a string from camel-case to lower-case with hyphens. */
	public static String camelCaseToHyphenated(String s) {
		return s.replaceAll("([^^])([A-Z][a-z])", "$1-$2").toLowerCase();
	}

	/** Encodes a byte array as a hex string. */
	public static String encodeAsHex(byte[] data) {
		StringBuilder sb = new StringBuilder();
		for (byte b : data) {
			sb.append(String.format("%02X", b & 0xff));
		}
		return sb.toString();
	}

	/** Decodes a byte array from a hex string. */
	public static byte[] decodeFromHex(String s) {
		byte[] result = new byte[s.length() / 2];
		for (int i = 0; i < result.length; ++i) {
			result[i] = (byte) Integer.parseInt(s.substring(2 * i, 2 * i + 2),
					16);
		}
		return result;
	}

	/**
	 * Format number with number formatter, if number formatter is
	 * <code>null</code>, this uses {@link String#valueOf(double)}.
	 */
	public static String format(double number, NumberFormat numberFormat) {
		if (numberFormat == null) {
			return String.valueOf(number);
		}
		return numberFormat.format(number);
	}

	/**
	 * Regex replacement methods like
	 * {@link Matcher#appendReplacement(StringBuffer, String)} or
	 * {@link String#replaceAll(String, String)} treat dollar signs as group
	 * references. This method escapes replacement strings so that dollar signs
	 * are treated as literals.
	 */
	public static String escapeRegexReplacementString(String replacement) {
		// this needs to be escape thrice as replaceAll also recognizes the
		// dollar sign
		return replacement.replaceAll("([$\\\\])", "\\\\$1");
	}

	/**
	 * Converts a string to a (UTF-8) byte representation. This returns null on
	 * a null input.
	 */
	public static byte[] stringToBytes(String s) {
		if (s == null) {
			return null;
		}
		return s.getBytes(FileSystemUtils.UTF8_CHARSET);
	}

	/**
	 * Converts a (UTF-8) byte array to a string. This returns null on a null
	 * input.
	 */
	public static String bytesToString(byte[] b) {
		if (b == null) {
			return null;
		}
		return new String(b, FileSystemUtils.UTF8_CHARSET);
	}

	/**
	 * Returns a list containing the string representations of the given
	 * collection of objects. {@link String#valueOf} is used to convert each
	 * object. <em>null</em> values are included, i.e., the resulting list is
	 * guaranteed to have the size of the initial collection.
	 */
	public static List<String> asStringList(Collection<?> objects) {
		List<String> result = new ArrayList<String>();
		for (Object o : objects) {
			result.add(String.valueOf(o));
		}
		return result;
	}

	/**
	 * Filters the given collection of strings by the given suffix, i.e. the
	 * resulting list contains only those strings that end with this suffix.
	 */
	public static List<String> filterBySuffix(String suffix,
			Collection<String> strings) {
		List<String> result = new ArrayList<String>();
		for (String s : strings) {
			if (s.endsWith(suffix)) {
				result.add(s);
			}
		}
		return result;
	}

	/**
	 * Converts the given Object array into a String array by invoking toString
	 * on each non-null element. For null entries in the input array, the output
	 * will contain a null entry as well
	 */
	public static String[] toStringArray(Object[] array) {
		String[] result = new String[array.length];
		for (int i = 0; i < array.length; i++) {
			if (array[i] == null) {
				result[i] = null;
			} else {
				result[i] = String.valueOf(array[i]);
			}
		}
		return result;
	}

	/**
	 * Truncates the given string (if necessary) by removing characters from the
	 * end and attaching the suffix such that the resulting string has at most
	 * length characters. length must be >= suffix.length();
	 */
	public static String truncate(String string, int length, String suffix) {
		CCSMAssert.isTrue(length >= suffix.length(),
				"Expected length >= suffix.length()");
		if (string.length() <= length) {
			return string;
		}
		return string.substring(0, length - suffix.length()) + suffix;
	}

}