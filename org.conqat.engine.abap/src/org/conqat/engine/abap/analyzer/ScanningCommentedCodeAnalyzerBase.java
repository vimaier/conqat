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
package org.conqat.engine.abap.analyzer;

import static org.conqat.engine.abap.analyzer.ECommentType.INCONCLUSIVE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.lib.scanner.IScanner;
import org.conqat.lib.scanner.IToken;
import org.conqat.lib.scanner.ScannerException;
import org.conqat.lib.scanner.ScannerFactory;
import org.conqat.lib.scanner.ScannerUtils;

/**
 * Base class for processors which mark a comment as code based on the tokens of
 * which the comment consists.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: 33CC75D91BF428DD05D26F248E009B75
 */
public abstract class ScanningCommentedCodeAnalyzerBase extends
		CommentedCodeAnalyzerBase {

	/** {@inheritDoc} */
	@Override
	protected ECommentType getType(Comment comment, ITokenElement element) {
		String content = comment.getContent();

		IScanner scanner = ScannerFactory.newScanner(element.getLanguage(),
				content, element.getUniformPath());
		List<IToken> commentTokens = new ArrayList<IToken>();
		try {
			ScannerUtils.readTokens(scanner, commentTokens,
					new ArrayList<ScannerException>());
		} catch (IOException e) {
			return INCONCLUSIVE;
		}

		return getType(comment, commentTokens);
	}

	/**
	 * Get the type of the comment based on the tokens of which it consists
	 * 
	 * @param comment
	 *            Comment
	 * @param commentTokens
	 *            Tokens of which the comment consists
	 * @return Type of the comment
	 */
	protected abstract ECommentType getType(Comment comment,
			List<IToken> commentTokens);
}