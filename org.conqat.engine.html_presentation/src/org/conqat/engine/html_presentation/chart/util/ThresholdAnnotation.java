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
package org.conqat.engine.html_presentation.chart.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.conqat.engine.commons.range_distribution.PercentageLessOrEqualRule;
import org.conqat.engine.html_presentation.EHtmlPresentationFont;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.string.StringUtils;
import org.jfree.chart.annotations.CategoryAnnotation;
import org.jfree.chart.axis.CategoryAnchor;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.ui.RectangleEdge;

/**
 * Annotation for bar charts that visualizes a threshold defined by a
 * {@link PercentageLessOrEqualRule}. This only works for horizontal charts.
 * 
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: D20C4D521FD785F975DA457AC561EC13
 */
public class ThresholdAnnotation implements CategoryAnnotation {

	/** Width of the threshold line. */
	private final static int STROKE_WIDTH = 3;

	/** The threshold. */
	private final double threshold;

	/** The category. */
	private final Comparable<?> category;

	/** The label (may be null). */
	private final String label;

	/** Constructor. */
	public ThresholdAnnotation(PercentageLessOrEqualRule rule, boolean showLabel) {
		category = rule.getSecondaryMetric();
		threshold = rule.getThreshold();
		if (showLabel) {
			label = StringUtils.concat(rule.getRangeNames(), "/") + " Thresh.";
		} else {
			label = null;
		}
	}

	/** {@inheritDoc} */
	@Override
	public void draw(Graphics2D g2, CategoryPlot plot, Rectangle2D dataArea,
			CategoryAxis domainAxis, ValueAxis rangeAxis) {

		if (plot.getOrientation() == PlotOrientation.VERTICAL) {
			CCSMAssert.fail("Not supported");
		}

		CategoryDataset dataset = plot.getDataset();
		int catIndex = dataset.getColumnIndex(category);

		int catCount = dataset.getColumnCount();

		if (catIndex < 0) {
			return;
		}

		PlotOrientation orientation = plot.getOrientation();

		RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(
				plot.getDomainAxisLocation(), orientation);
		RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(
				plot.getRangeAxisLocation(), orientation);

		int x = (int) rangeAxis.valueToJava2D(this.threshold, dataArea,
				rangeEdge);

		int topY = (int) domainAxis.getCategoryJava2DCoordinate(
				CategoryAnchor.START, catIndex, catCount, dataArea, domainEdge);
		int middleY = (int) domainAxis
				.getCategoryJava2DCoordinate(CategoryAnchor.MIDDLE, catIndex,
						catCount, dataArea, domainEdge);
		int bottomY = (int) domainAxis.getCategoryJava2DCoordinate(
				CategoryAnchor.END, catIndex, catCount, dataArea, domainEdge);

		g2.setPaint(Color.black);
		drawLabel(g2, x, topY);

		g2.setStroke(new BasicStroke(STROKE_WIDTH, BasicStroke.CAP_ROUND,
				BasicStroke.JOIN_ROUND));

		// we draw two short dashes to not cross out the label
		int textHeight = g2.getFontMetrics(domainAxis.getLabelFont())
				.getHeight();
		g2.drawLine(x, topY, x, middleY - (textHeight / 2) - 2);
		g2.drawLine(x, bottomY, x, middleY + (textHeight / 2) + 2);

	}

	/** Draw label (if defined). */
	private void drawLabel(Graphics2D g2, int x, int y) {
		if (label != null) {
			g2.setFont(EHtmlPresentationFont.SANS_CONDENSED.getFont());
			int width = g2.getFontMetrics().stringWidth(label);
			g2.drawString(label, x - width - STROKE_WIDTH, y
					+ g2.getFontMetrics().getAscent());
		}
	}

}
