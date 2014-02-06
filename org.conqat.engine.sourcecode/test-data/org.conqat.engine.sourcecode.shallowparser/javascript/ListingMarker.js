/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2012 the ConQAT Project                                   |
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

goog.provide('conqat.base.ListingMarker');

goog.require('goog.dom');
goog.require('goog.userAgent');
goog.require('goog.style');
goog.require('goog.ui.Tooltip');

/**
 * Provides the code for the interactive parts of listings.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 44076 $
 * @ConQAT.Rating GREEN Hash: 75FC9B26712597869F270BB83B469711
 */
conqat.base.ListingMarker;

/**
 * Static method for installing the listing markers.
 * 
 * @param {string} tooltipCss
 * @param {number} maxLine
 * @param {!Array} markers the markers to be rendered.
 */
conqat.base.ListingMarker.install = function(tooltipCss, maxLine, markers) {
	goog.ui.Tooltip.prototype.className = tooltipCss;

	var maxSlot = 0;
	for ( var i = 0; i < markers.length; ++i) {
		var marker = markers[i];
		if (marker['slot'] > maxSlot) {
			maxSlot = marker['slot'];
		}
	}

	// sort by start line
	markers.sort(function(m1, m2) {
		return m1['startLine'] - m2['startLine'];
	});

	var markerElements = [];
	var lineHeights = [];
	for ( var line = 1; line <= maxLine; ++line) {
		markerElements[line] = goog.dom.getElement('line' + line);
		lineHeights[line] = goog.style.getSize(markerElements[line].parentNode).height;
	}

	// detach DOM for increased performance (no upated during manipulation)
	var bodyFragment = goog.dom.getDocument().createDocumentFragment();
	var body = goog.dom.getElementsByTagNameAndClass('body')[0];
	while (body.firstChild) {
		bodyFragment.appendChild(body.firstChild);
	}

	var activeMarkers = [];
	var markerIndex = 0;
	for ( var line = 1; line <= maxLine; ++line) {
		// update active
		while (markerIndex < markers.length
				&& markers[markerIndex]['startLine'] == line) {
			activeMarkers[markers[markerIndex]['slot']] = markers[markerIndex];
			markerIndex += 1;
		}

		var markerElement = markerElements[line];
		goog.dom.removeChildren(markerElement);

		// for IE8, vertical-align: bottom does not work, so we use a hack with
		// negative margin
		if (goog.userAgent.IE && !goog.userAgent.isVersion(9)) {
			markerElement.style.marginBottom = "-4px";
		}

		var height = lineHeights[line];

		// include and remove completed
		var gap = 0;
		for ( var slot = 0; slot <= maxSlot; ++slot) {
			var marker = activeMarkers[slot];
			if (marker) {
				conqat.base.ListingMarker.addGap(markerElement, gap, height);
				gap = 0;

				var isFirst = marker['startLine'] == line;
				var isLast = marker['endLine'] == line;
				conqat.base.ListingMarker.insertMarkerPart(marker,
						markerElement, height, isFirst, isLast);

				if (isLast) {
					activeMarkers[slot] = null;
				}
			} else {
				gap += 1;
			}
		}
		conqat.base.ListingMarker.addGap(markerElement, gap, height);
	}

	// reattach DOM
	goog.dom.append(body, bodyFragment);
};

/**
 * Static method that inserts a gap.
 * 
 * @param {!Element} markerElement
 * @param {number} size
 * @param {number} height
 */
conqat.base.ListingMarker.addGap = function(markerElement, size, height) {
	if (size <= 0) {
		return;
	}

	var gapDiv = goog.dom.createDom('span');
	gapDiv.style.display = "inline-block";
	goog.style.setSize(gapDiv, 6 * size, height);

	goog.dom.appendChild(markerElement, gapDiv);
};

/**
 * Static method that inserts a line part of a marker.
 * 
 * @param {!Object} marker
 * @param {!Element} markerElement
 * @param {number} height
 * @param {boolean} isFirst
 * @param {boolean} isLast
 * @private
 */
conqat.base.ListingMarker.insertMarkerPart = function(marker, markerElement,
		height, isFirst, isLast) {
	var markerDiv = goog.dom.createDom('span');
	markerDiv.style.display = "inline-block";

	markerDiv.style.backgroundColor = marker['color'];

	if (isFirst) {
		height -= 2;
		markerDiv.style.marginTop = "2px";
	}
	if (isLast) {
		height -= 2;
		markerDiv.style.marginBottom = "2px";
	}

	goog.style.setSize(markerDiv, 4, height);
	markerDiv.style.marginLeft = "1px";
	markerDiv.style.marginRight = "1px";

	goog.dom.appendChild(markerElement, markerDiv);

	if (!marker.tooltip) {
		marker.tooltip = new goog.ui.Tooltip(markerDiv, marker['text']);
	} else {
		marker.tooltip.attach(markerDiv);
	}
};
