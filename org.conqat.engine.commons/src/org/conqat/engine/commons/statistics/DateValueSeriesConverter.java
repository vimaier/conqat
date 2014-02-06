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
package org.conqat.engine.commons.statistics;

import java.util.Date;
import java.util.Map.Entry;

import org.conqat.engine.commons.node.ListNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.util.ConQATInputProcessorBase;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 41450 $
 * @ConQAT.Rating GREEN Hash: FE0256EE759E0BE617D47EE0925A5E04
 */
@AConQATProcessor(description = "Converts a DateValueSeries into a rooted list of ConQAT nodes.")
public class DateValueSeriesConverter extends
		ConQATInputProcessorBase<DateValueSeries> {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Key under which value is stored", type = "java.lang.Double")
	public static final String VALUE_KEY = "value";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Key under which value is stored", type = "java.util.Date")
	public static final String DATE_KEY = "date";

	/** {@inheritDoc} */
	@Override
	public ListNode process() {
		ListNode root = new ListNode();
		NodeUtils.addToDisplayList(root, DATE_KEY, VALUE_KEY);

		for (Entry<Date, Double> entry : input.getValues().entrySet()) {
			ListNode node = new ListNode("Time: " + entry.getKey().getTime());
			node.setValue(VALUE_KEY, entry.getValue());
			node.setValue(DATE_KEY, entry.getKey());
			root.addChild(node);
		}

		return root;
	}

}
