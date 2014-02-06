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
package org.conqat.engine.commons.filter;

import java.util.HashMap;
import java.util.Map;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.commons.node.IRemovableConQATNode;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author Elmar Juergens
 * @author juergens
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @levd.rating GREEN Hash: B890AAC9C0F2A755EF3380EB7E8B1BD4
 */
@AConQATProcessor(description = "Subtracts the elements of one scope from a second scope."
		+ " Differencing is based on node ids.")
public class ScopeDifferencer extends
		ConQATPipelineProcessorBase<IRemovableConQATNode> {

	/** Root file system element whose children get subtracted */
	private IRemovableConQATNode subtract;

	/**
	 * Flag that determines whether for each file that occurs in the subtract
	 * scope, but is missing in the input scope, a warning is issued.
	 */
	private boolean warnMissingFiles = true;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "subtract", description = ""
			+ "Root of the scope that gets subtracted", minOccurrences = 1, maxOccurrences = 1)
	public void setSubtract(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) IRemovableConQATNode subtract) {
		this.subtract = subtract;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "warn", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Flag that determines whether for each file that occurs in the subtract "
			+ "scope, but is missing in the input scope, a warning is issued.")
	public void setWarnMissingFiles(
			@AConQATAttribute(name = "missingFiles", description = "Default behaviour is to issue warnings.") boolean warnMissingFiles) {
		this.warnMissingFiles = warnMissingFiles;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void processInput(IRemovableConQATNode input) {
		// create map of input scope elements
		Map<String, IRemovableConQATNode> inputElementsMap = new HashMap<String, IRemovableConQATNode>();
		for (IRemovableConQATNode element : TraversalUtils
				.listLeavesDepthFirst(input)) {
			inputElementsMap.put(element.getId(), element);
		}

		// iterate elements from subtract scope
		for (IRemovableConQATNode subtractElement : TraversalUtils
				.listLeavesDepthFirst(subtract)) {
			String subtractElementId = subtractElement.getId();
			if (!inputElementsMap.containsKey(subtractElementId)) {
				if (warnMissingFiles) {
					getLogger().warn(
							"Element '" + subtractElementId
									+ "' not found in input");
				}
			} else {
				inputElementsMap.get(subtractElementId).remove();
			}
		}

	}

}