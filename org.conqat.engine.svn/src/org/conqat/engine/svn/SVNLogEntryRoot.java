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
package org.conqat.engine.svn;

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.commons.node.ConQATNodeBase;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.IRemovableConQATNode;
import org.conqat.engine.commons.node.NodeConstants;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.lib.commons.clone.DeepCloneException;

/**
 * The root of a list of SVN log messages.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 6AC175CF76C8DBEE63596F33A0E7D597
 */
public class SVNLogEntryRoot extends ConQATNodeBase implements
		IRemovableConQATNode {

	/** URL for this root node. */
	private final String url;

	/** Head revision of the repository. */
	private final int headRevision;

	/** The list of child nodes. */
	private final List<SVNLogEntryNode> children = new ArrayList<SVNLogEntryNode>();

	/** Create a new log entry root. */
	public SVNLogEntryRoot(String url, int headRevision) {
		setValue(NodeConstants.HIDE_ROOT, true);
		setValue(NodeConstants.COMPARATOR, SVNRevisionComparator.getInstance());
		NodeUtils.addToDisplayList(this, SVNLogEntriesScope.KEY_AUTHOR,
				SVNLogEntriesScope.KEY_DATE, SVNLogEntriesScope.KEY_MESSAGE,
				SVNLogEntriesScope.KEY_PATHS);
		this.url = url;
		this.headRevision = headRevision;
	}

	/** Copy constructor. */
	private SVNLogEntryRoot(SVNLogEntryRoot root) throws DeepCloneException {
		super(root);
		url = root.url;
		headRevision = root.headRevision;
		for (SVNLogEntryNode c : root.children) {
			addChild(c.deepClone());
		}
	}

	/** Name string contains revision, message, author and date. */
	@Override
	public String getName() {
		StringBuilder sb = new StringBuilder();
		sb.append("SVN log messages for ");
		sb.append(url);
		sb.append(" (HEAD: ");
		sb.append(headRevision);
		sb.append(", # Entries: ");
		sb.append(children.size());

		if (hasChildren()) {
			SVNLogEntryNode[] nodes = getChildren();
			SVNLogEntryNode firstNode = nodes[nodes.length - 1];
			SVNLogEntryNode lastNode = nodes[0];
			sb.append(", ");
			sb.append(firstNode.getDate());
			sb.append(" - ");
			sb.append(lastNode.getDate());
		}
		sb.append(")");
		return sb.toString();
	}

	/** Returns constant string "SVN Root" since this is root. */
	@Override
	public String getId() {
		return "SVN Root";
	}

	/** {@inheritDoc} */
	@Override
	public SVNLogEntryRoot deepClone() throws DeepCloneException {
		return new SVNLogEntryRoot(this);
	}

	/** {@inheritDoc} */
	@Override
	public SVNLogEntryNode[] getChildren() {
		return children.toArray(new SVNLogEntryNode[children.size()]);
	}

	/** {@inheritDoc} */
	@Override
	public void remove() {
		// nothing to do.
	}

	/** {@inheritDoc} */
	@Override
	public IConQATNode getParent() {
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public boolean hasChildren() {
		return !children.isEmpty();
	}

	/** Add a child. */
	/* package */void addChild(SVNLogEntryNode child) {
		children.add(child);
		child.setParent(this);
	}

	/** Remove a child node. */
	/* package */void removeNode(SVNLogEntryNode node) {
		children.remove(node);
	}
}