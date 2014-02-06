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
package org.conqat.lib.cqddl.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.conqat.lib.commons.collections.PairList;

/**
 * A collection of utility methods for checking the parameters passed to a
 * {@link ICQDDLFunction}.
 * 
 * @author hummelb
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating YELLOW Hash: 5EB150175FC6D8DF0A287B36799F3D33
 */
public class CQDDLCheck {

	/** Ensures that the given parameter list is empty. */
	public static void empty(PairList<String, Object> params)
			throws CQDDLEvaluationException {
		if (!params.isEmpty()) {
			throw new CQDDLEvaluationException("Expected empty parameter list!");
		}
	}

	/**
	 * Ensures that the values of the parameter object are of the provided
	 * types.
	 */
	public static void parameters(PairList<String, Object> params,
			Class<?>... classes) throws CQDDLEvaluationException {
		if (params.size() != classes.length) {
			throw new CQDDLEvaluationException("Number of parameters differs!");
		}

		for (int i = 0; i < params.size(); ++i) {
			if (params.getSecond(i) != null
					&& !classes[i].isAssignableFrom(params.getSecond(i)
							.getClass())) {
				throw new CQDDLEvaluationException("Parameter " + (i + 1)
						+ " must be of type " + classes[i]);
			}
		}
	}

	/**
	 * Returns the values as a list.
	 * 
	 * @param clazz
	 *            the type expected for the values of the list.
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> asList(PairList<String, Object> params,
			Class<T> clazz) throws CQDDLEvaluationException {
		List<T> values = new ArrayList<T>();
		for (int i = 0; i < params.size(); ++i) {
			if (params.getSecond(i) != null
					&& !clazz.isAssignableFrom(params.getSecond(i).getClass())) {
				throw new CQDDLEvaluationException(
						"Invalid value type. Expected " + clazz);
			}
			values.add((T) params.getSecond(i));
		}
		return values;
	}

	/** Returns the values as a list. */
	public static List<Object> asList(PairList<String, Object> parms)
			throws CQDDLEvaluationException {
		return asList(parms, Object.class);
	}

	/**
	 * Returns a map representation of the parameters. If keys are not unique or
	 * a null key occurs, an exception is thrown.
	 */
	public static Map<String, Object> asMap(PairList<String, Object> params)
			throws CQDDLEvaluationException {
		return asMap(params, Object.class);
	}

	/**
	 * Returns a map representation of the parameters. If keys are not unique or
	 * a null key occurs, an exception is thrown.
	 * 
	 * @param clazz
	 *            the type expected for the values of the map.
	 */
	@SuppressWarnings("unchecked")
	public static <T> Map<String, T> asMap(PairList<String, Object> params,
			Class<T> clazz) throws CQDDLEvaluationException {
		Map<String, T> map = new HashMap<String, T>();
		for (int i = 0; i < params.size(); ++i) {
			String key = params.getFirst(i);
			if (key == null) {
				throw new CQDDLEvaluationException(
						"Encountered null key (which is not allowed for maps)!");
			}
			if (map.containsKey(key)) {
				throw new CQDDLEvaluationException("Duplicate key: " + key);
			}
			if (params.getSecond(i) != null
					&& !clazz.isAssignableFrom(params.getSecond(i).getClass())) {
				throw new CQDDLEvaluationException(
						"Invalid value type. Expected " + clazz);
			}

			map.put(key, (T) params.getSecond(i));
		}
		return map;
	}
}