/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
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
package org.conqat.engine.html_presentation.layouters;

import static org.conqat.lib.commons.html.EHTMLElement.BODY;
import static org.conqat.lib.commons.html.EHTMLElement.PRE;

import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.html_presentation.util.LayouterBase;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 45580 $
 * @ConQAT.Rating GREEN Hash: 5C7DC302DD10B6B1A6F55B48F2D6B88D
 */
@AConQATProcessor(description = "Layouts plain text as pre-formatted text, e.g. to output external log files.")
public class PlainTextLayouter extends LayouterBase {

	/** Name of the icon file for pages. */
	private static final String PAGE_ICON_NAME = "page.gif";

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "text", attribute = "value", description = "The plain text that should be displayed.")
	public String text;

	/** {@inheritDoc} */
	@Override
	protected String getIconName() {
		return PAGE_ICON_NAME;
	}

	/** {@inheritDoc} */
	@Override
	protected void layoutPage() {
		writer.openElement(BODY);
		writer.addClosedTextElement(PRE, text);
		writer.closeElement(BODY);
	}
}
