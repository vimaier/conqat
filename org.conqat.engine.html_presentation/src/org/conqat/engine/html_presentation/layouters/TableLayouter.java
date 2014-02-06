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
import static org.conqat.engine.html_presentation.CSSMananger.TABLE_HEADER_CELL;
import static org.conqat.lib.commons.html.ECSSProperty.BACKGROUND_COLOR;
import static org.conqat.lib.commons.html.ECSSProperty.BORDER_LEFT_COLOR;
import static org.conqat.lib.commons.html.ECSSProperty.BORDER_LEFT_STYLE;
import static org.conqat.lib.commons.html.ECSSProperty.BORDER_LEFT_WIDTH;
import static org.conqat.lib.commons.html.ECSSProperty.CURSOR;
import static org.conqat.lib.commons.html.ECSSProperty.FONT_WEIGHT;
import static org.conqat.lib.commons.html.EHTMLAttribute.ALIGN;
import static org.conqat.lib.commons.html.EHTMLAttribute.CELLPADDING;
import static org.conqat.lib.commons.html.EHTMLAttribute.CELLSPACING;
import static org.conqat.lib.commons.html.EHTMLAttribute.CLASS;
import static org.conqat.lib.commons.html.EHTMLAttribute.HREF;
import static org.conqat.lib.commons.html.EHTMLAttribute.ID;
import static org.conqat.lib.commons.html.EHTMLAttribute.STYLE;
import static org.conqat.lib.commons.html.EHTMLAttribute.TARGET;
import static org.conqat.lib.commons.html.EHTMLAttribute.WIDTH;
import static org.conqat.lib.commons.html.EHTMLElement.A;
import static org.conqat.lib.commons.html.EHTMLElement.TABLE;
import static org.conqat.lib.commons.html.EHTMLElement.TD;
import static org.conqat.lib.commons.html.EHTMLElement.TH;
import static org.conqat.lib.commons.html.EHTMLElement.TR;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.node.DisplayList;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.html_presentation.CSSMananger;
import org.conqat.engine.html_presentation.base.ColorConstants;
import org.conqat.engine.html_presentation.javascript.base.BaseJSModule;
import org.conqat.engine.html_presentation.links.ELinkTarget;
import org.conqat.engine.html_presentation.links.LinkProviderBase;
import org.conqat.engine.html_presentation.util.DefaultNodeAwareFindingFormatter;
import org.conqat.engine.html_presentation.util.LayouterBase;
import org.conqat.engine.html_presentation.util.PresentationUtils;
import org.conqat.engine.html_presentation.util.PresentationUtils.IContextSensitiveFormatter;
import org.conqat.lib.commons.color.ECCSMColor;
import org.conqat.lib.commons.html.CSSDeclarationBlock;
import org.conqat.lib.commons.html.ECSSProperty;
import org.conqat.lib.commons.html.EHTMLAttribute;
import org.conqat.lib.commons.html.EHTMLElement;
import org.conqat.lib.commons.string.StringUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 44623 $
 * @ConQAT.Rating GREEN Hash: F8CC09E97A032390427D69745324DC85
 */
@AConQATProcessor(description = "Table layout generator. This layouter creates a result "
		+ "table that has a row for each ConQATNode and a column for each value "
		+ "defined in the display list of the root node. If the 'showNodeIds'-parameter is "
		+ "set to false, the table shows the node names and visualizes the tree "
		+ "structure by indentation.")
public class TableLayouter extends LayouterBase {

	/** Style used for table border. */
	public static final CSSDeclarationBlock TABLE_BORDER_STYLE = new CSSDeclarationBlock()
			.setBorder("2px", "solid", ECCSMColor.BLUE.getHTMLColorCode());

	/** Style used for table header. */
	public static final CSSDeclarationBlock HEADER_CLASS = new CSSDeclarationBlock(
			TABLE_HEADER_CELL, BACKGROUND_COLOR,
			ECCSMColor.BLUE.getHTMLColorCode(), ECSSProperty.COLOR, "white",
			ECSSProperty.TEXT_ALIGN, "left");

