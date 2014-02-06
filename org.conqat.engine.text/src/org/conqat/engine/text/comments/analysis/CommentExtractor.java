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
package org.conqat.engine.text.comments.analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.shallowparser.IShallowParser;
import org.conqat.engine.sourcecode.shallowparser.ShallowParserFactory;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntityTraversalUtils;
import org.conqat.engine.text.comments.Comment;
import org.conqat.engine.text.comments.classification.features.MethodFinder;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.ETokenType.ETokenClass;
import org.conqat.lib.scanner.IToken;

/**
 * Extracts comments from a given list of tokens.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 46265 $
 * @ConQAT.Rating GREEN Hash: FA752A15D9B908629CD28E121495A67C
 */
public class CommentExtractor {

	/**
	 * Iterates over all comments in the given token list and extracts the
	 * comments.
	 * 
	 * @param includedCommentTokenTypes
	 *            if this set is empty, all comments are included. otherwise
	 *            only comments that are specified in this list are included.
	 */
	public static List<Comment> extractComments(List<IToken> tokens,
			ITokenElement element, Set<ETokenType> includedCommentTokenTypes)
			throws ConQATException {
		List<Comment> comments = new ArrayList<Comment>();
		IShallowParser parser = ShallowParserFactory.createParser(element
				.getLanguage());
		MethodFinder methodFinder = new MethodFinder(element, tokens);
		List<ShallowEntity> entities = parser.parseTopLevel(tokens);
		for (int i = 0; i < tokens.size(); i++) {
			if (isIncludedComment(tokens.get(i), includedCommentTokenTypes)) {
				comments.add(createComment(tokens, entities, element, i,
						methodFinder));
			}
		}
		return comments;
	}

	/**
	 * Calculates all necessary information for the comment at the given
	 * position in the given token list and adds the comment to the comment
	 * list.
	 */
	private static Comment createComment(List<IToken> tokens,
			List<ShallowEntity> entities, ITokenElement element, int position,
			MethodFinder methodFinder) {
		int astLocation = ASTLocationCalculator.getASTLocationAsNumber(
				tokens.get(position),
				ShallowEntityTraversalUtils.getAllEntities(entities));
		return new Comment(tokens.get(position).getText(), position,
				astLocation, element, tokens, methodFinder);
	}

	/** Checks if the token should be counted as a comment. */
	private static boolean isIncludedComment(IToken token,
			Set<ETokenType> includedTokenTypes) {
		ETokenType type = token.getType();
		if (type.getTokenClass() != ETokenClass.COMMENT) {
			return false;
		}
		if (includedTokenTypes.isEmpty()) {
			return true;
		}
		return includedTokenTypes.contains(type);
	}
}
