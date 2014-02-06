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

import org.conqat.lib.commons.collections.CounterSet;
import org.conqat.lib.commons.html.HTMLWriter;

/**
 * A HTML formatter for {@link CounterSet}s.
 * 
 * @author hummelb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: F5EA50240408780A6E86A1D8FF4A80D3
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class CounterSetHTMLFormatter implements IHTMLFormatter<CounterSet> {

	/** {@inheritDoc} */
	@Override
	public void formatObject(CounterSet set, HTMLWriter writer) {
		for (Object key : set.getKeys()) {
			writer.addText(key.toString());
			writer.addText(" : ");
			writer.addText(Integer.toString(set.getValue(key)));
			writer.addClosedElement(BR);
		}
	}

}