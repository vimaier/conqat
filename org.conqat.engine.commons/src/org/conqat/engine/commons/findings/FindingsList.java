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
package org.conqat.engine.commons.findings;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.IRemovableConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.clone.IDeepCloneable;

/**
 * A list of findings. Actually this list does not store the findings
 * themselves, but path descriptors which can be used to locate a finding in a
 * {@link FindingReport}.
 * 
 * @author $Author: juergens $
 * @version $Rev: 40783 $
 * @ConQAT.Rating GREEN Hash: 6EC9DBE1F814C8FEF5CF79F6873F697E
 */
public class FindingsList extends AbstractList<Finding> implements
		IDeepCloneable {

	/** The associated node. */
	private final IConQATNode node;

	/** The list storing the finding paths. */
	private final List<FindingPathDescriptor> findingPaths = new ArrayList<FindingPathDescriptor>();

	/**
	 * The last known value of the remove counter in the corresponding findings
	 * report. This is used to determine when more removes occurred and
	 * {@link #removeFindingsDeletedFromReport()} must perform actual work. The
	 * initial value of -1 ensures, that we perform the check at least once, as
	 * each report starts counting with 0.
	 */
	private long lastRemoveCounterInReport = -1;

	/** Constructor. */
	public FindingsList(IConQATNode node) {
		this.node = node;
	}

	/** Copy constructor. */
	public FindingsList(FindingsList other, IConQATNode node) {
		this.node = node;
		findingPaths.addAll(other.findingPaths);
	}

	/** {@inheritDoc} */
	@Override
	public Finding get(int index) {
		removeFindingsDeletedFromReport();
		return findingPaths.get(index).locateFinding(getReport());
	}

	/** Returns the finding report relevant for this list. */
	private FindingReport getReport() {
		return NodeUtils.getFindingReport(NodeUtils.getRootNode(node));
	}

	/** {@inheritDoc} */
	@Override
	public int size() {
		removeFindingsDeletedFromReport();
		return findingPaths.size();
	}

	/** {@inheritDoc} */
	@Override
	public Finding set(int index, Finding element) {
		removeFindingsDeletedFromReport();
		CCSMPre.isTrue(
				element.getParent().getParent().getParent() == getReport(),
				"May only add findings located in the root report!");
		Finding result = get(index);
		findingPaths.set(index, new FindingPathDescriptor(element));
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public void add(int index, Finding element) {
		removeFindingsDeletedFromReport();
		CCSMPre.isTrue(
				element.getParent().getParent().getParent() == getReport(),
				"May only add findings located in the root report!");
		findingPaths.add(index, new FindingPathDescriptor(element));
	}

	/** {@inheritDoc} */
	@Override
	public Finding remove(int index) {
		removeFindingsDeletedFromReport();
		Finding result = get(index);
		findingPaths.remove(index);
		return result;
	}

	/**
	 * This method compacts the internal representation in {@link #findingPaths}
	 * by removing all entries that can no longer be resolved (i.e. correspond
	 * to findings deleted in the report). This is required for correctness, as
	 * otherwise we might return null entries when findings no longer exist.
	 * <p>
	 * This is an expensive operation. To make this more efficient, this is only
	 * performed if there have been deletes in the report since the last call to
	 * this method.
	 */
	private void removeFindingsDeletedFromReport() {
		FindingReport report = getReport();
		if (lastRemoveCounterInReport == report.getRemoveCounter()) {
			return;
		}

		for (int i = findingPaths.size() - 1; i >= 0; --i) {
			if (findingPaths.get(i).locateFinding(report) == null) {
				findingPaths.remove(i);
			}
		}

		lastRemoveCounterInReport = report.getRemoveCounter();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Throws an exception as we do not support cloning at this level.
	 */
	@Override
	public IRemovableConQATNode deepClone() {
		throw new UnsupportedOperationException(
				"Deep cloning not supported at this level.");
	}

	/**
	 * Merges all findings from another {@link FindingsList} that do not already
	 * exist in this list. Merging is performed on the internal descriptor
	 * level.
	 */
	public void mergeIn(FindingsList other) {
		for (FindingPathDescriptor path : other.findingPaths) {
			if (!findingPaths.contains(path)) {
				findingPaths.add(path);
			}
		}
	}

	/** Finding descriptors. */
	private static final class FindingPathDescriptor {

		/** Description of the category. */
		private final String categoryName;

		/** Name of the group. */
		private final String groupName;

		/** Id of the finding. */
		private final int findingId;

		/** Constructor. */
		public FindingPathDescriptor(Finding finding) {
			findingId = finding.id;
			FindingGroup group = finding.getParent();
			groupName = group.getName();
			categoryName = group.getParent().getName();
		}

		/**
		 * Locates the finding identified by this path in the given report or
		 * returns <code>null</code> if it is not found.
		 */
		public Finding locateFinding(FindingReport report) {
			FindingCategory category = report.getCategory(categoryName);
			if (category == null) {
				return null;
			}
			FindingGroup group = category.getGroupByName(groupName);
			if (group == null) {
				return null;
			}
			return group.getFindingById(findingId);
		}

	}
}