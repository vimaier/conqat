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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.conqat.engine.commons.node.ConQATNodeBase;
import org.conqat.engine.commons.node.IRemovableConQATNode;
import org.conqat.lib.commons.clone.DeepCloneException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;

/**
 * A node representing a single SVN log entry.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: E7D9EFD20675AA02672F29E0E8294EC1
 */
public class SVNLogEntryNode extends ConQATNodeBase implements
		IRemovableConQATNode {

	/** The parent node. */
	private SVNLogEntryRoot parent = null;

	/** Author who commited. */
	private final String author;

	/** Commit date. */
	private final Date date;

	/** Commit revision. */
	private final long revision;

	/** Commit message. */
	private final String message;

	/** Changed changedPaths */
	private final Map<String, SVNLogEntryPath> changedPaths;

	/**
	 * Create a log entry node from a log entry object create of the JavaSVN
	 * library.
	 */
	@SuppressWarnings("unchecked")
	public SVNLogEntryNode(SVNLogEntry logEntry) {
		author = logEntry.getAuthor();
		date = logEntry.getDate();
		revision = logEntry.getRevision();
		message = logEntry.getMessage();
		changedPaths = logEntry.getChangedPaths();

		setValue(SVNLogEntriesScope.KEY_AUTHOR, author);
		setValue(SVNLogEntriesScope.KEY_MESSAGE, message);
		setValue(SVNLogEntriesScope.KEY_DATE, date);
		setValue(SVNLogEntriesScope.KEY_PATHS, new ArrayList<String>(
				changedPaths.keySet()));
	}

	/** Copy constructor. */
	private SVNLogEntryNode(SVNLogEntryNode node) throws DeepCloneException {
		// keys/values are handled here
		super(node);

		author = node.author;
		date = (Date) node.date.clone();
		revision = node.revision;
		message = node.message;
		changedPaths = new HashMap<String, SVNLogEntryPath>(node.changedPaths);
	}

	/** Name string is revision. */
	@Override
	public String getName() {
		return getId();
	}

	/** Returns the revision number. */
	@Override
	public String getId() {
		return String.valueOf(revision);
	}

	/** Returns the author. */
	public String getAuthor() {
		return author;
	}

	/** Returns the date. */
	public Date getDate() {
		return date;
	}

	/** Returns the message. */
	public String getMessage() {
		return message;
	}

	/** Returns the revision. */
	public long getRevision() {
		return revision;
	}

	/** {@inheritDoc} */
	@Override
	public SVNLogEntryNode deepClone() throws DeepCloneException {
		return new SVNLogEntryNode(this);
	}

	/** {@inheritDoc} */
	@Override
	public IRemovableConQATNode[] getChildren() {
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public void remove() {
		if (parent != null) {
			parent.removeNode(this);
		}
	}

	/** {@inheritDoc} */
	@Override
	public SVNLogEntryRoot getParent() {
		return parent;
	}

	/** {@inheritDoc} */
	@Override
	public boolean hasChildren() {
		return false;
	}

	/** Set the parent node. */
	/* package */void setParent(SVNLogEntryRoot parent) {
		this.parent = parent;
	}
}