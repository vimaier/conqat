/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
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
package org.conqat.engine.sourcecode.analysis.cpp;

import java.util.List;

import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.util.UniformPathUtils;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;
import org.conqat.lib.commons.string.StringUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: goede $
 * @version $Rev: 41726 $
 * @ConQAT.Rating GREEN Hash: F66B222239B0FE278A35A091F884BBDF
 */
@AConQATProcessor(description = "This processor analyzes source code for "
		+ "multiple toplevel class definitions per file. It also reports "
		+ "findings if the class name does not match the file name.")
public class CppTypeDefinitionsAnalyzer extends CppFindingAnalyzerBase {

	/** {@inheritDoc} */
	@Override
	protected void analyzeShallowEntities(ITokenElement element,
			List<ShallowEntity> entities) throws ConQATException {

		List<ShallowEntity> toplevelClasses = CppUtils
				.listToplevelClasses(entities);
		if (toplevelClasses.size() > 1) {
			for (ShallowEntity entity : toplevelClasses) {
				createFindingForEntityStart(
						"Multiple toplevel class definitions in file: "
								+ entity.getName(), element, entity);
			}
		} else if (toplevelClasses.size() == 1) {
			ShallowEntity entity = toplevelClasses.get(0);
			if (!entity.getName().equals(getBaseName(element))) {
				createFindingForEntityStart("Class name \"" + entity.getName()
						+ "\" does not match file name", element, entity);
			}
		}
	}

	/**
	 * Returns the base name of an element, i.e. removes path and extension from
	 * a file name.
	 */
	private static String getBaseName(ITokenElement element) {
		String elementName = UniformPathUtils.getElementName(element
				.getUniformPath());
		return StringUtils.removeLastPart(elementName, '.');
	}

	/** {@inheritDoc} */
	@Override
	protected String getFindingGroupName() {
		return "Multiple type definitions";
	}
}
