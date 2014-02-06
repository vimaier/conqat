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
package org.conqat.engine.resource.scope.memory;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IContentAccessor;
import org.conqat.engine.resource.base.ContentAccessorBase;

/**
 * A content accessor that has its content in memory.
 * 
 * @author hummelb
 * @author $Author: juergens $
 * @version $Rev: 35198 $
 * @ConQAT.Rating GREEN Hash: C0320638331BDFCC5A9EC8FA7A318E7F
 */
public class InMemoryContentAccessor extends ContentAccessorBase {

	/** The content. */
	private final byte[] content;

	/** Constructor. */
	public InMemoryContentAccessor(String uniformPath, byte[] content) {
		super(uniformPath);
		this.content = content;
	}

	/** {@inheritDoc} */
	@Override
	protected byte[] readContent() {
		return content.clone();
	}

	/** {@inheritDoc} */
	@Override
	public String getLocation() {
		return getUniformPath();
	}

	/** Always throws. */
	@Override
	public IContentAccessor createRelative(String relativePath)
			throws ConQATException {
		throw new ConQATException("Relative paths not supported in memory.");
	}

	/** {@inheritDoc} */
	@Override
	public String createRelativeUniformPath(String relativePath)
			throws ConQATException {
		throw new ConQATException("Relative paths not supported in memory.");
	}
}