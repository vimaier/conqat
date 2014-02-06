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

import org.conqat.lib.cqddl.function.CQDDLCheck;
import org.conqat.lib.cqddl.function.CQDDLEvaluationException;
import org.conqat.lib.cqddl.function.ICQDDLFunction;

import org.conqat.lib.commons.assessment.Assessment;
import org.conqat.lib.commons.assessment.ETrafficLightColor;
import org.conqat.lib.commons.collections.PairList;
import org.conqat.lib.commons.enums.EnumUtils;

/**
 * {@link ICQDDLFunction} for {@link Assessment}.
 * 
 * @author hummelb
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: 20B0FB1B5D5BD6903174C2152753CA51
 */
public class AssessmentCQDDLFunction implements ICQDDLFunction {

	/** {@inheritDoc} */
	@Override
	public Assessment eval(PairList<String, Object> params)
			throws CQDDLEvaluationException {
		Assessment result = new Assessment();
		for (Object o : CQDDLCheck.asList(params)) {
			if (o instanceof ETrafficLightColor) {
				result.add((ETrafficLightColor) o);
			} else if (o instanceof Assessment) {
				result.add((Assessment) o);
			} else if (o instanceof String) {
				ETrafficLightColor color = EnumUtils.valueOf(
						ETrafficLightColor.class, (String) o);
				if (color == null) {
					throw new CQDDLEvaluationException(
							"No valid traffic light color: " + o);
				}
				result.add(color);
			} else {
				throw new CQDDLEvaluationException(
						"Do not know how to deal with parameter of type "
								+ o.getClass());
			}
		}
		return result;
	}
}