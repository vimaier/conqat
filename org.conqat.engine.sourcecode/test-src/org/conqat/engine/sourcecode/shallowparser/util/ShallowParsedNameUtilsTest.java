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
package org.conqat.engine.sourcecode.shallowparser.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.shallowparser.ShallowParserFactory;
import org.conqat.engine.sourcecode.shallowparser.framework.EShallowEntityType;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntityTraversalUtils;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.commons.test.CCSMTestCaseBase;
import org.conqat.lib.scanner.ELanguage;
import org.conqat.lib.scanner.ScannerUtils;

/**
 * Test the {@link ShallowParsedNameUtils}.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 44065 $
 * @ConQAT.Rating GREEN Hash: 1325DD6484F28136A9675F9C93CE194F
 */
public class ShallowParsedNameUtilsTest extends CCSMTestCaseBase {

	/** The keyword used to find expected names in the source code. */
	private static final String EXPECTED_KEYWORD = "EXPECTED:";

	/** Tests extraction of fully qualified names. */
	public void testFullyQualifiedName() throws IOException, ConQATException {
		String code = FileSystemUtils
				.readFileUTF8(useTestFile("name-test.cpp"));

		List<ShallowEntity> entities = ShallowParserFactory.createParser(
				ELanguage.CPP).parseTopLevel(
				ScannerUtils.getTokens(code, ELanguage.CPP));

		List<String> expectedNames = extractExpectedNames(code);

		List<ShallowEntity> methodsAndAttributes = ShallowEntityTraversalUtils
				.listEntitiesOfTypes(entities, EnumSet
						.of(EShallowEntityType.METHOD,
								EShallowEntityType.ATTRIBUTE));

		assertEquals(expectedNames.size(), methodsAndAttributes.size());
		for (int i = 0; i < expectedNames.size(); ++i) {
			String actualName = ShallowParsedNameUtils
					.getFullyQualifiedName(methodsAndAttributes.get(i));
			assertEquals(expectedNames.get(i), actualName);
		}
	}

	/** Extracts the expected names from the source code. */
	private List<String> extractExpectedNames(String code) {
		List<String> expectedNames = new ArrayList<String>();
		for (String line : StringUtils.splitLinesAsList(code)) {
			String[] parts = line.split(EXPECTED_KEYWORD, 2);
			if (parts.length > 1) {
				expectedNames.add(parts[1].trim());
			}
		}
		return expectedNames;
	}
}
