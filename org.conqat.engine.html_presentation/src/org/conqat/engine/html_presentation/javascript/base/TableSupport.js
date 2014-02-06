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

goog.provide('conqat.base.TableSupport');

goog.require('goog.dom');
goog.require('goog.dom.classes');
goog.require('goog.array');
goog.require('goog.events');
goog.require('goog.cssom');
goog.require('goog.ui.TableSorter');
goog.require('goog.ui.DrilldownRow');
goog.require('goog.Timer');
goog.require('goog.style');

/**
 * Provides code for making tables sortable or treed.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 45505 $
 * @ConQAT.Rating GREEN Hash: 717324EAA31092E90568BAF448B7E0D4
 */
conqat.base.TableSupport;

/**
 * Static method for making a table sortable.
 * 
 * @param {string} tableId
 * @param {string} oddCss
 * @param {string} evenCss
 */
conqat.base.TableSupport.makeSortable = function(tableId, oddCss, evenCss) {
	var sorter = new goog.ui.TableSorter();
	sorter.setDefaultSortFunction(conqat.base.TableSupport.smartSort);

	var tableElement = goog.dom.getElement(tableId);
	sorter.decorate(tableElement);

	if (!oddCss || !evenCss) {
		return;
	}

	goog.events.listen(sorter, goog.ui.TableSorter.EventType.SORT, function(e) {
		var tBody = tableElement.tBodies[0];
		tableElement.removeChild(tBody);
		conqat.base.TableSupport.stripeTable(tBody, oddCss, evenCss);
		tableElement.insertBefore(tBody, tableElement.tBodies[0] || null);
	});
};

/**
 * The sorting/compare function used.
 * 
 * @param {*} a
 * @param {*} b
 * @return {number} Negative if a < b, 0 if a = b, and positive if a > b.
 * @private
 */
conqat.base.TableSupport.smartSort = function(a, b) {
	var numA = conqat.base.TableSupport.parseNumber(a);
	var numB = conqat.base.TableSupport.parseNumber(b);
	if (typeof numA == 'number' && typeof numB == 'number') {
		return numA - numB;
	}
	return goog.array.defaultCompare(a, b);
};

/**
 * Attempts to parse the given input as a number. If this fails, the value
 * itself is returned.
 * 
 * @param {*} value
 * @return {*}
 * @private
 */
