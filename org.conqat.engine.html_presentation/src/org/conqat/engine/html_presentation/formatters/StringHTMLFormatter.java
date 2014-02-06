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
import org.conqat.lib.commons.string.StringUtils;

/**
 * A HTML formatter for strings.
 * 
 * @author hummelb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: CE9B22FAE2554EF6A29DF4E7A6020D31
 */
public class StringHTMLFormatter implements IHTMLFormatter<String> {

	/**
	 * Adds the string as text, but use the non-breakable space if the string is
	 * empty.
	 */
	@Override
	public void formatObject(String string, HTMLWriter writer) {
		if (StringUtils.isEmpty(string)) {
			writer.addRawString("&nbsp;");
		} else {
			writer.addText(string);
		}
	}

}