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
 * @author $Author: deissenb $
 * @version $Rev: 39901 $
 * @ConQAT.Rating GREEN Hash: A723C9345F39F3DE4497CD1476FC26F2
 */

goog.provide('conqat.base.GotoBox');

goog.require('goog.dom');
goog.require('goog.object');
goog.require('goog.events');
goog.require('goog.events.EventType');
goog.require('goog.events.KeyHandler');
goog.require('goog.events.KeyCodes');
goog.require('goog.ui.AutoComplete.Basic');

/**
 * Static method for installing the goto box script. This enables an auto-completion menu on the goto box.
 * 
 * @param {string}
 *            textId the id of the main navigation tree.
 * @param {string}
 *            buttonId id of the expand all button.
 * @param {!Array.<string>} 
 *            cssClasses the CSS classes to be used (render, row, active, highlighted)
 * @param {!Object.<string, string>} 
 *            links mapping from strings to linked URLs
 */
conqat.base.GotoBox.install = function(textId, buttonId, cssClasses, links) {
	var textElement = goog.dom.getElement(textId);
	var buttonElement = goog.dom.getElement(buttonId);

	var data = goog.object.getKeys(links);
	var autocomplete = new goog.ui.AutoComplete.Basic(data, textElement, false,
			true);
	autocomplete.setAutoHilite(true);

	var renderer = autocomplete.getRenderer();

	renderer.className = cssClasses[0];
	renderer.rowClassName = cssClasses[1];
	renderer.activeClassName = cssClasses[2];
	renderer.highlightedClassName = cssClasses[3];

	var performGoto = function() {
		var url = links[textElement.value];
		if (url) {
			window.location.href = url;
		} else {
			alert("No URL known for " + textElement.value);
		}
	};

	goog.events.listen(buttonElement, goog.events.EventType.CLICK, performGoto);

	var keyHandler = new goog.events.KeyHandler(textElement);
	goog.events.listen(keyHandler, goog.events.KeyHandler.EventType.KEY,
			function(e) {
				var keyEvent = /** @type {goog.events.KeyEvent} */
				(e);

				if (keyEvent.keyCode == goog.events.KeyCodes.ENTER) {
					if (autocomplete.hasHighlight()) {
						autocomplete.selectHilited();
					}

					performGoto();
				}
			});
};
