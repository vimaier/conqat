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
package org.conqat.engine.code_clones.detection.filter;

import java.util.HashSet;
import java.util.Set;

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.util.ResourceTraversalUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: 447510537DBF68B197749CE6FBCE8958
 */
@AConQATProcessor(description = "Filters out all clone classes that don't span at least one file "
		+ "from a set of files")
public class SpannedElementsFilter extends CloneClassFilterBase {

	/** Set of uniform paths of which at least one needs to be spanned. */
	private final Set<String> uniformPaths = new HashSet<String>();

	/** Create file set from resource tree root */
	@AConQATParameter(name = "files", minOccurrences = 1, maxOccurrences = -1, description = ""
			+ "Resource root that contains the elements to be excluded.")
	public void setFileSetRoot(
			@AConQATAttribute(name = "ref", description = "Reference to producing processor") IResource root) {

		for (IElement element : ResourceTraversalUtils.listElements(root)) {
			uniformPaths.add(element.getUniformPath());
		}
	}

	/** {@inheritDoc} */
	@Override
	protected boolean filteredOut(CloneClass cloneClass) {
		for (Clone clone : cloneClass.getClones()) {
			if (uniformPaths.contains(clone.getUniformPath())) {
				return false;
			}
		}
		return true;
	}

}