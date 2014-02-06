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

goog.provide('conqat.config.LogPage');

goog.require('conqat.config.DashboardPageBase');
goog.require('conqat.config.UnitEditPart');
goog.require('conqat.config.TemplateUtils');

goog.require('goog.dom');
goog.require('goog.dom.classes');
goog.require('goog.dom.dataset');
goog.require('goog.dom.query');
goog.require('goog.events');
goog.require('goog.events.EventType');
goog.require('goog.soy');
goog.require('goog.array');
goog.require('goog.style');
goog.require('goog.string');
goog.require('goog.object');
goog.require('goog.i18n.DateTimeFormat');

/**
 * Base class that handles the workflow of the log page. Saves and manipulates
 * the entries in the dashboard's log.
 * 
 * @ConQAT.Rating GREEN Hash: 669B42C2D05EAF19026A09808629C09E
 * @constructor
 * @extends {conqat.config.DashboardPageBase}
 */
conqat.config.LogPage = function() {
	goog.base(this);

	/**
	 * The levels that are not filtered out by #isProcessorVisible().
	 * 
	 * @private
	 * @type {Object.<string, boolean>}
	 */
	this.visibleLevels = {
		'fatal' : true,
		'error' : true,
		'warn' : true
	};
	/**
	 * The processor id that is currently displayed. null, if all processors are
	 * shown.
	 * 
	 * @type {?number}
	 */
	this.processorFilter = null;
};
goog.inherits(conqat.config.LogPage, conqat.config.DashboardPageBase);

/**
 * The log singleton.
 * 
 * @see conqat.config.LogPage.create()
 * 
 * @private
 * @type {conqat.config.LogPage}
 */
conqat.config.LogPage.instance;

/**
 * Returns the Log singleton. Creates it if necessary.
 * 
 * @private
 * @returns {conqat.config.LogPage} The Log singleton.
 */
conqat.config.LogPage.getInstance = function() {
	if (!conqat.config.LogPage.instance) {
		conqat.config.LogPage.instance = new conqat.config.LogPage();
	}
	return conqat.config.LogPage.instance;
};

/**
 * Opens the log page for the processor with the given ID.
 * 
 * @public
 * @param {number} id The ID of the processor for which to open the log page.
 */
conqat.config.LogPage.openForProcessor = function(id) {
	location = 'log.html#' + id;
};

/**
 * Creates a log page.
 * 
 * @public
 */
conqat.config.LogPage.create = function() {
	var log = conqat.config.LogPage.getInstance();
	log.update();
};

/**
 * Listens to click events on the last inserted checkbox and filters the log
 * according to its state.
 * 
 * @public
 * @param {string} logLevel The level for which the checkbox is responsible.
 */
conqat.config.LogPage.setupFilterCheckbox = function(logLevel) {
	var filterBoxes = goog.dom.getElementsByTagNameAndClass('input');
	var checkBox = filterBoxes[filterBoxes.length - 1];
	var log = conqat.config.LogPage.getInstance();
	checkBox.checked = log.visibleLevels[logLevel];

	goog.events.listen(checkBox, goog.events.EventType.CLICK, function() {
		log.visibleLevels[logLevel] = checkBox.checked;
		log.update();
	});
};

/**
 * @private
 * @returns {Element} The log table of the current page.
 */
conqat.config.LogPage.getLogTable = function() {
	return goog.dom.getElement('log-table');
};

/** @inheritDoc */
conqat.config.LogPage.prototype.onHistoryChanged = function(event) {
	var token = goog.string.isEmptySafe(event.token) ? null : goog.string
			.toNumber(event.token);
	if (this.processorFilter != token) {
		this.processorFilter = token;
		this.update();
	}
};

/**
 * Checks whether the processor with the given id and log level should be shown.
 * 
 * @private
 */
conqat.config.LogPage.prototype.isProcessorVisible = function(processorId,
		logLevel) {
	if (this.processorFilter && processorId != this.processorFilter) {
		return false;
	}
	if (!this.visibleLevels[logLevel]) {
		return false;
	}
	return true;
};

/**
 * Updates the log page after settings have changed and when it is first
 * created.
 * 
 * @public
 */
conqat.config.LogPage.prototype.update = function() {
	this.updateLogTable();
	this.updateParametersTable();
	this.updateSubtitle();
};

/**
 * Updates the log table after settings have changed.
 * 
 * @private
 */
conqat.config.LogPage.prototype.updateLogTable = function() {
	var that = this;
	var logTableHolder = goog.dom.getElement('log-table');
	var dateFormat = new goog.i18n.DateTimeFormat('HH:mm:ss.SSS');

	var data = {
		rows : [],
		tableHeaderClass : conqat.config.DashboardPageBase
				.getCSSClass('tableHeader'),
		evenRowClass : conqat.config.DashboardPageBase.getCSSClass('evenRow'),
		oddRowClass : conqat.config.DashboardPageBase.getCSSClass('oddRow')
	};
	var processorIds = [];

	goog.array.forEach(conqat.config.DashboardPageBase.log, function(logEntry,
			index) {
		var processorId = logEntry[0];
		var level = logEntry[1];

		if (!that.isProcessorVisible(processorId, level)) {
			return;
		}

		var date = new Date(logEntry[3]);
		var processor = new conqat.config.UnitEditPart(processorId);

		var rowData = {
			name : processor.getName(),
			level : level,
			message : logEntry[2],
			time : dateFormat.format(date)
		};
		data.rows.push(rowData);
		processorIds.push(processorId);
	});

	if (goog.array.isEmpty(data.rows)) {
		goog.soy.renderElement(logTableHolder,
				conqat.config.DashboardTemplate.emptyLogTable);
		return;
	}

	goog.soy.renderElement(logTableHolder,
			conqat.config.DashboardTemplate.logTable, data);

	var rows = goog.dom.query('tbody tr', logTableHolder);
	goog.array.forEach(rows, function(row, index) {
		var processorId = processorIds[index];

		var filterLink = goog.dom.getElementsByTagNameAndClass('a',
				'processor', row)[0];
		goog.events.listen(filterLink, goog.events.EventType.CLICK, function(
				event) {
			event.preventDefault();
			that.setHistoryToken(processorId);
			that.filterProcessor = processorId;
			that.update();
		});
	});
}

/**
 * Updates the parameters table after settings have changed.
 * 
 * @private
 */
conqat.config.LogPage.prototype.updateParametersTable = function() {
	var logTable = conqat.config.LogPage.getLogTable();

	conqat.config.TemplateUtils.clearParametersTable();
	if (this.processorFilter) {
		var processor = new conqat.config.UnitEditPart(this.processorFilter);
		conqat.config.TemplateUtils.renderParametersTable(this.processorFilter);
	}
};

/**
 * Updates the page subtitle after settings have changed.
 * 
 * @private
 */
conqat.config.LogPage.prototype.updateSubtitle = function() {
	var logTable = conqat.config.LogPage.getLogTable();

	var subtitle;
	if (this.processorFilter) {
		var processor = new conqat.config.UnitEditPart(this.processorFilter);
		subtitle = "Log messages for processor " + processor.getName();
	} else {
		subtitle = "All log messages generated during ConQAT run.";
	}
	var subtitleElement = goog.dom.getElement("caption-subtitle");
	goog.dom.setTextContent(subtitleElement, subtitle);
};
