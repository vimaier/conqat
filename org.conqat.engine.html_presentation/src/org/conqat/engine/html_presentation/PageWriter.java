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

import static org.conqat.engine.html_presentation.CSSMananger.DEFAULT_FONT;
import static org.conqat.engine.html_presentation.CSSMananger.LIGHT_BLUE_BACKGROUND;
import static org.conqat.lib.commons.html.ECSSProperty.BACKGROUND_COLOR;
import static org.conqat.lib.commons.html.ECSSProperty.FONT_SIZE;
import static org.conqat.lib.commons.html.ECSSProperty.FONT_WEIGHT;
import static org.conqat.lib.commons.html.ECSSProperty.TEXT_ALIGN;
import static org.conqat.lib.commons.html.EHTMLAttribute.BORDER;
import static org.conqat.lib.commons.html.EHTMLAttribute.CELLSPACING;
import static org.conqat.lib.commons.html.EHTMLAttribute.CLASS;
import static org.conqat.lib.commons.html.EHTMLAttribute.ID;
import static org.conqat.lib.commons.html.EHTMLAttribute.SRC;
import static org.conqat.lib.commons.html.EHTMLAttribute.STYLE;
import static org.conqat.lib.commons.html.EHTMLAttribute.WIDTH;
import static org.conqat.lib.commons.html.EHTMLElement.BODY;
import static org.conqat.lib.commons.html.EHTMLElement.IMG;
import static org.conqat.lib.commons.html.EHTMLElement.TABLE;
import static org.conqat.lib.commons.html.EHTMLElement.TD;
import static org.conqat.lib.commons.html.EHTMLElement.TR;

import java.io.File;

import org.conqat.engine.html_presentation.util.ResourcesManager;
import org.conqat.engine.html_presentation.util.WriterBase;
import org.conqat.lib.commons.html.CSSDeclarationBlock;

/**
 * This class is used for writing an {@link IPageDescriptor} to a file.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: AF269F729FF24D698B92456BB012EEE6
 */
public class PageWriter extends WriterBase {

	/** The HTML id used for the title text. */
	public static final String CAPTION_TITLE_ID = "caption-title";

	/** The HTML id used for the sub-title (description) text. */
	public static final String CAPTION_SUBTITLE_ID = "caption-subtitle";

	/** CSS for body. */
	private static CSSDeclarationBlock WHITE_BODY = new CSSDeclarationBlock(
			DEFAULT_FONT, BACKGROUND_COLOR, "white");

	/** CSS for title in caption. */
	private static CSSDeclarationBlock TITLE_FONT = new CSSDeclarationBlock(
			DEFAULT_FONT, FONT_SIZE, "16px", TEXT_ALIGN, "left", FONT_WEIGHT,
			"bold");

	/** Page to write. */
	private final IPageDescriptor page;

	/**
	 * Create new writer.
	 * 
	 * @param filename
	 *            the name of the file to write to.
	 * @param page
	 *            page to write.
	 */
	public PageWriter(String filename, IPageDescriptor page) {
		super(new File(filename));
		this.page = page;
	}

	/**
	 * Create new writer.
	 * 
	 * @param outputDirectory
	 *            output directory.
	 * @param page
	 *            page to write.
	 */
	public PageWriter(File outputDirectory, IPageDescriptor page) {
		super(new File(outputDirectory, page.getFilename()));
		this.page = page;
	}

	/** {@inheritDoc} */
	@Override
	protected void addBody() {
		writer.openElement(BODY, CLASS, WHITE_BODY);

		writer.openElement(TABLE, WIDTH, "100%", CELLSPACING, "0", BORDER, "0");
		writer.openElement(TR);
		writer.openElement(TD, STYLE, LIGHT_BLUE_BACKGROUND);
		writeCaption();
		writer.closeElement(TD);
		writer.closeElement(TR);

		writer.openElement(TR);
		writer.openElement(TD);
		writer.addRawString(page.getContent());
		writer.closeElement(TD);
		writer.closeElement(TR);

		writer.closeElement(TABLE);
		writer.closeElement(BODY);
	}

	/** Write page caption. */
	private void writeCaption() {
		writer.openElement(TABLE, CELLSPACING, "0", BORDER, "0");
		writer.openElement(TR);
		writer.openElement(TD);
		writer.addClosedElement(
				IMG,
				SRC,
				ResourcesManager.IMAGES_DIRECTORY_NAME + "/"
						+ page.getIconName());
		writer.closeElement(TD);
		writer.addClosedTextElement(TD,
				page.getName() + " (" + page.getGroupId() + ")", CLASS,
				TITLE_FONT, ID, CAPTION_TITLE_ID);
		writer.closeElement(TR);
		writer.openElement(TR);
		writer.addClosedTextElement(TD, " ", CLASS, DEFAULT_FONT);
		writer.addClosedTextElement(TD, page.getDescription(), CLASS,
				DEFAULT_FONT, ID, CAPTION_SUBTITLE_ID);
		writer.closeElement(TR);
		writer.closeElement(TABLE);
	}

	/** {@inheritDoc} */
	@Override
	protected String getTitle() {
		return page.getName();
	}
}