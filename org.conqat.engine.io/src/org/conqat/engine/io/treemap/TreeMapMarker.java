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
package org.conqat.engine.io.treemap;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author hummelb
 * @author $Author: pfaller $
 * @version $Rev: 37470 $
 * @ConQAT.Rating GREEN Hash: DAA8498A3E50FCD064EF127880ED85D4
 */
@AConQATProcessor(description = "This processor marks a node hierarchy as tree map relevant. "
		+ "This is used in the context of QLaunch, to define the output handler used. "
		+ "The keys chosen for size and color will be renamed to default names, "
		+ "which are understood by external tools reading serialized tree map data. "
		+ "For this, the display list is adjusted, and the keys are copied if needed.")
public class TreeMapMarker extends ConQATProcessorBase {

	/** Target key used for size. */
	private static final String TARGET_SIZE_KEY = "size";

	/** Target key used for color. */
	private static final String TARGET_COLOR_KEY = "color";

	/** The wrapped node. */
	private IConQATNode input;

	/** Key used for size. */
	private String sizeKey;

	/** Key used for color. */
	private String colorKey = TARGET_COLOR_KEY;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.INPUT_NAME, minOccurrences = 1, maxOccurrences = 1, description = ConQATParamDoc.INPUT_DESC)
	public void setRoot(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) IConQATNode input) {
		this.input = input;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "size", minOccurrences = 1, maxOccurrences = 1, description = "Set the key defining the size.")
	public void setSizeKey(
			@AConQATAttribute(name = "key", description = "Name of the key to use for size.") String key) {
		sizeKey = key;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "color", maxOccurrences = 1, description = ""
			+ "Set the key defining the color. If this is not set, the key "
			+ TARGET_COLOR_KEY + " is used.")
	public void setColorKey(
			@AConQATAttribute(name = "key", description = "Name of the key to use for color.") String key) {
		colorKey = key;
	}

	/** {@inheritDoc} */
	@Override
	public TreeMapNode process() {
		exposeKey(colorKey, TARGET_COLOR_KEY);
		exposeKey(sizeKey, TARGET_SIZE_KEY);
		return new TreeMapNode(input);
	}

	/**
	 * Exposes the given keys by copying the values from source keys to target
	 * keys.
	 */
	private void exposeKey(String source, String target) {
		if (!source.equals(target)) {
			NodeUtils.getDisplayList(input).removeKey(source);
			for (IConQATNode node : TraversalUtils.listAllDepthFirst(input)) {
				node.setValue(target, node.getValue(source));
			}
		}
		NodeUtils.addToDisplayList(input, target);
	}
}