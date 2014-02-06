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
package org.conqat.engine.resource.regions;

import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.engine.resource.text.TextElementProcessorBase;
import org.conqat.lib.commons.region.RegionSet;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: juergens $
 * @version $Rev: 35198 $
 * @ConQAT.Rating GREEN Hash: D5BB3ED19700A692557D4B32A2922196
 */
@AConQATProcessor(description = "Counts the number of regions in a named RegionSet")
public class RegionCounter extends TextElementProcessorBase {

	/** Name of the RegionSet that gets counted */
	private String regionSetName;

	/** Name of the key into which result gets written */
	private String writeKey = "regioncount";

	/** Flag that determines whether result key gets displayed */
	private boolean displayKey = false;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "regionset", description = "Name of region set that gets counted", minOccurrences = 1, maxOccurrences = 1)
	public void setRegionSetName(
			@AConQATAttribute(name = "name", description = "Name of region set that gets counted") String regionSetName) {
		this.regionSetName = regionSetName;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "write", description = "Name of the key into which region count is written", minOccurrences = 0, maxOccurrences = 1)
	public void setWriteKey(
			@AConQATAttribute(name = "key", description = "Default is regioncount") String writeKey,
			@AConQATAttribute(name = "display", defaultValue = "false", description = "Flag that determines whether key is displayed in node's display list") boolean displayKey) {
		this.writeKey = writeKey;
		this.displayKey = displayKey;
	}

	/** {@inheritDoc} */
	@Override
	protected ETargetNodes getTargetNodes() {
		return ETargetNodes.LEAVES;
	}

	/** Add writeKey to displayList, if displayKey flag is set */
	@Override
	protected void setUp(ITextResource root) {
		if (displayKey) {
			NodeUtils.addToDisplayList(root, writeKey);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void processElement(ITextElement element) throws ConQATException {
		// count regions
		RegionSetDictionary dictionary = RegionSetDictionary.retrieve(element);
		int count = 0;
		if (dictionary != null && dictionary.contains(regionSetName)) {
			RegionSet regionSet = dictionary.get(regionSetName);
			count = regionSet.size();
		}

		// store count
		element.setValue(writeKey, count);

	}

}