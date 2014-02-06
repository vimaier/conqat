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
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.io.SVNRepository;

/**
 * A scope that consists of a list of SVN log entries. The scope's root element
 * is a {@link SVNLogEntryRoot} which holds a list of
 * {@link org.conqat.engine.svn.SVNLogEntryNode}s.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 51FA29E9F442B4C7F5AD47A846F0A094
 */
@AConQATProcessor(description = "This processor extracts log messages from a remote subversion repository."
		+ " You can specify a time range using the 'range' parameter."
		+ " Paths to include are specified via the 'target' parameter."
		+ " When neiter a range nor paths are specified, execution will"
		+ " be very slow on repositories with many revisions."
		+ " Authentifaction works by providing user name and password."
		+ " If your local subversion configuration allows authentication "
		+ " credentials storage, authentication information will be stored"
		+ " after the first sucessfull login. Afterwards you don't need to"
		+ " provide authentification information on this machine."
		+ " Additionally you may specify authors to include or exclude"
		+ " from the scope. By default all authors are included and no author"
		+ " is excluded. Exclusion is stronger than inclusion.")
public class SVNLogEntriesScope extends SVNProcessorBase {

	/** Key for message. */
	@AConQATKey(description = "The message for the commit entry.", type = "java.lang.String")
	public static final String KEY_MESSAGE = "Message";

	/** Key for author. */
	@AConQATKey(description = "The author for the commit entry.", type = "java.lang.String")
	public static final String KEY_AUTHOR = "Author";

	/** Key for date. */
	@AConQATKey(description = "The date of the entry.", type = "java.util.Date")
	public static final String KEY_DATE = "Date";

	/** Key for path list */
	@AConQATKey(description = "The files changed for the entry.", type = "java.util.List<String>")
	public static final String KEY_PATHS = "ChangedPaths";

	/** Constant identifying the head revision. */
	private final static int INVALID_REVISION = -1;

	/** The root that holds all entries. */
	private SVNLogEntryRoot root;

	/** Default start revision. */
	private int startRevision = 1;

	/** Default range length. */
	private int daysBack = INVALID_REVISION;

	/** also collect changed paths? */
	private boolean changedPaths = false;

	/** stop on copy? */
	private boolean strictNode = true;

	/** Set of included authors. */
	private final Set<String> includedAuthors = new HashSet<String>();

	/** Set of excluded authors. */
	private final Set<String> excludedAuthors = new HashSet<String>();

	/** The head revision of the repository. */
	private int headRevision;

	/** Paths to include. */
	private final List<String> paths = new ArrayList<String>();

	/** The SVN repository */
	private SVNRepository repository;

	/** Specify range. */
	@AConQATParameter(name = "range", maxOccurrences = 1, description = "Number of days before now")
	public void setRange(
			@AConQATAttribute(name = "days-back", description = "Number of days") int daysBack) {
		this.daysBack = daysBack;
	}

	/** Give target paths. */
	@AConQATParameter(name = "target", description = "Pathes to include")
	public void addPath(
			@AConQATAttribute(name = "path", description = "Path (relative to repository URL)") String path) {
		paths.add(path);
	}

	/** Add included author. */
	@AConQATParameter(name = ConQATParamDoc.INCLUDE_NAME, description = "Include author.")
	public void addIncludedAuthor(
			@AConQATAttribute(name = "author", description = "Author name") String author) {
		includedAuthors.add(author);
	}

	/** Add excluded author. */
	@AConQATParameter(name = ConQATParamDoc.EXCLUDE_NAME, description = "Exclude author.")
	public void addExcludedAuthor(
			@AConQATAttribute(name = "author", description = "Author name") String author) {
		excludedAuthors.add(author);
	}

	/** Whether to collect changed paths */
	@AConQATParameter(name = "paths", maxOccurrences = 1, description = "Store changed paths for each revision?")
	public void setChangedPaths(
			@AConQATAttribute(name = "collect", description = "Whether to collect paths (slow) [default: false]") boolean changedPaths) {
		this.changedPaths = changedPaths;
	}

	/** Stop on copy? */
	@AConQATParameter(name = "nodes", maxOccurrences = 1, description = "Whether to stop on copy")
	public void setNodesStrictly(
			@AConQATAttribute(name = "strict", description = "true: stop on copy, false: follow copies") boolean strictNode) {
		this.strictNode = strictNode;
	}

	/** {@inheritDoc} */
	@Override
	public SVNLogEntryRoot process() throws ConQATException {
		repository = createRepository();
		try {
			headRevision = (int) repository.getLatestRevision();
		} catch (SVNException e) {
			throw new ConQATException(e);
		}
		root = new SVNLogEntryRoot(getUrl(), headRevision);
		determineStartRevision();
		addLogEntries();
		return root;
	}

	/** Determine start revision number according to the rules explained above. */
	private void determineStartRevision() throws ConQATException {
		if (daysBack != INVALID_REVISION) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_YEAR, -daysBack);
			Date requestedDate = calendar.getTime();

			getLogger().info("Requested date: " + requestedDate);
			try {
				startRevision = (int) repository
						.getDatedRevision(requestedDate);
			} catch (SVNException e) {
				throw new ConQATException(e);
			}
		}
	}

	/**
	 * Add all log entries to the scope.
	 * 
	 * @throws ConQATException
	 *             if repository access fails
	 */
	private void addLogEntries() throws ConQATException {

		getLogger().info("Start revision: " + startRevision);

		String[] targetPathes;
		if (paths.isEmpty()) {
			targetPathes = new String[] { "" };
		} else {
			targetPathes = new String[paths.size()];
			paths.toArray(targetPathes);
		}

		try {
			@SuppressWarnings("unchecked")
			Collection<SVNLogEntry> entries = repository.log(targetPathes,
					null, INVALID_REVISION, startRevision, changedPaths,
					strictNode);

			for (SVNLogEntry logEntry : entries) {
				String author = logEntry.getAuthor();
				if (isAuthorIncluded(author) && !isAuthorExcluded(author)) {
					root.addChild(new SVNLogEntryNode(logEntry));
				}
			}
		} catch (SVNException e) {
			throw new ConQATException(e);
		}
	}

	/**
	 * Checks if an author is included. If the set is empty this method returns
	 * <code>true</code>.
	 */
	private boolean isAuthorIncluded(String author) {
		if (includedAuthors.isEmpty()) {
			return true;
		}
		return includedAuthors.contains(author);
	}

	/** Checks if an author is excluded. */
	private boolean isAuthorExcluded(String author) {
		return excludedAuthors.contains(author);
	}
}