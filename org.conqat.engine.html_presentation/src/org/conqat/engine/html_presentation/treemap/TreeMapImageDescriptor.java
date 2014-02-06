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
package org.conqat.engine.html_presentation.treemap;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.regex.Pattern;

import org.conqat.engine.commons.node.DisplayList;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.html_presentation.EHtmlPresentationFont;
import org.conqat.engine.html_presentation.image.ITooltipDescriptor;
import org.conqat.engine.html_presentation.image.ImageDescriptorBase;
import org.conqat.lib.commons.treemap.CushionTreeMapRenderer;
import org.conqat.lib.commons.treemap.FlatTreeMapRenderer;
import org.conqat.lib.commons.treemap.ITreeMapNode;
import org.conqat.lib.commons.treemap.ITreeMapRenderer;
import org.conqat.lib.commons.treemap.NodeTextRenderer;
import org.conqat.lib.commons.treemap.StripeTreeMapAlgorithm;

/**
 * Image descriptor for tree maps.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 5611BC782A3A90A8F367311EDBAAF649
 */
public class TreeMapImageDescriptor extends ImageDescriptorBase {

	/** Height for the cushions. */
	private final double cushionHeight;

	/** Scale for the cushions. */
	private final double cushionScale;

	/** Color for frames around top level elements */
	private final Color topLevelFrameColor;

	/** Color for node text */
	private final Color textColor;

	/**
	 * Pattern used to split tree map node names that are too long to be
	 * displayed into parts from which the last part is chosen for display (if
	 * this is null, name is not split)
	 */
	private final Pattern separationPattern;

	/** The root of the tree map. */
	private final ITreeMapNode<IConQATNode> treeMapRoot;

	/** The summary. */
	private final Object summary;

	/** Flag that determines whether or not to draw a legend */
	private final boolean drawLegend;

	/**
	 * Constructor.
	 * 
	 * @param cushionHeight
	 *            Height for the cushions.
	 * @param cushionScale
	 *            Scale for the cushions.
	 * @param topLevelFrameColor
	 *            Color for frames around top level elements.
	 * @param textColor
	 *            Color for node text.
	 * @param separationPattern
	 *            Pattern used to split tree map node names.
	 * @param treeMapRoot
	 *            root of the tree map
	 * @param summary
	 *            summary object, e.g. an assessment. This is used to store
	 *            summaries for assessment results.
	 * 
	 */
	public TreeMapImageDescriptor(double cushionHeight, double cushionScale,
			Color topLevelFrameColor, Color textColor,
			Pattern separationPattern, ITreeMapNode<IConQATNode> treeMapRoot,
			Object summary, boolean drawLegend) {
		super("tree_map.gif");
		this.cushionHeight = cushionHeight;
		this.cushionScale = cushionScale;
		this.topLevelFrameColor = topLevelFrameColor;
		this.textColor = textColor;
		this.separationPattern = separationPattern;
		this.treeMapRoot = treeMapRoot;
		this.summary = summary;
		this.drawLegend = drawLegend;
	}

