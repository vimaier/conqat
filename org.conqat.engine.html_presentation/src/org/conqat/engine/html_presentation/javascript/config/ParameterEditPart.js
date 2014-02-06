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

goog.provide('conqat.config.ParameterEditPart');

goog.require('conqat.config.EditPartBase');
goog.require('goog.math.Coordinate');

/**
 * An edit part that represents a parameter.
 * 
 * @ConQAT.Rating GREEN Hash: A324286FC5739DF606F74457E598574D
 * @constructor
 * @extends {conqat.config.EditPartBase}
 */
conqat.config.ParameterEditPart = function(id) {
	goog.base(this, id);
};
goog.inherits(conqat.config.ParameterEditPart, conqat.config.EditPartBase);

/**
 * @inheritDoc This implementation draws the shape of a parameter.
 */
conqat.config.ParameterEditPart.prototype.draw = function(paper) {
	paper.setStart();
	var x = this.bounds.left;
	var y = this.bounds.top;
	var w = this.bounds.width;
	var h = this.bounds.height;
	paper.path(
			"M" + x + "," + y + "L" + x + "," + (y + h * 2 / 3) + ","
					+ (x + w / 2) + "," + (y + h) + "," + (x + w) + ","
					+ (y + h * 2 / 3) + "," + (x + w) + "," + y + "Z").attr({
		fill : "270-#fff-" + this.color,
		stroke : "#000"
	});
	paper.text(x + w / 2, y + h / 3, this.name).attr("font-weight", "bold")
			.attr("font-size", 12);
	return paper.setFinish();
};

/**
 * @inheritDoc The port is the apex of the shape.
 */
conqat.config.ParameterEditPart.prototype.getPort = function(destination) {
	return new goog.math.Coordinate(this.bounds.left + this.bounds.width / 2,
			this.bounds.top + this.bounds.height);
};
