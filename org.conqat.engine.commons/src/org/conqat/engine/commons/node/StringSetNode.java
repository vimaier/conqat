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

import org.conqat.lib.commons.clone.DeepCloneException;

/**
 * A simple ConQAT node whose children are managed as a set.
 * 
 * @author Florian Deissenboeck
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: 7C5F89C69F5BC1092167A6DE7309B7AB
 */
public class StringSetNode extends ConQATGeneralNodeBase<StringSetNode> {

	/** Node id. */
	private final String id;

	/** Node name. */
	private final String name;

	/** Create node with dummy id and name. */
	public StringSetNode() {
		this("<root>");
	}

	/**
	 * Create node with id only. In this case the name of the node will be equal
	 * to its id.
	 */
	public StringSetNode(String id) {
		this.id = id;
		name = id;
	}

	/** Create node with id and name. */
	public StringSetNode(String id, String name) {
		this.id = id;
		this.name = name;
	}

	/** Copy constructor. */
	protected StringSetNode(StringSetNode node) throws DeepCloneException {
		super(node);
		id = node.id;
		name = node.name;
	}

	/** {@inheritDoc} */
	@Override
	public StringSetNode deepClone() throws DeepCloneException {
		return new StringSetNode(this);
	}

	/** {@inheritDoc} */
	@Override
	public String getId() {
		return id;
	}

	/** {@inheritDoc} */
	@Override
	public String getName() {
		return name;
	}

	/** {@inheritDoc} */
	@Override
	protected StringSetNode[] allocateArray(int size) {
		return new StringSetNode[size];
	}

}