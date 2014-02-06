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

import java.util.ArrayList;

import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.clone.DeepCloneException;

/**
 * A simple ConQAT node whose children are managed as a list, i.e. multiple
 * children can have the same name.
 * 
 * @author Florian Deissenboeck
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: 7C0402F1C5FE2896C0885365BC1650C4
 */
public class ListNode extends ConQATNodeBase implements IRemovableConQATNode {

	/** Child list (initialized lazily). */
	private ArrayList<ListNode> children;

	/** The parent node. */
	private ListNode parent;

	/** Node id. */
	private final String id;

	/** Node name. */
	private final String name;

	/** Create node with dummy id. */
	public ListNode() {
		this("<root>");
	}

	/** Create node with id. */
	public ListNode(String id) {
		this(id, id);
	}

	/** Create node with id. */
	public ListNode(String id, String name) {
		this.id = id;
		this.name = name;
	}

	/** Copy constructor. */
	protected ListNode(ListNode node) throws DeepCloneException {
		super(node);
		id = node.id;
		name = node.name;
		if (node.hasChildren()) {
			for (ListNode child : node.children) {
				addChild(child.deepClone());
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public ListNode deepClone() throws DeepCloneException {
		return new ListNode(this);
	}

	/** {@inheritDoc} */
	@Override
	public String getId() {
		return id;
	}

	/** Returns id. */
	@Override
	public String getName() {
		return name;
	}

	/** {@inheritDoc} */
	@Override
	public ListNode[] getChildren() {
		if (children == null) {
			return null;
		}
		return children.toArray(new ListNode[children.size()]);
	}

	/** {@inheritDoc} */
	@Override
	public void remove() {
		if (parent != null) {
			CCSMAssert.isFalse(parent.children == null,
					"Parent must have children");
			parent.children.remove(this);
			setParent(null);
		}
	}

	/** Set parent. */
	private void setParent(ListNode parent) {
		this.parent = parent;
	}

	/** {@inheritDoc} */
	@Override
	public IConQATNode getParent() {
		return parent;
	}

	/** {@inheritDoc} */
	@Override
	public boolean hasChildren() {
		if (children == null) {
			return false;
		}
		return !children.isEmpty();
	}

	/** Add child node. */
	public void addChild(ListNode child) {
		if (children == null) {
			children = new ArrayList<ListNode>();
		}
		children.add(child);
		child.setParent(this);
	}

}