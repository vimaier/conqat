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
package org.conqat.engine.sourcecode.shallowparser.preprocessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.commons.traversal.NodeTraversingProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IResource;
import org.conqat.lib.commons.clone.IDeepCloneable;
import org.conqat.lib.commons.collections.Pair;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;

/**
 * Base class for {@link IParserPreprocessor}s. Instances are expected to be
 * immutable.
 * <p>
 * This class provides some basic support for filtering. If this is not desired,
 * the preprocess method can also be overwritten.
 * <p>
 * Instances of this class (and sub classes) should be immutable, as we reuse
 * them for parsing of multiple elements.
 * 
 * @author $Author: pfaller $
 * @version $Rev: 40706 $
 * @ConQAT.Rating GREEN Hash: 960FD7EB93DF3EEB391362F3E1ABE2B6
 */
public abstract class ParserPreprocessorBase extends
		NodeTraversingProcessorBase<IResource> implements IParserPreprocessor {

	/** Names of identifiers that are completely removed from the token stream. */
	private final Set<String> filteredIdentifiers = new HashSet<String>();

	/**
	 * Names of identifiers that are mapped to another identifier (and possibly
	 * type).
	 */
	private static final Map<String, Pair<String, ETokenType>> mappedIdentifiers = new HashMap<String, Pair<String, ETokenType>>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "filtered-identifier", description = "Adds an identifier to be filtered.")
	public void addFilteredIdentifier(
			@AConQATAttribute(name = "text", description = "The text that is filtered (case-sensitive)") String text) {
		filteredIdentifiers.add(text);
	}

	/** Adds an identifier that should be mapped. */
	protected void addMappedIdentifier(String from, String to) {
		addMappedIdentifier(from, to, null);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "mapped-identifier", description = "Maps identifiers to different tokens.")
	public void addMappedIdentifier(
			@AConQATAttribute(name = "from", description = "The source text of the token to be mapped") String from,
			@AConQATAttribute(name = "to", description = "The text of the new token.") String to,
			@AConQATAttribute(name = "type", defaultValue = "IDENTIFIER", description = "The token type to map to.") ETokenType newType) {
		mappedIdentifiers.put(from, new Pair<String, ETokenType>(to, newType));
	}

	/** {@inheritDoc} */
	@Override
	public List<IToken> preprocess(List<IToken> tokens) throws ConQATException {
		List<IToken> result = new ArrayList<IToken>();

		int currentCollapseStart = 0;

		int index = -1;
		for (IToken token : tokens) {
			index += 1;

			if (token.getType() == ETokenType.IDENTIFIER) {
				String text = token.getText();
				if (filteredIdentifiers.contains(text)) {
					continue;
				}

				if (mappedIdentifiers.containsKey(text)) {
					Pair<String, ETokenType> mapped = mappedIdentifiers
							.get(text);
					ETokenType newType = mapped.getSecond();
					if (newType == null) {
						newType = token.getType();
					}

					// replace token
					token = token.newToken(newType, token.getOffset(),
							token.getLineNumber(), mapped.getFirst(),
							token.getOriginId());
				}
			}

			switch (processToken(token)) {
			case DISCARD:
				// do nothing
				break;
			case KEEP:
				result.add(token);
				break;
			case START_COLLAPSE:
				currentCollapseStart = index;
				break;
			case END_COLLAPSE:
				result.add(collapseTokens(tokens.subList(currentCollapseStart,
						index + 1)));
				break;
			}
		}

		return result;
	}

	/**
	 * Returns a single token for a list of tokens that are to be collapsed. The
	 * type is the same as the first token in the sequence, while the text in
	 * the concatenation of all tokens.
	 */
	protected IToken collapseTokens(List<IToken> subList) {
		StringBuilder text = new StringBuilder();
		IToken firstToken = subList.get(0);
		int start = firstToken.getOffset();
		for (IToken token : subList) {
			while (text.length() < token.getOffset() - start) {
				text.append(StringUtils.SPACE_CHAR);
			}
			text.append(token.getText());
		}

		return firstToken.newToken(firstToken.getType(), start,
				firstToken.getLineNumber(), text.toString(),
				firstToken.getOriginId());
	}

	/** Template method used to decide the action on the current token. */
	protected abstract ETokenAction processToken(IToken token)
			throws ConQATException;

	/** {@inheritDoc} */
	@Override
	public void visit(IResource node) {
		node.setValue(IParserPreprocessor.KEY, this);
	}

	/** {@inheritDoc} */
	@Override
	protected ETargetNodes getTargetNodes() {
		return ETargetNodes.ALL;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Returns <code>this</code> (i.e. no cloning) as instances of this class
	 * should be immutable.
	 */
	@Override
	public IDeepCloneable deepClone() {
		return this;
	}

	/** Possible actions for single tokens. */
	protected static enum ETokenAction {

		/** Keep the token as is. */
		KEEP,

		/** Discard the token. */
		DISCARD,

		/**
		 * The given token is the first to start a collapsed region. All tokens
		 * in this region will be used to create a single token from them.
		 */
		START_COLLAPSE,

		/**
		 * The given token is the end in the current collapsed region. All
		 * tokens in this region will be used to create a single token from
		 * them.
		 */
		END_COLLAPSE
	}
}
