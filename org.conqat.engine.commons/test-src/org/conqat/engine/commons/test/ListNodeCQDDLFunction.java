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
package org.conqat.engine.commons.test;

import java.util.Map;
import java.util.Map.Entry;

import org.conqat.lib.cqddl.function.CQDDLCheck;
import org.conqat.lib.cqddl.function.CQDDLEvaluationException;
import org.conqat.lib.cqddl.function.ICQDDLFunction;

import org.conqat.lib.commons.collections.PairList;
import org.conqat.engine.commons.node.ListNode;

/**
 * {@link ICQDDLFunction} for {@link ListNode}.
 * 
 * @author hummelb
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: 8035817FD72755A8F9F9C1B48BC73E10
 */
public class ListNodeCQDDLFunction implements ICQDDLFunction {

	/** {@inheritDoc} */
	@Override
	public ListNode eval(PairList<String, Object> params)
			throws CQDDLEvaluationException {

		if (params.size() == 0 || !(params.getSecond(0) instanceof String)) {
			throw new CQDDLEvaluationException(
					"First parameter must be ID of node!");
		}
		String id = (String) params.getSecond(0);
		ListNode result = new ListNode(id);

		if (params.size() > 1
				&& !(params.getSecond(1) instanceof PairList<?, ?>)) {
			throw new CQDDLEvaluationException(
					"Second parameter must be PairList giving key/values!");
		}

		@SuppressWarnings("unchecked")
		Map<String, Object> keyValues = CQDDLCheck
				.asMap((PairList<String, Object>) params.getSecond(1));
		for (Entry<String, Object> e : keyValues.entrySet()) {
			result.setValue(e.getKey(), e.getValue());
		}

		for (int i = 2; i < params.size(); ++i) {
			if (!(params.getSecond(i) instanceof ListNode)) {
				throw new CQDDLEvaluationException(
						"Sub-nodes must be ListNodes!");
			}
			result.addChild((ListNode) params.getSecond(i));
		}
		return result;
	}
}