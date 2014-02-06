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
package org.conqat.engine.persistence.store.base;

import java.io.Serializable;
import java.util.Comparator;

import org.conqat.engine.persistence.store.mem.InMemoryStorageSystem;

/**
 * Comparator for byte arrays. Comparison is based on lexicographic comparison
 * of (unsigned) byte content. Null arrays are sorted to the back, so null acts
 * as the largest key. This is crucial for the application in the
 * {@link InMemoryStorageSystem}, where the null key is used as artificial end.
 * 
 * @author hummelb
 * @author $Author: hummelb $
 * @version $Rev: 36296 $
 * @ConQAT.Rating GREEN Hash: E64B358678BBAB2BB10BCA4DF131B10F
 */
public class ByteArrayComparator implements Comparator<byte[]>, Serializable {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** Singleton instance. */
	public static final ByteArrayComparator INSTANCE = new ByteArrayComparator();

	/** {@inheritDoc} */
	@Override
	public int compare(byte[] b1, byte[] b2) {
		if (b1 == null) {
			if (b2 == null) {
				return 0;
			}
			return 1;
		}
		if (b2 == null) {
			return -1;
		}

		for (int i = 0;; i++) {
			if (i >= b1.length) {
				if (i >= b2.length) {
					// same length
					return 0;
				}
				return -1;
			}

			if (i >= b2.length) {
				return 1;
			}

			int v1 = b1[i] & 0xff;
			int v2 = b2[i] & 0xff;
			if (v1 < v2) {
				return -1;
			}
			if (v2 < v1) {
				return 1;
			}
		}
	}

}