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
package org.conqat.engine.html_presentation.seesoft;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.node.DisplayList;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.IConQATLogger;
import org.conqat.engine.html_presentation.image.ITooltipDescriptor;
import org.conqat.engine.html_presentation.image.ImageDescriptorBase;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.lib.commons.color.IColorProvider;

/**
 * Image descriptor for SeeSoft visualizations of code.
 * 
 * @author $Author: kanis $
 * @version $Rev: 43971 $
 * @ConQAT.Rating GREEN Hash: 031A2FFD53FEDDCE2B007C4EA35D0698
 */
public class SeeSoftImageDescriptor extends ImageDescriptorBase {

	/** Parameters that determine appearance */
	private final SeeSoftParameterInfo paramInfo;

	/** Code from which SeeSoft view gets created. */
	private final List<ITokenElement> elements;

	/** Compositor to use for combining the tiles. */
	private ColumnImageLayout compositor;

	/** Renderer to use for the tokenElements. */
	private final SeeSoftElementRenderer renderer;

	/**
	 * These 'tiles' are the (possibly partial) graphical representation for
	 * each token element. A token element will be split into multiple tiles, if
	 * the {@link #compositor} doesn't have sufficient contiguous free space for the
	 * whole rendered element.
	 */
	private final List<SeeSoftTile> tiles = new ArrayList<SeeSoftTile>();

	/** The display list for the values to show in the image map. */
	private final DisplayList displayList;

	/** Constructor */
	protected SeeSoftImageDescriptor(List<ITokenElement> elements,
			IConQATLogger logger, SeeSoftParameterInfo paramInfo,
			DisplayList displayList,
			IColorProvider<Finding> findingsColorProvider) {
		super("tree_map.gif");

		this.paramInfo = paramInfo;
		this.elements = elements;
		this.displayList = displayList;

		renderer = new SeeSoftElementRenderer(findingsColorProvider, logger,
				paramInfo);
	}

	/** {@inheritDoc} */
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(paramInfo.getWidth(), paramInfo.getHeight());
	}

	/** {@inheritDoc} */
	@Override
	public void draw(Graphics2D graphics, int width, int height)
			throws ConQATException {
		drawBackground(graphics, width, height);
		drawElements(graphics, width, height);
	}

	/** Fills the background of the graphics. */
	private void drawBackground(Graphics2D graphics, int width, int height) {
		graphics.setColor(paramInfo.getBackgroundColor());
		graphics.fillRect(0, 0, width, height);
	}

	/** Renders the tokenElements into the graphics context. */
	private void drawElements(Graphics2D graphics, int width, int height)
			throws ConQATException {
		compositor = new ColumnImageLayout(new Dimension(width, height),
				paramInfo.getPadding(), paramInfo.getColumnWidth()
						/ paramInfo.getCompressionFactor());

		for (ITokenElement element : elements) {
			Dimension size = renderer.getPreferredSize(element);
			if (size.width <= 0 || size.height <= 0) {
				continue;
			}

			BufferedImage bufferImage = new BufferedImage(size.width,
					size.height, BufferedImage.TYPE_INT_RGB);
			renderer.draw(bufferImage.createGraphics(), element);
			Set<Point> imagePoints = compositor.addImage(bufferImage);
			for (Point point : imagePoints) {
				tiles.add(createTile(element, point, size));
			}
		}

		compositor.draw(graphics);
	}

	/** Creates a tile for the given element. */
	private SeeSoftTile createTile(ITextElement element, Point point,
			Dimension size) {
		Rectangle bounds = new Rectangle(point, size);

		if (bounds.y < 0) {
			bounds.height += bounds.y;
			bounds.y = 0;
		}

		if (bounds.y + bounds.height > paramInfo.getHeight()) {
			bounds.height = paramInfo.getHeight() - bounds.y;
		}

		if (bounds.x + bounds.width > paramInfo.getWidth()) {
			bounds.width = paramInfo.getWidth() - bounds.x;
		}

		return new SeeSoftTile(element, bounds);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isVectorFormatSupported() {
		return false;
	}

	/** {@inheritDoc} */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public ITooltipDescriptor<Object> getTooltipDescriptor(int width, int height) {
		final SeeSoftTile dummyRootTile = new SeeSoftTile(null, new Rectangle(
				paramInfo.getWidth(), paramInfo.getHeight()));
		return (ITooltipDescriptor) new ITooltipDescriptor<SeeSoftTile>() {

			@Override
			public boolean hasChildren(SeeSoftTile tile) {
				return tile == dummyRootTile;
			}

			@Override
			public List<SeeSoftTile> obtainChildren(SeeSoftTile tile) {
				if (tile == dummyRootTile) {
					return tiles;
				}
				return null;
			}

			@Override
			public Rectangle2D obtainBounds(SeeSoftTile tile) {
				return tile.getBounds();
			}

			@Override
			public String obtainId(SeeSoftTile tile) {
				if (tile.getNode() != null) {
					return tile.getNode().getId();
				}
				return null;
			}

			@Override
			public Object obtainValue(SeeSoftTile tile, String key) {
				return tile.getNode().getValue(key);
			}

			@Override
			public SeeSoftTile getRoot() {
				return dummyRootTile;
			}

			@Override
			public DisplayList getDisplayList() {
				return displayList;
			}

			@Override
			public boolean isTooltipsForInnerNodes() {
				return false;
			}
		};
	}

}
