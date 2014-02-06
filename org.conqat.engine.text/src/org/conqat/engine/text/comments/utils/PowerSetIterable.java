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
package org.conqat.engine.text.comments.utils;

import java.util.Iterator;

/**
 * A class providing an iterator over all elements of the power set of a given
 * set of elements.
 * 
 * @author Marco13 (http://www.java-forum.org/members/8636.html)
 * @author $Author: steidl $
 * @version $Rev: 43756 $
 * @ConQAT.Rating RED Hash: 64BAC6E0A3F33D311DB108B357EB4577
 */
public class PowerSetIterable<String> implements Iterable<String[]>

{
	private String input[];
	private int numElements;

	/**
	 * A magic utility method that happens to return the number of bits that are
	 * set to '1' in the given number.
	 * 
	 * @param n
	 *            The number whose bits should be counted
	 * @return The number of bits that are '1' in n
	 */
	public static int countBits(int n) {
		int m = n - ((n >> 1) & 033333333333) - ((n >> 2) & 011111111111);
		return ((m + (m >> 3)) & 030707070707) % 63;
	}

	public PowerSetIterable(String[] input) {
		this.input = input.clone();
		numElements = 1 << input.length;
	}

	public Iterator<String[]> iterator() {
		return new Iterator<String[]>() {
			int current = 0;

			public boolean hasNext() {
				return current < numElements;
			}

			public String[] next() {
				int size = countBits(current);

				@SuppressWarnings("unchecked")
				String[] element = (String[]) java.lang.reflect.Array
						.newInstance(input.getClass().getComponentType(), size);

				// Insert into the current power set element
				// all elements of the input set that are at
				// indices where the current counter value
				// has a '1' in its binary representation
				int n = 0;
				for (int i = 0; i < input.length; i++) {
					long b = 1 << i;
					if ((current & b) != 0) {
						element[n++] = input[i];
					}
				}
				current++;
				return element;
			}

			public void remove() {
				throw new UnsupportedOperationException(
						"May not remove elements from a power set");
			}
		};

	}
}