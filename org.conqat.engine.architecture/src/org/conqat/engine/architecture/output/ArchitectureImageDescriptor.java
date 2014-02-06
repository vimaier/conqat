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

import static org.conqat.engine.architecture.format.ArchitectureDesignConstants.CORNER_RADIUS;
import static org.conqat.engine.architecture.format.ArchitectureDesignConstants.HORIZONTAL_LABEL_MARGIN;
import static org.conqat.engine.architecture.format.ArchitectureDesignConstants.VERTICAL_LABEL_MARGIN;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.List;

import org.conqat.engine.architecture.format.ArchitectureDesignConstants;
import org.conqat.engine.architecture.format.EStereotype;
import org.conqat.engine.architecture.scope.ArchitectureDefinition;
import org.conqat.engine.architecture.scope.ComponentNode;
import org.conqat.engine.architecture.scope.DependencyPolicy;
import org.conqat.engine.commons.node.DisplayList;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.html_presentation.EHtmlPresentationFont;
import org.conqat.engine.html_presentation.color.AssessmentColorizer;
import org.conqat.engine.html_presentation.image.ITooltipDescriptor;
import org.conqat.engine.html_presentation.image.ImageDescriptorBase;
import org.conqat.engine.html_presentation.image.ImageDescriptorUtils;
import org.conqat.lib.commons.image.GraphicsUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Image descriptor for architectures.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 063C1F505C6542DF2FF5C925B4B17FFC
 */
public class ArchitectureImageDescriptor extends ImageDescriptorBase {

	/** The font size for the label of vertices. */
	private static final float FONT_SIZE = 11f;

	/** The size of the barb on the end of edges. */
	private static final int ARROW_BARB = 12;

	/** The number of pixels used to separate overlapping lines. */
	private static final int LINE_SEPARATION = 7;

	/** The angle for the barb on the end of edges. */
	private static final double ARROW_PHI = Math.toRadians(20);

	/** The architecture we will render. */
	private final ArchitectureDefinition arch;

	/** Decorator used for components. */
	private final IComponentDecorator decorator;

	/** The render mode used. */
	private final ERenderMode renderMode;

	/** Distance from the image border to it's contents. */
	private static final int BORDER_WIDTH = 10;

	/** Constructor. */
	public ArchitectureImageDescriptor(ArchitectureDefinition arch,
			IComponentDecorator decorator, ERenderMode renderMode) {
		super("architecture.png");
		this.arch = arch;
		this.decorator = decorator;
		this.renderMode = renderMode;
	}

	/** {@inheritDoc} */
	@Override
	public Dimension getPreferredSize() {
		Rectangle bounds = determineImageBounds();
		return new Dimension(bounds.width, bounds.height);
	}

	/** {@inheritDoc} */
	@Override
	public void draw(Graphics2D graphics, int width, int height) {
		graphics.setFont(EHtmlPresentationFont.SANS_CONDENSED.getFont());
		graphics.setPaint(Color.WHITE);
		graphics.fillRect(0, 0, width, height);
		graphics.setPaint(Color.BLACK);

		Rectangle imageBounds = determineImageBounds();
		graphics.setTransform(ImageDescriptorUtils.adjust(width, height,
				imageBounds));

		for (ComponentNode component : arch.getChildren()) {
			paintComponent(graphics, component);
		}
		paintEdges(graphics);
	}

	/** Paints the component to the given {@link Graphics2D} context. */
	private void paintComponent(Graphics2D graphics, ComponentNode component) {
		Point position = component.getAbsolutePosition();
		Dimension size = component.getDimension();
		graphics.translate(position.x, position.y);
		graphics.setClip(0, 0, size.width + 1, size.height + 1);
		paintComponentBox(graphics, component);
		graphics.translate(-position.x, -position.y);

		// reset the clip, so we don't interfere with later renderings
		graphics.setClip(null);

		if (component.hasChildren()) {
			for (ComponentNode child : component.getChildren()) {
				paintComponent(graphics, child);
			}
		}
	}

