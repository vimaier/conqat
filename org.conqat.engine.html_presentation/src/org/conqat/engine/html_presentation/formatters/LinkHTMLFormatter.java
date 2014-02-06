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
import org.conqat.engine.html_presentation.util.HTMLLink;

/**
 * A HTML formatter for {@link HTMLLink}s.
 * 
 * @author hummelb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 6FC494B695E51E3F96BF42D0A35D943A
 */
public class LinkHTMLFormatter implements IHTMLFormatter<HTMLLink> {

	/** Adds the HTML representation of the link. */
	@Override
	public void formatObject(HTMLLink link, HTMLWriter writer) {
		link.writeTo(writer);
	}

}