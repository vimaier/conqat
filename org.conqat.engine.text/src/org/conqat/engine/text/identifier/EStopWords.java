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

import static org.conqat.lib.commons.collections.CollectionUtils.asHashSet;

import java.util.Set;

/**
 * Enumeration of language specific stop words.
 * 
 * @author juergens
 * @author $Author: juergens $
 * @version $Rev: 35207 $
 * @ConQAT.Rating GREEN Hash: 621E4AE4763AC8A95ACB139E4A83C3BA
 */
public enum EStopWords {

	/** German */
	GERMAN(asHashSet("aber", "als", "am", "an", "auch", "auf", "aus", "bei",
			"bin", "bis", "bist", "da", "dadurch", "daher", "darum", "das",
			"da\u00DF", "dass", "dein", "deine", "dem", "den", "der", "des",
			"dessen", "deshalb", "die", "dies", "dieser", "dieses", "doch",
			"dort", "du", "durch", "ein", "eine", "einem", "einen", "einer",
			"eines", "er", "es", "euer", "eure", "f\u00FCr", "hatte", "hatten",
			"hattest", "hattet", "hier 	", "hinter", "ich", "ihr", "ihre",
			"im", "in", "ist", "ja", "jede", "jedem", "jeden", "jeder",
			"jedes", "jener", "jenes", "jetzt", "kann", "kannst",
			"k\u00F6nnen", "k\u00F6nnt", "machen", "mein", "meine", "mit",
			"mu\u00DF", "muss", "mu\u00DFt", "musst", "m\u00FCssen",
			"m\u00FC\u00DFt", "m\u00FCsst", "nach", "nachdem", "nein", "nicht",
			"nun", "oder", "seid", "sein", "seine", "sich", "sie", "sind",
			"soll", "sollen", "sollst", "sollt", "sonst", "soweit", "sowie",
			"und", "unser 	", "unsere", "unter", "vom", "von", "vor", "wann",
			"warum", "was", "weiter", "weitere", "wenn", "wer", "werde",
			"werden", "werdet", "weshalb", "wie", "wieder", "wieso", "wir",
			"wird", "wirst", "wo", "woher", "wohin", "zu", "zum", "zur",
			"\u00FCber")),

	/** English */
	ENGLISH(asHashSet("a", "about", "above", "above", "across", "after",
			"afterwards", "again", "against", "all", "almost", "alone",
			"along", "already", "also", "although", "always", "am", "among",
			"amongst", "amoungst", "amount", "an", "and", "another", "any",
			"anyhow", "anyone", "anything", "anyway", "anywhere", "are",
			"around", "as", "at", "back", "be", "became", "because", "become",
			"becomes", "becoming", "been", "before", "beforehand", "behind",
			"being", "below", "beside", "besides", "between", "beyond", "bill",
			"both", "bottom", "but", "by", "call", "can", "cannot", "cant",
			"co", "con", "could", "couldnt", "cry", "de", "describe", "detail",
			"do", "done", "down", "due", "during", "each", "eg", "eight",
			"either", "eleven", "else", "elsewhere", "empty", "enough", "etc",
			"even", "ever", "every", "everyone", "everything", "everywhere",
			"except", "few", "fifteen", "fify", "fill", "find", "fire",
			"first", "five", "for", "former", "formerly", "forty", "found",
			"four", "from", "front", "full", "further", "get", "give", "go",
			"had", "has", "hasnt", "have", "he", "hence", "her", "here",
			"hereafter", "hereby", "herein", "hereupon", "hers", "herself",
			"him", "himself", "his", "how", "however", "hundred", "ie", "if",
			"in", "inc", "indeed", "interest", "into", "is", "it", "its",
			"itself", "keep", "last", "latter", "latterly", "least", "less",
			"ltd", "made", "many", "may", "me", "meanwhile", "might", "mill",
			"mine", "more", "moreover", "most", "mostly", "move", "much",
			"must", "my", "myself", "name", "namely", "neither", "never",
			"nevertheless", "next", "nine", "no", "nobody", "none", "noone",
			"nor", "not", "nothing", "now", "nowhere", "of", "off", "often",
			"on", "once", "one", "only", "onto", "or", "other", "others",
			"otherwise", "our", "ours", "ourselves", "out", "over", "own",
			"part", "per", "perhaps", "please", "put", "rather", "re", "same",
			"see", "seem", "seemed", "seeming", "seems", "serious", "several",
			"she", "should", "show", "side", "since", "sincere", "six",
			"sixty", "so", "some", "somehow", "someone", "something",
			"sometime", "sometimes", "somewhere", "still", "such", "system",
			"take", "ten", "than", "that", "the", "their", "them",
			"themselves", "then", "thence", "there", "thereafter", "thereby",
			"therefore", "therein", "thereupon", "these", "they", "thickv",
			"thin", "third", "this", "those", "though", "three", "through",
			"throughout", "thru", "thus", "to", "together", "too", "top",
			"toward", "towards", "twelve", "twenty", "two", "un", "under",
			"until", "up", "upon", "us", "very", "via", "was", "we", "well",
			"were", "what", "whatever", "when", "whence", "whenever", "where",
			"whereafter", "whereas", "whereby", "wherein", "whereupon",
			"wherever", "whether", "which", "while", "whither", "who",
			"whoever", "whole", "whom", "whose", "why", "will", "with",
			"within", "without", "would", "yet", "you", "your", "yours",
			"yourself", "yourselves", "the"));

	/** Set that stores the stop words */
	private final Set<String> stopWords;

	/** Constructor */
	private EStopWords(Set<String> stopWords) {
		this.stopWords = stopWords;
	}

	/** Determines whether a word is a stop word */
	public boolean isStopWord(String word) {
		return stopWords.contains(word.toLowerCase());
	}

}