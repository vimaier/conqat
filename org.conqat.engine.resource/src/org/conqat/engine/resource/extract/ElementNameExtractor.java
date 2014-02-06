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
package org.conqat.engine.resource.extract;

import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.engine.resource.util.UniformPathUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: juergens $
 * @version $Rev: 35198 $
 * @ConQAT.Rating GREEN Hash: B64F460C4E1E28872AEC19E66E918552
 */
@AConQATProcessor(description = "This processor extracts the extension for each element "
		+ "and stores it using as key 'Extension'.")
public class ElementNameExtractor extends
		ConQATPipelineProcessorBase<IResource> {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Extension", type = "java.lang.String")
	public static final String EXTENSION_KEY = "Extension";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Local Name", type = "java.lang.String")
	public static final String NAME_KEY = "Name";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "The element's uniform path.", type = "java.lang.String")
	public static final String UNIFORM_PATH_KEY = "Uniform Path";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "The element's location.", type = "java.lang.String")
	public static final String LOCATION_KEY = "Location";

	/** {@inheritDoc} */
	@Override
	protected void processInput(IResource input) {
		NodeUtils.addToDisplayList(input, NAME_KEY, UNIFORM_PATH_KEY,
				LOCATION_KEY, EXTENSION_KEY);

		for (IElement element : ResourceTraversalUtils.listElements(input)) {
			element.setValue(NAME_KEY, element.getName());
			element.setValue(UNIFORM_PATH_KEY, element.getUniformPath());
			element.setValue(LOCATION_KEY, element.getLocation());
			element.setValue(EXTENSION_KEY,
					UniformPathUtils.getExtension(element.getUniformPath()));
		}
	}

}