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
package org.conqat.engine.html_presentation.base;

import static org.conqat.engine.html_presentation.CSSMananger.DEFAULT_FONT;
import static org.conqat.engine.html_presentation.CSSMananger.DEFAULT_FONT_BOLD;
import static org.conqat.engine.html_presentation.CSSMananger.WHITE_BACKGROUND;
import static org.conqat.lib.commons.html.ECSSProperty.PADDING_LEFT;
import static org.conqat.lib.commons.html.ECSSProperty.PADDING_TOP;
import static org.conqat.lib.commons.html.EHTMLAttribute.ALIGN;
import static org.conqat.lib.commons.html.EHTMLAttribute.BORDER;
import static org.conqat.lib.commons.html.EHTMLAttribute.CELLPADDING;
import static org.conqat.lib.commons.html.EHTMLAttribute.CELLSPACING;
import static org.conqat.lib.commons.html.EHTMLAttribute.CLASS;
import static org.conqat.lib.commons.html.EHTMLAttribute.HREF;
import static org.conqat.lib.commons.html.EHTMLAttribute.SRC;
import static org.conqat.lib.commons.html.EHTMLAttribute.STYLE;
import static org.conqat.lib.commons.html.EHTMLAttribute.TARGET;
import static org.conqat.lib.commons.html.EHTMLAttribute.VALIGN;
import static org.conqat.lib.commons.html.EHTMLAttribute.WIDTH;
import static org.conqat.lib.commons.html.EHTMLElement.A;
import static org.conqat.lib.commons.html.EHTMLElement.BODY;
import static org.conqat.lib.commons.html.EHTMLElement.IMG;
import static org.conqat.lib.commons.html.EHTMLElement.SPAN;
import static org.conqat.lib.commons.html.EHTMLElement.TABLE;
import static org.conqat.lib.commons.html.EHTMLElement.TD;
import static org.conqat.lib.commons.html.EHTMLElement.TR;

import java.io.File;

import org.conqat.engine.html_presentation.CSSMananger;
import org.conqat.engine.html_presentation.util.WriterBase;
import org.conqat.lib.commons.color.ECCSMColor;
import org.conqat.lib.commons.date.DateUtils;
import org.conqat.lib.commons.html.CSSDeclarationBlock;
import org.conqat.lib.commons.html.ECSSProperty;

/**
 * This class generates the output page header ({@value #PAGE_NAME}).
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: A1FA71BD942573964BA73D6E43CAE196
 */
public class HeaderWriter extends WriterBase {

	/** Name of the page: {@value} */
	public static final String PAGE_NAME = "header.html";

	/** CSS class for header. */
	private static final CSSDeclarationBlock DESCRIPTION = new CSSDeclarationBlock(
			DEFAULT_FONT_BOLD, PADDING_LEFT, "22px", PADDING_TOP, "2px")
			.setMargin("0px");

	/** Name of the analyzed project. */
	private final String projectName;

	/**
	 * Create new header writer.
	 * 
	 * @param outputDirectory
	 *            output directory.
	 * @param projectName
	 *            name of the analyzed project.
	 */
	public HeaderWriter(File outputDirectory, String projectName) {
		super(new File(outputDirectory, PAGE_NAME));
		this.projectName = projectName;
	}

	/** {@inheritDoc} */
	@Override
	protected void addBody() {
		writer.openElement(BODY, CLASS, new CSSDeclarationBlock(
				CSSMananger.WHITE_BACKGROUND, ECSSProperty.COLOR,
				ECCSMColor.BLUE.getHTMLColorCode()));

		writer.openElement(TABLE, BORDER, "0", STYLE, new CSSDeclarationBlock()
				.setMargin("0px"), CELLSPACING, "0", CELLPADDING, "0", WIDTH,
				"100%");
		writer.openElement(TR);

		writer.openElement(TD, CLASS, WHITE_BACKGROUND, ALIGN, "left", VALIGN,
				"CENTER");
		writer.openElement(A, HREF, "http://www.conqat.org/", TARGET, "_blank");
		writer.addClosedElement(IMG, SRC, "images/conqat_logo.gif");
		writer.closeElement(A);
		writer.closeElement(TD);

		writer.openElement(TD, CLASS, DESCRIPTION, VALIGN, "CENTER");
		writer.addText(projectName + " @ ");
		writer.addClosedTextElement(SPAN, DateUtils.getNow().toString(), CLASS,
				DEFAULT_FONT);
		writer.closeElement(TD);

		writer.closeElement(TR);
		writer.closeElement(TABLE);

		writer.closeElement(BODY);

	}

	/** {@inheritDoc} */
	@Override
	protected String getTitle() {
		return "ConQAT Header";
	}

}