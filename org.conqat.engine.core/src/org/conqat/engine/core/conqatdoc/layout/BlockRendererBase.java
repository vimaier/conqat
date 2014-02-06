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
package org.conqat.engine.core.conqatdoc.layout;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import org.conqat.engine.core.driver.specification.IConditionalParameter;
import org.conqat.lib.commons.collections.PairList;
import org.conqat.lib.commons.image.GraphicsUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.w3c.dom.Element;

/**
 * Base class for generating the graph and image map for a block. This works
 * mainly on objects, which is due to the fact that we have no common base class
 * for block parameters, outputs, and units.
 * 
 * @author $Author: heineman $
 * @version $Rev: 39917 $
 * @ConQAT.Rating GREEN Hash: CFF6E079EB322D6CA51AC03209192FE4
 */
public abstract class BlockRendererBase {

	/** The supported child types. */
	public static enum EChildType {
		/** Block type. */
		BLOCK,

		/** Processor type. */
		PROCESSOR,

		/** Specification parameter (input). */
		PARAMETER,

		/** Specification output. */
		OUTPUT;
	}

	/** The meta data used when rendering this. */
	private final CQEditMetaData metaData = new CQEditMetaData();

	/**
	 * List to append image map coordinates to. See parameter of
	 * {@link #createImage(PairList)}.
	 */
	private PairList<String, Rectangle> imageMapCoords;

	/**
	 * Constructor.
	 * 
	 * @param metaElement
	 *            the meta element containing layout information. If this is
	 *            null, an extremely simple auto-layout will be used.
	 */
	protected BlockRendererBase(Element metaElement) {
		if (metaElement != null) {
			metaData.clearAndLoadFromElement(metaElement);
		}
	}

	/** Builds the graph and returns the HTML code for an image map. */
	public String renderGraph(File outputFile, String format)
			throws IOException {
		PairList<String, Rectangle> imageMapCoords = new PairList<String, Rectangle>();
		ImageIO.write(createImage(imageMapCoords), format, outputFile);

		StringBuilder imageMap = new StringBuilder();
		for (int i = 0; i < imageMapCoords.size(); ++i) {
			Rectangle bounds = imageMapCoords.getSecond(i);
			imageMap.append("<area shape=\"rect\" href=\""
					+ imageMapCoords.getFirst(i) + "\" coords=\"" + bounds.x
					+ "," + bounds.y + "," + bounds.getMaxX() + ","
					+ bounds.getMaxY() + "\">" + StringUtils.CR);
		}
		return imageMap.toString();
	}

	/**
	 * Creates the image showing the configuration graph.
	 * 
	 * @param imageMapCoords
	 *            contains pairs of the target (URL) and rectangle to be used in
	 *            an image map.
	 */
	public BufferedImage createImage(PairList<String, Rectangle> imageMapCoords) {
		this.imageMapCoords = imageMapCoords;

		Rectangle imageBounds = determineImageBounds();
		BufferedImage image = new BufferedImage(imageBounds.width,
				imageBounds.height, BufferedImage.TYPE_INT_ARGB);
		paint(image.createGraphics(), imageBounds);
		return image;
	}

	/** Performs the actual painting. */
	private void paint(Graphics2D graphics, Rectangle imageBounds) {
		graphics.setBackground(Color.WHITE);
		graphics.fillRect(0, 0, imageBounds.width, imageBounds.height);

		graphics.translate(-imageBounds.x, -imageBounds.y);
		graphics.setColor(Color.BLACK);
		graphics.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);

