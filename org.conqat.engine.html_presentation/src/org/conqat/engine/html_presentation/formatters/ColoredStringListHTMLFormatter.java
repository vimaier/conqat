/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 The ConQAT Project                                   |
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
package org.conqat.engine.html_presentation.formatters;

import static org.conqat.lib.commons.html.EHTMLElement.BR;

import org.conqat.engine.html_presentation.color.ColoredStringList;
import org.conqat.lib.commons.color.ColorUtils;
import org.conqat.lib.commons.html.CSSDeclarationBlock;
import org.conqat.lib.commons.html.ECSSProperty;
import org.conqat.lib.commons.html.EHTMLAttribute;
import org.conqat.lib.commons.html.EHTMLElement;
import org.conqat.lib.commons.html.HTMLWriter;

/**
 * A HTML formatter for {@link ColoredStringList}s.
 * 
 * @author $Author: heineman $
 * @version $Rev: 41618 $
 * @ConQAT.Rating GREEN Hash: 939C2DB14A0C104DDC576569713C11F1
 */
public class ColoredStringListHTMLFormatter implements
		IHTMLFormatter<ColoredStringList> {

	/** {@inheritDoc} */
	@Override
	public void formatObject(ColoredStringList strings, HTMLWriter writer) {
		for (int i = 0; i < strings.size(); ++i) {
			if (i > 0) {
				writer.addClosedElement(BR);
			}
			CSSDeclarationBlock style = new CSSDeclarationBlock(
					ECSSProperty.COLOR, ColorUtils.toHtmlString(strings
							.getSecond(i)));
			writer.addClosedTextElement(EHTMLElement.SPAN, strings.getFirst(i),
					EHTMLAttribute.STYLE, style);
		}
	}

}