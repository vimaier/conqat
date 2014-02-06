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
package org.conqat.engine.code_clones.result;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.code_clones.core.utils.StableCloneComparator;
import org.conqat.engine.commons.node.ConQATNodeBase;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.clone.DeepCloneException;
import org.conqat.lib.commons.collections.CollectionUtils;

/**
 * This class represents a clone class. It works as an adapter between
 * {@link CloneClass}es and {@link org.conqat.engine.commons.node.IConQATNode}s.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 35327 $
 * @ConQAT.Rating GREEN Hash: 78E193C8D0F0896DB228F5C6B8E028DB
 */
public class CloneClassNode extends ConQATNodeBase {

	/** The clone class represented by this node */
	private final CloneClass cloneClass;

	/**
	 * The children for this node, that is, the nodes representing the clones of
	 * this clone class.
	 */
	private final List<CloneNode> children = new ArrayList<CloneNode>();

	/** The parent of this node. */
	private DetectionResultRootNode parent = null;

	/**
	 * Create clone class node from a clone class. This automatically creates
	 * the child nodes ({@link CloneNode}).
	 */
	public CloneClassNode(CloneClass cloneClass, String rootPath) {
		CCSMAssert.isNotNull(cloneClass, "CloneClass must not be null");
		this.cloneClass = cloneClass;

		storeMetrics(cloneClass);

		for (Clone clone : CollectionUtils.sort(cloneClass.getClones(),
				StableCloneComparator.INSTANCE)) {
			addChild(new CloneNode(clone, rootPath));
		}
	}

	/** Set clone classes metric values */
	private void storeMetrics(CloneClass cloneClass) {
		setValue(CloneListBuilderBase.CARDINALITY, cloneClass.size());
		setValue(CloneListBuilderBase.NORMALIZED_LENGTH,
				cloneClass.getNormalizedLength());
		setValue(CloneListBuilderBase.VOLUME, cloneClass.getNormalizedLength()
				* cloneClass.size());
	}

	/** Copy constructor. */
	private CloneClassNode(CloneClassNode node) throws DeepCloneException {
		super(node);
		cloneClass = node.cloneClass;
		for (CloneNode c : children) {
			addChild(c.deepClone());
		}
	}

	/** Add a new node. */
	/* package */void addChild(CloneNode node) {
		children.add(node);
		node.setParent(this);
	}

	/** {@inheritDoc} */
	@Override
	public String getId() {
		return Long.toString(cloneClass.getId());
	}

	/** {@inheritDoc} */
	@Override
	public String getName() {
		return "Clone Class [" + cloneClass.getId() + "]";
	}

	/** {@inheritDoc} */
	@Override
	public CloneClassNode deepClone() throws DeepCloneException {
		return new CloneClassNode(this);
	}

	/** {@inheritDoc} */
	@Override
	public CloneNode[] getChildren() {
		return children.toArray(new CloneNode[children.size()]);
	}

	/** {@inheritDoc} */
	@Override
	public DetectionResultRootNode getParent() {
		return parent;
	}

	/** {@inheritDoc} */
	@Override
	public boolean hasChildren() {
		return !children.isEmpty();
	}

	/** Set the parent for this node. */
	/* package */void setParent(DetectionResultRootNode node) {
		parent = node;
	}

	/** Removes the given node from the child list. */
	/* package */void removeNode(CloneNode node) {
		children.remove(node);
	}

	/** Returns underlying {@link CloneClass} */
	public CloneClass getCloneClass() {
		return cloneClass;
	}

	/** Clone class comparator */
	public static class CloneClassNodeComparator implements
			Comparator<CloneClassNode> {

		/** Comparator used to compare clone classes */
		private final Comparator<CloneClass> cloneClassComparator;

		/** Constructor */
		public CloneClassNodeComparator(
				Comparator<CloneClass> cloneClassComparator) {
			this.cloneClassComparator = cloneClassComparator;
		}

		/** {@inheritDoc} */
		@Override
		public int compare(CloneClassNode o1, CloneClassNode o2) {
			if (o1 == null && o2 == null) {
				return 0;
			}
			if (o1 == null) {
				return -1;
			}
			if (o2 == null) {
				return 1;
			}

			return cloneClassComparator.compare(o1.getCloneClass(),
					o2.getCloneClass());
		}

	}
}