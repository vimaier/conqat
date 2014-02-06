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
package org.conqat.engine.html_presentation;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import org.apache.batik.svggen.SVGGraphics2D;
import org.conqat.lib.commons.filesystem.FileSystemUtils;

/**
 * Enumeration of fonts provided by the HTML presentation.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 6D7A8096CC77B96FD346C1A0A9B96AF8
 */
public enum EHtmlPresentationFont {

	/** The default sans serif condensed font. */
	SANS_CONDENSED("fonts/dejavu/DejaVuSansCondensed.ttf.gz");

	/** Default font size. */
	public static final int DEFAULT_SIZE = 12;

	/**
	 * This is an artificial graphics object that can be used to obtain
	 * {@link FontMetrics}. I took some effort to understand if this an valid
	 * approach. Theoretically, different graphics objects, e.g. PNG vs SVG,
	 * could use different font metrics as they may render fonts slightly
	 * different. In practice, this apparently is not the case. For example, the
	 * {@link SVGGraphics2D} object uses exactly the same artificial graphics
	 * Object to determine the font metrics as we do here.
	 */
	private static Graphics2D FONT_GRAPHICS = new BufferedImage(1, 1,
			BufferedImage.TYPE_INT_ARGB).createGraphics();

	/** The actual font used. */
	private final Font font;

	/** Constructor. */
	private EHtmlPresentationFont(String path) {
		try {
			InputStream in = FileSystemUtils.autoDecompressStream(BundleContext
					.getInstance().getResourceManager().getResourceAsStream(
							path));
			font = Font.createFont(Font.TRUETYPE_FONT, in).deriveFont(
					(float) DEFAULT_SIZE);
		} catch (IOException e) {
			throw new AssertionError("Could not load font: " + e.getMessage());
		} catch (FontFormatException e) {
			throw new AssertionError("Could not load font: " + e.getMessage());
		}
	}

	/** Returns the font. */
	public Font getFont() {
		return font;
	}

	/** Set font on graphics object. */
	public void setFont(Graphics2D graphics) {
		graphics.setFont(font);
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
	}

	/** Get the font metrics for this font. */
	public FontMetrics getFontMetrics() {
		return FONT_GRAPHICS.getFontMetrics(font);
	}
}