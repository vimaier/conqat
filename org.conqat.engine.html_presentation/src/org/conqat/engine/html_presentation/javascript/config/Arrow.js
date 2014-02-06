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

goog.provide('conqat.config.Arrow');

goog.require('conqat.config.EditPartBase');

/**
 * Represents a single drawable arrow between two EditParts on the config graph.
 * 
 * @ConQAT.Rating GREEN Hash: F3D01404845F82F4F1D0E6C389511725
 * @constructor
 * @param {conqat.config.EditPartBase} startPart The part from which the arrow
 *            originates.
 * @param {conqat.config.EditPartBase} endPart The part to which the arrow
 *            points.
 * @param {boolean} invisible Whether the arrow is invisible (i.e. dotted).
 */
conqat.config.Arrow = function(startPart, endPart, invisible) {
	/**
	 * The part from which the arrow originates.
	 * 
	 * @private
	 * @type {conqat.config.EditPartBase}
	 */
	this.startPart = startPart;
	/**
	 * The part to which the arrow points.
	 * 
	 * @private
	 * @type {conqat.config.EditPartBase}
	 */
	this.endPart = endPart;
	/**
	 * Whether the arrow is invisible (i.e. dotted).
	 * 
	 * @private
	 * @type {boolean}
	 */
	this.invisible = invisible;
};

/**
 * Creates the Arrow's shape on the paper.
 * 
 * Based on http://taitems.tumblr.com/post/549973287/drawing-arrows-in-raphaeljs
 * 
 * @public
 * @param {Object} paper The paper to draw on.
 */
conqat.config.Arrow.prototype.draw = function(paper) {
	var size = 6;
	var outPort = this.startPart.getPort(this.endPart);
	var inPort = this.endPart.getPort(this.startPart);
	var x1 = outPort.x;
	var y1 = outPort.y;
	var x2 = inPort.x;
	var y2 = inPort.y;

	var angle = Math.atan2(x1 - x2, y2 - y1);
	angle = (angle / (2 * Math.PI)) * 360;

	paper.setStart();
	paper.path(
			"M" + x2 + " " + y2 + " L" + (x2 - size) + " " + (y2 - size / 2)
					+ " L" + (x2 - size) + " " + (y2 + size / 2) + " L" + x2
					+ " " + y2).attr("fill", "black")
			.rotate(90 + angle, x2, y2);
	var shaft = paper.path("M" + x1 + " " + y1 + " L" + x2 + " " + y2);
	if (this.invisible) {
		shaft.attr("stroke-dasharray", "-");
	}

	return paper.setFinish();
};

/**
 * Returns the Arrow's start part.
 * 
 * @public
 * @returns {conqat.config.EditPartBase} The EditPart from which the arrow
 *          originates
 */
conqat.config.Arrow.prototype.getStartPart = function() {
	return this.startPart;
};

/**
 * Returns the Arrow's end part.
 * 
 * @public
 * @returns {conqat.config.EditPartBase} The EditPart to which the arrow points.
 */
conqat.config.Arrow.prototype.getEndPart = function() {
	return this.endPart;
};