	/** Paints the vertex to the given {@link Graphics2D} context. */
	private void paintComponentBox(Graphics2D graphics, ComponentNode component) {
		int height = component.getDimension().height;
		int width = component.getDimension().width;

		// store current paint and stroke
		Paint oldPaint = graphics.getPaint();
		Stroke oldStroke = graphics.getStroke();

		// fill rectangle
		graphics.setPaint(decorator.obtainFillPaint(component));
		graphics.fillRoundRect(0, 0, width, height, CORNER_RADIUS,
				CORNER_RADIUS);

		// draw outline
		graphics.setPaint(decorator.obtainOutlinePaint(component));
		graphics.setStroke(decorator.obtainStroke(component));
		graphics.drawRoundRect(0, 0, width, height, CORNER_RADIUS,
				CORNER_RADIUS);

		// reset paint and stroke
		graphics.setPaint(oldPaint);
		graphics.setStroke(oldStroke);

		FontMetrics metrics = graphics.getFontMetrics();
		int vMargin = metrics.getAscent() + VERTICAL_LABEL_MARGIN;

		paintLabel(component, graphics, width, vMargin, metrics);

		// draw line below label
		graphics.drawLine(HORIZONTAL_LABEL_MARGIN, vMargin + 2, width
				- HORIZONTAL_LABEL_MARGIN, vMargin + 2);

		paintDecoration(component, graphics, width, vMargin, metrics);

	}

	/** Draw the label. */
	private void paintLabel(ComponentNode component, Graphics2D graphics,
			int width, int vMargin, FontMetrics metrics) {
		String label = getLabel(component);

		if (StringUtils.isEmpty(label)) {
			label = "\u00ABNO NAME\u00BB";
		}

		graphics.setFont(graphics.getFont().deriveFont(FONT_SIZE));
		graphics.drawString(stripName(label, metrics, width),
				HORIZONTAL_LABEL_MARGIN, vMargin);
	}

	/** Draw the decoration. */
	private void paintDecoration(ComponentNode component, Graphics2D graphics,
			int width, int vMargin, FontMetrics metrics) {
		AffineTransform oldTransform = graphics.getTransform();
		graphics.translate(HORIZONTAL_LABEL_MARGIN, vMargin + 4);
		graphics.setClip(0, 0, width - HORIZONTAL_LABEL_MARGIN * 2,
				metrics.getAscent() + 4);
		decorator.decorate(component, graphics);
		graphics.setTransform(oldTransform);
	}

	/** Returns the label for the component. */
	private String getLabel(ComponentNode component) {
		if (component.getStereotype() == EStereotype.PUBLIC) {
			return ArchitectureDesignConstants.PUBLIC_STEREOTYPE_LABEL + ' '
					+ component.getName();
		} else if (component.getStereotype() == EStereotype.COMPONENT_PUBLIC) {
			return ArchitectureDesignConstants.COMPONENT_PUBLIC_STEREOTYPE_LABEL
					+ ' ' + component.getName();
		}
		return component.getName();
	}

	/** Strip name if required. */
	private String stripName(String name, FontMetrics metrics, int width) {
		if (fitsDimensions(name, metrics, width)) {
			return name;
		}
		return abbrName(name, metrics, width);
	}

	/** Abbreviate the name until it fits the vertex. */
	private String abbrName(String name, FontMetrics metrics, int width) {
		// cannot abbreviate
		if (StringUtils.isEmpty(name)) {
			return name;
		}
		String abbrName = name + "...";
		if (fitsDimensions(abbrName, metrics, width)) {
			return abbrName;
		}
		return abbrName(name.substring(0, name.length() - 1), metrics, width);
	}

	/** Checks if a string fits the dimensions of this vertex. */
	private boolean fitsDimensions(String name, FontMetrics metrics, int width) {
		return metrics.stringWidth(name) < width - HORIZONTAL_LABEL_MARGIN;
	}

	/** Paints the edges to the given {@link Graphics2D} context. */
	private void paintEdges(Graphics2D graphics) {
		List<DependencyPolicy> edges = arch.getSortedPolicies();
		for (DependencyPolicy edge : edges) {
			paintEdge(graphics, edge);
		}
	}

	/** Paints the given edge. */
	private void paintEdge(Graphics2D graphics, DependencyPolicy edge) {
		if (!renderMode.includePolicy(edge)) {
			return;
		}
		graphics.setColor(AssessmentColorizer.determineColor(renderMode
				.determineColor(edge)));
		graphics.setStroke(new BasicStroke(1));

		Point startPoint = getChopboxAnchor(edge.getSource(), edge.getTarget());
		Point endPoint = getChopboxAnchor(edge.getTarget(), edge.getSource());

		// shorten the line a bit, so the arrow doesn't look ugly (==>=)
		double theta = Math.atan2(endPoint.y - startPoint.y, endPoint.x
				- startPoint.x);
		int x = (int) (endPoint.x + ARROW_BARB * 0.5
				* Math.cos(theta + Math.PI));
		int y = (int) (endPoint.y + ARROW_BARB * 0.5
				* Math.sin(theta + Math.PI));

		// two connections are possible at max, because no two connections
		// can have the same source and direction at the same time
		if (edge.getTarget().hasPolicyTo(edge.getSource())) {

			// add a bend to the line if there's a reverse connection
			double alpha = Math.PI * 0.5 + theta;

			// middle point between start and end point
			x = (endPoint.x - startPoint.x) / 2 + startPoint.x;
			y = (endPoint.y - startPoint.y) / 2 + startPoint.y;

			// add a point 7 px upright to the connection line
			x += LINE_SEPARATION * Math.cos(alpha);
			y += LINE_SEPARATION * Math.sin(alpha);

			graphics.drawPolyline(new int[] { startPoint.x, x, endPoint.x },
					new int[] { startPoint.y, y, endPoint.y }, 3);
			graphics.fill(getArrowHead(new Point(x, y), endPoint));
		} else {
			graphics.drawLine(startPoint.x, startPoint.y, x, y);
			graphics.fill(getArrowHead(startPoint, endPoint));
		}
	}

