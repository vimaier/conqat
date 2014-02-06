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
package org.conqat.engine.html_presentation.color;

import java.awt.Color;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.commons.traversal.TargetExposedNodeTraversingProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.commons.collections.PairList;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: heineman $
 * @version $Rev: 41618 $
 * @ConQAT.Rating GREEN Hash: AD6FDB38A0496922981E29B6CB70E4A6
 */
@AConQATProcessor(description = "Colorizes the string value stored in a key by replacing it with a suitable ColoredStrings object. "
		+ "The value can either be a single string, a collection of strings, or an object that is converted to a string.")
public class StringKeyColorizer extends
		TargetExposedNodeTraversingProcessorBase<IConQATNode> {

	/** Mapping from pattern lists to colors. */
	private final PairList<PatternList, Color> colorMapping = new PairList<PatternList, Color>();

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.READKEY_NAME, attribute = ConQATParamDoc.READKEY_KEY_NAME, description = "Set the key to colorize.")
	public String readKey;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "default", attribute = "color", optional = true, description = "The color to be used if no pattern matches. Default is black.")
	public Color defaultColor = Color.BLACK;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "mapping", description = "Adds a string pattern to color mapping. The first pattern list that matches a string is used to determine its color.")
	public void addColor(
			@AConQATAttribute(name = "pattern", description = "The patterns used for matching the string.") PatternList pattern,
			@AConQATAttribute(name = "color", description = "The color used for the string matching the pattern.") Color color) {
		colorMapping.add(pattern, color);
	}

	/** {@inheritDoc} */
	@Override
	public void visit(IConQATNode node) {
		Object value = node.getValue(readKey);
		if (value == null) {
			return;
		}

		ColoredStringList result = new ColoredStringList();
		if (value instanceof Iterable<?>) {
			for (Object object : (Iterable<?>) value) {
				String string = String.valueOf(object);
				result.add(string, determineColor(string));
			}
		} else {
			// also valid for strings
			String string = String.valueOf(value);
			result.add(string, determineColor(string));
		}

		node.setValue(readKey, result);
	}

	/** Returns the color to be used for a string. */
	private Color determineColor(String value) {
		for (int i = 0; i < colorMapping.size(); ++i) {
			if (colorMapping.getFirst(i).matchesAny(value)) {
				return colorMapping.getSecond(i);
			}
		}
		return defaultColor;
	}

	/** {@inheritDoc} */
	@Override
	protected ETargetNodes getDefaultTargetNodes() {
		return ETargetNodes.ALL;
	}
}
