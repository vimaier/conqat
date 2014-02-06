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
package org.conqat.engine.html_presentation.layouters;

import static org.conqat.lib.commons.html.EHTMLAttribute.ALIGN;
import static org.conqat.lib.commons.html.EHTMLAttribute.CELLPADDING;
import static org.conqat.lib.commons.html.EHTMLAttribute.CELLSPACING;
import static org.conqat.lib.commons.html.EHTMLAttribute.CLASS;
import static org.conqat.lib.commons.html.EHTMLAttribute.COLSPAN;
import static org.conqat.lib.commons.html.EHTMLElement.TABLE;
import static org.conqat.lib.commons.html.EHTMLElement.TD;
import static org.conqat.lib.commons.html.EHTMLElement.TH;
import static org.conqat.lib.commons.html.EHTMLElement.TR;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.node.DisplayList;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.html_presentation.base.ColorConstants;
import org.conqat.engine.html_presentation.util.DefaultNodeAwareFindingFormatter;
import org.conqat.engine.html_presentation.util.LayouterBase;
import org.conqat.engine.html_presentation.util.PresentationUtils;
import org.conqat.engine.html_presentation.util.PresentationUtils.IContextSensitiveFormatter;
import org.conqat.lib.commons.html.CSSDeclarationBlock;
import org.conqat.lib.commons.html.ECSSProperty;
import org.conqat.lib.commons.html.EHTMLAttribute;

/**
 * {ConQAT.Doc}
 * 
 * @author $Author: heinemann $
 * @version $Rev: 42215 $
 * @ConQAT.Rating RED Hash: DA65156E406A87EB8202D23D2945BC2C
 */
@AConQATProcessor(description = "Layouts single nodes as small tables, where each row corresponds to an display list entry.")
public class SingleNodeLayouter extends LayouterBase {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.INPUT_NAME, attribute = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_DESC)
	public IConQATNode input;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "target", attribute = "nodes", optional = true, description = "The nodes to display as tables. Default is just the root.")
	public ETargetNodes targetNodes = ETargetNodes.ROOT;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "caption", attribute = "override", optional = true, description = "Allows to override the caption of the tables generated (default is to use the node's id).")
	public String captionOverride = null;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "summary", attribute = "inherit", optional = true, description = "Allows to inherit the summary stored at the input node (default is not to inherit).")
	public boolean inheritSummary = false;

	/** {@inheritDoc} */
	@Override
	protected String getIconName() {
		return TableLayouter.TABLE_ICON_NAME;
	}

	/** {@inheritDoc} */
	@Override
	protected void layoutPage() {
		for (IConQATNode node : TraversalUtils.listDepthFirst(input,
				targetNodes)) {
			addTableForNode(node, NodeUtils.getDisplayList(input));
		}
	}

	/** Adds an HTML table for the given node. */
	private void addTableForNode(IConQATNode node, DisplayList displayList) {

		writer.openElement(TABLE, CELLSPACING, 0, CELLPADDING, 2, CLASS,
				TableLayouter.TABLE_BORDER_STYLE);

		writer.openElement(TR, CLASS, TableLayouter.HEADER_CLASS);
		writer.addClosedTextElement(TH, determineCaption(node), COLSPAN, 2);
		writer.closeElement(TR);

		IContextSensitiveFormatter<?> findingFormatter = new DefaultNodeAwareFindingFormatter(
				node);
		boolean odd = true;
		for (String key : displayList) {
			writer.openElement(TR, CLASS, TableLayouter.ROW_CLASS);
			if (odd) {
				writer.addAttribute(EHTMLAttribute.STYLE,
						new CSSDeclarationBlock(ECSSProperty.BACKGROUND_COLOR,
								ColorConstants.TABLE_CELL_ODD));
			}
			odd = !odd;

			writer.addClosedTextElement(TD, key, CLASS,
					TableLayouter.CELL_CLASS);

			// TODO (LH) The following ~10 lines are redundant in
			// org.conqat.engine.html_presentation.layouters.TableLayouter.appendValues(IConQATNode,
			// DisplayList)
			writer.openElement(TD, CLASS, TableLayouter.CELL_CLASS);

			Object value = node.getValue(key);
			// flush right if it looks numeric
			if (value instanceof Number) {
				writer.addAttribute(ALIGN, "right");
			}

			PresentationUtils.appendValue(value, displayList.getFormatter(key),
					writer, getLogger(), findingFormatter);
			writer.closeElement(TD);

			writer.closeElement(TR);
		}

		writer.closeElement(TABLE);
	}

	/** Returns the caption to be used for a table. */
	private String determineCaption(IConQATNode node) {
		if (captionOverride != null) {
			return captionOverride;
		}

		return node.getId();
	}

	/** {@inheritDoc} */
	@Override
	protected Object getSummary() {
		if (inheritSummary) {
			return NodeUtils.getSummary(input);
		}

		return super.getSummary();
	}

}
