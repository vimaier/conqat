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

goog.provide('conqat.config.UnitEditPart');

goog.require('conqat.config.EditPartBase');
goog.require('goog.math.Coordinate');

/**
 * An edit part that represents a processor or block.
 * 
 * @ConQAT.Rating GREEN Hash: D5DB94934F282885E69974AF3D584222
 * @constructor
 * @extends {conqat.config.EditPartBase}
 */
conqat.config.UnitEditPart = function(id) {
	goog.base(this, id);

	var handler;
	if (conqat.config.DashboardPageBase.config.blockData[id]) {
		// there is a block, so clicking on the part should open it
		handler = function() {
			new conqat.config.ConfigGraphPage(id);
		};
	} else {
		// there is no block, so clicking on the part should open its log
		handler = function() {
			conqat.config.LogPage.openForProcessor(id);
		};
	}
	/**
	 * Handler for click events.
	 * 
	 * @private
	 * @type {Function}
	 */
	this.onClickHandler = handler;
};
goog.inherits(conqat.config.UnitEditPart, conqat.config.EditPartBase);

/**
 * @inheritDoc This implementation draws the shape of a processor.
 */
conqat.config.UnitEditPart.prototype.draw = function(paper) {
	paper.setStart();
	var x = this.bounds.left;
	var y = this.bounds.top;
	var w = this.bounds.width;
	var h = this.bounds.height;
	paper.rect(x, y, w, h, 5).attr({
		fill : "270-#fff-" + this.color,
		stroke : "#000"
	});
	paper.path("M" + x + "," + (y + h / 2) + "H" + (x + w)).attr("stroke",
			"#000");
	paper.text(x + w / 2, y + h / 4, this.typeName).attr({
		"font-weight" : "bold",
		"font-size" : 12
	});
	paper.text(x + w / 2, y + h / 4 * 3, this.name).attr({
		"font-size" : 12
	});
	var shape = paper.setFinish();
	shape.attr('cursor', 'pointer');
	shape.click(this.onClickHandler);
	return shape;
};

/**
 * @inheritDoc The port is the intersection of the bounding box with an arrow
 *             from the center of this part to the center of the destination
 *             part.
 */
conqat.config.UnitEditPart.prototype.getPort = function(destination) {
	var box = this.getBounds();
	var toBox = destination.getBounds();
	var referencePoint = {
		x : toBox.left + toBox.width / 2,
		y : toBox.top + toBox.height / 2
	};

	var baseX = box.left + box.width / 2;
	var baseY = box.top + box.height / 2;
	var refX = referencePoint.x;
	var refY = referencePoint.y;

	// This avoids divide-by-zero
	if (refX == baseX && refY == baseY) {
		return new goog.math.Coordinate(refX, refY);
	}

	var dx = refX - baseX;
	var dy = refY - baseY;

	// r.width, r.height, dx, and dy are guaranteed to be non-zero.
	var scale = 0.5 / Math.max(Math.abs(dx) / box.width, Math.abs(dy)
			/ box.height);
	baseX += dx * scale;
	baseY += dy * scale;
	return new goog.math.Coordinate(Math.round(baseX), Math.round(baseY));
};