conqat.base.TableSupport.parseNumber = function(value) {
	// main problem here is that we do not know the locale used
	// heuristic is to discard and dot/comma if exactly 3 digits follow
	// (thousand separator) and in a second step replace the remaining ones by a
	// dot.
	if (typeof (value) == 'string' && value.substring(0, 1) != '0') {
		value = value.replace(/[.,'](\d{3})($|\D)/g, '$1$2');
		value = value.replace(/,/g, '.');
	}

	var f = parseFloat(value);
	if (isNaN(f)) {
		return value;
	}
	return f;
};

/**
 * Static method for making a table tree-like.
 * 
 * @param {string} tableId
 * @param {Array.<number>} parentRows
 * @param {string} oddCss
 * @param {string} evenCss
 */
conqat.base.TableSupport.makeTree = function(tableId, parentRows, oddCss,
		evenCss) {
	var tableElement = goog.dom.getElement(tableId);
	var tBody = tableElement.tBodies[0];
	var rows = goog.array.clone(goog.dom.getChildren(tBody));

	if (rows.length == 0) {
		return;
	}

	if (parentRows.length != rows.length) {
		throw Error("Invalid parentRows array provided!");
	}

	conqat.base.TableSupport.installDrilldownRowCss();

	tableElement.removeChild(tBody);

	var rowHtml = {};
	var hasChildren = {};
	var roots = {};
	for ( var i = 0; i < parentRows.length; ++i) {
		rowHtml[i] = conqat.base.TableSupport.getRowOuterHtml(rows[i]);
		hasChildren[i] = false;
		var parent = parentRows[i];
		if (parent == -1) {
			roots[i] = rows[i];
		} else {
			hasChildren[parentRows[i]] = true;
		}
	}

	goog.dom.removeChildren(tBody);
	goog.object.forEach(roots, function(row) {
		goog.dom.appendChild(tBody, row);
	});

	var restripe = function() {
		// intentionally empty
	};
	if (oddCss && evenCss) {
		restripe = goog.bind(conqat.base.TableSupport.stripeTable, undefined,
				tBody, oddCss, evenCss);
	}

	var decorateLeaf = goog.bind(conqat.base.TableSupport.decorate, undefined,
			true, restripe);
	var decorateInner = goog.bind(conqat.base.TableSupport.decorate, undefined,
			false, restripe);

	var drillDowns = {};
	goog.object.forEach(roots, function(row, index) {
		drillDowns[index] = new goog.ui.DrilldownRow({
			decorator : decorateInner
		});
		drillDowns[index].decorate(row);
	});

	for ( var i = 0; i < parentRows.length; ++i) {
		if (!goog.object.containsKey(roots, i)) {
			drillDowns[i] = new goog.ui.DrilldownRow({
				html : rowHtml[i],
				decorator : (hasChildren[i] ? decorateInner : decorateLeaf)
			});
			drillDowns[parentRows[i]].addChild(drillDowns[i], true);
		}
	}

	conqat.base.TableSupport.installGlobalButtons(tableElement.tHead,
			drillDowns, restripe);

	tableElement.insertBefore(tBody, tableElement.tBodies[0] || null);

	setTimeout(function() {
		goog.object.forEach(drillDowns, function(drilldown) {
			drilldown.setExpanded(drilldown.getDepth() < 2);
		});
	}, 0);
};

/**
 * Helper function for returning the outer HTML code for a row element.
 * Additional effort is required, as IE does not implement table elements as
 * "normal" DOM elements.
 * 
 * @param {Element} row
 * @return {string}
 * @private
 */
conqat.base.TableSupport.getRowOuterHtml = function(row) {
	var table = goog.dom.createDom('table', null, row);
	var div = goog.dom.createDom('div', null, table);
	var tableHtml = div.innerHTML;
	return tableHtml.replace(/<\/?table>/ig, '');
};

/**
 * Stripes the given rows of a table body element.
 * 
 * @param {!Element} tBody the table body containing the rows to be striped.
 * @param {string} oddCss
 * @param {string} evenCss
 * @private
 */
conqat.base.TableSupport.stripeTable = function(tBody, oddCss, evenCss) {
	var even = true;
	var rows = goog.dom.getChildren(tBody);
	for ( var i = 0, e = rows.length; i < e; ++i) {
		if (!goog.style.isElementShown(rows[i])
				|| goog.dom.classes.has(rows[i],
						conqat.base.TableSupport.FILTERED_CLASS)) {
			continue;
		}

		if (even) {
			goog.dom.classes.swap(rows[i], oddCss, evenCss);
		} else {
			goog.dom.classes.swap(rows[i], evenCss, oddCss);
		}
		even = !even;
	}
};

/**
 * Installs the styles required by the DrilldownRow class.
 * 
 * @private
 */
conqat.base.TableSupport.installDrilldownRowCss = function() {
	goog.cssom
			.addCssText(".toggle { cursor: pointer; cursor: hand; background-repeat: none; background-position: right; }");
	goog.cssom
			.addCssText("tr.goog-drilldown-expanded .toggle { background-image: url('images/minus.png'); }");
	goog.cssom
			.addCssText("tr.goog-drilldown-collapsed .toggle { background-image: url('images/plus.png'); }");
};

/**
 * Installs bottons for expand all and collapse all into header.
 * 
 * @param {Element} tHead
 * @param {Object.<goog.ui.DrilldownRow>} drilldowns
 * @param {function()} callback called after expansion changed
 * @private
 */
conqat.base.TableSupport.installGlobalButtons = function(tHead, drilldowns,
		callback) {
	var row = goog.dom.getFirstElementChild(tHead);
	var cell = goog.dom.getFirstElementChild(row);

	var html = '<div class="toggle" style="width: 16px; float: left; background-image: url(\'images/expand_all.gif\');">'
			+ '&nbsp;</div>'
			+ '<div class="toggle" style="width: 16px; float: left; margin-right: 3px; background-image: url(\'images/collapse_all.gif\');">'
			+ '&nbsp;</div>';
	cell.insertBefore(goog.dom.htmlToDocumentFragment(html), cell.firstChild);

	var handler = function(expand) {
		goog.object.forEach(drilldowns, function(drilldown) {
			drilldown.setExpanded(expand);
		});
		callback();
	};

	var divElements = goog.dom.getElementsByTagNameAndClass('div', null, cell);

	var expandAll = divElements[0];
	goog.events.listen(expandAll, goog.events.EventType.CLICK, goog.bind(
			handler, undefined, true));

	var collapseAll = divElements[1];
	goog.events.listen(collapseAll, goog.events.EventType.CLICK, goog.bind(
			handler, undefined, false));
};

/**
 * Decorator function used for tree tables. This is copied from the closure
 * library and adjusted to our needs.
 * 
 * @param {!goog.ui.DrilldownRow} selfObj DrilldownRow to be decorated.
 * @param {function()} callback called after expansion changed
 * @param {boolean} isLeaf
 * @private
 */
conqat.base.TableSupport.decorate = function(isLeaf, callback, selfObj) {
	var depth = selfObj.getDepth();
	var row = selfObj.getElement();
	var cell = goog.dom.getFirstElementChild(row);
	var html = '<div style="float: left; width: ' + (16 * depth)
			+ 'px;"><div class=toggle style="width: 16px; float: right;">'
			+ '&nbsp;</div></div>';
	var fragment = selfObj.getDomHelper().htmlToDocumentFragment(html);
	cell.insertBefore(fragment, cell.firstChild);

	if (!isLeaf) {
		goog.dom.classes.add(row, selfObj.isExpanded() ? goog
				.getCssName('goog-drilldown-expanded') : goog
				.getCssName('goog-drilldown-collapsed'));
		// Default mouse event handling:
		var toggler = fragment.getElementsByTagName('div')[0];
		selfObj.getHandler().listen(toggler, 'click', function(event) {
			selfObj.setExpanded(!selfObj.isExpanded());
			callback();
		});
	}
};

/**
 * Name of the CSS class used for filtered/invisible cells.
 * 
 * @type {string}
 * @const
 * @private
 */
conqat.base.TableSupport.FILTERED_CLASS = 'cq-tbl-filtered';

/**
 * Registers the filters for a table.
 * 
 * @param {string} tableId
 * @param {Array.<number>} parentRows
 * @param {string} filterInputClass
 * @param {string} oddCss
 * @param {string} evenCss
 */
conqat.base.TableSupport.registerFilters = function(tableId, parentRows,
		filterInputClass, oddCss, evenCss) {
	var tableElement = goog.dom.getElement(tableId);

	// register filtering class
	goog.cssom.addCssText('.' + conqat.base.TableSupport.FILTERED_CLASS
			+ ' { display: none; }');

	var lastTimerId = null;
	var filterInputs = goog.dom.getElementsByClass(filterInputClass,
			tableElement);
	for ( var i = 0; i < filterInputs.length; ++i) {
		goog.events.listen(filterInputs[i], goog.events.EventType.KEYPRESS,
				function(e) {
					if (lastTimerId) {
						goog.Timer.clear(lastTimerId);
					}
					lastTimerId = goog.Timer.callOnce(function() {
						conqat.base.TableSupport.filterTable(tableElement,
								filterInputs, parentRows, oddCss, evenCss);
					}, 500);
				});
	}
};

/**
 * Performs the actual filtering.
 * 
 * @param {Element} tableElement
 * @param {{length : number}} filterInputs
 * @param {Array.<number>} parentRows
 * @param {string} oddCss
 * @param {string} evenCss
 * @private
 */
conqat.base.TableSupport.filterTable = function(tableElement, filterInputs,
		parentRows, oddCss, evenCss) {
	var patterns = [];
	for ( var i = 0; i < filterInputs.length; ++i) {
		var filterText = filterInputs[i].value;
		// escape regex, but replace * by .*
		var regex = '.*'
				+ filterText.replace(/([.?+^$[\]\\(){}|-])/g, '\\$1').replace(
						/\*/g, '.*') + '.*';
		patterns.push(new RegExp(regex, 'i'));
	}

	var tBody = tableElement.tBodies[0];
	tableElement.removeChild(tBody);
	var rows = goog.dom.getChildren(tBody);

	var keeps = [];
	for ( var i = 0, e = rows.length; i < e; ++i) {
		var keep = true;
		var cells = goog.dom.getChildren(rows[i]);
		for ( var j = 0; keep && j < patterns.length; ++j) {
			var content = goog.dom.getTextContent(cells[j]);
			if (!patterns[j].test(content)) {
				keep = false;
			}
		}
		keeps.push(keep);

		if (keep && parentRows && parentRows.length > i) {
			var parent = parentRows[i];
			while (parent >= 0) {
				keeps[parent] = true;
				parent = parentRows[parent];
			}
		}
	}

	for ( var i = 0, e = rows.length; i < e; ++i) {
		// we use a separate CSS class here instead of goog.dom.showElement, as
		// showElement is also used for
		// the tree rendering. Thus, we need a separate concept here.
		if (keeps[i]) {
			goog.dom.classes.remove(rows[i],
					conqat.base.TableSupport.FILTERED_CLASS);
		} else {
			goog.dom.classes.add(rows[i],
					conqat.base.TableSupport.FILTERED_CLASS);
		}
	}

	conqat.base.TableSupport.stripeTable(tBody, oddCss, evenCss);
	tableElement.insertBefore(tBody, tableElement.tBodies[0] || null);
};
