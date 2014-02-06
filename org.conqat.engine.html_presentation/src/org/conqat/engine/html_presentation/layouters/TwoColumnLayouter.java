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

import static org.conqat.engine.html_presentation.CSSMananger.TABLE_CELL;
import static org.conqat.lib.commons.html.EHTMLAttribute.BORDER;
import static org.conqat.lib.commons.html.EHTMLAttribute.CELLPADDING;
import static org.conqat.lib.commons.html.EHTMLAttribute.CELLSPACING;
import static org.conqat.lib.commons.html.EHTMLAttribute.CLASS;
import static org.conqat.lib.commons.html.EHTMLElement.TABLE;
import static org.conqat.lib.commons.html.EHTMLElement.TD;
import static org.conqat.lib.commons.html.EHTMLElement.TR;

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.html_presentation.IPageDescriptor;
import org.conqat.engine.html_presentation.util.LayouterBase;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 99E77EBD3B4426396EBB948AB6C9A0F3
 */
@AConQATProcessor(description = "This layout merges two pages into two columns. "
		+ "The side from which the summary is taken can be specified (left page by default). "
		+ "This might create havoc "
		+ "with JavaScript-enabled pages due to conflicting element ids.")
public class TwoColumnLayouter extends LayouterBase {

	/** Enum used to indicate a side */
	public enum ESide {
		/** Left side */
		LEFT,

		/** Right side */
		RIGHT;
	}

	/** Name of the page icon. */
	private static final String MERGED_PAGE_ICON_NAME = "merged_page.gif";

	/** Left page */
	private IPageDescriptor leftPage;

	/** Right page */
	private IPageDescriptor rightPage;

	/** Side from which summary is taken */
	private ESide summarySide = ESide.LEFT;

	/** ConQAT Parameter */
	@AConQATParameter(name = "pages", minOccurrences = 1, maxOccurrences = 1, description = "Set pages")
	public void setPages(
			@AConQATAttribute(name = "left", description = "Left page") IPageDescriptor leftPage,
			@AConQATAttribute(name = "right", description = "Right page") IPageDescriptor rightPage) {
		this.leftPage = leftPage;
		this.rightPage = rightPage;
	}

	/** ConQAT Parameter */
	@AConQATParameter(name = "summary", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Side from which summary is taken")
	public void setSummary(
			@AConQATAttribute(name = "side", description = "Default: Left side") ESide summarySide) {
		this.summarySide = summarySide;
	}

	/** Returns {@inheritDoc} */
	@Override
	protected String getIconName() {
		return MERGED_PAGE_ICON_NAME;
	}

	/** {@inheritDoc} */
	@Override
	protected void layoutPage() {
		writer.openElement(TABLE, BORDER, "0", CELLSPACING, "2", CELLPADDING,
				"0");
		writer.openElement(TR);

		writePage(leftPage);
		writePage(rightPage);

		writer.closeElement(TR);
		writer.closeElement(TABLE);
	}

	/** Writes a page content into a table cell */
	private void writePage(IPageDescriptor page) {
		writer.openElement(TD, CLASS, TABLE_CELL);
		writer.addRawString(page.getContent());
		writer.closeElement(TD);
	}

	/** Returns assessment of summary page. */
	@Override
	protected Object getSummary() {
		if (summarySide == ESide.RIGHT) {
			return rightPage.getSummary();
		}
		return leftPage.getSummary();
	}
}