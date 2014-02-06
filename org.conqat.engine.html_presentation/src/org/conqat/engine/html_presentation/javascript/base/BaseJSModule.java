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
package org.conqat.engine.html_presentation.javascript.base;

import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.html_presentation.javascript.JavaScriptGenerationUtils;
import org.conqat.engine.html_presentation.javascript.JavaScriptModuleBase;
import org.conqat.engine.html_presentation.listing.ListingMarkerDescriptor;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.assessment.AssessmentUtils;
import org.conqat.lib.commons.assessment.ETrafficLightColor;
import org.conqat.lib.commons.color.ColorUtils;
import org.conqat.lib.commons.html.CSSDeclarationBlock;

/**
 * This module contains code/scripts for basic ConQAT functionality (tables,
 * trees, tooltips).
 * 
 * @author $Author: heinemann $
 * @version $Rev: 43168 $
 * @ConQAT.Rating GREEN Hash: C402D6DFB1CFA452DB6A14A4B88C7A6C
 */
public class BaseJSModule extends JavaScriptModuleBase {

	/** The name of the function used for installing the navigation page script. */
	private static final String NAVIGATION_PAGE_INSTALL_CALL = "conqat.base.NavigationPage.install";

	/** The name of the function used for installing the goto box script. */
	private static final String GOTOBOX_INSTALL_CALL = "conqat.base.GotoBox.install";

	/** The name of the function used for installing the goto box script. */
	private static final String LISTING_MARKER_INSTALL_CALL = "conqat.base.ListingMarker.install";

	/** The name of the function used for installing the interactive map script. */
	private static final String INTERACTIVE_MAP_INSTALL_CALL = "conqat.base.InteractiveMap.install";

	/** The name of the function used for making a table sortable. */
	private static final String MAKE_SORTABLE_TABLE_CALL = "conqat.base.TableSupport.makeSortable";

	/** The name of the function used for making a table tree-like. */
	private static final String MAKE_TREE_TABLE_CALL = "conqat.base.TableSupport.makeTree";

	/** The name of the function used for registering table filters. */
	private static final String REGISTER_TABLE_FILTERS_CALL = "conqat.base.TableSupport.registerFilters";

	/** The name of the function used for loading content. */
	private static final String LOAD_CONTENT_CALL = "conqat.base.Utils.loadContent";

	/** The name of the function used toggling the visibilty of an element. */
	private static final String TOGGLE_VISIBILITY_CALL = "conqat.base.Utils.toggleVisibility";

	/** The name of the function used for showing a single element. */
	private static final String SHOW_ELEMENT_CALL = "conqat.base.Utils.showElement";

	/** The name of the function used for showing all elements on a page. */
	private static final String SHOW_ALL_CALL = "conqat.base.Utils.showAll";

	/** {@inheritDoc} */
	@Override
	protected void createJavaScriptFiles() throws ConQATException {
		registerExports("NavigationPage.js", NAVIGATION_PAGE_INSTALL_CALL);
		registerExports("GotoBox.js", GOTOBOX_INSTALL_CALL);
		registerExports("ListingMarker.js", LISTING_MARKER_INSTALL_CALL);
		registerExports("InteractiveMap.js", INTERACTIVE_MAP_INSTALL_CALL);
		registerExports("TableSupport.js", MAKE_SORTABLE_TABLE_CALL,
				MAKE_TREE_TABLE_CALL, REGISTER_TABLE_FILTERS_CALL);
		registerExports("Utils.js", LOAD_CONTENT_CALL, TOGGLE_VISIBILITY_CALL,
				SHOW_ELEMENT_CALL, SHOW_ALL_CALL);

		addCustomJavaScriptAndSoyFromCurrentPackage();

		addTrafficLightColors();
	}

