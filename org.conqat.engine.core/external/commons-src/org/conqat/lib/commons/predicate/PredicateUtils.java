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
package org.conqat.lib.commons.predicate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Utility methods for working with predicates.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 45908 $
 * @ConQAT.Rating YELLOW Hash: 2D9753B9085E9917D88FC92D1F4E295D
 */
public class PredicateUtils {

	/**
	 * Returns all input elements that are contained in the set described by the
	 * predicate.
	 * 
	 * @param <T>
	 *            Type in collection.
	 */
	public static <T> List<T> obtainContained(Collection<T> input,
			IPredicate<? super T> predicate) {
		List<T> result = new ArrayList<T>();
		for (T t : input) {
			if (predicate.isContained(t)) {
				result.add(t);
			}
		}
		return result;
	}

	/**
	 * Returns all input elements that are <b>not</b> contained in the set
	 * described by the predicate.
	 */
	public static <T> List<T> obtainNonContained(Collection<T> input,
			IPredicate<? super T> predicate) {
		return obtainContained(input, InvertingPredicate.create(predicate));
	}
}
