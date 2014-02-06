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
package org.conqat.engine.code_clones.core.constraint;

import java.util.Map;

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.lib.commons.collections.PairList;

/**
 * Base class for constraints that also require the elements that contain the
 * clone instances.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: EF580B20F8763DE22CA180CC11796AB5
 */
public abstract class ElementConstraintBase extends ConstraintBase {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.INPUT_NAME, attribute = ConQATParamDoc.INPUT_REF_NAME, description = "The scope containing all files inspected during clone detection.")
	public IResource root;

	/** Mapping from uniform path to element. */
	private Map<String, IElement> uniformPathToElementMap;

	/** {@inheritDoc} */
	@Override
	public ICloneClassConstraint process() {
		uniformPathToElementMap = ResourceTraversalUtils
				.createUniformPathToElementMap(root, IElement.class);
		return super.process();
	}

	/** {@inheritDoc} */
	@Override
	public final boolean satisfied(CloneClass cloneClass)
			throws ConQATException {
		PairList<Clone, IElement> clonesAndElements = new PairList<Clone, IElement>();
		for (Clone clone : cloneClass.getClones()) {
			clonesAndElements.add(clone,
					determineElement(clone.getUniformPath()));
		}
		return satisfied(cloneClass, clonesAndElements);
	}

	/**
	 * Returns the element for a uniform path.
	 * 
	 * @throws ConQATException
	 *             if no element for a uniform path was found.
	 */
	private IElement determineElement(String uniformPath)
			throws ConQATException {
		IElement element = uniformPathToElementMap.get(uniformPath);
		if (element == null) {
			throw new ConQATException("No element found for uniform path "
					+ uniformPath + "! Configuration error?");
		}
		return element;
	}

	/**
	 * Returns whether the constraint is satisfied for a clone class, given also
	 * its clones and elements containing clones.
	 */
	protected abstract boolean satisfied(CloneClass cloneClass,
			PairList<Clone, IElement> clonesAndElements) throws ConQATException;

}
