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
package org.conqat.engine.simulink.util;

import java.io.FileNotFoundException;

import org.conqat.engine.resource.IContentAccessor;
import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.build.ResourceBuilder;
import org.conqat.engine.resource.scope.filesystem.FileContentAccessor;
import org.conqat.engine.resource.test.ResourceProcessorTestCaseBase;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.engine.simulink.scope.ISimulinkElement;
import org.conqat.engine.simulink.scope.ISimulinkResource;
import org.conqat.engine.simulink.scope.SimulinkElementFactory;
import org.conqat.engine.simulink.scope.SimulinkResourceSelector;
import org.conqat.lib.commons.filesystem.CanonicalFile;

/**
 * Base class for test that need a Simulink Scope to operate.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: DEB76775F493FE8059F084D39F07C908
 */
public abstract class SimulinkTestCaseBase extends
		ResourceProcessorTestCaseBase {

	/**
	 * Read a specific test data file from the test-data directory.
	 * 
	 * @throws FileNotFoundException
	 *             if file was not found.
	 */
	protected ISimulinkResource readSimulinkElement(String... filenames)
			throws Exception {
		IContentAccessor[] contentAccessors = new IContentAccessor[filenames.length];

		CanonicalFile root = useCanonicalTestFile(".");
		for (int i = 0; i < filenames.length; ++i) {
			CanonicalFile file = useCanonicalTestFile(filenames[i] + ".mdl");
			if (!file.isFile()) {
				throw new FileNotFoundException("Can't read file "
						+ filenames[i]);
			}
			contentAccessors[i] = new FileContentAccessor(file, root, "TEST");
		}

		IResource resource = (IResource) executeProcessor(
				ResourceBuilder.class, "(scope=(ref=", contentAccessors,
				"), factory=(pattern='**', ref=",
				executeProcessor(SimulinkElementFactory.class, "()"), "))");

		return (ISimulinkResource) executeProcessor(
				SimulinkResourceSelector.class, "(input=(ref=", resource, "))");
	}

	/**
	 * Read the test date file that belongs to the test case from the test-data
	 * directory.
	 * 
	 * @throws FileNotFoundException
	 *             if file was not found.
	 */
	protected ISimulinkResource readSimulinkElement() throws Exception {
		String filename = getClass().getSimpleName();
		return readSimulinkElement(filename);
	}

	/**
	 * This method iterates over the children of the provided element and
	 * returns the child that is a {@link ISimulinkElement} and has the name of
	 * the test class.
	 * 
	 * @return the model or <code>null</code> if the model was not found.
	 */
	protected ISimulinkElement getModelElement(ISimulinkResource element) {
		return getModelElement(element, getClass().getSimpleName());
	}

	/**
	 * This method iterates over the children of the provided element and
	 * returns the child that is a {@link ISimulinkElement} and has the
	 * specified name.
	 * 
	 * @return the model or <code>null</code> if the model was not found.
	 */
	protected ISimulinkElement getModelElement(ISimulinkResource element,
			String name) {
		for (ISimulinkElement modelElement : ResourceTraversalUtils
				.listElements(element, ISimulinkElement.class)) {
			if (modelElement.getModel().getName().equals(name)) {
				return modelElement;
			}
		}
		return null;
	}
}