	/** Adds the {@link ETrafficLightColor} enum with correct colors attached. */
	private void addTrafficLightColors() {
		LinkedHashMap<String, Map<String, String>> values = new LinkedHashMap<String, Map<String, String>>();
		for (ETrafficLightColor color : ETrafficLightColor.values()) {
			Map<String, String> descriptor = new HashMap<String, String>();
			descriptor.put("color",
					ColorUtils.toHtmlString(AssessmentUtils.getColor(color)));
			descriptor.put("name", color.name());
			values.put(color.name(), descriptor);
		}
		addGeneratedJavaScript(ETrafficLightColor.class,
				JavaScriptGenerationUtils.generateEnumJS(values,
						"conqat.base.ETrafficLightColor"));
	}

	/**
	 * Returns the JavaScript code used to install the navigation page.
	 * 
	 * @param treeId
	 *            the id of the main navigation tree.
	 * 
	 * @param expandAllId
	 *            id of the expand all button.
	 * @param collapseAllId
	 *            id of the collapse all button.
	 * @param openIcon
	 *            the url of the icon used for open groups.
	 * @param closeIcon
	 *            the url of the icon used for closed groups.
	 */
	public static String installNavigationPage(String treeId,
			String expandAllId, String collapseAllId, String openIcon,
			String closeIcon) {
		return javaScriptCall(NAVIGATION_PAGE_INSTALL_CALL, treeId,
				expandAllId, collapseAllId, openIcon, closeIcon);
	}

	/**
	 * Returns the JavaScript code used to install the goto box.
	 * 
	 * @param textId
	 *            id of the text element used to input the target.
	 * @param buttonId
	 *            id of the button used to start navigation.
	 * @param cssClasses
	 *            the CSS classes to be used (render, row, active, highlighted)
	 * @param links
	 *            mapping from strings to URLs.
	 */
	public static String installGotoBox(String textId, String buttonId,
			CSSDeclarationBlock[] cssClasses, Map<String, String> links) {
		CCSMAssert.isTrue(cssClasses.length == 4,
				"Expecting exactly 4 classes!");
		return javaScriptCall(GOTOBOX_INSTALL_CALL, textId, buttonId,
				cssClasses, links);
	}

	/** Returns the JavaScript code used to install listing markers. */
	public static String installListingMarkers(String tooltipClass,
			int numLines, List<ListingMarkerDescriptor> markers) {
		return javaScriptCall(LISTING_MARKER_INSTALL_CALL, tooltipClass,
				numLines, markers);
	}

	/**
	 * Returns the JavaScript code used to make a table sortable.
	 * 
	 * @param tableId
	 *            the id of the table.
	 * @param oddRowCSS
	 *            the CSS for odd rows (may be null to disable explicit
	 *            striping).
	 * @param evenRowCSS
	 *            the CSS for even rows (may be null to disable explicit
	 *            striping).
	 */
	public static String makeTableSortable(String tableId,
			CSSDeclarationBlock oddRowCSS, CSSDeclarationBlock evenRowCSS) {
		return javaScriptCall(MAKE_SORTABLE_TABLE_CALL, tableId, oddRowCSS,
				evenRowCSS);
	}

	/**
	 * Returns the JavaScript code used to make a table tree-like.
	 * 
	 * @param tableId
	 *            the id of the table.
	 * @param parentRows
	 *            a list that provides for each row the 0-based index of the
	 *            parent row.
	 * @param oddRowCSS
	 *            the CSS for odd rows (may be null to disable explicit
	 *            striping).
	 * @param evenRowCSS
	 *            the CSS for even rows (may be null to disable explicit
	 *            striping).
	 */
	public static String makeTableTreelike(String tableId,
			List<Integer> parentRows, CSSDeclarationBlock oddRowCSS,
			CSSDeclarationBlock evenRowCSS) {
		return javaScriptCall(MAKE_TREE_TABLE_CALL, tableId, parentRows,
				oddRowCSS, evenRowCSS);
	}

