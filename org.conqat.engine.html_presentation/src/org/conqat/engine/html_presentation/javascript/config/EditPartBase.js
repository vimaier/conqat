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

goog.provide('conqat.config.EditPartBase');

goog.require('goog.math.Rect');

/**
 * Represents a single edit part on the canvas and provides the base code for
 * drawing EditParts onto the config graph, e.g. Processors or Outputs.
 * 
 * @ConQAT.Rating GREEN Hash: B7D7EFD512FBD0084A49163EA8CEAB5D
 * @constructor
 * @private
 * @param {number} id The id of the part
 */
conqat.config.EditPartBase = function(id) {
	/**
	 * The id of the EditPart.
	 * 
	 * @protected
	 * @type {number}
	 */
	this.id = id;

	var data = conqat.config.DashboardPageBase.config.elements[id];

	/**
	 * The name of the EditPart.
	 * 
	 * @private
	 * @type {string}
	 */
	this.name = data[1];

	/**
	 * The name of the type of the EditPart.
	 * 
	 * @private
	 * @type {string}
	 */
	this.typeName = data[2];

	/**
	 * The color of the EditPart.
	 * 
	 * @private
	 * @type {string}
	 */
	this.color = data[3];

	/**
	 * The bounds of the EditPart.
	 * 
	 * @private
	 * @type {goog.math.Rect}
	 */
	this.bounds = new goog.math.Rect(data[4], data[5], data[6], data[7]);
};

/**
 * Factory method for EditParts.
 * 
 * @param {number} id The id of the part
 */
conqat.config.EditPartBase.create = function(id) {
	var data = conqat.config.DashboardPageBase.config.elements[id];
	var type = data[0];

	var types = {
		'PARAMETER' : conqat.config.ParameterEditPart,
		'PROCESSOR' : conqat.config.UnitEditPart,
		'BLOCK' : conqat.config.UnitEditPart,
		'OUTPUT' : conqat.config.OutputEditPart
	};
	var clazz = types[type];
	return new clazz(id);
};

/**
 * Creates the shape of the part on the given paper. Must be implemented by
 * subclasses.
 * 
 * @public
 * @param {Object} paper The paper to draw on.
 */
conqat.config.EditPartBase.prototype.draw = function(paper) { /* abstract */
};

/**
 * Returns the EditPart's type name.
 * 
 * @public
 * @returns {string}
 */
conqat.config.EditPartBase.prototype.getTypeName = function() {
	return this.typeName;
};

/**
 * Returns the EditPart's name.
 * 
 * @public
 * @returns {string}
 */
conqat.config.EditPartBase.prototype.getName = function() {
	return this.name;
};

/**
 * Returns the EditPart's id.
 * 
 * @public
 * @returns {number}
 */
conqat.config.EditPartBase.prototype.getId = function() {
	return this.id;
};

/**
 * Returns the bounds of this edit part.
 * 
 * @public
 * @returns {goog.math.Rect}
 */
conqat.config.EditPartBase.prototype.getBounds = function() {
	return this.bounds;
};

/**
 * Moves the editpart by xoffset, yoffset. Has to be called, before a shape is
 * drawn.
 * 
 * @public
 * @param {number} xoffset The offset by which to move the part horizontally.
 * @param {number} yoffset The offset by which to move the part vertically.
 */
conqat.config.EditPartBase.prototype.move = function(xoffset, yoffset) {
	this.bounds.left += xoffset;
	this.bounds.top += yoffset;
};

/**
 * Calculates the point on this edit part where an arrow starts, if it points to
 * the destination.
 * 
 * @public
 * @param {conqat.config.EditPartBase} destination The other end of the arrow.
 * @returns {goog.math.Coordinate} The point to use for the arrow port.
 */
conqat.config.EditPartBase.prototype.getPort = function(destination) {/* abstract */
};
