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
package org.conqat.engine.architecture.output;

import static org.conqat.engine.architecture.format.ArchitectureDesignConstants.COLOR_COMPONENT_BACKGROUND_DARK;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.conqat.engine.architecture.format.ArchitectureDesignConstants;
import org.conqat.engine.architecture.scope.ComponentNode;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.html_presentation.base.AssessmentDrawer;
import org.conqat.engine.html_presentation.color.ColorizerBase;
import org.conqat.lib.commons.assessment.Assessment;

/**
 * Default implementation of {@link IComponentDecorator}. This can be explicitly
 * used in a block but is also used by {@link ArchitectureImageCreator} if no
 * decorator is specified.
 * <p>
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: DEDAFEB08D0B6F5BDFB4445410CE1F0F
 */
@AConQATProcessor(description = "This is the default decorator for components. It "
		+ "allows to specify a list of keys whose values are presented in the "
		+ "decoration area of the components. Additionally, the decorator respects "
		+ "the key "
		+ ColorizerBase.COLOR_KEY_DEFAULT
		+ " to select the component color. Hence, the colorizers provided by the "
		+ "HTML presentation bundle can be applied to colorize components.")
public class DefaultComponentDecorator extends ConQATProcessorBase implements
		IComponentDecorator {

	/** Value used to indicate that no alias is used. */
	/* package */static final String NO_ALIAS = "__NO_ALIAS__";

	/** Width of assessment visualization bar. */
	private static final int ASSESSMENT_BAR_WIDTH = 50;

	/** Mapping from input keys to aliases. */
	private final Map<String, String> keys = new LinkedHashMap<String, String>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.READKEY_NAME, minOccurrences = 1, description = ""
			+ "The key to show in the decoration area.")
	public void addKey(
			@AConQATAttribute(name = ConQATParamDoc.READKEY_KEY_NAME, description = ConQATParamDoc.READKEY_KEY_DESC) String readKey,
			@AConQATAttribute(name = "alias", description = "This parameter can be (optionally) used to define an alias "
					+ "for the key that is used in the visualization. If the value is "
					+ NO_ALIAS + " no alias is used.", defaultValue = NO_ALIAS) String alias)
			throws ConQATException {
		if (NO_ALIAS.equals(alias)) {
			alias = readKey;
		}
		if (keys.put(readKey, alias) != null) {
			throw new ConQATException("Duplicate read key: " + readKey);
		}
	}

	/** This method returns the instance of the class itself. */
	@Override
	public IComponentDecorator process() {
		return this;
	}

	/**
	 * This method creates the visualizations of the keys. It calls
	 * {@link #visualize(String, ComponentNode, Graphics2D)} for each key and
	 * separates the visualization of each key with a middle dot.
	 */
	@Override
	public void decorate(ComponentNode component, Graphics2D graphics) {
		Iterator<String> it = keys.keySet().iterator();
		FontMetrics metrics = graphics.getFontMetrics();
		while (it.hasNext()) {
			visualize(it.next(), component, graphics);
			if (it.hasNext()) {
				graphics.translate(3, 0);
				graphics.drawString("\u00b7", 0, metrics.getAscent());
				graphics.translate(6, 0);
			}
		}
	}

	/**
	 * Visualize a single key (and its value). This must ensure that the
	 * translation of the graphics context is set to (width of current
	 * decoration, 0) after visualizing the key.
	 * 
	 * @param key
	 *            the key to visualize.
	 * @param component
	 *            the decorated component.
	 * @param graphics
	 *            the graphics context
	 */
	private void visualize(String key, ComponentNode component,
			Graphics2D graphics) {

		String label = keys.get(key) + ": ";

		FontMetrics metrics = graphics.getFontMetrics();
		graphics.drawString(label, 0, metrics.getAscent());
		int width = metrics.stringWidth(label);
		graphics.translate(width, 0);

		Object value = component.getValue(key);
		if (value instanceof Assessment) {
			drawAssessment((Assessment) value, graphics);
		} else {
			drawText(value, graphics);
		}
	}

	/** Draw an assessment value. */
	private void drawAssessment(Assessment value, Graphics2D graphics) {
		int vMargin = 2;
		graphics.translate(0, vMargin);

		// we need to store this as assessment drawer changes it
		Font font = graphics.getFont();
		Paint paint = graphics.getPaint();

		FontMetrics metrics = graphics.getFontMetrics();
		AssessmentDrawer drawer = new AssessmentDrawer(ASSESSMENT_BAR_WIDTH,
				metrics.getAscent());
		drawer.drawSummary(value, graphics);

		graphics.translate(ASSESSMENT_BAR_WIDTH, -vMargin);

		// restore context
		graphics.setFont(font);
		graphics.setPaint(paint);
	}

	/** Draw text label. */
	private void drawText(Object value, Graphics2D graphics) {
		FontMetrics metrics = graphics.getFontMetrics();
		String label = formatValue(value);
		graphics.drawString(label, 0, metrics.getAscent());
		graphics.translate(metrics.stringWidth(label), 0);
	}

	/** Format a value to be visualized as text. */
	private String formatValue(Object value) {
		if (value instanceof Number) {
			return NumberFormat.getInstance().format(value);
		}
		return String.valueOf(value);
	}

	/**
	 * This checks if a color is present at key
	 * {@link ColorizerBase#COLOR_KEY_DEFAULT} and creates a paint that
	 * describes a gradient from white to this color. If no key is present, a
	 * gradient from white to
	 * {@link ArchitectureDesignConstants#COLOR_COMPONENT_BACKGROUND_DARK} is
	 * created.
	 */
	@Override
	public Paint obtainFillPaint(ComponentNode component) {
		Color color = NodeUtils.getValue(component,
				ColorizerBase.COLOR_KEY_DEFAULT, Color.class,
				COLOR_COMPONENT_BACKGROUND_DARK);
		return new GradientPaint(0, 0, Color.white, 0,
				component.getDimension().height, color);
	}

	/** Returns the color black. */
	@Override
	public Paint obtainOutlinePaint(ComponentNode component) {
		return Color.BLACK;
	}

	/** Returns a simple stroke with one-point width. */
	@Override
	public Stroke obtainStroke(ComponentNode component) {
		return new BasicStroke();
	}

}