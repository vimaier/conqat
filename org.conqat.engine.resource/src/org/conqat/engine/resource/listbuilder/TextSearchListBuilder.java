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
package org.conqat.engine.resource.listbuilder;

import static org.conqat.engine.commons.ConQATParamDoc.INPUT_REF_DESC;
import static org.conqat.engine.commons.ConQATParamDoc.INPUT_REF_NAME;

import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.analysis.TextElementAnalyzerBase;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.lib.commons.collections.TwoDimHashMap;
import org.conqat.lib.commons.string.StringUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: E473DFE47C712192A8DF5CEDE085CDE7
 */
@AConQATProcessor(description = "This processor can be supplied with multiple search terms "
		+ "and searches all terms in all elements. Each element "
		+ "is annotated with a list of all terms found. This can be used for a "
		+ "primitive form of dependency analysis.")
public class TextSearchListBuilder extends TextElementAnalyzerBase {

	/** Result key. */
	@AConQATKey(description = "List of search terms found", type = "java.util.List<String>")
	public static final String KEY = "term-list";

	/**
	 * This maps from (prefix x normalized term) -> search term. If no
	 * normalization is applied, normalized term and search term are equal.
	 */
	private TwoDimHashMap<String, String, String> terms = new TwoDimHashMap<String, String, String>();

	/** Delimiters used for tokenization. */
	private String delimiters = " \t\n\r\f()[]{}\"'.,;:";

	/** Flag that indicates if case should be ignored. */
	private boolean ignoreCase = false;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "search-terms", description = "Add list of search terms.", minOccurrences = 1)
	public void addSearchTerms(
			@AConQATAttribute(name = INPUT_REF_NAME, description = INPUT_REF_DESC) String[] searchTerms,
			@AConQATAttribute(name = "prefix", description = "If a prefix is supplied, all terms defined "
					+ "with this parameter will be prefixed with the prefix in the search term list "
					+ "added to this node. The prefix, however, will not be used in the search. Prefixing "
					+ "the search terms is helpful if the list should be used as basis for architecture "
					+ "analysis as this allows to define suitable mappings. By default the empty string "
					+ "is used as prefix.", defaultValue = StringUtils.EMPTY_STRING) String prefix) {
		for (String term : searchTerms) {
			terms.putValue(prefix, term.trim(), term.trim());
		}
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "delimiters", description = "Set the delimiters to be used for the "
			+ "tokenization of the searched text [if not set, whitespace, braces, parentheses, "
			+ "quotation marks and punctuation is used].", maxOccurrences = 1)
	public void setDelimiters(
			@AConQATAttribute(name = "value", description = "A string that contains the delimiters. This is "
					+ "the same format as used in the constructor of java.util.StringTokenizer.") String delimiters) {
		this.delimiters = delimiters;
	}

	/** {@ConQAT.Doc} */
	// This is named 'normalization' and not 'ignore-case' as I am quite sure we
	// will add other normalizations later on
	@AConQATParameter(name = "normalization", description = "This parameter controls the normalization of "
			+ "the searched text and the search terms [by default search is case-sensitive].", maxOccurrences = 1)
	public void setNormalization(
			@AConQATAttribute(name = "ignore-case", description = "If set to true, case is ignored.") boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

	/** Returns {@value #KEY} */
	@Override
	protected String[] getKeys() {
		return new String[] { KEY };
	}

	/**
	 * If case is ignored this, creates a new instance of {@link #terms} with
	 * all terms in lower case.
	 */
	@Override
	protected void setUp(ITextResource root) throws ConQATException {
		if (ignoreCase) {
			TwoDimHashMap<String, String, String> tmp = new TwoDimHashMap<String, String, String>();
			for (String prefix : terms.getFirstKeys()) {
				for (String term : terms.getSecondKeys(prefix)) {
					tmp.putValue(prefix, term.toLowerCase(), term);
				}
			}
			terms = tmp;
		}
		super.setUp(root);
	}

	/** {@inheritDoc} */
	@Override
	protected void analyzeElement(ITextElement element) throws ConQATException {
		String content = element.getTextContent();
		HashSet<String> tokens = tokenize(content);

		for (String prefix : terms.getFirstKeys()) {
			HashSet<String> tmp = new HashSet<String>(tokens);
			tmp.retainAll(terms.getSecondKeys(prefix));

			List<String> list = NodeUtils.getOrCreateStringList(element, KEY);
			for (String term : tmp) {
				list.add(prefix + terms.getValue(prefix, term));
			}
		}

	}

	/** Tokenizes a string a returns a set of the tokens. */
	private HashSet<String> tokenize(String content) {
		HashSet<String> result = new HashSet<String>();

		StringTokenizer st = new StringTokenizer(content, delimiters);
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (ignoreCase) {
				token = token.toLowerCase();
			}
			result.add(token.trim());
		}

		return result;
	}

}