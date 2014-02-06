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

import org.conqat.lib.commons.html.HTMLWriter;
import org.conqat.engine.html_presentation.util.PresentationUtils;

/**
 * A HTML formatter for numbers.
 * 
 * @author hummelb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 485D1B9C1DBD3705E5A36ABAAEF5ABA3
 */
public class NumberHTMLFormatter implements IHTMLFormatter<Number> {

	/** Adds the formatted number as text. */
	@Override
	public void formatObject(Number number, HTMLWriter writer) {
		writer.addText(PresentationUtils.NUMBER_FORMATTER.format(number));
	}

}