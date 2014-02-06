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

import static org.conqat.lib.commons.html.EHTMLAttribute.CLASS;
import static org.conqat.lib.commons.html.EHTMLAttribute.SRC;
import static org.conqat.lib.commons.html.EHTMLElement.BR;
import static org.conqat.lib.commons.html.EHTMLElement.IMG;
import static org.conqat.lib.commons.html.EHTMLElement.TABLE;
import static org.conqat.lib.commons.html.EHTMLElement.TD;
import static org.conqat.lib.commons.html.EHTMLElement.TR;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import javax.imageio.ImageIO;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.html_presentation.treemap.TreeMapCreator;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.html.HTMLWriter;
import org.conqat.lib.commons.treemap.IDrawingPattern;

/**
 * Utility code for dealing with patterns.
 * 
 * @author hummelb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 6E521B00B38DF9B17AB3EAAE9D90C622
 */
public class PatternUtils {

	/**
	 * Creates HTML code for a pattern legend (i.e. a mapping from some
	 * description objects to pattern).
	 * 
	 * @param legend
	 *            the legend being output.
	 * @param writer
	 *            the writer to add the HTML code to.
	 * @param fileBase
	 *            the base string used for creating file names. THe images are
	 *            written to files starting with this string.
	 * @param hrefBase
	 *            the base string used for creating references to the created
	 *            images. This must be consistent with the fileBase.
	 * @throws ConQATException
	 *             if there are problems with writing the images to disk.
	 */
	public static void makeHtmlLegend(
			Map<Comparable<Comparable<?>>, IDrawingPattern> legend,
			HTMLWriter writer, String fileBase, String hrefBase)
			throws ConQATException {
		writer.addClosedElement(BR);
		writer.addClosedElement(BR);
		writer.openElement(TABLE, CLASS, TreeMapCreator.MAP_LEGEND);

		ArrayList<Comparable<Comparable<?>>> keys = new ArrayList<Comparable<Comparable<?>>>(
				legend.keySet());
		Collections.sort(keys);
		int patternCount = 0;
		for (Comparable<?> key : keys) {
			String extension = patternCount++ + ".png";
			renderDrawingPattern(legend.get(key),
					new File(fileBase + extension));
			writer.openElement(TR);
			writer.openElement(TD);
			writer.addClosedElement(IMG, SRC, hrefBase + extension);
			writer.closeElement(TD);
			writer.addClosedTextElement(TD, key.toString());
			writer.closeElement(TR);
		}
		writer.closeElement(TABLE);
	}

	/** Renders a drawing pattern with a frame. */
	private static void renderDrawingPattern(IDrawingPattern pattern,
			File pngFile) throws ConQATException {
		try {
			FileSystemUtils.ensureParentDirectoryExists(pngFile);
			ImageIO.write(createPatternImage(pattern, 30, 20), "png", pngFile);
		} catch (IOException e) {
			throw new ConQATException("Could not write image.", e);
		}
	}

	/** Create image with pattern. */
	public static BufferedImage createPatternImage(IDrawingPattern pattern,
			int width, int height) {
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < image.getWidth(); ++x) {
			for (int y = 0; y < image.getHeight(); ++y) {
				int color = 0xffffff;
				if (pattern.isForeground(x, y)) {
					color = 0;
				}
				image.setRGB(x, y, color);
			}
		}
		return image;
	}
}