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

import static org.conqat.lib.commons.color.ECCSMColor.DARK_GRAY;
import static org.conqat.lib.commons.color.ECCSMColor.LIGHT_GRAY;
import static org.conqat.lib.commons.html.ECSSProperty.BACKGROUND_COLOR;
import static org.conqat.lib.commons.html.ECSSProperty.COLOR;
import static org.conqat.lib.commons.html.ECSSProperty.CURSOR;
import static org.conqat.lib.commons.html.ECSSProperty.DISPLAY;
import static org.conqat.lib.commons.html.ECSSProperty.FILTER;
import static org.conqat.lib.commons.html.ECSSProperty.FONT_FAMILY;
import static org.conqat.lib.commons.html.ECSSProperty.FONT_SIZE;
import static org.conqat.lib.commons.html.ECSSProperty.FONT_WEIGHT;
import static org.conqat.lib.commons.html.ECSSProperty.MARGIN_RIGHT;
import static org.conqat.lib.commons.html.ECSSProperty.OPACITY;
import static org.conqat.lib.commons.html.ECSSProperty.PADDING_LEFT;
import static org.conqat.lib.commons.html.ECSSProperty.PADDING_RIGHT;
import static org.conqat.lib.commons.html.ECSSProperty.TEXT_ALIGN;
import static org.conqat.lib.commons.html.ECSSProperty.TEXT_DECORATION;
import static org.conqat.lib.commons.html.ECSSProperty.VERTICAL_ALIGN;
import static org.conqat.lib.commons.html.ECSSProperty.WHITE_SPACE;
import static org.conqat.lib.commons.html.ECSSProperty.WIDTH;
import static org.conqat.lib.commons.html.EHTMLElement.A;
import static org.conqat.lib.commons.html.EHTMLElement.IMG;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.color.ECCSMColor;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.html.CSSDeclarationBlock;
import org.conqat.lib.commons.html.CSSManagerBase;
import org.conqat.lib.commons.html.ECSSPseudoClass;

/**
 * The CSS manager used for ConQATDoc. This class defines a number of basic
 * classes that should be reused in other places. Furthermore it sets the
 * default styles for some HTML elments.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating YELLOW Hash: 4201B3C2891D612234548DB0095E40F5
 */
public class CSSMananger extends CSSManagerBase {

	/** Sans-serif font. */
	public static final CSSDeclarationBlock SAN_SERIF_FONT = new CSSDeclarationBlock(
			FONT_FAMILY, "verdana, helvetica, sans-serif");

	/** Default font. */
	public static final CSSDeclarationBlock DEFAULT_FONT = new CSSDeclarationBlock(
			SAN_SERIF_FONT, FONT_SIZE, "13px");

	/** Small font. */
	public static final CSSDeclarationBlock SMALL_FONT = new CSSDeclarationBlock(
			SAN_SERIF_FONT, FONT_SIZE, "10px");

	/** Bold font. */
	public static final CSSDeclarationBlock DEFAULT_FONT_BOLD = new CSSDeclarationBlock(
			DEFAULT_FONT, FONT_WEIGHT, "bold");

	/** Syntax highlighting style for comments */
	public static final CSSDeclarationBlock COMMENT = new CSSDeclarationBlock(
			SMALL_FONT, COLOR, "green");

	/** Syntax highlighting style for keywords */
	public static final CSSDeclarationBlock KEYWORD = new CSSDeclarationBlock(
			SMALL_FONT, COLOR, "brown", FONT_WEIGHT, "bold");

	/** Syntax highlighting style for literals */
	public static final CSSDeclarationBlock LITERAL = new CSSDeclarationBlock(
			SMALL_FONT, COLOR, "blue");

	/** Syntax highlighting style for changed gaps */
	public static final CSSDeclarationBlock GAP_CHANGED = new CSSDeclarationBlock(
			BACKGROUND_COLOR, "yellow");

	/** Syntax highlighting style for removed gaps */
	public static final CSSDeclarationBlock GAP_REMOVED = new CSSDeclarationBlock(
			BACKGROUND_COLOR, "orange");

	/** Syntax highlighting style for added gaps */
	public static final CSSDeclarationBlock GAP_ADDED = new CSSDeclarationBlock(
			BACKGROUND_COLOR, "lightgreen");

	/** Dark gray border. */
	public static final CSSDeclarationBlock DARK_GRAY_BORDER = new CSSDeclarationBlock()
			.setBorder("1px", "solid", DARK_GRAY.getHTMLColorCode());

	/** Light blue border plus margin. */
	public static final CSSDeclarationBlock IMAGE_STYLE = new CSSDeclarationBlock()
			.setBorderStyle("none").setMargin("8px");

	/** Base class for table cells. */
	private static final CSSDeclarationBlock TABLE_CELL_BASE = new CSSDeclarationBlock(
			SMALL_FONT, TEXT_ALIGN, "left").setMargin("0px").setPadding("2px");

	/** Default table header cell. */
	public static final CSSDeclarationBlock TABLE_HEADER_CELL = new CSSDeclarationBlock(
			TABLE_CELL_BASE, COLOR, "white", BACKGROUND_COLOR,
			DARK_GRAY.getHTMLColorCode(), VERTICAL_ALIGN, "text-bottom",
			FONT_WEIGHT, "bold", WHITE_SPACE, "nowrap");

	/** Default table cell. */
	public static final CSSDeclarationBlock TABLE_CELL = new CSSDeclarationBlock(
			TABLE_CELL_BASE, BACKGROUND_COLOR, LIGHT_GRAY.getHTMLColorCode(),
			VERTICAL_ALIGN, "middle", WHITE_SPACE, "nowrap", WIDTH, "auto");