	/** {@inheritDoc} */
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(800, 600);
	}

	/** {@inheritDoc} */
	@Override
	public void draw(Graphics2D graphics, int width, int height) {

		// white background
		graphics.setPaint(Color.white);
		graphics.fillRect(0, 0, width, height);

		LegendDescriptor legendDrawer = new LegendDescriptor(width,
				treeMapRoot.getUserDatum());

		// Even if the legend is disabled, we do not set its height to zero,
		// since this changes the dimensions of the TreeMap. which can change
		// the layout dramatically. We don't want this in dashboards that
		// contain several treemaps.
		int legendHeight = legendDrawer.getHeight();
		int actualHeight = calcActualHeight(height, legendHeight);

		new StripeTreeMapAlgorithm().layout(treeMapRoot, new Rectangle(0, 0,
				width, actualHeight));

		renderTreeMap(graphics);
		drawTopLevelFrame(graphics);
		drawText(graphics);

		graphics.translate(0, height - legendHeight);
		if (drawLegend) {
			legendDrawer.draw(graphics);
		}
	}

	/** Calculate height of the tree map w.r.t. to the height of the legend. */
	private int calcActualHeight(int height, int legendHeight) {
		if (legendHeight == 0) {
			return height;
		}
		// separation between tree map and legend.
		return height - legendHeight - 10;
	}

	/** Render the tree map. */
	private void renderTreeMap(Graphics2D graphics) {
		ITreeMapRenderer renderer;

		if (useCushionRenderer()) {
			renderer = new CushionTreeMapRenderer(cushionHeight, cushionScale);
		} else {
			renderer = new FlatTreeMapRenderer();
		}

		renderer.renderTreeMap(treeMapRoot, graphics);
	}

	/** Draw top level frames in the image. */
	private void drawTopLevelFrame(Graphics2D graphics) {
		if (topLevelFrameColor == null) {
			return;
		}
		graphics.setColor(topLevelFrameColor);
		graphics.setStroke(new BasicStroke(3));
		ITreeMapNode<IConQATNode> node = treeMapRoot;
		while (node.getChildren().size() == 1) {
			node = node.getChildren().get(0);
		}
		for (ITreeMapNode<IConQATNode> child : node.getChildren()) {
			graphics.draw(child.getLayoutRectangle());
		}
	}

	/** Render the text. */
	private void drawText(Graphics2D graphics) {
		if (textColor == null) {
			return;
		}

		graphics.setFont(EHtmlPresentationFont.SANS_CONDENSED.getFont());
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		NodeTextRenderer texter = new NodeTextRenderer(textColor,
				separationPattern);
		texter.renderTreeMap(treeMapRoot, graphics);
	}

	/** Check if cushion renderer is used. */
	private boolean useCushionRenderer() {
		return cushionHeight >= 0;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isVectorFormatSupported() {
		return !useCushionRenderer();
	}

	/** {@inheritDoc} */
	@Override
	public Object getSummary() {
		return summary;
	}

	/** {@inheritDoc} */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ITooltipDescriptor<Object> getTooltipDescriptor(int width, int height) {
		LegendDescriptor legendDrawer = new LegendDescriptor(width,
				treeMapRoot.getUserDatum());
		return (ITooltipDescriptor) new TooltipDescriptor(treeMapRoot, width,
				calcActualHeight(height, legendDrawer.getHeight()));
	}

	/** Tool tip descriptor. */
	private static class TooltipDescriptor implements
			ITooltipDescriptor<ITreeMapNode<IConQATNode>> {

		/** Root of the tree map. */
		private final ITreeMapNode<IConQATNode> treeMapRoot;

		/** Constructor. */
		private TooltipDescriptor(ITreeMapNode<IConQATNode> treeMapRoot,
				int width, int height) {
			this.treeMapRoot = treeMapRoot;
			new StripeTreeMapAlgorithm().layout(treeMapRoot, new Rectangle(0,
					0, width, height));
		}

		/** {@inheritDoc} */
		@Override
		public DisplayList getDisplayList() {
			return NodeUtils.getDisplayList(treeMapRoot.getUserDatum());
		}

		/** {@inheritDoc} */
		@Override
		public ITreeMapNode<IConQATNode> getRoot() {
			return treeMapRoot;
		}

		/** {@inheritDoc} */
		@Override
		public Rectangle2D obtainBounds(ITreeMapNode<IConQATNode> node) {
			return node.getLayoutRectangle();
		}

		/** {@inheritDoc} */
		@Override
		public String obtainId(ITreeMapNode<IConQATNode> node) {
			return node.getTooltipId();
		}

		/** {@inheritDoc} */
		@Override
		public Object obtainValue(ITreeMapNode<IConQATNode> node, String key) {
			return node.getTooltipValue(key);
		}

		/** {@inheritDoc} */
		@Override
		public boolean hasChildren(ITreeMapNode<IConQATNode> node) {
			return !node.getChildren().isEmpty();
		}

		/** {@inheritDoc} */
		@Override
		public List<ITreeMapNode<IConQATNode>> obtainChildren(
				ITreeMapNode<IConQATNode> node) {
			return node.getChildren();
		}

		/** {@inheritDoc} */
		@Override
		public boolean isTooltipsForInnerNodes() {
			return false;
		}
	}
}