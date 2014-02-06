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
package org.conqat.engine.resource.text;

import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.build.ResourceBuilder;
import org.conqat.engine.resource.test.ResourceProcessorTestCaseBase;
import org.conqat.engine.resource.util.ResourceTraversalUtils;

/**
 * Tests the {@link TextResourceSelector}.
 * 
 * @author hummelb
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: D9F6ACD3AB23C1F073819519D6F586BB
 */
public class TextResourceSelectorTest extends ResourceProcessorTestCaseBase {

	/** Tests the basic behavior. */
	public void testBasic() throws Exception {
		IResource input = (IResource) executeProcessor(
				ResourceBuilder.class,
				"(scope=(ref=memScope('TEST/a/b.txt'=B, 'TEST/a/c.bin'=C, 'TEST/d.txt'=D, 'TEST/x/y/z.bin'=E)), ",
				"factory=(pattern='**/*.txt', ref=textFactory()), factory=(pattern='**', ref=binFactory()))");

		IResource result = (IResource) executeProcessor(
				TextResourceSelector.class, "(input=(ref=", input, "))");

		assertNoEmptyContainer(result, TextContainer.class);
		assertEquals(2, ResourceTraversalUtils.countNonContainers(result));
		assertValidPath((TextContainer) result, "TEST/a/b.txt", TextContainer.class,
				TextElement.class);
		assertValidPath((TextContainer) result, "TEST/d.txt", TextContainer.class,
				TextElement.class);
	}

}