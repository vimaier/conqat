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

/**
 * @author $Author: hummelb $
 * @version $Rev: 37435 $
 * @ConQAT.Rating GREEN Hash: DC097BA5CC6FBB7AB47AAF23DFB83F9D
 */

goog.provide('conqat.base.NavigationPage');

goog.require('goog.dom');
goog.require('goog.style');
goog.require('goog.events');
goog.require('goog.events.EventType');

/**
 * This class provides the code for the navigation page of the dashboard.
 * 
 * @param {string} openIcon
 * @param {string} closeIcon
 * @constructor
 */
conqat.base.NavigationPage = function(openIcon, closeIcon) {

	/**
	 * @type {string}
	 * @private
	 */
	this.openIcon_ = openIcon;

	/**
	 * @type {string}
	 * @private
	 */
	this.closeIcon_ = closeIcon;
};

/**
 * Static method for installing the navigation page script.
 * 
 * @param {string}
 *            treeId the id of the main navigation tree.
 * @param {string}
 *            expandAllId id of the expand all button.
 * @param {string}
 *            collapseAllId id of the collapse all button.
 * @param {string}
 *            openIcon the url of the icon used for open groups.
 * @param {string}
 *            closeIcon the url of the icon used for closed groups.
 */
conqat.base.NavigationPage.install = function(treeId, expandAllId,
		collapseAllId, openIcon, closeIcon) {

	var navigationPage = new conqat.base.NavigationPage(openIcon, closeIcon);
	var treeElement = goog.dom.getElement(treeId);

	goog.events.listen(treeElement, goog.events.EventType.CLICK, function(e) {
		if (e.target.nodeName.toLowerCase() === 'span') {
			e.stopPropagation();
			navigationPage.toggleGroup(e.target);
		}
	});

	goog.events.listen(goog.dom.getElement(expandAllId),
			goog.events.EventType.CLICK, function(e) {
				navigationPage.toggleAll(treeElement, true);
			});
	goog.events.listen(goog.dom.getElement(collapseAllId),
			goog.events.EventType.CLICK, function(e) {
				navigationPage.toggleAll(treeElement, false);
			});
};

/**
 * Toggles a single group.
 * 
 * @param {!Element}
 *            spanElement the element corresponding to the group's caption.
 */
conqat.base.NavigationPage.prototype.toggleGroup = function(spanElement) {
	var ulElement = goog.dom.getNextElementSibling(spanElement);
	var visible = goog.style.isElementShown(ulElement);

	goog.style.showElement(ulElement, !visible);

	var parentElement = spanElement.parentNode;
	if (goog.dom.getNextElementSibling(parentElement) === null) {
		// do not change icon for info group (which is the last one)
		return;
	}

	var icon = this.openIcon_;
	if (visible) {
		icon = this.closeIcon_;
	}
	parentElement.style.backgroundImage = "url(" + icon + ")";
};

/**
 * Toggles all groups in the given tree.
 * 
 * @param {!Element}
 *            treeElement the element containing the tree.
 * @param {boolean}
 *            open whether the target state should be open or closed.
 */
conqat.base.NavigationPage.prototype.toggleAll = function(treeElement, open) {
	var groupElements = goog.dom.getElementsByTagNameAndClass('span', null,
			treeElement);
	for ( var i = 0, length = groupElements.length; i < length; i++) {
		var ulElement = goog.dom.getNextElementSibling(groupElements[i]);
		var visible = goog.style.isElementShown(ulElement);

		if (visible !== open) {
			this.toggleGroup(groupElements[i]);
		}
	}
};


