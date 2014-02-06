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

import java.util.regex.Pattern;

import org.conqat.lib.cqddl.function.CQDDLEvaluationException;
import org.conqat.lib.cqddl.function.ICQDDLFunction;

import org.conqat.lib.commons.collections.PairList;
import org.conqat.engine.commons.pattern.PatternList;

/**
 * {@link ICQDDLFunction} for {@link PatternList}.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: 289F21F9F6C5F4BAA33D5249DF7BA085
 */
public class PatternListCQDDLFunction implements ICQDDLFunction {

	/** {@inheritDoc} */
	@Override
	public Object eval(PairList<String, Object> params)
			throws CQDDLEvaluationException {
		PatternList result = new PatternList();
		for (int i = 0; i < params.size(); ++i) {
			Object o = params.getSecond(i);
			if (o instanceof PatternList) {
				result.addAll((PatternList) o);
			} else if (o instanceof Pattern) {
				result.add((Pattern) o);
			} else if (o instanceof String) {
				result.add(Pattern.compile((String) o));
			} else {
				throw new CQDDLEvaluationException("Unsupported object type: "
						+ o.getClass());
			}
		}
		return result;
	}
}