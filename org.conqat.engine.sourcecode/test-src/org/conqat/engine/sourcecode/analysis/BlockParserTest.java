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
package org.conqat.engine.sourcecode.analysis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.analysis.BlockParser.BlockParserException;
import org.conqat.engine.sourcecode.analysis.BlockParser.EMatchStyle;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.test.CCSMTestCaseBase;
import org.conqat.lib.scanner.ELanguage;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;
import org.conqat.lib.scanner.ScannerFactory;
import org.conqat.lib.scanner.ScannerUtils;

/**
 * Test case for {@link BlockParserTest}
 * 
 * @author ladmin
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: 1CA87F756A9E02040DA7446DEFB20108
 */
public class BlockParserTest extends CCSMTestCaseBase {

	/** Java scope keywords */
	Set<ETokenType> javaScopeKeywords = CollectionUtils.asHashSet(
			ETokenType.CLASS, ETokenType.INTERFACE, ETokenType.ENUM);

	/**
	 * Test recognition of method regions in Java source, matching only their
	 * bodies.
	 */
	public void testParseJavaMethodBodies() throws IOException,
			ConQATException, BlockParserException {
		List<Block> regions = parse(1, EMatchStyle.ONLY_BODY,
				"SLOCTestFile01.java");

		assertEquals(4, regions.size());

		assertRegion(53, 55, 0, regions);
		assertRegion(59, 62, 1, regions);
		assertRegion(65, 76, 2, regions);
		assertRegion(79, 90, 3, regions);
	}

	/** Test recognition of the class block, only matching the body */
	public void testParseJavaClassBlock() throws IOException, ConQATException,
			BlockParserException {
		List<Block> regions = parse(0, EMatchStyle.ONLY_BODY,
				"SLOCTestFile01.java");

		assertEquals(1, regions.size());
		assertRegion(45, 92, 0, regions);
	}

	/** Test recognition of the class block, matching the whole class decl */
	public void testParseJavaClassDeclaration() throws IOException,
			ConQATException, BlockParserException {
		List<Block> regions = parse(0, EMatchStyle.DECLARATION_AND_BODY,
				"SLOCTestFile01.java");

		assertEquals(1, regions.size());
		assertRegion(33, 92, 0, regions);
	}

	/**
	 * Test recognition of method regions in Java source, matching the entire
	 * declaration.
	 */
	public void testParseJavaMethodDeclarations() throws IOException,
			ConQATException, BlockParserException {
		List<Block> regions = parse(1, EMatchStyle.DECLARATION_AND_BODY,
				"SLOCTestFile01.java");

		assertEquals(4, regions.size());

		assertRegion(51, 55, 0, regions);
		assertRegion(57, 62, 1, regions);
		assertRegion(64, 76, 2, regions);
		assertRegion(78, 90, 3, regions);
	}

	/** Test behavior on empty expected results */
	public void testParseEmptyFile() throws IOException, ConQATException,
			BlockParserException {
		assertFindsNoBlocksFor("SLOCTestFile02.java");
		assertFindsNoBlocksFor("SLOCTestFile03.java");
		assertFindsNoBlocksFor("SLOCTestFile04.java");
		assertFindsNoBlocksFor("SLOCTestFile05.java");
	}

	/** Test correct resolution of method in inner classes */
	public void testParseMethodsInInnerAndOuterClasses() throws IOException,
			ConQATException, BlockParserException {
		List<Block> regions = parse(1, EMatchStyle.ONLY_BODY,
				"ClassWithInnerClass.java");

		assertEquals(20, regions.size());

		assertRegion(78, 82, 0, regions);
		assertRegion(93, 95, 1, regions);
		assertRegion(99, 103, 2, regions);
		assertRegion(110, 127, 3, regions);
		assertRegion(130, 134, 4, regions);
		assertRegion(137, 140, 5, regions);
		assertRegion(144, 147, 6, regions);
		assertRegion(150, 163, 7, regions);
		assertRegion(166, 180, 8, regions);
		assertRegion(183, 206, 9, regions);
		assertRegion(209, 211, 10, regions);
		assertRegion(222, 224, 11, regions);
		assertRegion(227, 229, 12, regions);
		assertRegion(232, 234, 13, regions);
		assertRegion(252, 255, 14, regions);
		assertRegion(258, 260, 15, regions);
		assertRegion(263, 266, 16, regions);
		assertRegion(278, 280, 17, regions);
		assertRegion(283, 285, 18, regions);
		assertRegion(288, 302, 19, regions);
	}

	/** Test invalid nesting. */
	public void testInvalidNesting() throws IOException, ConQATException {
		checkException("InvalidNesting01.java");
		checkException("InvalidNesting02.java");
	}

	/** Check if a {@link BlockParserException} is raised for the provided file. */
	private void checkException(String filename) throws IOException,
			ConQATException {
		try {
			parse(-1, EMatchStyle.DECLARATION_AND_BODY, filename);
			fail();
		} catch (BlockParserException e) {
			// this is expected
		}

		try {
			parse(-1, EMatchStyle.ONLY_BODY, filename);
			fail();
		} catch (BlockParserException e) {
			// this is expected
		}
	}

	/** Parse file and assert that no blocks are found */
	private void assertFindsNoBlocksFor(String filename) throws IOException,
			ConQATException, BlockParserException {
		List<Block> regions = parse(1, EMatchStyle.DECLARATION_AND_BODY,
				filename);
		assertEquals(0, regions.size());
	}

	/** Run parser on file and return block regions */
	private List<Block> parse(int depth, EMatchStyle matchStyle, String filename)
			throws IOException, ConQATException, BlockParserException {
		BlockParser parser = new BlockParser(depth, matchStyle,
				javaScopeKeywords,
				CollectionUtils.asHashSet(ETokenType.LBRACE), CollectionUtils
						.asHashSet(ETokenType.RBRACE));
		ArrayList<IToken> tokens = tokensFor(filename);
		return parser.createBlocks(tokens);
	}

	/** Assert that region is as expected */
	private void assertRegion(int expectedStartLine, int expectedEndLine,
			int regionIndex, List<Block> regions) {
		Block region = regions.get(regionIndex);
		// increment lines, since token line numbers are 0 based
		int actualStartLine = region.getFirst().getLineNumber() + 1;
		int actualEndLine = region.getLast().getLineNumber() + 1;
		assertEquals("Start line not as expected", expectedStartLine,
				actualStartLine);
		assertEquals("End line not as expected", expectedEndLine, actualEndLine);
	}

	/** Get tokens for file */
	private ArrayList<IToken> tokensFor(String filename) throws IOException {
		File file = useTestFile(filename);
		ArrayList<IToken> tokens = new ArrayList<IToken>(ScannerUtils
				.readTokens(ScannerFactory.newLenientScanner(ELanguage.JAVA,
						file)));
		return tokens;
	}
}