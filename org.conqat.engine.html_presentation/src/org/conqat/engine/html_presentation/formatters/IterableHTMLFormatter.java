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

import org.conqat.engine.html_presentation.util.PresentationUtils;
import org.conqat.lib.commons.html.HTMLWriter;

/**
 * A HTML formatter for {@link Iterable}s.
 * 
 * @author hummelb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: C4DE746A717DC2462C74D557A0F234A2
 */
@SuppressWarnings({ "rawtypes" })
public class IterableHTMLFormatter implements IHTMLFormatter<Iterable> {

	/**
	 * Adds the entries of the {@link Iterable} line by line, separated by break
	 * tags.
	 */
	@Override
	public void formatObject(Iterable collection, HTMLWriter writer) {
		for (Object item : collection) {
			PresentationUtils.appendValue(item, writer);
			writer.addClosedElement(BR);
		}
	}

}