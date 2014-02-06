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
package org.conqat.lib.commons.equals;

/**
 * This class provides utility methods on equality, usually the methods are used
 * when overriding {@link Object#equals(Object)},
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46756 $
 * @ConQAT.Rating GREEN Hash: 3E27390C56329B89A9B7F1DB86E39D50
 */
public abstract class EqualsUtils {

	/**
	 * Checks if o1 and o2 are either both <code>null</code> (and thus equal) or
	 * if <code>o1.equals(o2)</code> is <code>true</code>. This used to simplify
	 * the implementation of <code>equals()</code> if object attributes have to
	 * be checked on equality, too. The implementation of <code>equals()</code>
	 * would then be as follows:
	 * 
	 * 
	 * <code><pre>
	 * public boolean equals(Object obj) {
	 *   if (this == obj) {
	 *     return true;
	 *   }
	 *   if (obj == null) {
	 *     return false;
	 *   }
	 *   if (getClass() != obj.getClass()) {
	 *     return false;
	 *   }
	 *   MyType other = (MyType) obj;
	 *   
	 *   if (!EqualsUtils.isEqual(attributeA, other.attributeA) 
	 *         || !EqualsUtils.isEqual(attributeB, other.attributeB
	 *         || ... ) {
	 * 	  return false;
	 *   }
	 *   return true;
	 * }
	 * </pre></code>
	 */
	public static boolean isEqual(Object o1, Object o2) {
		if (o1 == null) {
			return o2 == null;
		}
		if (o2 == null) {
			return false;
		}

		return o1.equals(o2);
	}

}