	/** Wide table cell. */
	public static final CSSDeclarationBlock WIDE_TABLE_CELL = new CSSDeclarationBlock(
			TABLE_CELL_BASE, BACKGROUND_COLOR, LIGHT_GRAY.getHTMLColorCode(),
			VERTICAL_ALIGN, "middle", WHITE_SPACE, "nowrap", WIDTH, "100%");
	
	/** Additional styling for a table cell that contains an attribute. */
	public static final CSSDeclarationBlock ATTRIBUTE_TABLE_CELL = new CSSDeclarationBlock(
			TABLE_CELL, PADDING_LEFT, "13px", PADDING_RIGHT, "25px");

	/** Link in table. */
	public static CSSDeclarationBlock TABLE_LINK = new CSSDeclarationBlock(
			SMALL_FONT, FONT_WEIGHT, "normal", COLOR, "black", TEXT_DECORATION,
			"none");

	/** Link in table head. */
	public static CSSDeclarationBlock TABLE_HEADER_LINK = new CSSDeclarationBlock(
			SMALL_FONT, FONT_WEIGHT, "bold", COLOR, "white", TEXT_DECORATION,
			"none");

	/** Icon in table. */
	public static CSSDeclarationBlock TABLE_ICON = new CSSDeclarationBlock(
			DISPLAY, "inline", VERTICAL_ALIGN, "middle", MARGIN_RIGHT, "6px");

	/** Style for DIVs used in tool-tips. */
	public static final CSSDeclarationBlock TOOL_TIP_DIV = new CSSDeclarationBlock(
			DARK_GRAY_BORDER, COLOR, "black", DISPLAY, "none",
			BACKGROUND_COLOR, LIGHT_GRAY.getHTMLColorCode(), OPACITY, "0.8",
			FILTER, "alpha(opacity = 80)");

	/** Style for tool-tip captions. */
	public static final CSSDeclarationBlock TOOL_TIP_CAPTION = new CSSDeclarationBlock(
			DARK_GRAY_BORDER, COLOR, "white", BACKGROUND_COLOR,
			DARK_GRAY.getHTMLColorCode()).inheritFrom(SMALL_FONT);

	/** Style for tool-tip cells. */
	public static final CSSDeclarationBlock TOOL_TIP_CELL = new CSSDeclarationBlock(
			SMALL_FONT, COLOR, "black", BACKGROUND_COLOR,
			LIGHT_GRAY.getHTMLColorCode());

	/** Blue background. */
	public static final CSSDeclarationBlock BLUE_BACKGROUND = new CSSDeclarationBlock(
			BACKGROUND_COLOR, ECCSMColor.BLUE.getHTMLColorCode(), COLOR,
			"white");

	/** Light blue background. */
	public static final CSSDeclarationBlock LIGHT_BLUE_BACKGROUND = new CSSDeclarationBlock(
			BACKGROUND_COLOR, ECCSMColor.LIGHT_BLUE.getHTMLColorCode());

	/** White background. */
	public static final CSSDeclarationBlock WHITE_BACKGROUND = new CSSDeclarationBlock(
			BACKGROUND_COLOR, "white");

	/** Zero padding, zero margin. */
	public static CSSDeclarationBlock DEFAULT_CONTAINER = new CSSDeclarationBlock(
			DEFAULT_FONT).setMargin("0px").setPadding("0px");

	/** CSS for inlined image. */
	public static CSSDeclarationBlock INLINED_IMAGE = new CSSDeclarationBlock(
			DISPLAY, "inline", VERTICAL_ALIGN, "text-bottom");

	/** CSS for link cursor. */
	public static final CSSDeclarationBlock LINK_CURSOR = new CSSDeclarationBlock(
			CURSOR, "pointer");

	/** Create and register all CSS blocks used. */
	private CSSMananger() {
		addDefaultDeclaration(IMG,
				new CSSDeclarationBlock().setBorderWidth("0px"));

		addDefaultDeclaration(A, new CSSDeclarationBlock(DEFAULT_FONT,
				FONT_SIZE, "12px", FONT_WEIGHT, "600", COLOR, "black",
				TEXT_DECORATION, "none"));

		addDefaultDeclaration(A, ECSSPseudoClass.HOVER,
				new CSSDeclarationBlock(TEXT_DECORATION, "underline"));

	}

	/** The singleton instance. */
	private static CSSMananger instance;

	/** Get singleton instance. */
	public static CSSMananger getInstance() {
		if (instance == null) {
			instance = new CSSMananger();
		}
		return instance;
	}

	/**
	 * Write style file to disk.
	 * 
	 * @throws ConQATException
	 *             if style file could not be written.
	 */
	public void write(File outputDirectory) throws ConQATException {
		File file = new File(outputDirectory, "css/style.css");
		try {
			FileSystemUtils.ensureParentDirectoryExists(file);
			PrintStream stream = new PrintStream(file);
			writeOut(stream);
			stream.close();
		} catch (IOException ex) {
			throw new ConQATException("Could not write style file: " + file);
		}

	}

	/** Write out declarations. This exposes the super class method. */
	@Override
	public void writeOutDeclarations(PrintStream stream) {
		super.writeOutDeclarations(stream);
	}

	/**
	 * Write out default declarations for element (i.e. without specific class).
	 * This exposes the super class method.
	 */
	@Override
	public void writeOutDefaultDeclarations(PrintStream stream) {
		super.writeOutDefaultDeclarations(stream);
	}
}