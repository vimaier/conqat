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
package org.conqat.engine.sourcecode.shallowparser;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntityTraversalUtils;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.commons.test.TestletBase;
import org.conqat.lib.scanner.ELanguage;
import org.junit.Ignore;

/**
 * This class describes single smoke tests.
 * 
 * @author $Author: goede $
 * @version $Rev: 40359 $
 * @ConQAT.Rating GREEN Hash: 6B29535DBC788AB9596D84F19AA0DA97
 */
@Ignore
public class ShallowParserSmokeTestlet extends TestletBase {

	/** File under test. */
	private final File codeFile;

	/** File used as reference. */
	private final File refFile;

	/** Language to test. */
	private final ELanguage language;

	/** Constructor. */
	public ShallowParserSmokeTestlet(File codeFile, File refFile,
			ELanguage language) {
		this.codeFile = codeFile;
		this.refFile = refFile;
		this.language = language;
	}

	/** Parse whole file and check against reference. */
	@Override
	public void test() throws IOException {
		List<ShallowEntity> entities = ShallowParserTestBase.parse(
				FileSystemUtils.readFileUTF8(codeFile), language, false);

		String expected = StringUtils.normalizeLineBreaks(FileSystemUtils
				.readFileUTF8(refFile));
		String actual = ShallowParserTestBase.normalizeParseResult(entities);
		// store actual result in temporary file for debugging.
		FileSystemUtils.writeFile(new File(getTmpDirectory(), language.name()
				+ "/" + codeFile.getName() + ".actual"), actual);

		// we expect all entities to be closed, as the files should be
		// compilable
		assertNull(ShallowEntityTraversalUtils.findIncompleteEntity(entities));

		// check whether all nodes have valid position information
		assertValidPositions(entities);

		assertEquals(expected, actual);
	}

	/** Checks that all positions are valid. */
	private static void assertValidPositions(List<ShallowEntity> entities) {
		for (ShallowEntity entity : entities) {
			assertValidPositions(entity);
		}
	}

	/** Checks that all positions are valid. */
	private static void assertValidPositions(ShallowEntity entity) {
		assertTrue(entity.getStartTokenIndex() < entity.getEndTokenIndex());
		assertValidPositions(entity.getChildren());
	}

	/** Name of the test case is the name of the smoke test file. */
	@Override
	public String getName() {
		return "[" + language.name() + "] " + codeFile.getPath();
	}
}