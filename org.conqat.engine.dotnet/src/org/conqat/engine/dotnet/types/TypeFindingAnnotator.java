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
package org.conqat.engine.dotnet.types;

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.findings.location.ElementLocation;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.findings.FindingsAnnotatorBase;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.lib.commons.collections.IdentityHashSet;
import org.conqat.lib.commons.collections.ListMap;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating GREEN Hash: 2D5EE986260FFB65138D0F6ECA1C3830
 */
@AConQATProcessor(description = "Annotate findings from types to their source elements")
public class TypeFindingAnnotator extends
		FindingsAnnotatorBase<ITokenResource, IElement> {

	/** Map from element ids to fqnames of contained types */
	private final ListMap<String, String> elementToTypes = new ListMap<String, String>();

	/** Keeps track of findings that have been annotated */
	private final IdentityHashSet<Finding> annotatedFindings = new IdentityHashSet<Finding>();

	/** {@inheritDoc} */
	@Override
	protected void setUp(ITokenResource root) throws ConQATException {
		super.setUp(root);

		List<ITokenElement> elements = ResourceTraversalUtils.listElements(
				root, ITokenElement.class);
		for (ITokenElement element : elements) {
			List<String> typeFqNames = CodeEntityFactory.codeEntitiesFor(
					element.getTokens(getLogger())).collectTypeNames();
			elementToTypes.addAll(element.getId(), typeFqNames);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected List<Finding> determineFindings(IElement element) {
		List<Finding> result = new ArrayList<Finding>();
		List<String> elementTypes = elementToTypes.getCollection(element
				.getId());
		if (elementTypes != null) {
			for (String elementId : elementTypes) {
				List<Finding> typeFindings = findingsByElement
						.getCollection(elementId);

				if (typeFindings != null) {
					for (Finding finding : typeFindings) {
						ElementLocation location = new ElementLocation(
								element.getLocation(), element.getUniformPath());
						finding.setLocation(location);

						result.add(finding);
						annotatedFindings.add(finding);

						getLogger().debug(
								"Annotated finding " + finding
										+ " to location "
										+ location.getUniformPath());
					}
				}

			}
		}

		return result;
	}

	/** {@inheritDoc} */
	@Override
	protected void finish(ITokenResource root) {
		super.finish(root);

		IdentityHashSet<Finding> allFindings = new IdentityHashSet<Finding>(
				findingsByElement.getValues());
		allFindings.removeAll(annotatedFindings);

		for (Finding finding : allFindings) {
			String warningMessage = "Finding could not be annotated to element. Is the element missing in the scope? "
					+ finding
					+ " (uniformPath: "
					+ finding.getLocation().getUniformPath()
					+ ", message:"
					+ finding.getMessage() + ")";
			getLogger().warn(warningMessage);
		}
	}

}
