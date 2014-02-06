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

goog.provide('conqat.base.InteractiveMapBase');

goog.require('goog.Disposable');
goog.require('goog.dom');
goog.require('goog.events');
goog.require('goog.events.EventType');
goog.require('goog.math.Coordinate');
goog.require('goog.math.Box');

/**
 * Base class for interactive maps.
 * 
 * @constructor
 * @extends {goog.Disposable}
 * 
 * @param {Element} containerElement The DOM element to insert the treemap into.
 * @param {Element} eventElement The DOM element to listen for mouse events on.
 * @param {Array.<string>} cssClasses CSS classes for tool tips.
 * 
 * @ConQAT.Rating GREEN Hash: 358C50E65E86EE1EB999F2C021670895
 */
conqat.base.InteractiveMapBase = function(containerElement, eventElement,
		cssClasses) {

	goog.base(this);

	/**
	 * The container element.
	 * 
	 * @type {Element}
	 * @private
	 */
	this.containerElement = containerElement;

	/**
	 * The element for mouse event listening.
	 * 
	 * @type {Element}
	 * @private
	 */
	this.eventElement = eventElement;

	/**
	 * The id of the currently focused area.
	 * 
	 * @type {?string}
	 * @private
	 */
	this.focusedAreaId = null;

	/**
	 * The DIV element used for showing the tool tip.
	 * 
	 * @type {!Element}
	 * @protected
	 */
	this.tooltipElement = goog.dom.createDom('div', {
		'class' : cssClasses[0]
	});
	goog.style.showElement(this.tooltipElement, false);

	var bodyElement = goog.dom.getDocument().body;
	goog.dom.appendChild(bodyElement, this.tooltipElement);

	/**
	 * The styles we have to install in the tool tip template.
	 * 
	 * @type {!Object}
	 * @private
	 */
	this.tooltipCss = {
		titleCss : cssClasses[1],
		evenRowCss : cssClasses[2],
		oddRowCss : cssClasses[3]
	};

	goog.events.listen(this.eventElement, goog.events.EventType.MOUSEDOWN,
			conqat.base.InteractiveMapBase.prototype.mouseDownHandler, false,
			this);

	goog.events.listen(this.eventElement, goog.events.EventType.MOUSEMOVE,
			conqat.base.InteractiveMapBase.prototype.mouseMoveHandler, false,
			this);
};
goog.inherits(conqat.base.InteractiveMapBase, goog.Disposable);

/**
 * Handler for the mouse down event. Opens the URL that is saved as the
 * <code>link</code> property of the area object under the mouse, if any.
 * 
 * @param {!goog.events.BrowserEvent} event
 * @private
 */
conqat.base.InteractiveMapBase.prototype.mouseDownHandler = function(event) {
	var documentPosition = this.calculateDocumentCoordinates(event);
	if (!this.isInContainer(documentPosition)) {
		return;
	}

	var coordinates = this.calculateContainerCoordinates(documentPosition);
	var area = this.findArea(coordinates.x, coordinates.y);

	if (area == null || !goog.isDefAndNotNull(this.getLink(area))) {
		return;
	}

	if (event.isButton(goog.events.BrowserEvent.MouseButton.MIDDLE)) {
		window.open(area.link);
	} else if (event.isButton(goog.events.BrowserEvent.MouseButton.LEFT)) {
		window.location = area.link;
	}
};

/**
 * Return link to code listing for a node. Subclasses may override if this link
 * needs to be determined dynamically.
 * 
 * @returns {string}
 */
conqat.base.InteractiveMapBase.prototype.getLink = function(area) {
	return area['link'];
};

/**
 * Handler for the mouse move event. Handles showing/hiding tool tips and
 * highlighting the area under the mouse.
 * 
 * @param {!goog.events.BrowserEvent} event
 * @private
 */
conqat.base.InteractiveMapBase.prototype.mouseMoveHandler = function(event) {
	var documentPosition = this.calculateDocumentCoordinates(event);
	if (!this.isInContainer(documentPosition)) {
		this.hideTooltipAndHighlight();
		return;
	}

	var coordinates = this.calculateContainerCoordinates(documentPosition);
	if (!coordinates) {
		this.hideTooltipAndHighlight();
		return;
	}

	var area = this.findArea(coordinates.x, coordinates.y);
	if (!goog.isDefAndNotNull(area)) {
		this.hideTooltipAndHighlight();
		return;
	}
	if (this.focusedAreaId != this.getAreaId(area)) {
		this.focusedAreaId = this.getAreaId(area);
		this.fillTooltip(area);
		this.highlightArea(area, false);
	}
	this.moveTooltipTo(documentPosition.x, documentPosition.y);
};

