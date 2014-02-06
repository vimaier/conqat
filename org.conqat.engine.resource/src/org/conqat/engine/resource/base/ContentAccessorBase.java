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
package org.conqat.engine.resource.base;

import java.io.IOException;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IContentAccessor;
import org.conqat.lib.commons.clone.IDeepCloneable;

/**
 * Base class for content accessors.
 * 
 * @author deissenb
 * @author $Author: juergens $
 * @version $Rev: 35198 $
 * @ConQAT.Rating GREEN Hash: 6DEE324C44ABC8CB21D77CDB49BA9837
 */
public abstract class ContentAccessorBase implements IContentAccessor,
		IDeepCloneable {

	/** The uniform path. */
	private final String uniformPath;

	/** Create content accessor. */
	protected ContentAccessorBase(String uniformPath) {
		this.uniformPath = uniformPath;
	}

	/** {@inheritDoc} */
	@Override
	public byte[] getContent() throws ConQATException {
		try {
			return readContent();
		} catch (IOException ex) {
			throw new ConQATException("Couldn't read content for "
					+ getLocation() + ":  " + ex.getMessage(), ex);
		}
	}

	/** {@inheritDoc} */
	@Override
	public String getUniformPath() {
		return uniformPath;
	}

	/** Template method to read content. */
	protected abstract byte[] readContent() throws IOException, ConQATException;

	/** Returns this. */
	@Override
	public IDeepCloneable deepClone() {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof IContentAccessor)) {
			return false;
		}

		IContentAccessor otherContentAccessor = (IContentAccessor) other;
		return getUniformPath().equals(otherContentAccessor.getUniformPath());
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return getUniformPath().hashCode();
	}
}