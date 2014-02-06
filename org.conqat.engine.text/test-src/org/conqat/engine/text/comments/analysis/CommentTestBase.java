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

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.conqat.engine.core.bundle.BundleException;
import org.conqat.engine.core.bundle.BundleInfo;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.testutils.LoggerMock;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.TokenTestCaseBase;
import org.conqat.engine.text.comments.Comment;
import org.conqat.lib.scanner.ELanguage;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;

/**
 * Base class for comment tests, providing functionality to extract comments.
 * 
 * @author $Author: steidl $
 * @version $Rev: 46279 $
 * @ConQAT.Rating RED Hash: 70CB02FEB67A992B91FA6EEFF8F5800F
 */
public abstract class CommentTestBase extends TokenTestCaseBase {

	/** Initializes the bundle contexts of the text bundle. */
	// TODO (BH): Why not do this in the setUp() method?
	protected void initBundleContexts() throws BundleException {
		File textBundle = getBundleDir(org.conqat.engine.text.BundleContext.class);
		new org.conqat.engine.text.BundleContext(new BundleInfo(textBundle));
	}

	/** Extracts all comments in the file with the given name */
	protected List<Comment> getCommentsInFile(String filename) {
		ITokenElement element = createTokenElement(
				useCanonicalTestFile(filename), ELanguage.JAVA);
		try {
			return CommentExtractor.extractComments(getTokens(element),
					element, new HashSet<ETokenType>());
		} catch (ConQATException e) {
			// TODO (BH): In a test it is better to throw the exception, as the
			// framework will deal with it then.
			System.out.println("Error in extracting comments");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns all tokens of the given element and merges comments that
	 * consisted of multiple single lines into one token.
	 */
	protected List<IToken> getTokens(ITokenElement element)
			throws ConQATException {
		List<IToken> modifiableTokens = new ArrayList<IToken>(
				element.getTokens(new LoggerMock()));
		CommentAnalysisBase.unifyMultipleSingleLineComments(modifiableTokens,
				new HashSet<ETokenType>());
		return modifiableTokens;
	}
}