	/**
	 * The ChopboxAnchor's location is found by calculating the intersection of
	 * a line drawn from the center point of to a reference point on that box.
	 * Code borrowed from org.eclipse.draw2d.ChopboxAnchor.
	 */
	private Point getChopboxAnchor(ComponentNode baseNode,
			ComponentNode referenceNode) {
		Rectangle rectangle = baseNode.getAbsoluteBounds();
		rectangle.grow(1, 1);

		double refX = referenceNode.getAbsoluteBounds().getCenterX();
		double refY = referenceNode.getAbsoluteBounds().getCenterY();
		return GraphicsUtils.getChopboxAnchor(rectangle, new Point((int) refX,
				(int) refY));
	}

	/** Returns a path for the barb at the end of an edge. */
	private GeneralPath getArrowHead(Point p1, Point p2) {
		return GraphicsUtils.getArrowHead(p1, p2, ARROW_BARB, ARROW_PHI);
	}

	/** Determine the bounds of the image. */
	private Rectangle determineImageBounds() {
		Rectangle imageBounds = null;

		// it is sufficient to check the bounds of the top level components as
		// all subcomponents are clipped to these.
		for (ComponentNode component : arch.getChildren()) {
			Rectangle bounds = component.getAbsoluteBounds();
			if (imageBounds == null) {
				imageBounds = bounds;
			} else {
				imageBounds = imageBounds.union(bounds);
			}
		}

		if (imageBounds == null) {
			imageBounds = new Rectangle(0, 0, 10, 10);
		}

		imageBounds.grow(BORDER_WIDTH, BORDER_WIDTH);
		return imageBounds;
	}

	/** {@inheritDoc} */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ITooltipDescriptor<Object> getTooltipDescriptor(int width, int height) {
		return (ITooltipDescriptor) new TooltipDescriptor(width, height);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isVectorFormatSupported() {
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public Object getSummary() {
		return NodeUtils.getSummary(arch);
	}

	/** The tooltip descriptor for architectures. */
	private class TooltipDescriptor implements
			ITooltipDescriptor<ComponentNode> {

		/** The transform applied to match the bounds. */
		private final AffineTransform transform;

		/** Constructor. */
		public TooltipDescriptor(int width, int height) {
			transform = ImageDescriptorUtils.adjust(width, height,
					determineImageBounds());
		}

		/** {@inheritDoc} */
		@Override
		public DisplayList getDisplayList() {
			return NodeUtils.getDisplayList(arch);
		}

		/** {@inheritDoc} */
		@Override
		public ComponentNode getRoot() {
			return arch;
		}

		/** {@inheritDoc} */
		@Override
		public boolean hasChildren(ComponentNode node) {
			return node.hasChildren();
		}

		/** {@inheritDoc} */
		@Override
		public boolean isTooltipsForInnerNodes() {
			return true;
		}

		/** {@inheritDoc} */
		@Override
		public Rectangle2D obtainBounds(ComponentNode node) {
			Rectangle bounds = node.getAbsoluteBounds();
			Point2D min = new Point2D.Double(bounds.getMinX(), bounds.getMinY());
			Point2D max = new Point2D.Double(bounds.getMaxX(), bounds.getMaxY());
			Point2D minNew = new Point2D.Double();
			Point2D maxNew = new Point2D.Double();
			transform.transform(min, minNew);
			transform.transform(max, maxNew);
			return new Rectangle2D.Double(minNew.getX(), minNew.getY(),
					maxNew.getX() - minNew.getX(), maxNew.getY()
							- minNew.getY());
		}

		/** {@inheritDoc} */
		@Override
		public List<ComponentNode> obtainChildren(ComponentNode node) {
			return Arrays.asList(node.getChildren());
		}

		/** {@inheritDoc} */
		@Override
		public String obtainId(ComponentNode node) {
			return node.getId();
		}

		/** {@inheritDoc} */
		@Override
		public Object obtainValue(ComponentNode node, String key) {
			return node.getValue(key);
		}
	}
}