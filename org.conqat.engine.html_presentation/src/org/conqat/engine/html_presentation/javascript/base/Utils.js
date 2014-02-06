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

goog.provide('conqat.base.Utils');

goog.require('goog.net.XhrIo');
goog.require('goog.dom');
goog.require('goog.style');

/**
 * Provides various simple utility methods that are often directly embedded in
 * the generated HTML pages.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 43502 $
 * @ConQAT.Rating GREEN Hash: 4D716E6ED1C957180CD9D304CBCF44CE
 */
conqat.base.Utils;

/**
 * Replaces the content of an element with text loaded from another file.
 * 
 * @param {string} elementId the Id of the element whose content is replaced.
 * @param {string} fileUrl the name/URL of the file providing the content.
 */
conqat.base.Utils.loadContent = function(elementId, fileUrl) {
	var element = goog.dom.getElement(elementId);
	goog.net.XhrIo.send(fileUrl, function(e) {
		var xhr = /** @type {goog.net.XhrIo} */
		(e.target);
		element.innerHTML = xhr.getResponseText();
	});
};

/**
 * Toggles the visibility of the given element.
 * 
 * @param {string} id the id of the element.
 */
conqat.base.Utils.toggleVisibility = function(id) {
	var element = goog.dom.getElement(id);
	goog.style.showElement(element, !goog.style.isElementShown(element));
};

/**
 * Makes the given element visible.
 * 
 * @param {string} id the id of the element.
 */
conqat.base.Utils.showElement = function(id) {
	var element = goog.dom.getElement(id);
	goog.style.showElement(element, true);
};

/** Makes all elements in the current document visible. */
conqat.base.Utils.showAll = function() {
	var all = goog.dom.getElementsByTagNameAndClass();
	for ( var i = 0, e = all.length; i < e; ++i) {
		goog.style.showElement(all[i], true);
	}
};
