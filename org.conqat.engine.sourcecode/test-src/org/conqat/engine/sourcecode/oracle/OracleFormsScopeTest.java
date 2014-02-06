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
package org.conqat.engine.sourcecode.oracle;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IContentAccessor;
import org.conqat.engine.sourcecode.resource.TokenTestCaseBase;
import org.conqat.lib.commons.filesystem.FileSystemUtils;

/**
 * Test for {@link OracleFormsScope}.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @levd.rating GREEN Hash: 7A7898FA40590F51BE8FA06C025ECB3C
 */
public class OracleFormsScopeTest extends TokenTestCaseBase {

	/** Basic test for the XML extraction. */
	public void test() throws ConQATException {
		IContentAccessor[] accessors = loadOracleFormsElement("test01.xml");
		checkAccessor(accessors[0], "TEST/test01.xml/test[Prozedur]",
				"/* Comment */\n\nPROCEDURE test IS\n"
						+ "BEGIN\n    test\nEND;");
		checkAccessor(accessors[1], "TEST/test01.xml/test1[Paket]",
				"/* Comment */\n\n" + "PROCEDURE test IS\nBEGIN\n"
						+ " & < > ' \" A A A A \u00f6 END;");
	}

	/**
	 * Checks if the accessor has the specified uniform path and the specified
	 * UTF-8-encoded content.
	 */
	private void checkAccessor(IContentAccessor accessor, String uniformPath,
			String content) throws ConQATException {
		assertEquals(uniformPath, accessor.getUniformPath());
		assertEquals(content, new String(accessor.getContent(),
				FileSystemUtils.UTF8_CHARSET));
	}

	/** Create content accessors from specified file. */
	private IContentAccessor[] loadOracleFormsElement(String filename)
			throws ConQATException {
		IContentAccessor[] accessors = (IContentAccessor[]) executeProcessor(
				OracleFormsScope.class, "('oracle-forms-xml'=(ref=",
				createTextScope(useTestFile(""), new String[] { filename },
						new String[0]), "))");
		return accessors;
	}
}