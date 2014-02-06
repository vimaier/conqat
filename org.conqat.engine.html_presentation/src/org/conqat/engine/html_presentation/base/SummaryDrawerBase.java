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
package org.conqat.engine.html_presentation.base;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.conqat.engine.html_presentation.image.ImageDescriptorUtils;
import org.conqat.lib.commons.color.ECCSMColor;
import org.conqat.lib.commons.filesystem.FileSystemUtils;

/**
 * Base class for summary drawers.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: CCA5942D714C2AD1CBCDC63CB2DCABB4
 */
public abstract class SummaryDrawerBase<T> {

	/** Width of the image. */
	protected final int width;

	/** Height of the image. */
	protected final int height;

	/** Constructor. */
	protected SummaryDrawerBase(int width, int height) {
		this.height = height;
		this.width = width;
	}

	/** Generate PNG image for summary. */
	public void generatePNGImage(T summary, File file) throws IOException {
		BufferedImage image = createImage(summary);
		FileSystemUtils.ensureParentDirectoryExists(file);
		ImageIO.write(image, "png", file);
	}

	/** Create image. */
	private BufferedImage createImage(T summary) {
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);

		Graphics2D graphics = image.createGraphics();
		ImageDescriptorUtils.initGraphics(graphics);
		if (summary == null) {
			drawUndefined(graphics);
		} else {
			drawSummary(summary, graphics);
		}
		return image;
	}

	/** Draw a gray rectangle on white background.  */
	protected void drawUndefined(Graphics2D graphics) {
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, width, height);
		graphics.setColor(Color.gray);
		graphics.drawRect(0, 0, width - 1, height - 1);
	}

	/** Draw a caption. */
	protected void drawCaption(Graphics2D graphics, String caption) {
		setCaptionFont(graphics, caption);

		float x = (width - graphics.getFontMetrics().stringWidth(caption)) / 2f;
		float y = height
				- ((height - graphics.getFontMetrics().getHeight()) / 2f)
				- graphics.getFontMetrics().getDescent();
		graphics.setColor(ECCSMColor.DARK_GRAY.getColor());
		graphics.drawString(caption, x, y);
	}

	/**
	 * This method reduces the font for the caption until the caption can be fit
	 * in the assessment image.
	 */
	private void setCaptionFont(Graphics2D graphics, String caption) {
		// in the y-dimension we only care about the ascent. As our captions do
		// not have a descent we do not need to take into account the full
		// height
		while (graphics.getFontMetrics().stringWidth(caption) >= width
				|| graphics.getFontMetrics().getAscent() >= height) {
			Font currentFont = graphics.getFont();
			Font newFont = currentFont
					.deriveFont((float) currentFont.getSize() - 1);
			graphics.setFont(newFont);
		}
	}

	/** Template method to draw the summary. */
	protected abstract void drawSummary(T summary, Graphics2D graphics);
}
