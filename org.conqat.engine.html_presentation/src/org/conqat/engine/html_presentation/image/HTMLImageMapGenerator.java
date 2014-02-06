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
package org.conqat.engine.html_presentation.image;

import static org.conqat.lib.commons.html.ECSSProperty.BACKGROUND_COLOR;
import static org.conqat.lib.commons.html.ECSSProperty.COLOR;
import static org.conqat.lib.commons.html.ECSSProperty.FILTER;
import static org.conqat.lib.commons.html.ECSSProperty.OPACITY;
import static org.conqat.lib.commons.html.ECSSProperty.POSITION;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.commons.findings.location.ElementLocation;
import org.conqat.engine.commons.format.IValueFormatter;
import org.conqat.engine.commons.node.DisplayList;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.html_presentation.CSSMananger;
import org.conqat.engine.html_presentation.javascript.base.BaseJSModule;
import org.conqat.engine.html_presentation.javascript.base.BaseJSModule.InteractiveMapArea;
import org.conqat.engine.html_presentation.javascript.base.BaseJSModule.InteractiveMapContainer;
import org.conqat.engine.html_presentation.links.LinkProviderBase;
import org.conqat.engine.html_presentation.util.NodeAwareFindingFormatterBase;
import org.conqat.engine.html_presentation.util.PresentationUtils;
import org.conqat.engine.html_presentation.util.PresentationUtils.IContextSensitiveFormatter;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.color.ColorUtils;
import org.conqat.lib.commons.html.CSSDeclarationBlock;
import org.conqat.lib.commons.html.HTMLWriter;
import org.conqat.lib.commons.string.StringUtils;

/**
 * This generates JavaScript-based tool-tips based on the information provided
 * by an {@link ITooltipDescriptor}.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 42730 $
 * @ConQAT.Rating GREEN Hash: 3914ACFCD4AE882A0FBC7542C7AA91DF
 */
public class HTMLImageMapGenerator {

	/** Style for DIVs used in tool-tips. */
	public final static CSSDeclarationBlock TOOLTIP_DIV = new CSSDeclarationBlock(
			CSSMananger.DARK_GRAY_BORDER, COLOR, "black", POSITION, "absolute",
			BACKGROUND_COLOR, "white", OPACITY, "0.8", FILTER,
			"alpha(opacity = 80)");

	/** CSS used to hightligh the active area in the map. */
	private final static CSSDeclarationBlock AREA_HIGHLIGHT_CSS = new CSSDeclarationBlock(
			POSITION, "absolute", BACKGROUND_COLOR, "white", OPACITY, "0.2");

	/** CSS used for even cells. */
	public final static CSSDeclarationBlock TOOLTIP_CELL_EVEN = new CSSDeclarationBlock(
			CSSMananger.TOOL_TIP_CELL, BACKGROUND_COLOR, "white");

	/** CSS used for odd cells. */
	public final static CSSDeclarationBlock TOOLTIP_CELL_ODD = CSSMananger.TOOL_TIP_CELL;

	/**
	 * Unique Id of this image. Images need unique ids in case several are on
	 * the same page.
	 */
	private final String imageId;

	/** Counter used to generate fresh ids */
	private static int idCounter;

	/** Keys to display in tool tip. */
	private final DisplayList displayList;

	/**
	 * Color for dynamic frames. Frames are turned off if this is
	 * <code>null</code>.
	 */
	private final Color frameColor;

	/** The tooltip descriptor. */
	private final ITooltipDescriptor<Object> tooltipDescriptor;

	/**
	 * Create new generator.
	 * 
	 * @param frameColor
	 *            defines the color used for dynamically highlighting the area
	 *            the tool tip belongs to (highlighting can be turned off by
	 *            setting this to <code>null</code>).
	 */
	public HTMLImageMapGenerator(Color frameColor,
			ITooltipDescriptor<Object> tooltipDescriptor) {
		this.displayList = tooltipDescriptor.getDisplayList();
		this.frameColor = frameColor;
		this.imageId = "interactive_map" + idCounter++;
		this.tooltipDescriptor = tooltipDescriptor;
	}

