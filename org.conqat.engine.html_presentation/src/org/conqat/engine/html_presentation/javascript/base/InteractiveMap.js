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

goog.provide('conqat.base.InteractiveMap');

goog.require('conqat.base.InteractiveMapBase');
goog.require('conqat.base.InteractiveMapTemplate');

goog.require('goog.soy');
goog.require('goog.object');
goog.require('goog.dom');
goog.require('goog.dom.classes');
goog.require('goog.style');
goog.require('goog.positioning');
goog.require('goog.events');
goog.require('goog.events.EventType');
goog.require('goog.math.Coordinate');
goog.require('goog.math.Box');

/**
 * Class implementing interactive maps, i.e. areas with tooltip support.
 * 
 * @constructor
 * @extends {conqat.base.InteractiveMapBase}
 * 
 * @param {string} containerId the id of the image map container (typically an
 *            image)
 * @param {!Array.<string>} cssClasses array of CSS class names used
 *            (tooltipDiv, tooltipTitle, tooltipEvenRow, tooltipOddRow,
 *            areaHighlight, containerFrame (may be null)).
 * @param {!Array.<string>} keys the list of keys displayed in the tooltip.
 * @param {!Array} containers the containers.
 * @param {!Array} areas the active areas.
 * 
 * @ConQAT.Rating GREEN Hash: 181F7417005BAABD9D8809AAD940D5CF
 */
conqat.base.InteractiveMap = function(containerId, cssClasses, keys,
		containers, areas) {

	// super constructor
	goog.base(this, goog.dom.getElement(containerId),
			goog.dom.getDocument().body, cssClasses);

	/**
	 * The name of the CSS class for highlighting the current area.
	 * 
	 * @type {string}
	 * @private
	 */
	this.areaHighlightCss = cssClasses[4];

	/**
	 * The name of the CSS class for displaying the container frames (may be
	 * null to indicate that no frames should be drawn).
	 * 
	 * @type {?string}
	 * @private
	 */
	this.containerFrameCss = null;
	if (cssClasses[5]) {
		this.containerFrameCss = cssClasses[5];
	}

	/**
	 * The displayed keys of the nodes.
	 * 
	 * @type {!Array.<string>}
	 * @private
	 */
	this.keys = keys;

	/**
	 * Map to find parent containers by id.
	 * 
	 * @type {!Object.<string, Object>}
	 * @private
	 */
	this.parentContainers = {};
	for ( var i = 0; i < containers.length; ++i) {
		this.parentContainers[containers[i]['id']] = containers[i];
	}

	/**
	 * The areas of the map.
	 * 
	 * @type {!Array}
	 * @private
	 */
	this.areas = areas;

	/**
	 * The DIV element used for grouping the container frames.
	 * 
	 * @type {!Element}
	 * @private
	 */
	this.containerFramesRoot = goog.dom.createDom('div');

	var bodyElement = goog.dom.getDocument().body;
	goog.dom.insertChildAt(bodyElement, this.containerFramesRoot, 0);
};
goog.inherits(conqat.base.InteractiveMap, conqat.base.InteractiveMapBase);

/**
 * Fill the tool tip based on data from the given area descriptor.
 * 
 * @param {!Object} area
 * @protected
 * @override
 */
conqat.base.InteractiveMap.prototype.fillTooltip = function(area) {
	var params = {
		title : area.id,
		keys : [],
		values : []
	};
	goog.object.extend(params, this.tooltipCss);

	// filter to include only keys where the value is non-empty
	for ( var i = 0; i < this.keys.length; ++i) {
		if (area.values[i] != null && area.values[i] !== '') {
			params.keys.push(this.keys[i]);
			params.values.push(area.values[i]);
		}
	}

	goog.soy.renderElement(this.tooltipElement,
			conqat.base.InteractiveMapTemplate.tooltip, params);
	goog.style.showElement(this.tooltipElement, true);
};

/**
 * Highlight an area.
 * 
 * @protected
 * @override
 * 
 * @param {!Object} area
 * @param {boolean} recursiveCall
 */
conqat.base.InteractiveMap.prototype.highlightArea = function(area,
		recursiveCall) {
	if (!this.containerFrameCss) {
		return;
	}

	if (!recursiveCall) {
		this.hideAreaHighlight();

		var div = goog.dom.createElement('div');
		goog.dom.classes.add(div, this.areaHighlightCss);
		goog.style.setOpacity(div, 0.2);
		goog.style.setPosition(div, this.containerPosition.x + area.x + 2,
				this.containerPosition.y + area.y + 2);
		goog.style.setSize(div, area.x2 - area.x - 2, area.y2 - area.y - 2);
		goog.dom.appendChild(this.containerFramesRoot, div);
	}

	// outline parent containers
	if ((goog.isDefAndNotNull(area.parentId) && area.parentId !== '')
			|| !recursiveCall) {
		var div = goog.dom.createElement('div');
		goog.dom.classes.add(div, this.containerFrameCss);
		goog.style.setPosition(div, this.containerPosition.x + area.x,
				this.containerPosition.y + area.y);
		goog.style.setSize(div, area.x2 - area.x - 4, area.y2 - area.y - 4);
		goog.dom.appendChild(this.containerFramesRoot, div);

		var parent = this.parentContainers[area.parentId];
		if (parent) {
			this.highlightArea(parent, true);
		}
	}
};

/**
 * Hides the highlight.
 * 
 * @protected
 * @override
 */
conqat.base.InteractiveMap.prototype.hideAreaHighlight = function() {
	goog.dom.removeChildren(this.containerFramesRoot);
};

/**
 * Finds the most specific area record that contains the given coordinate, i.e.
 * the one that has the deepest level in the area tree. The algorithm assumes
 * that if two areas overlap, that one is an ancestor of the other in the
 * underlying tree.
 * 
 * @protected
 * @override
 * 
 * @returns {Object|undefined} The area record that was found or
 *          <code>undefined</code>.
 */
conqat.base.InteractiveMap.prototype.findArea = function(x, y) {
	var self = this;
	// find all areas that contain the point
	var matchingAreas = goog.array.filter(this.areas, function(area) {
		return area.x <= x && area.y <= y && x <= area.x2 && y <= area.y2;
	});
	// sort areas according to the ancestor relationship
	goog.array.sort(matchingAreas, function(area1, area2) {
		if (area1 == area2) {
			return 0;
		}
		var currentArea = area1;
		while (currentArea) {
			if (currentArea == area2) {
				return -1;
			}
			currentArea = self.parentContainers[currentArea.parentId];
		}
		return 1;
	});

	// return the lowest node in the ancestor path
	return matchingAreas[0];
};

/**
 * Returns the JavaScript code used to install an interactive image map.
 * 
 * @param {string} containerId the id of the image map container (typically an
 *            image),
 * @param {!Array.<string>} cssClasses array of CSS class names used
 *            (tooltipDiv, tooltipTitle, tooltipEvenRow, tooltipOddRow,
 *            areaHighlight, containerFrame (may be null)).
 * @param {!Array.<string>} keys the list of keys displayed in the tooltip.
 * @param {!Array} containers the containers.
 * @param {!Array} areas the active areas.
 */
conqat.base.InteractiveMap.install = function(containerId, cssClasses, keys,
		containers, areas) {
	new conqat.base.InteractiveMap(containerId, cssClasses, keys, containers,
			areas);
};