	/** Style used for normal rows. */
	public static final CSSDeclarationBlock ROW_CLASS = new CSSDeclarationBlock(
			TABLE_CELL, BACKGROUND_COLOR, ColorConstants.TABLE_CELL_EVEN);

	/** Style used for striped rows. */
	public static final CSSDeclarationBlock ROW_STRIPE_CLASS = new CSSDeclarationBlock(
			ROW_CLASS, BACKGROUND_COLOR, ColorConstants.TABLE_CELL_ODD);

	/** Style used for all cells but the left-most. */
	public static final CSSDeclarationBlock CELL_CLASS = new CSSDeclarationBlock(
			BORDER_LEFT_WIDTH, "1px", BORDER_LEFT_STYLE, "solid",
			BORDER_LEFT_COLOR, ECCSMColor.BLUE.getHTMLColorCode());

	/** Style used for linked names. */
	public static final CSSDeclarationBlock LINK_CLASS = new CSSDeclarationBlock(
			CSSMananger.SMALL_FONT, FONT_WEIGHT, "normal", CURSOR, "pointer");

	/** Style used for linked names. */
	public static final CSSDeclarationBlock FILTER_INPUT_CLASS = new CSSDeclarationBlock(
			CSSMananger.SMALL_FONT, ECSSProperty.WIDTH, "98%");

	/** Name of the table icon. */
	public static final String TABLE_ICON_NAME = "table.gif";

	/** Default id column label */
	private static final String ID_COLUMN_LABEL = "Element";