		paintNodes(graphics, imageBounds);
		paintEdges(graphics);
	}

	/** Paints the nodes. */
	private void paintNodes(Graphics2D graphics, Rectangle imageBounds) {
		for (Object child : getChildren()) {
			switch (getType(child)) {
			case PROCESSOR:
				paintProcessor(graphics, child);
				break;
			case BLOCK:
				paintBlock(graphics, child);
				break;
			case PARAMETER:
				if (IConditionalParameter.PARAMETER_NAME.equals(getName(child))) {
					continue;
				}
				paintParameter(graphics, child);
				break;
			case OUTPUT:
				paintOutput(graphics, child);
				break;
			}

			String url = getImageMapURL(child);
			if (!StringUtils.isEmpty(url)) {
				Rectangle bounds = getBounds(child);
				bounds.x -= imageBounds.x;
				bounds.y -= imageBounds.y;
				imageMapCoords.add(url, bounds);
			}
		}
	}

	/** Paints the given processor. */
	private void paintProcessor(Graphics2D graphics, Object child) {
		paintUnit(graphics, child, DesignConstants.COLOR_PROCESSOR_FOREGROUND);
	}

	/** Paints the given block. */
	private void paintBlock(Graphics2D graphics, Object child) {
		paintUnit(graphics, child, DesignConstants.COLOR_BLOCK_FOREGROUND);
	}

	/** Paints a unit. */
	private void paintUnit(Graphics2D graphics, Object child,
			Color foregroundColor) {
		Rectangle bounds = getBounds(child);
		bounds.grow(-DesignConstants.PORT_INSET, -DesignConstants.PORT_INSET);

		graphics.setPaint(new GradientPaint(0, bounds.y,
				DesignConstants.GRADIENT_COLOR, 0, (float) bounds.getMaxY(),
				getBackgroundColor(child)));
		graphics.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height,
				DesignConstants.UNIT_CORNER, DesignConstants.UNIT_CORNER);

		graphics.setColor(foregroundColor);
		int y = (int) bounds.getCenterY();
		graphics.drawLine(bounds.x, y, bounds.x + bounds.width - 1, y);

		graphics.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height,
				DesignConstants.UNIT_CORNER, DesignConstants.UNIT_CORNER);

		graphics.setColor(Color.BLACK);
		Rectangle clip = new Rectangle(bounds);
		clip.grow(-3, -3);
		graphics.setClip(clip);
		Font oldFont = graphics.getFont();
		graphics.setFont(oldFont.deriveFont(Font.BOLD,
				DesignConstants.TITLE_FONT_SIZE));
		graphics.drawString(getTypeName(child), bounds.x
				+ DesignConstants.HORIZONTAL_LABEL_MARGIN,
				(int) (bounds.getCenterY() - 5));
		graphics.setFont(oldFont
				.deriveFont(DesignConstants.TITLE_FONT_SIZE - 1));
		graphics.drawString(getName(child), bounds.x
				+ DesignConstants.HORIZONTAL_LABEL_MARGIN, bounds.y
				+ bounds.height - 5);
		graphics.setFont(oldFont);
		graphics.setClip(null);
	}

	/** Returns the background color used for the element. */
	protected Color getBackgroundColor(Object child) {
		switch (getType(child)) {
		case PROCESSOR:
			return DesignConstants.COLOR_PROCESSOR_BACKGROUND;
		case BLOCK:
			return DesignConstants.COLOR_BLOCK_BACKGROUND;
		case PARAMETER:
			return DesignConstants.COLOR_SPEC_PARAM_BACKGROUND;
		case OUTPUT:
			return DesignConstants.COLOR_SPEC_OUT_BACKGROUND;
		}
		throw new AssertionError("Can not happen!");
	}

	/** Paints the given parameter. */
	private void paintParameter(Graphics2D graphics, Object child) {
		paintSpecIO(graphics, child, true);
	}

	/** Paints the given output. */
	private void paintOutput(Graphics2D graphics, Object child) {
		paintSpecIO(graphics, child, false);
	}

	/** Paints a specification input/output. */
	private void paintSpecIO(Graphics2D graphics, Object child,
			boolean apexBottom) {
		Rectangle bounds = getBounds(child);
		int[] points = BlockSpecIOShapeUtils.getPoints(bounds, apexBottom);
		Polygon p = new Polygon();
		for (int i = 0; i < points.length;) {
			p.addPoint(points[i++], points[i++]);
		}

		graphics.setColor(getBackgroundColor(child));
		graphics.fillPolygon(p);

		bounds = BlockSpecIOShapeUtils.getGradientBounds(bounds, apexBottom);
		if (apexBottom) {
			graphics.setPaint(new GradientPaint(0, bounds.y,
					DesignConstants.GRADIENT_COLOR, 0,
					(float) bounds.getMaxY(), getBackgroundColor(child)));
		} else {
			graphics.setPaint(new GradientPaint(0, bounds.y,
					getBackgroundColor(child), 0, (float) bounds.getMaxY(),
					DesignConstants.GRADIENT_COLOR));
		}
		graphics.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

		graphics.setColor(Color.BLACK);
		graphics.drawPolygon(p);

		Font oldFont = graphics.getFont();
		graphics.setFont(oldFont.deriveFont(Font.BOLD,
				DesignConstants.TITLE_FONT_SIZE));
		bounds.grow(-2, -2);
		graphics.setClip(bounds);
		graphics.drawString(getName(child), bounds.x + 5, bounds.y
				+ bounds.height - 5);
		graphics.setClip(null);
		graphics.setFont(oldFont);
	}

	/**
	 * Template method for painting the edges. Use the method
	 * {@link #paintEdge(Graphics2D, Object, Object)} to draw them.
	 */
	protected abstract void paintEdges(Graphics2D graphics);

	/** Paints a single edge. */
	protected void paintEdge(Graphics2D graphics, Object source, Object target) {
		graphics.setColor(DesignConstants.COLOR_CONNECTION_FOREGROUND);
		Stroke oldStroke = graphics.getStroke();
		float[] dash = new float[] { 1 };
		if (getSourceEdgesInvisible(source)) {
			dash = new float[] { 3, 7 };
		}
		graphics.setStroke(new BasicStroke(DesignConstants.LINE_WIDTH,
				BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 2, dash, 0));

		Point sourcePoint = getAnchor(source, target);
		Point targetPoint = getAnchor(target, source);
		graphics.drawLine(sourcePoint.x, sourcePoint.y, targetPoint.x,
				targetPoint.y);

		graphics.setStroke(oldStroke);
		graphics.fill(GraphicsUtils.getArrowHead(sourcePoint, targetPoint, 12,
				Math.toRadians(20)));
	}

	/** Returns source edge visibility for the given element. */
	protected boolean getSourceEdgesInvisible(Object source) {
		return metaData.getSourceEdgesInvisible(getName(source));
	}

	/** Calculates the anchor for an object relative to a reference object. */
	private Point getAnchor(Object child, Object refObject) {
		Rectangle bounds = getBounds(child);
		switch (getType(child)) {
		case PROCESSOR:
		case BLOCK:
			Rectangle refBounds = getBounds(refObject);
			bounds.grow(0, -DesignConstants.PORT_INSET);
			Point referencePoint = new Point((int) refBounds.getCenterX(),
					(int) refBounds.getCenterY());
			return GraphicsUtils.getChopboxAnchor(bounds, referencePoint);
		case PARAMETER:
			return new Point((int) bounds.getCenterX(), (int) bounds.getMaxY());
		case OUTPUT:
			return new Point((int) bounds.getCenterX(), bounds.y);
		}
		return new Point((int) bounds.getCenterX(), (int) bounds.getCenterY());
	}

	/** Returns the bounds of the graph. */
	protected Rectangle determineImageBounds() {
		Rectangle imageBounds = null;
		for (Object child : getChildren()) {
			Rectangle bounds = getBounds(child);
			if (imageBounds == null) {
				imageBounds = bounds;
			} else {
				imageBounds = imageBounds.union(bounds);
			}
		}

		// make sure we have valid bounds even if no children exist.
		if (imageBounds == null) {
			imageBounds = new Rectangle(0, 0, 10, 10);
		}

		imageBounds.grow(DesignConstants.MARGIN, DesignConstants.MARGIN);
		return imageBounds;
	}

	/** Returns the bounds of the given object. */
	protected Rectangle getBounds(Object child) {
		Point position = metaData.getPosition(getName(child));
		if (position == null) {
			position = new Point(0, getChildren().indexOf(child)
					* (DesignConstants.UNIT_HEIGHT + 10));
		}

		Rectangle bounds = new Rectangle(position);
		switch (getType(child)) {
		case PROCESSOR:
		case BLOCK:
			bounds.width = DesignConstants.UNIT_WIDTH;
			bounds.height = DesignConstants.UNIT_HEIGHT;
			break;
		case PARAMETER:
		case OUTPUT:
			bounds.width = DesignConstants.SPEC_IO_WIDTH;
			bounds.height = DesignConstants.SPEC_IO_HEIGHT;
			break;
		}
		return bounds;
	}

	/** Returns the type of the object. */
	protected abstract EChildType getType(Object child);

	/** Returns the name for the object. */
	protected abstract String getName(Object child);

	/** Returns the type name for the object. */
	protected abstract String getTypeName(Object child);

	/** Returns the children. */
	protected abstract List<Object> getChildren();

	/**
	 * Returns the URL used for the image map. Return null if no URL should be
	 * used for this element.
	 */
	protected abstract String getImageMapURL(Object child);
}