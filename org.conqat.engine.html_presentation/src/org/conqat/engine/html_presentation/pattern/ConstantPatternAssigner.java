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
package org.conqat.engine.html_presentation.pattern;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.conqat.lib.commons.treemap.IDrawingPattern;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * Assigns each constant value a given pattern.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 84B90CB5BC0941C92BD57D3882C08F06
 */
@AConQATProcessor(description = "Assigns drawing patterns to the provided nodes based on the "
		+ "stored value, where the values are assigned fixed colors.")
public class ConstantPatternAssigner extends
		PatternAssignerBase<Object, String> {

	/** Colors already fixed. */
	private final Map<String, Color> colors = new HashMap<String, Color>();

	/** Patterns already fixed. */
	private final Map<String, IDrawingPattern> patterns = new HashMap<String, IDrawingPattern>();

	/** Set size key. */
	@AConQATParameter(name = "rule", minOccurrences = 1, description = "Fixes the color and pattern for all values whose "
			+ "string representation matches the given value.")
	public void addPattern(
			@AConQATAttribute(name = "value", description = "The value for which this pattern is used.")
			String value,
			@AConQATAttribute(name = ConQATParamDoc.HTML_COLOR_NAME, description = ConQATParamDoc.HTML_COLOR_DESC)
			Color color,
			@AConQATAttribute(name = "pattern", description = "The drawing pattern to be used.")
			IDrawingPattern pattern) {

		colors.put(value, color);
		patterns.put(value, pattern);
	}

	/** {@inheritDoc} */
	@Override
	protected void handleNode(IConQATNode node, Object value) {
		String key = value.toString();
		if (patterns.containsKey(key)) {
			assignPattern(node, patterns.get(key), colors.get(key));
		}
	}

	/** {@inheritDoc} */
	@Override
	protected Map<String, IDrawingPattern> getLegend() {
		return patterns;
	}
}