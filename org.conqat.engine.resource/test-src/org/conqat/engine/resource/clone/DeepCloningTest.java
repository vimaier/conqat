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
package org.conqat.engine.resource.clone;

import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.build.ResourceBuilder;
import org.conqat.engine.resource.test.ResourceProcessorTestCaseBase;
import org.conqat.engine.resource.text.TextResourceSelector;

import org.conqat.lib.commons.test.DeepCloneTestUtils;
import org.conqat.engine.commons.testutils.NodeTestUtils;

/**
 * Tests deep cloning of binary elements and text elements.
 * 
 * @author hummelb
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: A9C4B2A0922250D8D993DA364FAAF2C0
 */
public class DeepCloningTest extends ResourceProcessorTestCaseBase {

	/** Tests cloning of the binary element. */
	public void testBinaryCloning() throws Exception {
		String factoryDescription = "factory=(pattern='**', ref=binFactory())";
		IResource hierarchy = createHierarchy(factoryDescription);
		DeepCloneTestUtils.testDeepCloning(hierarchy, hierarchy.deepClone(),
				new NodeTestUtils.ConQATNodeIdProvider(), "org.conqat");
	}

	/** Tests cloning of the text element. */
	public void testTextCloning() throws Exception {
		String factoryDescription = "factory=(pattern='**', ref=textFactory())";
		IResource hierarchy = createHierarchy(factoryDescription);
		hierarchy = (IResource) executeProcessor(TextResourceSelector.class,
				"(input=(ref=", hierarchy, "))");
		DeepCloneTestUtils.testDeepCloning(hierarchy, hierarchy.deepClone(),
				new NodeTestUtils.ConQATNodeIdProvider(), "org.conqat");
	}

	/** Tests cloning of a mixed binary/text element. */
	public void testMixedScopeCloning() throws Exception {
		String factoryDescription = "factory=(pattern='**/*.txt', ref=textFactory()), "
				+ "factory=(pattern='**', ref=binFactory())";
		IResource hierarchy = createHierarchy(factoryDescription);
		DeepCloneTestUtils.testDeepCloning(hierarchy, hierarchy.deepClone(),
				new NodeTestUtils.ConQATNodeIdProvider(), "org.conqat");
	}

	/** Creates a hierarchy that can be used for deep clone testing. */
	private IResource createHierarchy(String factoryDescription)
			throws Exception {
		return (IResource) executeProcessor(
				ResourceBuilder.class,
				"(scope=(ref=memScope('TEST/a/b.txt'=B, 'TEST/a/c.bin'=C, 'TEST/d.txt'=D, 'TEST/x/y/z.bin'=E)), ",
				factoryDescription, ")");
	}

}