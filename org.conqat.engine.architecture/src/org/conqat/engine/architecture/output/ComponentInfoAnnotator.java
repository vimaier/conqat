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
package org.conqat.engine.architecture.output;

import java.util.Collection;
import java.util.List;

import org.conqat.engine.architecture.assessment.ArchitectureAnalyzer;
import org.conqat.engine.architecture.assessment.shared.ICodeMapping;
import org.conqat.engine.architecture.format.ECodeMappingType;
import org.conqat.engine.architecture.scope.ArchitectureDefinition;
import org.conqat.engine.architecture.scope.ComponentNode;
import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43811 $
 * @ConQAT.Rating GREEN Hash: A1B81CD06BFD9B4DEF067094236DC16A
 */
@AConQATProcessor(description = "Annotate architecture components with (possibly summarized) information on code mappings "
		+ "and matched types and add corresponding keys to the display list.")
public class ComponentInfoAnnotator extends
		ConQATPipelineProcessorBase<ArchitectureDefinition> {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Key under which exclude patters are stored.", type = "java.util.List<java.lang.String>")
	public static final String EXCLUDE_KEY = ECodeMappingType.EXCLUDE.name();

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Key under which include patters are stored.", type = "java.util.List<java.lang.String>")
	public static final String INCLUDE_KEY = ECodeMappingType.INCLUDE.name();

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Key under which number of all matched types is stored.", type = "java.util.List<java.lang.String>")
	public static final String MATCHED_TYPES_COUNT_KEY = "#Own Types";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Key under which matched type names are stored. List gets truncated for space reasons.", type = "java.util.List<java.lang.String>")
	public static final String MATCHED_TYPES_PREVIEW_KEY = "Own Types";
	
	/** {@ConQAT.Doc} */
    @AConQATKey(description = "Key under which the component's description is stored.", type = "java.util.List<java.lang.String>")
    public static final String DESCRIPTION_KEY = "Description";

	/** Number of types after which list gets truncated to limit its screen size */
	private static final int NUMBER_OF_DISPLAYED_TYPES = 5;

	/** {@inheritDoc} */
	@Override
	protected void processInput(ArchitectureDefinition input) {
		NodeUtils.addToDisplayList(input, INCLUDE_KEY, EXCLUDE_KEY,
				MATCHED_TYPES_PREVIEW_KEY, MATCHED_TYPES_COUNT_KEY,
				DESCRIPTION_KEY);

		annotate(input);
	}

	/** Annotate component and its children */
	private void annotate(ComponentNode node) {
	    if (!StringUtils.isEmpty(node.getDescription())) {
	        node.setValue(DESCRIPTION_KEY, node.getDescription());
	    }
		annotateMappings(node);
		annotateMatchedTypes(node);

		if (node.hasChildren()) {
			for (ComponentNode child : node.getChildren()) {
				annotate(child);
			}
		}
	}

	/** Annotate mappings to single component */
	private void annotateMappings(ComponentNode node) {
		for (ICodeMapping codeMapping : node.getCodeMappings()) {
			NodeUtils.getOrCreateStringList(node, codeMapping.getType().name())
					.add(codeMapping.getRegex());
		}
	}

	/** Annotates first matched types to single component */
	private void annotateMatchedTypes(ComponentNode node) {
		Collection<String> matchedTypes = NodeUtils.getStringCollection(node,
				ArchitectureAnalyzer.MATCHED_TYPES_KEY);

		int matchedTypesCount = 0;

		if (matchedTypes != null) {
			List<String> matchedTypesList = CollectionUtils.sort(matchedTypes);
			matchedTypesCount = matchedTypes.size();
			CollectionUtils.truncateEnd(matchedTypesList,
					NUMBER_OF_DISPLAYED_TYPES);
			if (matchedTypesList.size() < matchedTypesCount) {
				matchedTypesList.add("...");
			}
			node.setValue(MATCHED_TYPES_PREVIEW_KEY, matchedTypesList);
		}

		node.setValue(MATCHED_TYPES_COUNT_KEY, matchedTypesCount);
	}
}
