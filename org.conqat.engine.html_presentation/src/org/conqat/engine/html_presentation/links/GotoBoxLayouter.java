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
package org.conqat.engine.html_presentation.links;

import static org.conqat.lib.commons.color.ECCSMColor.LIGHT_BLUE;
import static org.conqat.lib.commons.html.ECSSProperty.BACKGROUND_COLOR;
import static org.conqat.lib.commons.html.ECSSProperty.CURSOR;
import static org.conqat.lib.commons.html.ECSSProperty.FONT_WEIGHT;
import static org.conqat.lib.commons.html.ECSSProperty.POSITION;
import static org.conqat.lib.commons.html.ECSSProperty.TEXT_ALIGN;
import static org.conqat.lib.commons.html.ECSSProperty.WIDTH;
import static org.conqat.lib.commons.html.EHTMLAttribute.CLASS;
import static org.conqat.lib.commons.html.EHTMLAttribute.ID;
import static org.conqat.lib.commons.html.EHTMLAttribute.PLACEHOLDER;
import static org.conqat.lib.commons.html.EHTMLAttribute.STYLE;
import static org.conqat.lib.commons.html.EHTMLAttribute.TYPE;
import static org.conqat.lib.commons.html.EHTMLElement.BUTTON;
import static org.conqat.lib.commons.html.EHTMLElement.DIV;
import static org.conqat.lib.commons.html.EHTMLElement.INPUT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.html_presentation.CSSMananger;
import org.conqat.engine.html_presentation.base.OverviewPageWriter;
import org.conqat.engine.html_presentation.javascript.base.BaseJSModule;
import org.conqat.engine.html_presentation.util.LayouterBase;
import org.conqat.lib.commons.html.CSSDeclarationBlock;

/**
 * {@ConQAT.Doc}
 * <p>
 * The styling is mostly inspired by the {@link OverviewPageWriter}, as the box
 * will commonly be placed on the overview page of a dashboard.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 46651 $
 * @ConQAT.Rating GREEN Hash: F0DBB62B7571054D52FD58E3C3156A52
 */
@AConQATProcessor(description = "Provides a text box with auto-completion that can be used to jump to a linked page.")
public class GotoBoxLayouter extends LayouterBase {

	/** CSS for a blue border. */
	private static final CSSDeclarationBlock BLUE_BORDER_CSS = new CSSDeclarationBlock(
			CSSMananger.DEFAULT_FONT).setBorder("2px", "solid",
			LIGHT_BLUE.getHTMLColorCode());

	/** CSS for the input box. */
	private static final CSSDeclarationBlock INPUT_BOX_CSS = new CSSDeclarationBlock(
			BLUE_BORDER_CSS, WIDTH, "500px");

	/** CSS for the go to button. */
	private static final CSSDeclarationBlock BUTTON_CSS = new CSSDeclarationBlock(
			CSSMananger.DEFAULT_FONT, BACKGROUND_COLOR,
			LIGHT_BLUE.getHTMLColorCode());

	/**
	 * CSS for the outer box. Inspired by the CSS for closures' autocomplete
	 * demo.
	 */
	private static final CSSDeclarationBlock AUTOCOMPLETE_RENDERER = new CSSDeclarationBlock(
			CSSMananger.DEFAULT_FONT, POSITION, "absolute", BACKGROUND_COLOR,
			"#ffffff", WIDTH, "500px").setBorder("2px", "solid",
			LIGHT_BLUE.getHTMLColorCode());

	/** CSS for the rows. Inspired by the CSS for closures' autocomplete demo. */
	private static final CSSDeclarationBlock AUTOCOMPLETE_ROW = new CSSDeclarationBlock(
			CURSOR, "pointer").setPadding(".4em");

	/**
	 * CSS for the active entry. Inspired by the CSS for closures' autocomplete
	 * demo.
	 */
	private static final CSSDeclarationBlock AUTOCOMPLETE_ACTIVE = new CSSDeclarationBlock(
			BACKGROUND_COLOR, LIGHT_BLUE.getHTMLColorCode());

	/**
	 * CSS for the highlighted part. Inspired by the CSS for closures'
	 * autocomplete demo.
	 */
	private static final CSSDeclarationBlock AUTOCOMPLETE_HIGHLIGHTED = new CSSDeclarationBlock(
			FONT_WEIGHT, "bold");

	/** Counter used for generation of unique IDs. */
	private static int idCounter = 0;

	/** List of input scopes for search box */
	private List<IConQATNode> inputs = new ArrayList<IConQATNode>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.INPUT_NAME, description = ConQATParamDoc.INPUT_DESC, minOccurrences = 1, maxOccurrences = -1)
	public void addInput(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) IConQATNode input) {
		inputs.add(input);
	}

	/** {@inheritDoc} */
	@Override
	protected void layoutPage() throws ConQATException {
		Map<String, String> linkMap = extractLinks();
		if (linkMap.isEmpty()) {
			throw new ConQATException("No links found in input scope!");
		}

		String textId = "goto-box-text-" + idCounter++;
		String buttonId = "goto-box-button-" + idCounter++;

		writer.openElement(DIV, CLASS, BLUE_BORDER_CSS);
		writer.addClosedTextElement(DIV, "Go to element", CLASS,
				OverviewPageWriter.GROUP_TABLE_HEADER);

		writer.openElement(DIV, STYLE, new CSSDeclarationBlock(TEXT_ALIGN,
				"center").setPadding("13px"));
		writer.addClosedElement(INPUT, TYPE, "text", ID, textId, CLASS,
				INPUT_BOX_CSS, PLACEHOLDER, "Enter target's name");
		writer.addClosedTextElement(BUTTON, "Go To", ID, buttonId, CLASS,
				BUTTON_CSS);
		writer.closeElement(DIV);

		writer.closeElement(DIV);

		writer.insertJavaScript(BaseJSModule.installGotoBox(textId, buttonId,
				new CSSDeclarationBlock[] { AUTOCOMPLETE_RENDERER,
						AUTOCOMPLETE_ROW, AUTOCOMPLETE_ACTIVE,
						AUTOCOMPLETE_HIGHLIGHTED }, linkMap));
	}

	/**
	 * Extracts all links from {@link #inputs} and returns them as an ID to link
	 * map.
	 */
	private Map<String, String> extractLinks() {
		Map<String, String> linkMap = new HashMap<String, String>();
		for (IConQATNode input : inputs) {
			for (IConQATNode node : TraversalUtils.listAllDepthFirst(input)) {
				String link = LinkProviderBase.obtainLink(node);
				if (link != null) {
					linkMap.put(node.getId(), link);
				}
			}
		}
		return linkMap;
	}

	/** {@inheritDoc} */
	@Override
	protected String getIconName() {
		return "file.gif";
	}
}
