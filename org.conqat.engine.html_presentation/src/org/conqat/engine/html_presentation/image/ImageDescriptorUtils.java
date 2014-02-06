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
package org.conqat.engine.html_presentation.image;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.core.IConQATProcessorInfo;
import org.conqat.engine.html_presentation.EHtmlPresentationFont;
import org.conqat.lib.commons.filesystem.CanonicalFile;

/**
 * Utility methods for {@link IImageDescriptor}s.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 51E8094D4F25FF059BBEBA35F7CB8674
 */
public class ImageDescriptorUtils {

	/**
	 * Write image to a temporary file. If the the image descriptor supports
	 * vector formats, the results file format is PDF otherwise PNG.
	 * 
	 * @param processorInfo
	 *            the processor info to create temporary file.
	 * @param descriptor
	 *            the image descriptor
	 * @param width
	 *            width of result image (may be {@link EImageFormat#FLEXIBLE})
	 * @param height
	 *            height of result image (may be {@link EImageFormat#FLEXIBLE})
	 * @param vectorFormat
	 *            if the descriptor supports vector formats (see
	 *            {@link IImageDescriptor#isVectorFormatSupported()}, this
	 *            output format is used.
	 * @param rasterFormat
	 *            if the descriptor does not support vector formats, this format
	 *            is used.
	 * 
	 * @return the file the image was written to.
	 * 
	 * @throws ConQATException
	 *             if image creation or storage fails.
	 */
	public static CanonicalFile writeImage(IConQATProcessorInfo processorInfo,
			IImageDescriptor descriptor, int width, int height,
			EImageFormat vectorFormat, EImageFormat rasterFormat)
			throws ConQATException {

		if (descriptor.isVectorFormatSupported()) {
			return vectorFormat.writeImage(processorInfo, descriptor, width,
					height);
		}

		return rasterFormat
				.writeImage(processorInfo, descriptor, width, height);
	}

	/**
	 * Create a transform that ensures that that the specified bounds match the
	 * defined width and height.
	 */
	public static AffineTransform adjust(int width, int height, Rectangle bounds) {
		double xShift = -bounds.x;
		double yShift = -bounds.y;
		double xScale = (double) width / bounds.width;
		double yScale = (double) height / bounds.height;

		AffineTransform transform = new AffineTransform();
		// order of transforms is important
		transform.scale(xScale, yScale);
		transform.translate(xShift, yShift);
		return transform;
	}

	/**
	 * Initialize graphics object.
	 */
	public static void initGraphics(Graphics2D graphics) {
		EHtmlPresentationFont.SANS_CONDENSED.setFont(graphics);
	}
}