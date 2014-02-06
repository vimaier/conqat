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

import java.util.List;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.shallowparser.IShallowParser;
import org.conqat.engine.sourcecode.shallowparser.ShallowParserFactory;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;
import org.conqat.engine.text.comments.Comment;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.scanner.ELanguage;
import org.conqat.lib.scanner.IToken;

/**
 * Test for calculating the {@link ASTLocationCalculator}.
 * 
 * @author $Author: steidl $
 * @version $Rev: 45905 $
 * @ConQAT.Rating GREEN Hash: 4A17BFB1724BC7E3AF1DC476B61711D7
 */
public class ASTLocationCalculatorTest extends CommentTestBase {

	/**
	 * Tests the calculation of the ast location.
	 */
	public void testASTLocation() throws ConQATException {
		List<Comment> comments = getCommentsInFile("CommentClassification.java");

		List<ShallowEntity> entities = getEntities(comments);

		assertAstLocation(comments.get(0), entities,
				ASTLocationCalculator.HEADER_LOCATION);
		assertAstLocation(comments.get(1), entities,
				ASTLocationCalculator.HEADER_LOCATION);
		assertAstLocation(comments.get(2), entities,
				ASTLocationCalculator.INTERFACE_LOCATION);
		assertAstLocation(comments.get(3), entities,
				ASTLocationCalculator.INTERFACE_LOCATION);
		assertAstLocation(comments.get(4), entities,
				ASTLocationCalculator.INTERFACE_LOCATION);
		assertAstLocation(comments.get(5), entities,
				ASTLocationCalculator.INLINE_LOCATION);
		assertAstLocation(comments.get(6), entities,
				ASTLocationCalculator.INTERFACE_LOCATION);
		assertAstLocation(comments.get(7), entities,
				ASTLocationCalculator.INLINE_LOCATION);

	}

	/**
	 * Asserts that the calculated ast location for the given comment matches
	 * the expected value.
	 */
	private void assertAstLocation(Comment comment,
			List<ShallowEntity> entities, int expectedASTLocation) {

		int astLocation = ASTLocationCalculator.getASTLocationAsNumber(
				comment.getToken(), entities);
		assertEquals(expectedASTLocation, astLocation);
	}

	/** Test AST location for all possible comments. */
	public void testASTLocationComplete() throws ConQATException {
		List<Comment> comments = getCommentsInFile("ASTLocationTestData.java");
		List<ShallowEntity> entities = getEntities(comments);

		for (Comment comment : comments) {
			Integer expectedValue = Integer.valueOf(comment.getCommentString()
					.replaceAll("//", StringUtils.EMPTY_STRING).trim());
			assertAstLocation(comment, entities, expectedValue);
		}

	}

	/**
	 * Returns the shallow parsed entities under the assumption that all given
	 * comments stem from the same element.
	 */
	private List<ShallowEntity> getEntities(List<Comment> comments)
			throws ConQATException {
		assertNotNull(comments);
		assertTrue("File did not have any comments", !comments.isEmpty());
		List<IToken> tokens = comments.get(0).getTokens();
		IShallowParser parser = ShallowParserFactory
				.createParser(ELanguage.JAVA);
		List<ShallowEntity> entities = parser.parseTopLevel(tokens);
		return entities;
	}

}
