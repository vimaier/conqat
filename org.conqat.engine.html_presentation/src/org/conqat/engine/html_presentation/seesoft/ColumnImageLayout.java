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
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Column Layout for a rectangular image area. Subimages are wrapped, if they
 * are too long for a single column.
 * 
 * @author $Author: kanis $
 * @version $Rev: 43971 $
 * @ConQAT.Rating GREEN Hash: B0067C05E04F3E1EF7F8537854CFD02F
 */
public class ColumnImageLayout {

	/** Vertical padding between images. */
	private final int paddingVertical;

	/** Horizontal padding between images. */
	private final int paddingHorizontal;

	/**
	 * Set of points for the top left corner(s) for each image. images have
	 * multiple positions, if they are wrapped.
	 */
	private final Map<BufferedImage, Set<Point>> imagePositions = new HashMap<BufferedImage, Set<Point>>();

	/**
	 * The current layout position. This is the top-left corner for any image
	 * rendered next.
	 */
	private final Point currentPosition = new Point(0, 0);

	/** The size of the resulting image. */
	private final Dimension size;

	/** The width of the columns. */
	private final int columnWidth;

	/** Constructor. */
	public ColumnImageLayout(Dimension size, Dimension padding,
			int columnWidth) {
		this.size = size;
		paddingHorizontal = padding.width;
		paddingVertical = padding.height;
		this.columnWidth = columnWidth;
	}

	/**
	 * Add an image. It gets layouted at the next available column space. You
	 * need to call {@link #draw(Graphics2D)} when you added all the images.
	 * 
	 * @return The positions where the image will be drawn. There are multiple
	 *         positions if the image is too high for the output and is thus
	 *         wrapped.
	 */
	public Set<Point> addImage(BufferedImage image) {
		Set<Point> points = new HashSet<Point>();
		addImage(image, 0, points);
		imagePositions.put(image, points);
		return points;
	}

	/**
	 * Adds an image. You need to call {@link #draw(Graphics2D)} when you added
	 * all the images.
	 */
	private void addImage(BufferedImage image, int drawnHeight,
			Set<Point> points) {
		int remainingHeight = size.height - Math.max(0, currentPosition.y);

		points.add(new Point(currentPosition));
		if (remainingHeight < image.getHeight() - drawnHeight) {
			currentPosition.x += columnWidth + paddingHorizontal;
			drawnHeight += remainingHeight;
			currentPosition.y = -drawnHeight;
			addImage(image, drawnHeight, points);
		} else {
			currentPosition.y += image.getHeight() + paddingVertical;
		}
	}

	/** Draw all added images to the given {@link Graphics2D} context. */
	public void draw(Graphics2D graphics) {
		for (Map.Entry<BufferedImage, Set<Point>> entry : imagePositions
				.entrySet()) {
			BufferedImage image = entry.getKey();
			for (Point point : entry.getValue()) {
				graphics.drawImage(image, point.x, point.y, null);
			}
		}
	}

}
