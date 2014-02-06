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

goog.provide('conqat.config.DashboardPageBase');

goog.require('goog.events');
goog.require('goog.History');
goog.require('goog.asserts');

/**
 * Base class for dashboard pages that holds the JSON data and initializes the
 * page.
 * 
 * @ConQAT.Rating GREEN Hash: C5BC8DEEBD86BE6103AB02B23992BA52
 * @constructor
 * @public
 */
conqat.config.DashboardPageBase = function() {
	if (!conqat.config.DashboardPageBase.history) {
		conqat.config.DashboardPageBase.history = new goog.History();
	}
	goog.events.removeAll(conqat.config.DashboardPageBase.history);
	goog.events.listen(conqat.config.DashboardPageBase.history,
			goog.history.EventType.NAVIGATE, goog.bind(this.onHistoryChanged,
					this));
};

/**
 * The global history object.
 * 
 * @private
 * @type {goog.History}
 */
conqat.config.DashboardPageBase.history = null;

/**
 * Enables the history management functions. Must be called after a dashboard
 * page has been created.
 */
conqat.config.DashboardPageBase.enableHistory = function() {
	conqat.config.DashboardPageBase.history.setEnabled(true);
};

/**
 * Holds the JSON for the config graphs.
 * 
 * @type {*}
 * @public
 */
conqat.config.DashboardPageBase.config = null;

/**
 * Holds the JSON for the log entries.
 * 
 * @type {Array}
 * @public
 */
conqat.config.DashboardPageBase.log = null;

/**
 * Holds registered CSS classes.
 * 
 * @private
 * @type {Object.<string, string>}
 */
conqat.config.DashboardPageBase.cssClassNames = {};

/**
 * Holds registered statistics descriptions.
 * 
 * @public
 * @type {Array.<string>}
 */
conqat.config.DashboardPageBase.statisticsDescriptions;

/**
 * Installs the given JSON config data.
 * 
 * @public
 * @param {*} jsonData The data to install.
 */
conqat.config.DashboardPageBase.installConfigJSON = function(jsonData) {
	conqat.config.DashboardPageBase.config = jsonData;
};

/**
 * Installs the given JSON log data.
 * 
 * @public
 * @param {Array} jsonData The data to install.
 */
conqat.config.DashboardPageBase.installLogJSON = function(jsonData) {
	conqat.config.DashboardPageBase.log = jsonData;
};

/**
 * Called when the history token changed.
 * 
 * @protected
 * @param {goog.history.Event} event The history event that took place.
 */
conqat.config.DashboardPageBase.prototype.onHistoryChanged = function(event) { /* abstract */
};

/**
 * Sets a new history token.
 * 
 * @protected
 * @param {string} token The new history token.
 */
conqat.config.DashboardPageBase.prototype.setHistoryToken = function(token) {
	conqat.config.DashboardPageBase.history.setToken(token);
};

/**
 * Registers a dynamically created CSS class name with the dashboard under a
 * key.
 * 
 * @public
 * @param {string} key The key to register the class name under
 * @param {string} className The dynamically generated className
 */
conqat.config.DashboardPageBase.addCSSClass = function(key, className) {
	conqat.config.DashboardPageBase.cssClassNames[key] = className;
};

/**
 * Registers the descriptive texts for the statistics table entries.
 * 
 * @public
 * @param {Array.<string>} descriptions The descriptions of the statistics
 *            table.
 */
conqat.config.DashboardPageBase.setStatisticsDescriptions = function(
		descriptions) {
	conqat.config.DashboardPageBase.statisticsDescriptions = descriptions;
};

/**
 * Looks up a dynamically generated class name by it's key.
 * 
 * @public
 * @param {string} key The key of the the class
 * @returns {string} The generated class name
 */
conqat.config.DashboardPageBase.getCSSClass = function(key) {
	return conqat.config.DashboardPageBase.cssClassNames[key];
};
