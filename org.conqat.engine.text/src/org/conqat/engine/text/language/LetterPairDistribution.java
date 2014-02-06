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
package org.conqat.engine.text.language;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;

import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Class for storing distributions of letter pairs in a natural language.
 * 
 * @author hummelb
 * @author $Author: hummelb $
 * @version $Rev: 41338 $
 * @ConQAT.Rating GREEN Hash: 9A0FA2E8005532E731B35CE7B2CC4926
 */
public class LetterPairDistribution {

	/** The distribution. */
	private final Map<String, Double> distribution = new HashMap<String, Double>();

	/** Hidden constructor. */
	/* package */LetterPairDistribution() {
		// empty by design
	}

	/** Loads a distribution from a file. */
	public static LetterPairDistribution loadFromFile(File inputFile)
			throws IOException {
		LetterPairDistribution lpd = new LetterPairDistribution();
		for (String line : StringUtils.splitLines(FileSystemUtils
				.readFileUTF8(inputFile))) {
			if (StringUtils.isEmpty(line) || line.startsWith("#")) {
				continue;
			}
			Scanner s = new Scanner(line);

			// Use US locale as we store our numbers in this format (dot as
			// decimal separator)
			s.useLocale(Locale.US);
			String pair = s.next();
			if (pair.length() != 2 || !s.hasNextDouble()) {
				throw new IOException("Invalid file format!");
			}
			lpd.distribution.put(pair, s.nextDouble());
		}

		lpd.normalize();
		return lpd;
	}

	/**
	 * Prepares the text for pair identification by converting to lower case,
	 * replacing non-letters by a dot and returning the char array.
	 */
	/* package */static char[] prepareText(String text) {
		char[] chars = new char[text.length()];
		text.toLowerCase().getChars(0, text.length(), chars, 0);
		for (int i = 0; i < chars.length; ++i) {
			if (!Character.isLetter(chars[i])) {
				chars[i] = '.';
			}
		}
		return chars;
	}

	/** Inserts a pair into the distribution table. */
	/* package */void insertPair(String s) {
		Double value = distribution.get(s);
		if (value == null) {
			value = 0.;
		}
		distribution.put(s, value + 1);
	}

	/**
	 * Normalizes the table to 600, which is about the size of the english
	 * table. This is done to have distribution values near 1, which helps to
	 * avoid numeric underflows/overflows when multiplying them in
	 * {@link #calculateTextValue(String)}.
	 */
	/* package */void normalize() {
		double sum = 0;
		for (double v : distribution.values()) {
			sum += v;
		}
		if (sum == 0) {
			return;
		}
		for (Entry<String, Double> e : distribution.entrySet()) {
			e.setValue(600. * e.getValue() / sum);
		}
	}

	/**
	 * Calculates the value of a text according to this table. The value is the
	 * product of the value of letter pairs in the table, but small values are
	 * limited to 0.01 to not overly penalize non-existing pairs (which might be
	 * typos after all). The resulting (positive) number indicates confidence in
	 * the decision, but may not be interpreted on an absolute scale but
	 * relative to other measurements on the same text.
	 */
	public double calculateTextValue(String text) {
		double result = 1;
		char[] chars = prepareText(text);
		for (int i = 1; i < chars.length; ++i) {
			if (chars[i - 1] != '.') {
				String s = new String(chars, i - 1, 2);
				Double value = distribution.get(s);
				if (value == null)
					value = 0.;
				result *= Math.max(value, .0000001);
			}
		}
		return result;
	}

	/** Writes the table. */
	public void writeTable(Writer writer) throws IOException {
		StringBuilder sb = new StringBuilder();
		for (String key : CollectionUtils.sort(distribution.keySet())) {
			sb.append(key + " " + distribution.get(key) + StringUtils.CR);
		}
		writer.append(sb.toString());
	}
}