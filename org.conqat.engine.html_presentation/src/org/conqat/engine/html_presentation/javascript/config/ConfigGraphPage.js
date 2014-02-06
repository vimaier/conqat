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

goog.provide('conqat.config.ConfigGraphPage');

goog.require('conqat.config.DashboardPageBase');
goog.require('conqat.config.ParameterEditPart');
goog.require('conqat.config.EditPartBase');
goog.require('conqat.config.TemplateUtils');

goog.require('goog.soy');
goog.require('goog.array');
goog.require('goog.object');
goog.require('goog.string');
goog.require('goog.dom');
goog.require('goog.math.Size');

/**
 * Handles all aspects of rendering the page that contains the config graph and
 * related information.
 * 
 * @ConQAT.Rating GREEN Hash: FAAE62A1789D776984AC3732B7C92DCB
 * @constructor
 * @extends {conqat.config.DashboardPageBase}
 * @public
 * @param {number} id The id of the config.
 * @param {boolean=} dontSetHistoryToken If this is <code>true</code>, the
 *            config will not set a history token.
 */
conqat.config.ConfigGraphPage = function(id, dontSetHistoryToken) {
	goog.base(this);

	/**
	 * The id of the config.
	 * 
	 * @private
	 * @type {number}
	 */
	this.id = id;

	/**
	 * The edit parts that belong to this config.
	 * 
	 * @private
	 * @type {Object.<number, conqat.config.EditPartBase>}
	 */
	this.editParts = {};

	/**
	 * The arrows that belong to this config.
	 * 
	 * @private
	 * @type {Array.<conqat.config.Arrow>}
	 */
	this.arrows = [];

	if (!dontSetHistoryToken) {
		this.setHistoryToken("" + id);
	}

	var blockData = conqat.config.DashboardPageBase.config.blockData[id];

	goog.array.forEach(blockData.members, function(memberId) {
		this.editParts[memberId] = conqat.config.EditPartBase.create(memberId);
	}, this);

	var bounds = this.minimize();

	goog.array.forEach(blockData.connections, function(connection) {
		var source = this.editParts[connection[0]];
		var target = this.editParts[connection[1]];
		// source == target for all blocks, so ignore those arrows
		if (!source || !target || source == target) {
			return;
		}
		var invisible = connection[2];
		this.arrows.push(new conqat.config.Arrow(source, target, invisible));
	}, this);

	var paperHolder = goog.dom
			.getElement(conqat.config.ConfigGraphPage.PAPER_HOLDER_ID);
	goog.dom.removeChildren(paperHolder);
	var paper = Raphael(paperHolder, bounds.width, bounds.height);
	this.draw(paper);
};
goog.inherits(conqat.config.ConfigGraphPage, conqat.config.DashboardPageBase);

/**
 * The ID of the element that holds Raphael's paper.
 * 
 * @private
 * @type {string}
 */
conqat.config.ConfigGraphPage.PAPER_HOLDER_ID;

/**
 * Creates a config page.
 * 
 * @public
 * @param {string} graphPaperId The id of the element onto which the Raphael
 *            graph should be rendered.
 */
conqat.config.ConfigGraphPage.create = function(graphPaperId) {
	conqat.config.ConfigGraphPage.PAPER_HOLDER_ID = graphPaperId;
	var paperHolder = goog.dom.getElement(graphPaperId);
	var statisticsHolder = goog.dom.createDom('div', {
		'id' : conqat.config.TemplateUtils.STATISTICS_TABLE_ID
	});
	var parametersHolder = goog.dom.createDom('div', {
		'id' : conqat.config.TemplateUtils.PARAMETERS_TABLE_ID
	});
	goog.dom.insertSiblingBefore(statisticsHolder, paperHolder);
	goog.dom.insertSiblingAfter(parametersHolder, paperHolder);
	new conqat.config.ConfigGraphPage(
			conqat.config.DashboardPageBase.config.rootBlockId, true);
};

/**
 * Height of the title above the graph.
 * 
 * @private
 * @final
 * @type {number}
 */
conqat.config.ConfigGraphPage.titleHeight = 30;

/** @inheritDoc */
conqat.config.ConfigGraphPage.prototype.onHistoryChanged = function(event) {
	var token = event.token;
	var id = conqat.config.DashboardPageBase.config.rootBlockId;
	if (!goog.string.isEmptySafe(event.token)) {
		id = parseInt(token, 10);
	}
	new conqat.config.ConfigGraphPage(id);
};

/**
 * Minimizes the area used by this config and returns the new bounds.
 * 
 * @private
 * @returns {goog.math.Size} The new bounds.
 */
conqat.config.ConfigGraphPage.prototype.minimize = function() {
	// minimize graph size
	var xmin = Infinity;
	var ymin = Infinity;
	goog.object.forEach(this.editParts, function(part) {
		var bounds = part.getBounds();
		xmin = Math.min(xmin, bounds.left);
		ymin = Math.min(ymin, bounds.top);
	});
	var xoffset = -xmin;
	var yoffset = -ymin + conqat.config.ConfigGraphPage.titleHeight;
	goog.object.forEach(this.editParts, function(part) {
		part.move(xoffset, yoffset);
	});

	// get the minimum bounds of the graph
	var width = 0;
	var height = 0;
	goog.object.forEach(this.editParts, function(part) {
		var bounds = part.getBounds();
		width = Math.max(width, bounds.left + bounds.width);
		height = Math.max(height, bounds.top + bounds.height);
	});

	return new goog.math.Size(width, height);
};

/**
 * Draws the config.
 * 
 * @private
 * @param {Object} paper The paper to draw on.
 */
conqat.config.ConfigGraphPage.prototype.draw = function(paper) {
	goog.object.forEach(this.editParts, function(editPart) {
		editPart.draw(paper);
	});
	goog.array.forEach(this.arrows, function(arrow) {
		arrow.draw(paper);
	});
	var titlePart = new conqat.config.Title(this.id);
	titlePart.draw(paper);

	conqat.config.TemplateUtils.renderStatisticsTable(this.id);
	conqat.config.TemplateUtils.renderParametersTable(this.id);

	var titleElement = goog.dom.getElement("caption-title");
	var subtitleElement = goog.dom.getElement("caption-subtitle");
	if (this.id == 0) {
		goog.dom.setTextContent(titleElement, "Config");
		goog.dom.setTextContent(subtitleElement,
				"Top-level Configuration Graph.");
	} else {
		var part = new conqat.config.EditPartBase(this.id);
		goog.dom.setTextContent(titleElement, "Block " + part.getName());
		goog.dom.setTextContent(subtitleElement, "Config Graph for Block "
				+ part.getName() + ".");
	}
};