/**
 * Returns the id used to uniquely identify the area object. Default
 * implementation returns the id property.
 * 
 * @param {Object} area an object identifying an area as returned by the
 *            findArea() method (which is abstract, so the exact type depends on
 *            the subclass).
 * @return {string}
 * @protected
 */
conqat.base.InteractiveMapBase.prototype.getAreaId = function(area) {
	return area.id;
}

/**
 * Checks whether a given document position is within the container.
 * 
 * @param {!goog.math.Coordinate} documentPosition
 * @return {boolean}
 * @private
 */
conqat.base.InteractiveMapBase.prototype.isInContainer = function(
		documentPosition) {
	var containerPosition = goog.style.getPageOffset(this.containerElement);
	var containerSize = goog.style.getSize(this.containerElement);

	return documentPosition.x >= containerPosition.x
			&& documentPosition.x < containerPosition.x + containerSize.width
			&& documentPosition.y >= containerPosition.y
			&& documentPosition.y < containerPosition.y + containerSize.height;
};

/**
 * Hides the tool tip and the area highlight.
 * 
 * @protected
 */
conqat.base.InteractiveMapBase.prototype.hideTooltipAndHighlight = function() {
	if (goog.isDefAndNotNull(this.focusedAreaId)) {
		this.focusedAreaId = null;
		goog.style.showElement(this.tooltipElement, false);
		this.hideAreaHighlight();
	}
};

/**
 * Hide the area highlight.
 * 
 * @protected
 */
conqat.base.InteractiveMapBase.prototype.hideAreaHighlight = goog.abstractMethod;

/**
 * Fill the tooltip based on data from the given object.
 * 
 * @param {!Object} data
 * @protected
 */
conqat.base.InteractiveMapBase.prototype.fillTooltip = goog.abstractMethod;

/**
 * Highlight an area.
 * 
 * @protected
 * 
 * @param {!Object} area
 * @param {boolean} recursiveCall
 */
conqat.base.InteractiveMapBase.prototype.highlightArea = goog.abstractMethod;

/**
 * Finds the most specific area record that contains the given coordinate, i. e.
 * the one that has the deepest level in the area tree.
 * 
 * @protected
 * 
 * @param {number} x
 * @param {number} y
 * @returns {Object|undefined}
 */
conqat.base.InteractiveMapBase.prototype.findArea = goog.abstractMethod;

/**
 * Moves the tool tip to the provided cursor position.
 * 
 * @private
 * 
 * @param {number} x
 * @param {number} y
 */
conqat.base.InteractiveMapBase.prototype.moveTooltipTo = function(x, y) {
	var cursorPosition = new goog.math.Coordinate(x, y);
	var viewport = goog.style.getVisibleRectForElement(goog.style
			.getClientViewportElement(this.tooltipElement));
	var marginBox = new goog.math.Box(15, 15, 15, 15);

	goog.positioning.positionAtCoordinate(cursorPosition, this.tooltipElement,
			goog.positioning.Corner.TOP_LEFT, marginBox, viewport,
			goog.positioning.Overflow.ADJUST_X
					| goog.positioning.Overflow.ADJUST_Y);
};

/**
 * Calculates the position of the mouse relative to the document.
 * 
 * @param {!goog.events.BrowserEvent} event
 * @returns {!goog.math.Coordinate} The calculated coordinates.
 */
conqat.base.InteractiveMapBase.prototype.calculateDocumentCoordinates = function(
		event) {
	var documentPosition = new goog.math.Coordinate(event.clientX,
			event.clientY);
	documentPosition = goog.math.Coordinate.sum(documentPosition, goog.dom
			.getDocumentScroll());
	return documentPosition;
};

/**
 * Calculates the position within the container from the position within the
 * document.
 * 
 * @param {goog.math.Coordinate} documentPosition
 * @returns {goog.math.Coordinate} The calculated coordinate.
 */
conqat.base.InteractiveMapBase.prototype.calculateContainerCoordinates = function(
		documentPosition) {
	var containerPosition = goog.style.getPageOffset(this.containerElement);
	var x = documentPosition.x - containerPosition.x - 2;
	var y = documentPosition.y - containerPosition.y - 2;
	return new goog.math.Coordinate(x, y);
};

/**
 * Unlisten mouse events.
 * 
 * @override
 */
conqat.base.InteractiveMapBase.prototype.disposeInternal = function() {
	goog.events.unlisten(this.eventElement, goog.events.EventType.MOUSEDOWN,
			conqat.base.InteractiveMapBase.prototype.mouseDownHandler, false,
			this);
	goog.events.unlisten(this.eventElement, goog.events.EventType.MOUSEMOVE,
			conqat.base.InteractiveMapBase.prototype.mouseMoveHandler, false,
			this);
	goog.dom.removeNode(this.tooltipElement);
};
