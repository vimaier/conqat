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
package org.conqat.engine.resource.util;

import java.io.File;
import java.io.IOException;

import org.conqat.lib.commons.test.CCSMTestCaseBase;
import org.conqat.engine.core.core.ConQATException;

/**
 * Tests for {@link ConQATFileUtils}.
 * 
 * @author hummelb
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: 4096AD9DEB321B95C3A0FF329C4941EE
 */
public class ConQATFileUtilsTest extends CCSMTestCaseBase {

	/** Tests canonical file creation. */
	public void testCreateCanonicalFile() throws ConQATException, IOException {
		String filename = "a.txt";
		File file = new File(filename);

		assertEquals(file.getCanonicalFile(), ConQATFileUtils
				.createCanonicalFile(filename));
		assertEquals(file.getCanonicalFile(), ConQATFileUtils
				.createCanonicalFile(file));

		try {
			ConQATFileUtils.createCanonicalFile(createUncanonizableFilename());
			fail("Expected exception!");
		} catch (ConQATException e) {
			// ignore
		}
	}

	/** Returns a filename that is not canonizable on any platform. */
	private static String createUncanonizableFilename() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 100000; ++i) {
			sb.append("/test");
		}
		return "c:/.../text.txt" + sb.toString();
	}
}