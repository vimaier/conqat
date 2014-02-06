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

import java.awt.Rectangle;

/**
 * This class contains utility code for describing block spec parameters and
 * outputs (a rectangle with an apex). This contains the coordinate calculations
 * and not the rendering code, as this is easier to share between ConQATDoc
 * (AWT) and cq.edit (draw2d).
 * 
 * @author $Author: juergens $
 * @version $Rev: 35194 $
 * @ConQAT.Rating GREEN Hash: FEEE789EF6DF601E426A0D12F8258DA0
 */
public class BlockSpecIOShapeUtils {

	/**
	 * This cBlockSpecIOShapeUtilses at what height the break for the apex is
	 * made. It must be between 0 and 1.
	 */
	private static final float VERTICAL_PARTITION_FACTOR = 0.7f;

	/**
	 * Returns the point x-coordinates unscaled, i.e. ready to fit in a one unit
	 * square. The points are chosen for the bottom apex.
	 */
	private static final double[] UNSCALED_POINTS_X = { 0, 1, 1, .5, 0, };
	/**
	 * Returns the point y-coordinates unscaled, i.e. ready to fit in a one unit
	 * square. The points are chosen for the bottom apex.
	 */
	private static final double[] UNSCALED_POINTS_Y = { 0, 0,
			VERTICAL_PARTITION_FACTOR, 1, VERTICAL_PARTITION_FACTOR };

	/**
	 * Returns the point list used for drawing the polygon.
	 * 
	 * @param apexBottom
	 *            if this is true, the apex will be at the bottom, else at the
	 *            top.
	 */
	public static int[] getPoints(Rectangle bounds, boolean apexBottom) {
		int[] points = new int[2 * UNSCALED_POINTS_X.length];

		int pointsIndex = 0;
		for (int i = 0; i < UNSCALED_POINTS_X.length; ++i) {
			points[pointsIndex++] = (int) Math.round(UNSCALED_POINTS_X[i]
					* bounds.width + bounds.x);
			double y = UNSCALED_POINTS_Y[i];
			if (!apexBottom) {
				y = 1 - y;
			}
			points[pointsIndex++] = (int) Math.round(y * bounds.height
					+ bounds.y);
		}

		return points;
	}

	/**
	 * Returns the bounds adjusted for port insets.
	 * 
	 * @param apexBottom
	 *            if this is true, the apex will be at the bottom, else at the
	 *            top.
	 */
	public static Rectangle getInsetBounds(Rectangle bounds, boolean apexBottom) {
		int topCrop = 1;
		int bottomCrop = 1;
		if (apexBottom) {
			bottomCrop = DesignConstants.PORT_INSET;
		} else {
			topCrop = DesignConstants.PORT_INSET;
		}

		return new Rectangle(bounds.x, bounds.y + topCrop, bounds.width,
				bounds.height - topCrop - bottomCrop);
	}

	/**
	 * Returns the bounds used for the gradient. This contains only the
	 * rectangular part of the figure (without the apex) as we want to limit the
	 * gradient to the rectangular part when drawing.
	 * 
	 * @param apexBottom
	 *            if this is true, the apex will be at the bottom, else at the
	 *            top.
	 */
	public static Rectangle getGradientBounds(Rectangle bounds,
			boolean apexBottom) {
		bounds = getInsetBounds(bounds, apexBottom);

		if (!apexBottom) {
			bounds.y += Math.round(bounds.height
					* (1 - VERTICAL_PARTITION_FACTOR));
		}
		bounds.height = Math.round(bounds.height * VERTICAL_PARTITION_FACTOR);

		return bounds;
	}
}