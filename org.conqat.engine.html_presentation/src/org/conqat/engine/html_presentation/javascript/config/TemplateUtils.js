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

goog.provide('conqat.config.TemplateUtils');

goog.require('goog.soy');
goog.require('goog.dom');

/**
 * Provides static utility methods for easier template rendering.
 * 
 * @ConQAT.Rating GREEN Hash: 2E750968F9DD3A73958BF8286E5162F6
 */
conqat.config.TemplateUtils;

/**
 * The id of the element to which statistics tables are rendered.
 * 
 * @public
 * @final
 * @type {string}
 */
conqat.config.TemplateUtils.STATISTICS_TABLE_ID = "statistics-table";

/**
 * The id of the element to which parameter tables are rendered.
 * 
 * @public
 * @final
 * @type {string}
 */
conqat.config.TemplateUtils.PARAMETERS_TABLE_ID = "parameters-table";

/**
 * Renders the statistics table to the page.
 * 
 * @see conqat.config.TemplateUtils.statisticsTableId
 * 
 * @public
 * @param {number} blockId The id of the block for which to render the
 *            statistics.
 */
conqat.config.TemplateUtils.renderStatisticsTable = function(blockId) {
	var statistics = conqat.config.DashboardPageBase.config.blockData[blockId].statistics;
	var statisticsTable = goog.dom
			.getElement(conqat.config.TemplateUtils.STATISTICS_TABLE_ID);
	var statisticsData = {
		descriptions : conqat.config.DashboardPageBase.statisticsDescriptions,
		values : statistics,
		tableClass : conqat.config.DashboardPageBase.getCSSClass("table"),
		headerClass : conqat.config.DashboardPageBase
				.getCSSClass("tableHeader"),
		cellClass : conqat.config.DashboardPageBase.getCSSClass("tableCell"),
		wideCellClass : conqat.config.DashboardPageBase
				.getCSSClass("wideTableCell")
	};
	goog.soy.renderElement(statisticsTable,
			conqat.config.DashboardTemplate.statisticsTable, statisticsData);
};

/**
 * Renders the parameters table to the page.
 * 
 * @see conqat.config.TemplateUtils.parametersTableId
 * 
 * @public
 * @param {number} id The id of the parameter or block for which to render the
 *            parameters.
 */
conqat.config.TemplateUtils.renderParametersTable = function(id) {
	var parameters = conqat.config.DashboardPageBase.config.parameters[id];
	var parametersTable = goog.dom
			.getElement(conqat.config.TemplateUtils.PARAMETERS_TABLE_ID);
	var parametersData = {
		parameters : parameters,
		tableClass : conqat.config.DashboardPageBase.getCSSClass("table"),
		headerClass : conqat.config.DashboardPageBase
				.getCSSClass("tableHeader"),
		cellClass : conqat.config.DashboardPageBase.getCSSClass("tableCell"),
		wideCellClass : conqat.config.DashboardPageBase
				.getCSSClass("wideTableCell"),
		attributeCellClass : conqat.config.DashboardPageBase
				.getCSSClass("attributeTableCell")
	};
	goog.soy.renderElement(parametersTable,
			conqat.config.DashboardTemplate.parametersTable, parametersData);
};

/**
 * Removes any existing parameters table from the page.
 * 
 * @see conqat.config.TemplateUtils.parametersTableId
 * 
 * @public
 */
conqat.config.TemplateUtils.clearParametersTable = function() {
	var parametersTable = goog.dom
			.getElement(conqat.config.TemplateUtils.PARAMETERS_TABLE_ID);
	goog.dom.removeChildren(parametersTable);
};
