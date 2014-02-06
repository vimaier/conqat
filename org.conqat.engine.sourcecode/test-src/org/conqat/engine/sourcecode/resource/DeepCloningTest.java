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
package org.conqat.engine.sourcecode.resource;

import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.build.ResourceBuilder;
import org.conqat.engine.sourcecode.resource.TokenResourceSelector;

import org.conqat.lib.commons.test.DeepCloneTestUtils;
import org.conqat.engine.commons.testutils.NodeTestUtils;

/**
 * Tests deep cloning of binary elements and text elements.
 * 
 * @author hummelb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @levd.rating GREEN Hash: 74B3C890BD3710D9B8184D9B6FA58FBB
 */
public class DeepCloningTest extends TokenTestCaseBase {

	/** Tests cloning of the text element. */
	public void testDeepCloning() throws Exception {
		String factoryDescription = "factory=(pattern='**', ref=tokenFactory())";
		IResource hierarchy = createHierarchy(factoryDescription);
		hierarchy = (IResource) executeProcessor(TokenResourceSelector.class,
				"(input=(ref=", hierarchy, "))");
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