	/**
	 * Returns the JavaScript code for registering filters.
	 * 
	 * @param tableId
	 *            the id of the table.
	 * @param parentRows
	 *            a list that provides for each row the 0-based index of the
	 *            parent row. This may be empty if this is not a tree table.
	 * @param filterInputClass
	 *            the CSS class used to mark the filter inputs.
	 * @param oddRowCSS
	 *            the CSS for odd rows (may be null to disable explicit
	 *            striping).
	 * @param evenRowCSS
	 *            the CSS for even rows (may be null to disable explicit
	 *            striping).
	 */
	public static String registerTableFilters(String tableId,
			List<Integer> parentRows, CSSDeclarationBlock filterInputClass,
			CSSDeclarationBlock oddRowCSS, CSSDeclarationBlock evenRowCSS) {
		return javaScriptCall(REGISTER_TABLE_FILTERS_CALL, tableId, parentRows,
				filterInputClass, oddRowCSS, evenRowCSS);
	}

	/**
	 * Returns JavaScript code that can be used to replace the content of an
	 * element with text loaded from a file.
	 * 
	 * @param id
	 *            the id of the element whose content is replaced.
	 * @param fileURL
	 *            the name/URL of the file.
	 */
	public static String loadContent(String id, String fileURL) {
		return javaScriptCall(LOAD_CONTENT_CALL, id, fileURL);
	}

	/**
	 * Returns JavaScript code that toggles the visibility of the element with
	 * given id.
	 */
	public static String toggleVisibility(String id) {
		return javaScriptCall(TOGGLE_VISIBILITY_CALL, id);
	}

	/** Returns JavaScript code that make the element with given id visible. */
	public static String showElement(String id) {
		return javaScriptCall(SHOW_ELEMENT_CALL, id);
	}

	/** Returns JavaScript code that can be used to show all elements on a page. */
	public static String showAll() {
		return javaScriptCall(SHOW_ALL_CALL);
	}

	/**
	 * Returns the JavaScript code used to install an interactive image map.
	 * 
	 * @param containerId
	 *            the id of the image map container (typically an image),
	 * @param cssClasses
	 *            array of CSS class names used (tooltipDiv, tooltipTitle,
	 *            tooltipEvenRow, tooltipOddRow, areaHighlight, containerFrame
	 *            (may be null)).
	 * @param keys
	 *            the list of keys displayed in the tooltip.
	 * @param containers
	 *            the containers.
	 * @param areas
	 *            the active areas.
	 */
	public static String installInteractiveMap(String containerId,
			CSSDeclarationBlock[] cssClasses, String[] keys,
			InteractiveMapContainer[] containers, InteractiveMapArea[] areas) {
		CCSMAssert.isTrue(cssClasses.length == 6,
				"Expecting exactly 6 classes!");
		return javaScriptCall(INTERACTIVE_MAP_INSTALL_CALL, containerId,
				cssClasses, keys, containers, areas);
	}

	/**
	 * Utility class describing a container in an interactive map. The names of
	 * the fields are important as they are used during serialization.
	 */
	@SuppressWarnings("unused")
	public static class InteractiveMapContainer {

		/** The first (left) x coordinate. */
		private final int x;

		/** The first (top) y coordinate. */
		private final int y;

		/** The second (right) x coordinate. */
		private final int x2;

		/** The second (bottom) y coordinate. */
		private final int y2;

		/**
		 * Id of this container. Also used as title for
		 * {@link InteractiveMapArea}
		 */
		private final String id;

		/** Id of the parent container (may be null). */
		private final String parentId;

		/** Constructor. */
		public InteractiveMapContainer(Rectangle2D bounds, String id,
				String parentId) {
			x = (int) Math.round(bounds.getMinX());
			y = (int) Math.round(bounds.getMinY());
			x2 = (int) Math.round(bounds.getMaxX());
			y2 = (int) Math.round(bounds.getMaxY());
			this.id = id;
			this.parentId = parentId;
		}
	}

	/**
	 * Utility class describing an area in an interactive map. The names of the
	 * fields are important as they are used during serialization.
	 */
	@SuppressWarnings("unused")
	public static final class InteractiveMapArea extends
			InteractiveMapContainer {

		/** The values used. */
		private final String[] values;

		/** The link (may be null). */
		private final String link;

		/** Constructor. */
		public InteractiveMapArea(Rectangle2D bounds, String id,
				String parentId, String[] values, String link) {
			super(bounds, id, parentId);
			this.values = values;
			this.link = link;
		}
	}
}
