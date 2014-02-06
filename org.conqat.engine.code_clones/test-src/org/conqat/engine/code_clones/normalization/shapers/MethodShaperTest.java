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
package org.conqat.engine.code_clones.normalization.shapers;

import java.nio.charset.Charset;

import org.conqat.engine.code_clones.lazyscope.IElementProvider;
import org.conqat.engine.code_clones.lazyscope.TokenElementProvider;
import org.conqat.engine.code_clones.normalization.token.ITokenProvider;
import org.conqat.engine.code_clones.normalization.token.TokenProvider;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.testutils.ProcessorInfoMock;
import org.conqat.engine.resource.scope.filesystem.FileContentAccessor;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.engine.sourcecode.resource.TokenElement;
import org.conqat.lib.commons.filesystem.CanonicalFile;
import org.conqat.lib.commons.test.CCSMTestCaseBase;
import org.conqat.lib.scanner.ELanguage;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;

/**
 * Test case for {@link MethodShaper}
 * 
 * @author juergens
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: F3B2029B956C26A721A7328B612C023E
 */
public class MethodShaperTest extends CCSMTestCaseBase {

	/** Name of test file */
	private static final String TEST_FILE = "nesting.cs";

	/** Test method shaping */
	public void testMethodShaping() throws ConQATException {
		// set up method shaper on test file
		int sentinels = countSentinels(useCanonicalTestFile(TEST_FILE));

		// test expected number of sentinels
		assertEquals(7, sentinels);
	}

	/** Runs the method shaper on a file and counts the sentinels */
	private int countSentinels(CanonicalFile file) throws ConQATException {
		ITokenElement root = new TokenElement(new FileContentAccessor(file,
				file.getParentFile(), "TEST"), Charset.defaultCharset(),
				ELanguage.CS);

		IElementProvider<ITokenResource, ITokenElement> elementProvider = new TokenElementProvider();
		ITokenProvider tokenProvider = new TokenProvider(elementProvider);
		MethodShaper shaper = new MethodShaper();
		shaper.init(new ProcessorInfoMock());

		shaper.setTokenProvider(tokenProvider);
		shaper.setMethodDepth(1);
		shaper.addScopeKeyword(ETokenType.CLASS);
		shaper.addScopeKeyword(ETokenType.NAMESPACE);

		shaper.init(root, new ProcessorInfoMock().getLogger());

		// count sentinel units
		int sentinels = 0;
		IToken token = shaper.getNext();
		while (token != null) {
			if (token.getType() == ETokenType.SENTINEL) {
				sentinels++;
			}
			token = shaper.getNext();
		}
		return sentinels;
	}

}