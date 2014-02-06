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

goog.provide('conqat.config.Title');

goog.require('conqat.config.EditPartBase');

/**
 * An edit part that shows a simple title string for a given block id.
 * 
 * @ConQAT.Rating GREEN Hash: 6C09FD53D5B955D634985F907AA29093
 * @constructor
 * @param {number} id The id of the block for which to construct the title.
 */
conqat.config.Title = function(id) {
	var part = new conqat.config.EditPartBase(id);
	var title = part.getName() + " [" + part.getTypeName() + "]";

	/**
	 * The title string.
	 * 
	 * @private
	 * @type {string}
	 */
	this.title = title;
};

/**
 * Draws the title.
 * 
 * @public
 * @param {Object} paper The paper to draw on.
 */
conqat.config.Title.prototype.draw = function(paper) {
	return paper.text(10, 10, this.title).attr({
		"text-anchor" : "start",
		"font-size" : "12px",
		"font-weight" : "bold"
	});
};
