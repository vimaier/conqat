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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.conqat.lib.commons.collections.CounterSet;
import org.conqat.lib.commons.collections.PairList;
import org.conqat.lib.commons.filesystem.FileExtensionFilter;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.engine.core.bundle.BundleResourceManager;
import org.conqat.engine.text.BundleContext;

/**
 * Class for deciding for a given string the language it belongs to.
 * 
 * @author hummelb
 * @author $Author: hummelb $
 * @version $Rev: 41338 $
 * @ConQAT.Rating GREEN Hash: 2399129766CD7DAD811C3C29AF7B94E0
 */
public class LanguageDecider {

	/** Result for inconclusive language decision. */
	public static final String INCONCLUSIVE = "??";

	/** Name of the directory containing the pair tables. */
	private static final String PAIR_TABLES_DIR = "pair_tables";

	/** Extension of table files. */
	private static final String TABLE_EXTENSION = "table";

	/** Extension of dictionary files. */
	private static final String DICT_EXTENSION = "dict";

	/** List of supported languages. */
	private static PairList<String, LetterPairDistribution> languages;

	/**
	 * Dictionary containing well known words and mapping them to a language
	 * code.
	 */
	private static Map<String, String> dictionary = new HashMap<String, String>();

	/** Confidence factor used when determining the language of a word. */
	private static final double CONFIDENCE_FACTOR = 2;

	/**
	 * Returns the two letter language code for a given text providing the
	 * language to which the text probably belongs. The method does not guess
	 * correct in every case and the supported languages are limited by the pair
	 * tables provided in the resources directory. If the results are
	 * inconclusive, a value of {@value #INCONCLUSIVE} is returned.
	 */
	public static String decideLanguage(String text) {
		if (languages == null) {
			initLanguages(BundleContext.getInstance().getResourceManager()
					.getResourceAsFile(PAIR_TABLES_DIR));
		}

		CounterSet<String> set = new CounterSet<String>();
		for (String word : text.split("\\P{L}+")) {
			word = word.toLowerCase();
			if (word.length() < 2) {
				continue;
			}
			String category = dictionary.get(word);
			if (category == null) {
				category = LanguageDecider.decideWord(word);
			}
			set.inc(category);
		}

		int total = set.getTotal();
		for (String key : set.getKeys()) {
			if (set.getValue(key) > .5 * total) {
				return key;
			}
		}
		return LanguageDecider.INCONCLUSIVE;
	}

	/**
	 * Decides a single word based on the pair tables. This works by finding the
	 * two languages with the highest score (calculated by
	 * {@link LetterPairDistribution#calculateTextValue(String)}) and returning
	 * the winner only if it is better than the second one by a factor of
	 * {@value #CONFIDENCE_FACTOR}. Otherwise {@value #INCONCLUSIVE} is
	 * returned.
	 */
	private static String decideWord(String text) {
		double secondBest = -1;
		double best = -1;
		String bestLang = INCONCLUSIVE;
		for (int i = 0; i < languages.size(); ++i) {
			double score = languages.getSecond(i).calculateTextValue(text);
			if (score > best) {
				secondBest = best;
				best = score;
				bestLang = languages.getFirst(i);
			} else if (score > secondBest) {
				secondBest = score;
			}
		}
		if (secondBest > 0 && best / secondBest < CONFIDENCE_FACTOR) {
			return INCONCLUSIVE;
		}
		return bestLang;
	}

	/**
	 * Loads the languages from the given directory into {@link #languages}.
	 * Note that old tables will be replaced, so check {@link #languages} for
	 * <code>null</code> if loading should happen only once.
	 */
	private static void initLanguages(File tablesDir) {
		languages = new PairList<String, LetterPairDistribution>();
		if (tablesDir.isDirectory()) {
			for (File file : tablesDir.listFiles(new FileExtensionFilter(
					TABLE_EXTENSION))) {
				try {
					languages.add(StringUtils.stripSuffix(
							"." + TABLE_EXTENSION, file.getName()),
							LetterPairDistribution.loadFromFile(file));
				} catch (IOException e) {
					throw new IllegalStateException(
							"Missconfiguration of bundle!", e);
				}
			}

			for (File file : tablesDir.listFiles(new FileExtensionFilter(
					DICT_EXTENSION))) {
				try {
					loadDict(file, StringUtils.stripSuffix(
							"." + DICT_EXTENSION, file.getName()));
				} catch (IOException e) {
					throw new IllegalStateException(
							"Missconfiguration of bundle!", e);
				}
			}
		}
	}

	/** Loads a dictionary. */
	private static void loadDict(File file, String lang) throws IOException {
		for (String line : StringUtils.splitLines(FileSystemUtils.readFile(
				file, FileSystemUtils.UTF8_ENCODING))) {
			dictionary.put(line.trim().toLowerCase(), lang);
		}
	}

	/** This is a small program for testing language recognition. */
	public static void main(String[] args) throws IOException {
		initLanguages(new File(BundleResourceManager.RESOURCES_LOCATION,
				PAIR_TABLES_DIR));
		BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
		String s = null;
		while ((s = r.readLine()) != null) {
			System.out.println(decideWord(s));
		}
	}
}