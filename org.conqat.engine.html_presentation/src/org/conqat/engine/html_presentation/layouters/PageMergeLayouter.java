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
package org.conqat.engine.html_presentation.layouters;

import java.util.ArrayList;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.html_presentation.CSSMananger;
import org.conqat.engine.html_presentation.IPageDescriptor;
import org.conqat.engine.html_presentation.util.LayouterBase;
import org.conqat.lib.commons.color.ECCSMColor;
import org.conqat.lib.commons.html.CSSDeclarationBlock;
import org.conqat.lib.commons.html.ECSSProperty;
import org.conqat.lib.commons.html.EHTMLAttribute;
import org.conqat.lib.commons.html.EHTMLElement;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: EDAC7094A185203714A5EE6554F79296
 */
@AConQATProcessor(description = "This layout merges multiple pages on one page. "
		+ "Assessment summary is taken from first page. "
		+ "This might create havoc with JavaScript-enabled "
		+ "pages due to conflicting element ids.")
public class PageMergeLayouter extends LayouterBase {

	/** Name of the page icon. */
	public static final String MERGED_PAGE_ICON_NAME = "merged_page.gif";

	/** The style used for the headings. */
	private static final CSSDeclarationBlock headingStyle = new CSSDeclarationBlock(
			CSSMananger.DEFAULT_FONT_BOLD, ECSSProperty.COLOR,
			ECCSMColor.BLUE.getHTMLColorCode(), ECSSProperty.FONT_SIZE, "20px");

	/** List of pages. */
	private final ArrayList<IPageDescriptor> pages = new ArrayList<IPageDescriptor>();

	/** Horizontal layout? */
	private boolean horizontal = false;

	/** Display headings for joined parts. */
	private boolean displayHeadings = false;

	/** Add page */
	@AConQATParameter(name = ConQATParamDoc.INPUT_NAME, minOccurrences = 1, description = ConQATParamDoc.INPUT_DESC)
	public void addPage(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) IPageDescriptor page) {
		pages.add(page);
	}

	/** Horizontal layout? */
	@AConQATParameter(name = "layout", minOccurrences = 0, maxOccurrences = 1, description = "Default layout is vertical.")
	public void setHorizontal(
			@AConQATAttribute(name = "horizontal", description = "If true, layout horizontal instead of vertical.") boolean horizontal) {
		this.horizontal = horizontal;
	}

	/** Display headings for joined parts? */
	@AConQATParameter(name = "headings", minOccurrences = 0, maxOccurrences = 1, description = "Default is not to show headings.")
	public void setDisplayHeadings(
			@AConQATAttribute(name = "visible", description = "If true, headings are shown.") boolean displayHeadings) {
		this.displayHeadings = displayHeadings;
	}

	/** Returns {@inheritDoc} */
	@Override
	protected String getIconName() {
		return MERGED_PAGE_ICON_NAME;
	}

	/** {@inheritDoc} */
	@Override
	protected void layoutPage() {
		if (horizontal) {
			writer.openElement(EHTMLElement.TABLE);
			writer.openElement(EHTMLElement.TR);

			for (IPageDescriptor page : pages) {
				writer.openElement(EHTMLElement.TD);
				if (displayHeadings) {
					writer.addClosedTextElement(EHTMLElement.DIV,
							page.getName(), EHTMLAttribute.CLASS, headingStyle);
				}
				writer.addRawString(page.getContent());
				writer.closeElement(EHTMLElement.TD);
			}

			writer.closeElement(EHTMLElement.TR);
			writer.closeElement(EHTMLElement.TABLE);
		} else {
			for (IPageDescriptor page : pages) {
				if (displayHeadings) {
					writer.addClosedTextElement(EHTMLElement.DIV,
							page.getName(), EHTMLAttribute.CLASS, headingStyle);
				}
				writer.addRawString(page.getContent());
			}
		}
	}

	/** Returns assessment of first page. */
	@Override
	protected Object getSummary() {
		// must have at least one entry
		return pages.get(0).getSummary();
	}

}