	/** Counter used for ID generation. */
	private static int idCounter = 0;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.INPUT_NAME, attribute = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_DESC)
	public IConQATNode input;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "id-column", attribute = "label", description = "Label used to layout the id column. Default is "
			+ ID_COLUMN_LABEL, optional = true)
	public String idColumnLabel = ID_COLUMN_LABEL;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "tableWidth", attribute = "value", optional = true, description = ""
			+ "Specifies desired width of table (e.g. '100px', '20%', '300em',...). "
			+ "If the width is left unspecified, the table will be as wide as needed.")
	public String tableWidth;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "showNodeIds", attribute = "value", optional = true, description = ""
			+ "Specifies if the table shows node names or node ids. Use true for ids, false for names [true].")
	public boolean showIds = true;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "name-from", attribute = "key", optional = true, description = ""
			+ "If set, the displayed name is that of the specified key instead of the element's name, "
			+ "showNodeIds must be set to false, otherwise the parameter has no effect. If nothing "
			+ "is storead under the given key, the element name is used as default.")
	public String nameFromKey;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "display", attribute = "mode", optional = true, description = ""
			+ "The rendering mode used to display the table.")
	public ETableMode tableMode = ETableMode.PLAIN;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "filtering", attribute = "enabled", optional = true, description = ""
			+ "Whether filtering should be supported by this table (default: false). "
			+ "If enabled, input fields that allow to enter filter expressions are included below the headings. "
			+ "These expressions can then be used to filter the visual representation of the table.")
	public boolean filteringEnabled = false;

	/** Key used to read the color from. */
	private String colorKey = null;

	/** Factor used to pre-multiply saturation of custom colors. */
	private float saturationMultiplier;

	/** Factory used to pre-multiply the brightness of custom colors. */
	private float brightnessMultiplier;

	/** Counts the number of rows added. */
	private int rowCounter = 0;

	/**
	 * This list stores for each row of the table the index of its logical
	 * parent row.
	 */
	private final List<Integer> parentRows = new ArrayList<Integer>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "color", maxOccurrences = 1, description = ""
			+ "Defines the key from which the color can be looked up. "
			+ "If this is not given, a default color is used. "
			+ "As the color is used as a background color, it may be modified by multipliers to make it less obtrusive.")
	public void setColorKey(
			@AConQATAttribute(name = "key", description = ""
					+ "The name of the key to read the color from.") String key,
			@AConQATAttribute(name = "saturation", defaultValue = "0.5", description = ""
					+ "A factor (between 0 and 1) to premultiply the saturation of the color read from the key.") float saturationMultiplier,
			@AConQATAttribute(name = "brightness", defaultValue = "0.5", description = ""
					+ "A factor (between 0 and 1) to premultiply the inverse of the brightness rad from the key.") float brightnessMultiplier)
			throws ConQATException {
		if (saturationMultiplier < 0 || saturationMultiplier > 1) {
			throw new ConQATException("Saturation must be between 0 and 1!");
		}
		if (brightnessMultiplier < 0 || brightnessMultiplier > 1) {
			throw new ConQATException("Brightness must be between 0 and 1!");
		}
		colorKey = key;
		this.saturationMultiplier = saturationMultiplier;
		this.brightnessMultiplier = brightnessMultiplier;
	}

	/** {@inheritDoc} */
	@Override
	protected void layoutPage() {
		DisplayList displayList = NodeUtils.getDisplayList(input);

		String tableId = "table-id" + idCounter++;
		writer.openElement(TABLE, ID, tableId, CELLSPACING, "0", CELLPADDING,
				"2", STYLE, TABLE_BORDER_STYLE);
		if (tableWidth != null) {
			writer.addAttribute(WIDTH, tableWidth);
		}

		writer.openElement(EHTMLElement.THEAD);
		createHeader(displayList);

		if (filteringEnabled) {
			writer.openElement(TR, CLASS, HEADER_CLASS);
			for (int i = 0; i <= displayList.size(); ++i) {
				writer.openElement(TH);
				writer.addClosedElement(EHTMLElement.INPUT,
						EHTMLAttribute.CLASS, FILTER_INPUT_CLASS,
						EHTMLAttribute.TYPE, "text",
						EHTMLAttribute.PLACEHOLDER, "Filter...");
				writer.closeElement(TH);
			}
			writer.closeElement(TR);
		}

		writer.closeElement(EHTMLElement.THEAD);

		writer.openElement(EHTMLElement.TBODY);
		layoutTableBody(displayList);
		writer.closeElement(EHTMLElement.TBODY);

		writer.closeElement(TABLE);

		appendTableScript(tableId);
	}

	/** Layouts the body of the table. */
	private void layoutTableBody(DisplayList displayList) {
		boolean hideRoot = NodeUtils.getHideRoot(input);
		if (hideRoot) {
			// start with children
			if (input.hasChildren()) {
				for (IConQATNode node : NodeUtils.getSortedChildren(input)) {
					appendNode(node, -1, displayList);
				}
			}
		} else {
			// start with root node
			appendNode(input, -1, displayList);
		}
	}

	/** Appends the script required for activating the table mode. */
	private void appendTableScript(String tableId) {
		CSSDeclarationBlock oddClass = ROW_STRIPE_CLASS;
		CSSDeclarationBlock evenClass = ROW_CLASS;

		// no striping if explicit colors are used
		if (colorKey != null) {
			oddClass = null;
			evenClass = null;
		}

		switch (tableMode) {
		case SORTABLE:
			writer.insertJavaScript(BaseJSModule.makeTableSortable(tableId,
					oddClass, evenClass));
			break;
		case TREE:
			writer.insertJavaScript(BaseJSModule.makeTableTreelike(tableId,
					parentRows, oddClass, evenClass));
			break;
		}

		if (filteringEnabled) {
			writer.insertJavaScript(BaseJSModule.registerTableFilters(tableId,
					parentRows, FILTER_INPUT_CLASS, oddClass, evenClass));
		}
	}

	/** Create table header with keys. */
	private void createHeader(DisplayList displayList) {
		writer.openElement(TR, CLASS, HEADER_CLASS);

		Object[] attributes = new Object[0];
		if (tableMode == ETableMode.SORTABLE) {
			attributes = new Object[] { EHTMLAttribute.STYLE,
					new CSSDeclarationBlock(ECSSProperty.CURSOR, "pointer") };
		}

		writer.addClosedTextElement(TH, idColumnLabel, attributes);
		for (String key : displayList) {
			writer.addClosedTextElement(TH, key, attributes);
		}
		writer.closeElement(TR);
	}

	/**
	 * Recursively add all elements (with all keys) to the table.
	 * 
	 * @param node
	 *            node to start with
	 * @param parentRow
	 *            one based index of parent row (or 0 for root nodes)
	 * @param displayList
	 *            list of keys to display
	 */
	private void appendNode(IConQATNode node, int parentRow,
			DisplayList displayList) {

		parentRows.add(parentRow);
		int rowId = rowCounter++; // post-increment to be zero-based

		if (colorKey != null) {
			writer.openElement(TR, CLASS, ROW_CLASS);
			String color = getColor(node);
			if (color != null) {
				writer.addAttribute(EHTMLAttribute.STYLE,
						new CSSDeclarationBlock(BACKGROUND_COLOR, color));
			}
		} else if (rowId % 2 == 1) {
			writer.openElement(TR, CLASS, ROW_STRIPE_CLASS);
		} else {
			writer.openElement(TR, CLASS, ROW_CLASS);
		}

		writer.openElement(TD);
		String link = LinkProviderBase.obtainLink(node);
		if (link == null) {
			writer.addText(obtainDisplayName(node));
		} else {
			writeLink(node, link);
		}
		writer.closeElement(TD);

		appendValues(node, displayList);

		writer.closeElement(TR);

		if (node.hasChildren()) {
			for (IConQATNode child : NodeUtils.getSortedChildren(node)) {
				appendNode(child, rowId, displayList);
			}
		}
	}

	/** Writes an HTML anchor link. */
	private void writeLink(IConQATNode node, String link) {
		writer.openElement(A);
		writer.addAttribute(HREF, link);
		ELinkTarget target = LinkProviderBase.obtainLinkTarget(node);
		if (target != null) {
			writer.addAttribute(TARGET, target.getValue());
		}
		writer.addAttribute(CLASS, LINK_CLASS);
		writer.addText(obtainDisplayName(node));
		writer.closeElement(A);
	}

	/** Determines the color of the given node. */
	private String getColor(IConQATNode baseNode) {
		Object value = baseNode.getValue(colorKey);
		if (value instanceof Color) {
			Color color = (Color) value;
			float[] hsbValues = Color.RGBtoHSB(color.getRed(),
					color.getGreen(), color.getBlue(), null);

			hsbValues[1] *= saturationMultiplier;
			hsbValues[2] = 1 - (1 - hsbValues[2]) * brightnessMultiplier;

			color = Color.getHSBColor(hsbValues[0], hsbValues[1], hsbValues[2]);

			return String.format("#%06X", color.getRGB() & 0xffffff);
		}
		return null;
	}

	/** Append all values for node to the table. */
	private void appendValues(IConQATNode node, DisplayList displayList) {
		IContextSensitiveFormatter<?> findingFormatter = new DefaultNodeAwareFindingFormatter(
				node);
		for (String key : displayList) {
			writer.openElement(TD, CLASS, CELL_CLASS);

			Object value = node.getValue(key);

			// flush right if it looks numeric
			if (value instanceof Number) {
				writer.addAttribute(ALIGN, "right");
			}

			PresentationUtils.appendValue(value, displayList.getFormatter(key),
					writer, getLogger(), findingFormatter);
			writer.closeElement(TD);
		}
	}

	/**
	 * If {@link #showIds} is enabled this returns the node id, otherwise the
	 * node name or the name given by {@link #nameFromKey}.
	 */
	private String obtainDisplayName(IConQATNode node) {
		if (showIds) {
			return node.getId();
		}
		if (!StringUtils.isEmpty(nameFromKey)) {
			return NodeUtils.getStringValue(node, nameFromKey, node.getName());
		}
		return node.getName();
	}

	/** {@inheritDoc} */
	@Override
	protected Object getSummary() {
		return NodeUtils.getSummary(input);
	}

	/** Returns {@inheritDoc}. */
	@Override
	protected String getIconName() {
		if (tableMode == ETableMode.TREE) {
			return "tree_table.gif";
		}
		return TABLE_ICON_NAME;
	}

	/** Enumeration describing the different modes. */
	public static enum ETableMode {

		/** Plain table without any JavaScript. */
		PLAIN,

		/** Table that supports sorting. */
		SORTABLE,

		/** Tree table. */
		TREE;
	}
}