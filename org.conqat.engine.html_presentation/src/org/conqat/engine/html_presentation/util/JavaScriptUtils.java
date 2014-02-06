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
package org.conqat.engine.html_presentation.util;

import org.conqat.lib.commons.string.StringUtils;

/**
 * Utility methods for dealing with JavaScript.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 8AC1FA3D1DFD9EE321E6CADFA1E4F19A
 */
public class JavaScriptUtils {

	/**
	 * Escapes critical characters in JavaScript strings: backslash, double
	 * quotes, new lines.
	 */
	public static String escapeJavaScript(String s) {
		// escape backslash
		s = s.replaceAll("\\\\", "\\\\\\\\");
		// escape double quote
		s = s.replaceAll("\"", "\\\\\"");
		// escape newlines
		s = StringUtils.replaceLineBreaks(s);
		return s;
	}
}
