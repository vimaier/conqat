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
package org.conqat.engine.resource.binary;

import org.conqat.engine.resource.IContentAccessor;
import org.conqat.engine.resource.base.ElementBase;
import org.conqat.lib.commons.clone.DeepCloneException;

/**
 * Basic element that support access to binary content.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 34724 $
 * @ConQAT.Rating GREEN Hash: 31B2A3877DDD2A6E2DD8D5F59FD67382
 */
public class BinaryElement extends ElementBase {

	/** Constructor */
	public BinaryElement(IContentAccessor accessor) {
		super(accessor);
	}

	/** Copy constructor. */
	protected BinaryElement(BinaryElement other) throws DeepCloneException {
		super(other);
	}

	/** {@inheritDoc} */
	@Override
	public BinaryElement deepClone() throws DeepCloneException {
		return new BinaryElement(this);
	}

	/**
	 * Contains all attributes of a {@link BinaryElement} that can be used to
	 * uniquely identify it.
	 */
	protected static class BinaryElementKey {

		/** The path that identifies the element. */
		protected final String path;

		/** Constructor. */
		protected BinaryElementKey(BinaryElement element) {
			path = element.getUniformPath();
		}

		/** {@inheritDoc} */
		@Override
		public boolean equals(Object obj) {
			return (obj instanceof BinaryElementKey)
					&& ((BinaryElementKey) obj).path.equals(path);
		}

		/** {@inheritDoc} */
		@Override
		public int hashCode() {
			return path.hashCode();
		}
	}
}