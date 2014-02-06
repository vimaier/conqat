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

import java.util.HashMap;
import java.util.Map;

import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.clone.DeepCloneException;
import org.conqat.engine.commons.node.ConQATNodeBase;
import org.conqat.engine.commons.node.IRemovableConQATNode;

/**
 * A finding category collects finding groups from the same context (often the
 * same detection tool).
 * 
 * @author hummelb
 * @author $Author: juergens $
 * @version $Rev: 40777 $
 * @ConQAT.Rating GREEN Hash: 4F211318A059C908790C339AF2785CF5
 */
public class FindingCategory extends ConQATNodeBase implements
		IRemovableConQATNode {

	/** The finding groups in this category. */
	private final Map<String, FindingGroup> findingGroups = new HashMap<String, FindingGroup>();

	/** The report this belongs to. */
	private final FindingReport report;

	/** The name of this category. */
	private final String name;

	/** Hidden constructor. Use factory method in {@link FindingReport} instead. */
	/* package */FindingCategory(FindingReport report, String name) {
		this.report = report;
		this.name = name;
	}

	/** Copy constructor. */
	/* package */FindingCategory(FindingCategory other,
			FindingReport findingReport) throws DeepCloneException {
		super(other);
		this.report = findingReport;
		this.name = other.name;
		for (FindingGroup group : other.findingGroups.values()) {
			findingGroups.put(group.getName(), new FindingGroup(group, this));
		}
	}

	/** {@inheritDoc} */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Creates a new finding group with the given description. The description
	 * should be unique within this category. This may not be called, if a group
	 * of this name already exists (check by {@link #getGroupByName(String)}).
	 */
	public FindingGroup createFindingGroup(String description) {
		CCSMPre.isFalse(findingGroups.containsKey(description),
				"Finding group of given name already exists.");
		FindingGroup findingGroup = new FindingGroup(description, this);
		findingGroups.put(findingGroup.getName(), findingGroup);
		return findingGroup;
	}

	/**
	 * Returns the specified finding group or creates a new one if it does not
	 * exist.
	 */
	public FindingGroup getOrCreateFindingGroup(String description) {
		FindingGroup group = getGroupByName(description);
		if (group != null) {
			return group;
		}
		return createFindingGroup(description);
	}

	/** Returns the named group or <code>null</code>. */
	public FindingGroup getGroupByName(String name) {
		return findingGroups.get(name);
	}

	/** {@inheritDoc} */
	@Override
	public FindingGroup[] getChildren() {
		return findingGroups.values().toArray(
				new FindingGroup[findingGroups.size()]);
	}

	/** {@inheritDoc} */
	@Override
	public void remove() {
		report.remove(this);
	}

	/** {@inheritDoc} */
	@Override
	public String getId() {
		return name;
	}

	/** {@inheritDoc} */
	@Override
	public FindingReport getParent() {
		return report;
	}

	/** {@inheritDoc} */
	@Override
	public boolean hasChildren() {
		return !findingGroups.isEmpty();
	}

	/** Removes the given finding group from the category. */
	/* package */void remove(FindingGroup findingGroup) {
		findingGroups.remove(findingGroup.getName());
		report.incrementRemoveCounter();
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
}