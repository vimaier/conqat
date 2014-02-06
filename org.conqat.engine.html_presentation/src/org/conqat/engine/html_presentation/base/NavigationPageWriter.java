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
import static org.conqat.engine.html_presentation.CSSMananger.LINK_CURSOR;
import static org.conqat.lib.commons.html.ECSSProperty.BACKGROUND_COLOR;
import static org.conqat.lib.commons.html.ECSSProperty.BACKGROUND_IMAGE;
import static org.conqat.lib.commons.html.ECSSProperty.BACKGROUND_POSITION;
import static org.conqat.lib.commons.html.ECSSProperty.BACKGROUND_REPEAT;
import static org.conqat.lib.commons.html.ECSSProperty.CURSOR;
import static org.conqat.lib.commons.html.ECSSProperty.FONT_SIZE;
import static org.conqat.lib.commons.html.ECSSProperty.FONT_WEIGHT;
import static org.conqat.lib.commons.html.ECSSProperty.LIST_STYLE_TYPE;
import static org.conqat.lib.commons.html.ECSSProperty.MARGIN_BOTTOM;
import static org.conqat.lib.commons.html.ECSSProperty.MARGIN_TOP;
import static org.conqat.lib.commons.html.ECSSProperty.PADDING_BOTTOM;
import static org.conqat.lib.commons.html.ECSSProperty.PADDING_LEFT;
import static org.conqat.lib.commons.html.ECSSProperty.PADDING_RIGHT;
import static org.conqat.lib.commons.html.ECSSProperty.PADDING_TOP;
import static org.conqat.lib.commons.html.ECSSProperty.TEXT_ALIGN;
import static org.conqat.lib.commons.html.ECSSProperty.TEXT_DECORATION;
import static org.conqat.lib.commons.html.ECSSProperty.WHITE_SPACE;
import static org.conqat.lib.commons.html.EHTMLAttribute.CLASS;
import static org.conqat.lib.commons.html.EHTMLAttribute.HREF;
import static org.conqat.lib.commons.html.EHTMLAttribute.ID;
import static org.conqat.lib.commons.html.EHTMLAttribute.SRC;
import static org.conqat.lib.commons.html.EHTMLAttribute.STYLE;
import static org.conqat.lib.commons.html.EHTMLAttribute.TARGET;
import static org.conqat.lib.commons.html.EHTMLElement.A;
import static org.conqat.lib.commons.html.EHTMLElement.BODY;
import static org.conqat.lib.commons.html.EHTMLElement.DIV;
import static org.conqat.lib.commons.html.EHTMLElement.IMG;
import static org.conqat.lib.commons.html.EHTMLElement.LI;
import static org.conqat.lib.commons.html.EHTMLElement.SPAN;
import static org.conqat.lib.commons.html.EHTMLElement.UL;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.html_presentation.HTMLPresentation;
import org.conqat.engine.html_presentation.IPageDescriptor;
import org.conqat.engine.html_presentation.javascript.base.BaseJSModule;
import org.conqat.engine.html_presentation.util.WriterBase;
import org.conqat.lib.commons.collections.ListMap;
import org.conqat.lib.commons.color.ECCSMColor;
import org.conqat.lib.commons.html.CSSDeclarationBlock;
import org.conqat.lib.commons.html.EHTMLAttribute;

/**
 * This class generates the page with the navigation tree ({@value #PAGE_NAME}).
 * As the navigation page doesn't have a name, description, icon, ... this is
 * not implemented as {@link IPageDescriptor}.
 * <p>
 * The navigation page is implemented as a nested HTML list with some JavaScript
 * for expanding and collapsing the tree.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: EE8EB26FA21E1474F09C6FD117EC976E
 */
public class NavigationPageWriter extends WriterBase {

	/** Name of the footer page: {@value} */
	public static final String PAGE_NAME = "navigation.html";

	/** Id for the expand all button. */
	private static final String EXPAND_ALL_ID = "expand-all";

	/** Id for the collapse all button. */
	private static final String COLLAPSE_ALL_ID = "collapse-all";

	/** URL for the open icon. */
	private static final String GROUP_OPEN_ICON = "images/group_open.gif";

	/** URL for the close icon. */
	private static final String GROUP_CLOSE_ICON = "images/group.gif";

	/** CSS for the lists that make up the tree. */
	private final static CSSDeclarationBlock LIST = new CSSDeclarationBlock(
			LIST_STYLE_TYPE, "none").setMargin("0px").setPadding("0px");

	/**
	 * Base class for backgroung images.
	 * <p>
	 * This class is also used as base class in
	 * {@link #addPage(IPageDescriptor)} for the icons of the different pages.
	 * <p>
	 * <b>Pitfall:</b> If used as a class the URL to the image must be prefixed
	 * with '..' as it is relative to the <code>css</code>-directory. If used in
	 * a &lt;style&gt;-tag no prefix is necessary.
	 */
	private final static CSSDeclarationBlock ICON = new CSSDeclarationBlock(
			BACKGROUND_REPEAT, "no-repeat", BACKGROUND_POSITION, "0 0",
			PADDING_TOP, "0px", PADDING_BOTTOM, "0px", PADDING_LEFT, "22px",
			PADDING_RIGHT, "0px", MARGIN_TOP, "0px", MARGIN_BOTTOM, "0px");

