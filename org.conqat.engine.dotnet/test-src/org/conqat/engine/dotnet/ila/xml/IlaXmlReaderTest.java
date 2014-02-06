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
package org.conqat.engine.dotnet.ila.xml;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import org.conqat.engine.commons.keys.IDependencyListKey;
import org.conqat.engine.commons.node.ListNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.test.ResourceProcessorTestCaseBase;
import org.conqat.lib.commons.filesystem.FileSystemUtils;

/**
 * Smoke test for the {@link IlaXmlReader} class.
 * 
 * @author $Author: poehlmann $
 * @version $Revision: 42242 $
 * @ConQAT.Rating YELLOW Hash: D38FB8FAD4753488E77597F02B030B2F
 */
public class IlaXmlReaderTest extends ResourceProcessorTestCaseBase {

	/** Name of the test file. */
	public static final String SIMPLE_XML = "Simple.xml";

	/** Name of the yield example test file. */
	public static final String YIELD_XML = "Yield.exe.xml";

	/** Xml reader smoke test. */
	public void testParseFile() throws Exception {
		parseTestFile(SIMPLE_XML);
	}

	/** Parses a test file and returns the Xml reader. */
	private IlaXmlReader parseTestFile(String xmlFile) throws IOException,
			ConQATException {
		String content = FileSystemUtils.readFile(useTestFile(xmlFile));
		IlaXmlReader reader = new IlaXmlReader(dummyTextElement(content),
				new ListNode(), new HashSet<String>(), new HashSet<String>());
		reader.parse();
		return reader;
	}

	/** Tests removal of synthetic code resulting from yield statements. */
	public void testRemovingSyntheticYieldCode() throws IOException,
			ConQATException {
		IlaXmlReader xmlReader = parseTestFile(YIELD_XML);

		ListNode[] children = xmlReader.root.getChildren();
		assertEquals(1, children.length);

		ListNode type = children[0];
		@SuppressWarnings("unchecked")
		Collection<String> dependencies = NodeUtils.getValue(type,
				IDependencyListKey.DEPENDENCY_LIST_KEY, Collection.class);

		assertEquals(1, dependencies.size());
		assertEquals("Yield.Provider", dependencies.iterator().next());
	}
}