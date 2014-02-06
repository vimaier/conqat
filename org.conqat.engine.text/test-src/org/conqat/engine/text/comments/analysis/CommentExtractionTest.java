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

import org.conqat.engine.text.comments.Comment;

/**
 * Test to extract comments from a file.
 * 
 * @author $Author: steidl $
 * @version $Rev: 45904 $
 * @ConQAT.Rating RED Hash: 641E88AF67A16DA82A7E3ECAC29A727C
 */
// TODO (BH): If possible, call the test class the same as the tested class with
// "Test" extension, i.e. in this case "CommentExtractorTest". It's also nice to
// name the tested class in the class comment.
public class CommentExtractionTest extends CommentTestBase {

	/** Tests that the correct number of comments is extracted */
	public void testCommentExtraction() {
		// TODO (BH): Why use this local variable? Just inline.
		String filename = "CommentClassification.java";
		List<Comment> comments = getCommentsInFile(filename);
		assertNotNull(comments);
		assertEquals(8, comments.size());
		// TODO (BH): Check content of 1 or two comments? Preferably one with
		// merged single line comments

		filename = "ASTLocationTestData.java";
		comments = getCommentsInFile(filename);
		assertNotNull(comments);
		assertEquals(15, comments.size());
		// TODO (BH): Check content of 1 or two comments? Preferably one with
		// merged single line comments
	}
}