	/** CSS for overview image. */
	private final static CSSDeclarationBlock OVERVIEW = new CSSDeclarationBlock(
			ICON, BACKGROUND_IMAGE, "url(../images/overview.gif)");

	/** CSS for group image. */
	private final static CSSDeclarationBlock GROUP = new CSSDeclarationBlock(
			ICON, BACKGROUND_IMAGE, "url(../" + GROUP_OPEN_ICON + ")",
			MARGIN_TOP, "3px");

	/** CSS for info group image. */
	private final static CSSDeclarationBlock INFO_GROUP = new CSSDeclarationBlock(
			ICON, BACKGROUND_IMAGE, "url(../images/info_group.gif)",
			MARGIN_TOP, "3px");

	/** CSS for navigation links. */
	private static CSSDeclarationBlock LINK = new CSSDeclarationBlock(
			DEFAULT_FONT, FONT_SIZE, "12px", FONT_WEIGHT, "normal",
			TEXT_DECORATION, "none");

	/** List of of pages included in the navigation. */
	private final ListMap<String, IPageDescriptor> groups2Pages;

	/**
	 * Create new navigation page factory for a list of pages.
	 * 
	 * @param groups2Pages
	 *            list of pages
	 */
	public NavigationPageWriter(File outputDirectory,
			ListMap<String, IPageDescriptor> groups2Pages) {
		super(new File(outputDirectory, PAGE_NAME));
		this.groups2Pages = groups2Pages;
	}

	/** {@inheritDoc} */
	@Override
	protected void addBody() {

		// sort to make sure that info group appears last
		List<String> groupNames = new ArrayList<String>(groups2Pages.getKeys());

		writer.openElement(BODY, CLASS, DEFAULT_FONT);
		appendControlHeader();

		String treeId = "navigation-tree";
		writer.openElement(UL, CLASS, LIST, ID, treeId);

		// append first tree node that links to overview page
		writer.openElement(LI, CLASS, OVERVIEW);
		writer.addClosedTextElement(A, "Overview", HREF, "overview.html",
				TARGET, "content");
		writer.closeElement(LI);

		for (String groupName : groupNames) {
			addGroup(groupName, groups2Pages.getCollection(groupName));
		}

		writer.closeElement(UL);

		writer.insertJavaScript(BaseJSModule.installNavigationPage(treeId,
				EXPAND_ALL_ID, COLLAPSE_ALL_ID, GROUP_OPEN_ICON,
				GROUP_CLOSE_ICON));

		writer.closeElement(BODY);
	}

	/** Append header with 'expand all/collapse all' buttons. */
	private void appendControlHeader() {
		writer.openElement(
				DIV,
				STYLE,
				new CSSDeclarationBlock(BACKGROUND_COLOR, ECCSMColor.LIGHT_BLUE
						.getHTMLColorCode(), TEXT_ALIGN, "right",
						MARGIN_BOTTOM, "3px", PADDING_RIGHT, "1px"));
		writer.addClosedElement(IMG, CLASS, LINK_CURSOR, SRC,
				"images/expand_all.gif", EHTMLAttribute.TITLE, "Expand All",
				ID, EXPAND_ALL_ID);
		writer.addClosedElement(IMG, CLASS, LINK_CURSOR, SRC,
				"images/collapse_all.gif", EHTMLAttribute.TITLE,
				"Collapse All", ID, COLLAPSE_ALL_ID);
		writer.closeElement(DIV);
	}

	/** Add a group of pages. */
	private void addGroup(String groupName, List<IPageDescriptor> pages) {

		if (HTMLPresentation.INVISIBLE_GROUP_NAME.equals(groupName)) {
			return;
		}

		CSSDeclarationBlock groupClass = GROUP;
		if (HTMLPresentation.INFO_GROUP_NAME.equals(groupName)) {
			groupClass = INFO_GROUP;
		}

		writer.openElement(LI, CLASS, groupClass);
		writer.addClosedTextElement(SPAN, groupName, STYLE,
				new CSSDeclarationBlock(FONT_WEIGHT, "bold", CURSOR, "pointer",
						WHITE_SPACE, "nowrap"));

		writer.openElement(UL, CLASS, LIST);
		for (IPageDescriptor page : pages) {
			if (OverviewPageWriter.SUMMARY_PAGE_NAME.equals(page.getName())) {
				continue;
			}
			addPage(page);
		}
		writer.closeElement(UL);
		writer.closeElement(LI);
	}

	/** Add a single result page. */
	private void addPage(IPageDescriptor page) {
		String pageName = page.getFilename();

		writer.openElement(LI, STYLE, new CSSDeclarationBlock(ICON,
				BACKGROUND_IMAGE, "url(images/" + page.getIconName() + ")"));
		writer.addClosedTextElement(A, page.getName(), CLASS, LINK, HREF,
				pageName, TARGET, "content");
		writer.closeElement(LI);
	}

	/** {@inheritDoc} */
	@Override
	protected String getTitle() {
		return "Navigation";
	}
}
