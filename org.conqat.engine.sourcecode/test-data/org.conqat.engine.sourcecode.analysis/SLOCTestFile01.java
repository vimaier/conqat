/*--------------------------------------------------------------------------+
   $Id: SLOCTestFile01.java 23268 2009-08-06 09:06:30Z deissenb $
 |                                                                          |
 | Copyright 2005-2008 Technische Universitaet Muenchen                     |
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
 +--------------------------------------------------------------------------*/
package edu.tum.cs.conqat.sourcecode.analysis;

import java.util.List;

import edu.tum.cs.conqat.commons.node.NodeUtils;
import edu.tum.cs.conqat.commons.traversal.ETargetNodes;
import edu.tum.cs.conqat.commons.traversal.NodeTraversingProcessorBase;
import edu.tum.cs.conqat.core.AConQATKey;
import edu.tum.cs.conqat.core.AConQATProcessor;
import edu.tum.cs.conqat.core.ConQATException;
import edu.tum.cs.conqat.sourcecode.library.SourceCodeLibrary;
import edu.tum.cs.conqat.sourcecode.scope.ISourceCodeElement;
import edu.tum.cs.scanner.IToken;
import edu.tum.cs.scanner.ETokenType.ETokenClass;

/**
 * Determines Comment Ratio (CR) at the character level.
 * 
 * @author Daniel Ratiu
 * @author Florian Deissenboeck
 * @author Tilman Seifert
 * @author $Author: deissenb $
 * @version $Rev: 23268 $
 * @levd.rating GREEN Rev: 17529
 */
@AConQATProcessor(description = "Determines the comment ratio at the character level.")
public class CommentRatioAnalyzer extends
		NodeTraversingProcessorBase<ISourceCodeElement> {

	/** Key for CR result */
	@AConQATKey(description = "Comment Ratio", type = "java.lang.Double")
	public static final String KEY = "CR";

	/** {@inheritDoc} */
	@Override
	protected ETargetNodes getTargetNodes() {
		return ETargetNodes.LEAVES;
	}

	/** Add CR key to the display list. */
	@Override
	protected void setUp(ISourceCodeElement root) throws ConQATException {
		super.setUp(root);
		NodeUtils.addToDisplayList(root, KEY);
	}

	/** Annotates nodes with their comment ratio. */
	public void visit(ISourceCodeElement node) {
		try {
			List<IToken> tokens = SourceCodeLibrary.getInstance().getTokens(
					node);
			node.setValue(KEY, calculateCR(tokens));
		} catch (ConQATException e) {
			getLogger().warn(
					"Could not obtain tokens for " + node.getId() + ": "
							+ e.getMessage());
			return;
		}
	}

	/** Calculate comment ratio for a list of tokens. */
	private double calculateCR(List<IToken> tokens) {
		int totalCharacterCount = 0;
		int commentCharacterCount = 0;

		for (IToken token : tokens) {
			totalCharacterCount += token.getText().length();
			if (token.getType().getTokenClass() == ETokenClass.COMMENT) {
				commentCharacterCount += token.getText().length();
			}
		}
		return (double) commentCharacterCount / (double) totalCharacterCount;
	}

}