	/**
	 * Generate (write to the writer) the Java script code for the interactive
	 * map .
	 */
	public String generateJS() {

		List<InteractiveMapContainer> containers = new ArrayList<InteractiveMapContainer>();
		List<InteractiveMapArea> areas = new ArrayList<InteractiveMapArea>();

		fillContainersAndAreas(tooltipDescriptor.getRoot(), null, containers,
				areas);

		CSSDeclarationBlock containerFrameCss = null;
		if (frameColor != null) {
			containerFrameCss = new CSSDeclarationBlock(POSITION, "absolute")
					.setBorder("3px", "solid",
							ColorUtils.toHtmlString(frameColor));
		}

		CSSDeclarationBlock[] css = new CSSDeclarationBlock[] { TOOLTIP_DIV,
				CSSMananger.TOOL_TIP_CAPTION, TOOLTIP_CELL_EVEN,
				TOOLTIP_CELL_ODD, AREA_HIGHLIGHT_CSS, containerFrameCss };

		return BaseJSModule
				.installInteractiveMap(imageId, css, CollectionUtils.toArray(
						displayList.getKeyList(), String.class),
						CollectionUtils.toArray(containers,
								InteractiveMapContainer.class), CollectionUtils
								.toArray(areas, InteractiveMapArea.class));
	}

	/** Fills the data structures for containers and areas. */
	private void fillContainersAndAreas(Object node, String parent,
			List<InteractiveMapContainer> containers,
			List<InteractiveMapArea> areas) {
		Rectangle2D dimensions = tooltipDescriptor.obtainBounds(node);

		if (tooltipDescriptor.hasChildren(node)) {
			if (tooltipDescriptor.isTooltipsForInnerNodes()) {
				areas.add(generateArea(node, parent, dimensions));
			}
			containers.add(new InteractiveMapContainer(dimensions,
					tooltipDescriptor.obtainId(node), parent));
			for (Object child : tooltipDescriptor.obtainChildren(node)) {
				String parentNode = tooltipDescriptor.obtainId(node);
				fillContainersAndAreas(child, parentNode, containers, areas);
			}
		} else {
			areas.add(generateArea(node, parent, dimensions));
		}

	}

	/** Generates an area from a node. */
	private InteractiveMapArea generateArea(Object node, String parent,
			Rectangle2D dimensions) {

		IContextSensitiveFormatter<?> findingFormatter = createFindingFormatter(node);
		String[] values = new String[displayList.size()];
		for (int i = 0; i < displayList.size(); ++i) {
			values[i] = formatValue(node, displayList.getKeyList().get(i),
					findingFormatter);
		}

		Object link = tooltipDescriptor.obtainValue(node,
				LinkProviderBase.LINK_KEY);
		if (!(link instanceof String)) {
			link = null;
		}

		return new InteractiveMapArea(dimensions,
				tooltipDescriptor.obtainId(node), parent, values, (String) link);
	}

	/** Creates a finding formatter to use for the given node. */
	private IContextSensitiveFormatter<?> createFindingFormatter(Object node) {
		IConQATNode conqatNode = null;
		if (node instanceof IConQATNode) {
			conqatNode = (IConQATNode) node;
		}

		return new NodeAwareFindingFormatterBase(conqatNode) {
			@Override
			protected String determineUrl(ElementLocation location) {
				// always return null as we do not want links in the tooltips.
				return null;
			}
		};
	}

	/** Format the value stored at node. */
	private String formatValue(Object node, String key,
			IContextSensitiveFormatter<?> findingFormatter) {
		Object value = tooltipDescriptor.obtainValue(node, key);
		if (value == null) {
			return StringUtils.EMPTY_STRING;
		}
		StringWriter content = new StringWriter();
		HTMLWriter writer = new HTMLWriter(new PrintWriter(content),
				CSSMananger.getInstance());

		IValueFormatter formatter = displayList.getFormatter(key);
		if (formatter != null) {
			try {
				value = formatter.format(value);
			} catch (ConQATException e) {
				// as we have no logger here, just ignore formatting problems in
				// such a case
			}
		}

		PresentationUtils.appendValue(value, writer, findingFormatter);
		writer.close();
		return content.toString();
	}

	/** @return imageId of this interactive map */
	public String getImageId() {
		return imageId;
	}
}