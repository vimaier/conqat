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

import java.util.Comparator;

import org.conqat.lib.commons.clone.IDeepCloneable;

/**
 * Comparator for {@link SVNLogEntryNode}s based on their revision.
 * 
 * @author Benjamin Hummel
 * @author $Author: juergens $
 * @version $Rev: 35205 $
 * @ConQAT.Rating GREEN Hash: 438245CC8E124CF2EF2143FC9997BB0D
 */
public class SVNRevisionComparator implements Comparator<SVNLogEntryNode>,
		IDeepCloneable {

	/** The single instance. */
	private static final SVNRevisionComparator instance = new SVNRevisionComparator();

	/** Private constructor to avoid instantiation. */
	private SVNRevisionComparator() {
		// nothing to do.
	}

	/** Returns the only instance of this comparator. */
	public static SVNRevisionComparator getInstance() {
		return instance;
	}

	/** {@inheritDoc} */
	@Override
	public int compare(SVNLogEntryNode node1, SVNLogEntryNode node2) {
		return Long.signum(node2.getRevision() - node1.getRevision());
	}

	/** Do not clone. */
	@Override
	public SVNRevisionComparator deepClone() {
		return instance;
	}
}