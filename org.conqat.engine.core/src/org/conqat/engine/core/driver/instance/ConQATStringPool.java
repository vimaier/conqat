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
package org.conqat.engine.core.driver.instance;

import java.util.HashMap;

/**
 * Implementation of a ConQAT wide processor local string pool. The pooling
 * strategy is not smart with respect to garbage collection, i.e., the interned
 * strings are not automatically released. However, the pool is only processor
 * local, so a full clean (which releases all strings) is performed after each
 * processor execution.
 * 
 * @author $Author: juergens $
 * @version $Rev: 35194 $
 * @ConQAT.Rating GREEN Hash: C8EF137D2DB660A0F196659E055268A2
 */
public class ConQATStringPool {

	/** The string pool. */
	private static final HashMap<String, String> STRING_POOL = new HashMap<String, String>();

	/**
	 * Interns the string into the pool and returns the interned version. The
	 * returned string is guaranteed to be unique within the context of the pool
	 * (i.e. comparison can be made by reference). This guarantee is valid only
	 * between consecutive calls to {@link #clear()}, which happen automatically
	 * at the end of a processor execution.
	 */
	public static synchronized String intern(String string) {
		String interned = STRING_POOL.get(string);
		if (interned == null) {
			STRING_POOL.put(string, string);
			return string;
		}
		return interned;
	}

	/** Clears the string pool. */
	public static synchronized void clear() {
		STRING_POOL.clear();
	}
}