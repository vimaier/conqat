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
package org.conqat.engine.commons.node;

/**
 * An {@link IConQATNode} which delegates all calls to another node.
 * 
 * @author hummelb
 * @author $Author: hummelb $
 * @version $Rev: 36404 $
 * @ConQAT.Rating GREEN Hash: BAD30A615E9E30B2965A6112916E7C72
 */
public abstract class DelegatingConQATNodeBase implements IConQATNode {

	/** The contained node. */
	protected final IConQATNode inner;

	/** Constructor. */
	protected DelegatingConQATNodeBase(IConQATNode inner) {
		this.inner = inner;
	}

	/** {@inheritDoc} */
	@Override
	public String getId() {
		return inner.getId();
	}

	/** {@inheritDoc} */
	@Override
	public String getName() {
		return inner.getName();
	}

	/** {@inheritDoc} */
	@Override
	public IConQATNode[] getChildren() {
		return inner.getChildren();
	}

	/** {@inheritDoc} */
	@Override
	public IConQATNode getParent() {
		return inner.getParent();
	}

	/** {@inheritDoc} */
	@Override
	public Object getValue(String key) {
		return inner.getValue(key);
	}

	/** {@inheritDoc} */
	@Override
	public boolean hasChildren() {
		return inner.hasChildren();
	}

	/** {@inheritDoc} */
	@Override
	public void setValue(String key, Object value) {
		inner.setValue(key, value);
	}

}