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
package org.conqat.engine.html_presentation.listing;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.conqat.engine.html_presentation.util.JavaScriptUtils;
import org.conqat.lib.commons.color.ColorUtils;

/**
 * Describes a marker. Besides the start/end line and color, we also manage a
 * "slot". As markers cover multiple lines, we can have overlapping markers.
 * Such markers are drawn next to each other. This side-by-side position is
 * denoted as "slot" here.
 * <p>
 * Note that this class gets serialized to JSON, so changing attribute names
 * also affects JavaScript code.
 * 
 * @author $Author: goede $
 * @version $Rev: 41731 $
 * @ConQAT.Rating GREEN Hash: E3D845E2E33DEFA2DDE7E9548D29BE60
 */
public final class ListingMarkerDescriptor implements
		Comparable<ListingMarkerDescriptor> {

	/** The horizontal offset of the marker. */
	private int slot;

	/** The start line (inclusive). */
	private final int startLine;

	/** The end line (inclusive). */
	private final int endLine;

	/** The color. */
	private final Color color;

	/** The tooltip text. */
	private final String text;

	/** Constructor. */
	public ListingMarkerDescriptor(int startLine, int endLine, Color color,
			String text) {
		this.startLine = startLine;
		this.endLine = endLine;
		this.color = color;
		this.text = text;
	}

	/** Compares by start line only. */
	@Override
	public int compareTo(ListingMarkerDescriptor other) {
		return startLine - other.startLine;
	}

	/** Returns the JavaScript code for creating the marker. */
	public String getMarkerJavaScript() {
		return "addMarker(" + slot + "," + startLine + "," + endLine + ",'"
				+ ColorUtils.toHtmlString(color) + "',\""
				+ JavaScriptUtils.escapeJavaScript(text) + "\");";
	}

	/**
	 * Fills the slot fields of the given markers. For this the list will be
	 * sorted. The algorithm used is slightly naive and assumes that the number
	 * of overlapping markers will be not too large.
	 * 
	 * @return the number of horizontal slots used.
	 */
	public static int calculateSlots(List<ListingMarkerDescriptor> markers) {
		int slots = 0;
		Collections.sort(markers);

		List<ListingMarkerDescriptor> activeMarkers = new ArrayList<ListingMarkerDescriptor>();
		for (ListingMarkerDescriptor marker : markers) {
			filterActiveMarkers(activeMarkers, marker.startLine);

			int slot = 0;
			while (isUsedSlot(slot, activeMarkers)) {
				slot += 1;
			}
			marker.slot = slot;
			activeMarkers.add(marker);

			slots = Math.max(slots, slot + 1);
		}

		return slots;
	}

	/** Returns whether the current slot is in use. */
	private static boolean isUsedSlot(int slot,
			List<ListingMarkerDescriptor> activeMarkers) {
		for (ListingMarkerDescriptor marker : activeMarkers) {
			if (marker.slot == slot) {
				return true;
			}
		}
		return false;
	}

	/** Removes all markers that have ended before the current line. */
	private static void filterActiveMarkers(
			List<ListingMarkerDescriptor> markers, int currentLine) {
		for (int i = markers.size() - 1; i >= 0; i--) {
			if (markers.get(i).endLine < currentLine) {
				markers.remove(i);
			}
		}
	}
}
