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
package org.conqat.engine.html_presentation.color;

import java.awt.Color;

import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.commons.traversal.NodeTraversingProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: deissenb $
 * @version $Rev: 42191 $
 * @ConQAT.Rating GREEN Hash: 02341CD7258494B3F66ECC9A0F27E42F
 */
@AConQATProcessor(description = "Sets a fixed color for specified values or on all nodes")
public class FixedValueColorizer extends
		NodeTraversingProcessorBase<IConQATNode> {

	/** Color key */
	@AConQATKey(description = "Color key", type = "java.awt.Color")
	public static final String COLOR_KEY = "color";

	/** Color to set. */
	private Color color;

	/** {@ConQAT.Doc} */
	@AConQATParameter(description = "Fixed color that gets set on all nodes", name = "fixed", minOccurrences = 1, maxOccurrences = 1)
	public void setColor(
			@AConQATAttribute(description = "Color value", name = COLOR_KEY) Color color) {
		this.color = color;
	}

	/** {@inheritDoc} */
	@Override
	protected ETargetNodes getTargetNodes() {
		return ETargetNodes.ALL;
	}

	/** Sets the fixed color. */
	@Override
	public void visit(IConQATNode node) {
		node.setValue(COLOR_KEY, color);
	}

}