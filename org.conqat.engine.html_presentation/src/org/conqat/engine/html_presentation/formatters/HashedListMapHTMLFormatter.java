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
import org.conqat.lib.commons.collections.HashedListMap;
import org.conqat.lib.commons.html.HTMLWriter;

/**
 * A HTML formatter for {@link HashedListMap}s.
 * 
 * @author hummelb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 22F5A135A29A048B217A9464C2D9A36A
 */
@SuppressWarnings("unchecked")
public class HashedListMapHTMLFormatter implements
		IHTMLFormatter<HashedListMap> {

	/** {@inheritDoc} */
	@Override
	public void formatObject(HashedListMap map, HTMLWriter writer) {
		for (Object key : map.getKeys()) {
			for (Object value : map.getList(key)) {
				writer.addText(key.toString());
				writer.addText(" : ");
				writer.addText(value.toString());
				writer.addClosedElement(BR);
			}
		}
	}

}