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

import static org.conqat.engine.architecture.format.ArchitectureDesignConstants.COLOR_COMPONENT_BACKGROUND_DARK;

import java.awt.Color;
import java.awt.Paint;

import org.conqat.engine.architecture.scope.ComponentNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.html_presentation.color.ColorizerBase;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: goede $
 * @version $Rev: 38080 $
 * @ConQAT.Rating GREEN Hash: 6BEFCC6A80C346E332057F55EEDDAA6E
 */
@AConQATProcessor(description = "Component decorator that uses a single color for filling a component. "
		+ "Besides that the behavior is equal to that of DefaultComponentDecorator.")
public class SingleColorComponentDecorator extends DefaultComponentDecorator {

	/** {@inheritDoc} */
	@Override
	public Paint obtainFillPaint(ComponentNode component) {
		return NodeUtils.getValue(component, ColorizerBase.COLOR_KEY_DEFAULT,
				Color.class, COLOR_COMPONENT_BACKGROUND_DARK);
	}

}
