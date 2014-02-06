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
package org.conqat.engine.simulink.scope;

import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.testutils.NodeTestUtils;
import org.conqat.engine.resource.IContentAccessor;
import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.build.ResourceBuilder;
import org.conqat.engine.resource.scope.filesystem.FileSystemScope;
import org.conqat.engine.resource.test.ResourceProcessorTestCaseBase;
import org.conqat.lib.commons.collections.IIdProvider;
import org.conqat.lib.commons.test.DeepCloneTestUtils;
import org.conqat.lib.simulink.model.SimulinkModel;
import org.conqat.lib.simulink.util.SimulinkIdProvider;

/**
 * Test for Simulink Resources
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: D88778029785003C14EFB8081B29FB41
 */
public class SimulinkResourceTest extends ResourceProcessorTestCaseBase {

	/** Creates a scope with {@link ISimulinkResource}s. */
	protected ISimulinkResource createTestFileSystemElementTree()
			throws Exception {

		IContentAccessor[] contentAccessors = (IContentAccessor[]) executeProcessor(
				FileSystemScope.class, "(project=(name=TEST), root=(dir='",
				useCanonicalTestFile(".").getCanonicalPath(),
				"'), exclude=(pattern='**/.svn/**'), include=(pattern='**/*.mdl'))");

		IResource resource = (IResource) executeProcessor(
				ResourceBuilder.class, "(scope=(ref=", contentAccessors,
				"), factory=(pattern='**', ref=",
				executeProcessor(SimulinkElementFactory.class, "()"), "))");

		return (ISimulinkResource) executeProcessor(
				SimulinkResourceSelector.class, "(input=(ref=", resource, "))");
	}

	/** Tests deep cloning. */
	public void testDeepClone() throws Exception {
		ISimulinkResource root = createTestFileSystemElementTree();
		DeepCloneTestUtils.testDeepCloning(root, root.deepClone(),
				new IdProvider(),
				ISimulinkElement.class.getPackage().getName(),
				SimulinkModel.class.getPackage().getName());
	}

	/**
	 * An Id provider for {@link ISimulinkElement}s. If the provided element is
	 * an {@link IConQATNode} this class uses the
	 * {@link NodeTestUtils.ConQATNodeIdProvider} if not it delegates to
	 * {@link SimulinkIdProvider}.
	 */
	private class IdProvider implements IIdProvider<String, Object> {

		/** Provider for ConQAT nodes. */
		private final IIdProvider<String, Object> conqatNodeIdProvider = new NodeTestUtils.ConQATNodeIdProvider();

		/** Provider for Simulink model elements. */
		private final IIdProvider<String, Object> simulinkIdProvider = new SimulinkIdProvider();

		/** Delegates to other providers. */
		@Override
		public String obtainId(Object object) {
			if (object instanceof IConQATNode) {
				return conqatNodeIdProvider.obtainId(object);
			}
			return simulinkIdProvider.obtainId(object);
		}

	}

}