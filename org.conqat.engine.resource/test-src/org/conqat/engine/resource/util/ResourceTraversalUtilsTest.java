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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.build.ResourceBuilder;
import org.conqat.engine.resource.test.ResourceProcessorTestCaseBase;
import org.conqat.engine.resource.text.ITextElement;

import org.conqat.engine.core.core.ConQATException;

/**
 * Tests the {@link ResourceTraversalUtils}.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: 9A62C4FC4AECBC6C4885FA8096814BD9
 */
public class ResourceTraversalUtilsTest extends ResourceProcessorTestCaseBase {

	/** All paths created in {@link #createScope()}. */
	private static final String[] ALL_PATHS = { "TEST/a/b.txt", "TEST/a/c.bin",
			"TEST/d.txt", "TEST/x/y/z.bin" };

	/** Tests element listing. */
	public void testListElements() throws ConQATException {
		IResource scope = createScope();

		assertUniformPaths(ResourceTraversalUtils.listElements(scope),
				ALL_PATHS);

		assertUniformPaths(ResourceTraversalUtils.listElements(scope,
				IElement.class), ALL_PATHS);

		assertUniformPaths(ResourceTraversalUtils.listElements(scope,
				ITextElement.class), "TEST/a/b.txt", "TEST/d.txt");
	}

	/** Tests the various methods for creating maps. */
	public void testMapCreation() throws ConQATException {
		IResource scope = createScope();

		Map<String, IElement> elementMap = ResourceTraversalUtils
				.createUniformPathToElementMap(scope, IElement.class);
		assertEquals(new HashSet<String>(Arrays.asList(ALL_PATHS)), elementMap
				.keySet());
		for (String key : elementMap.keySet()) {
			assertEquals(key, elementMap.get(key).getUniformPath());
		}

		Map<String, String> idMap = ResourceTraversalUtils
				.createUniformPathToIdMapping(scope, ITextElement.class);
		assertEquals(2, idMap.size());
		assertEquals("TEST/a/b.txt", idMap.get("TEST/a/b.txt"));
		assertEquals("TEST/d.txt", idMap.get("TEST/d.txt"));
	}

	/**
	 * Asserts that the given list of elements has the given uniform paths (not
	 * necessarily in order).
	 */
	private void assertUniformPaths(List<? extends IElement> elements,
			String... uniformPaths) {
		Set<String> expected = new HashSet<String>(Arrays.asList(uniformPaths));
		Set<String> actual = new HashSet<String>();
		for (IElement element : elements) {
			actual.add(element.getUniformPath());
		}

		assertEquals(expected, actual);
	}

	/** Creates the scope to work on. */
	private IResource createScope() throws ConQATException {
		return (IResource) executeProcessor(
				ResourceBuilder.class,
				"(scope=(ref=memScope('TEST/a/b.txt'=B, 'TEST/a/c.bin'=C, 'TEST/d.txt'=D, 'TEST/x/y/z.bin'=E)), ",
				"factory=(pattern='**/*.txt', ref=textFactory()), factory=(pattern='**', ref=binFactory()))");
	}
}