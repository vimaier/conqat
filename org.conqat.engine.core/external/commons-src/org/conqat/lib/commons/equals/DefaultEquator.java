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
package org.conqat.lib.commons.equals;

/**
 * {@link IEquator} using the normal equals (but respecting null).
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46755 $
 * @ConQAT.Rating GREEN Hash: D8DA6D278F008E7BB6321BA7DAF9AF0B
 */
public class DefaultEquator implements IEquator<Object> {

	/** Singleton instance. */
	public static final DefaultEquator INSTANCE = new DefaultEquator();

	/** Constructor */
	private DefaultEquator() {
		// enforce use of singleton instance
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object element1, Object element2) {
		return EqualsUtils.isEqual(element1, element2);
	}

}