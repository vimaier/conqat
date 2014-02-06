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
package org.conqat.engine.text.identifier;

import org.tartarus.snowball.SnowballProgram;
import org.tartarus.snowball.ext.DanishStemmer;
import org.tartarus.snowball.ext.DutchStemmer;
import org.tartarus.snowball.ext.EnglishStemmer;
import org.tartarus.snowball.ext.FinnishStemmer;
import org.tartarus.snowball.ext.FrenchStemmer;
import org.tartarus.snowball.ext.German2Stemmer;
import org.tartarus.snowball.ext.HungarianStemmer;
import org.tartarus.snowball.ext.ItalianStemmer;
import org.tartarus.snowball.ext.NorwegianStemmer;
import org.tartarus.snowball.ext.PortugueseStemmer;
import org.tartarus.snowball.ext.RomanianStemmer;
import org.tartarus.snowball.ext.RussianStemmer;
import org.tartarus.snowball.ext.SpanishStemmer;
import org.tartarus.snowball.ext.SwedishStemmer;
import org.tartarus.snowball.ext.TurkishStemmer;

/**
 * Enumeration of stemmers for different languages. A stemmer transforms a word
 * into its root form. The stemmers are described in detail at <a
 * href="http://snowball.tartarus.org/">http://snowball.tartarus.org/</a> while
 * the version used here is taken from Apache lucene which contains Java classes
 * generated from the snowball code.
 * <p>
 * The following stemmers are not included:
 * <ul>
 * <li>Porter, as it is superseded by English</li>
 * <li>German, as we use German2</li>
 * <li>Lovins and KP as these are old algorithms for languages already supported
 * </li>
 * </ul>
 * 
 * @author hummelb
 * @author $Author: juergens $
 * @version $Rev: 35207 $
 * @ConQAT.Rating GREEN Hash: 4E94E8A9A0D49B7B542E1D92F716E3C7
 */
public enum EStemmer {

	/** Stemmer for danish language. */
	DANISH(new DanishStemmer()),

	/** Stemmer for dutch language. */
	DUTCH(new DutchStemmer()),

	/** Stemmer for english language. */
	ENGLISH(new EnglishStemmer()),

	/** Stemmer for finnish language. */
	FINNISH(new FinnishStemmer()),

	/** Stemmer for french language. */
	FRENCH(new FrenchStemmer()),

	/** Stemmer for german language. */
	GERMAN(new German2Stemmer()),

	/** Stemmer for hungarian language. */
	HUNGARIAN(new HungarianStemmer()),

	/** Stemmer for italian language. */
	ITALIAN(new ItalianStemmer()),

	/** Stemmer for norwegian language. */
	NORWEGIAN(new NorwegianStemmer()),

	/** Stemmer for portuguese language. */
	PORTUGUESE(new PortugueseStemmer()),

	/** Stemmer for romanian language. */
	ROMANIAN(new RomanianStemmer()),

	/** Stemmer for russian language. */
	RUSSIAN(new RussianStemmer()),

	/** Stemmer for spanish language. */
	SPANISH(new SpanishStemmer()),

	/** Stemmer for swedish language. */
	SWEDISH(new SwedishStemmer()),

	/** Stemmer for turkish language. */
	TURKISH(new TurkishStemmer());

	/** The stemmer used. */
	private final SnowballProgram stemmer;

	/** Constructor. */
	private EStemmer(SnowballProgram stemmer) {
		this.stemmer = stemmer;
	}

	/** Stems the given word. */
	public synchronized String stem(String s) {
		stemmer.setCurrent(s);
		stemmer.stem();
		return stemmer.getCurrent();
	}

}