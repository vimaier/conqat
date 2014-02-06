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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.conqat.lib.commons.clone.DeepCloneException;
import org.conqat.lib.commons.date.DateUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.engine.commons.node.ConQATNodeBase;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.IRemovableConQATNode;

/**
 * A finding report is a collection of finding categories.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46038 $
 * @ConQAT.Rating GREEN Hash: 817B93948DF8FBB805D83E19ECD7FF9F
 */
public class FindingReport extends ConQATNodeBase implements
		IRemovableConQATNode {

	/** The time this report was created. */
	private final Date time;

	/** The categories. */
	private final Map<String, FindingCategory> categories = new HashMap<String, FindingCategory>();

	/**
	 * Counts the number of remove operations in this report. This is used to be
	 * more efficient when updating findings lists.
	 */
	private long removeCounter = 0;

	/** Constructor. */
	public FindingReport() {
		this(DateUtils.getNow());
	}

	/** Constructor. */
	public FindingReport(Date time) {
		this.time = (Date) time.clone();
	}

	/** Copy constructor. */
	/* package */FindingReport(FindingReport other) throws DeepCloneException {
		super(other);
		time = (Date) other.time.clone();
		for (FindingCategory category : other.categories.values()) {
			categories.put(category.getName(), new FindingCategory(category,
					this));
		}
	}

	/** Returns the category with the given name or null. */
	public FindingCategory getCategory(String name) {
		return categories.get(name);
	}

	/**
	 * Returns the category with the given name or creates one using the finding
	 * and location types.
	 */
	public FindingCategory getOrCreateCategory(String name) {
		FindingCategory category = categories.get(name);
		if (category == null) {
			category = new FindingCategory(this, name);
			categories.put(name, category);
		}
		return category;
	}

	/** Returns the time when the report was created. */
	public Date getTime() {
		return time;
	}

	/** {@inheritDoc} */
	@Override
	public FindingCategory[] getChildren() {
		return categories.values().toArray(
				new FindingCategory[categories.size()]);
	}

	/** {@inheritDoc} */
	@Override
	public void remove() {
		// does nothing
	}

	/** Removes the given category. */
	/* package */void remove(FindingCategory findingCategory) {
		categories.remove(findingCategory.getName());
		incrementRemoveCounter();
	}

	/** {@inheritDoc} */
	@Override
	public String getId() {
		return getName();
	}

	/** {@inheritDoc} */
	@Override
	public String getName() {
		return "";
	}

	/** {@inheritDoc} */
	@Override
	public IConQATNode getParent() {
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public boolean hasChildren() {
		return !categories.isEmpty();
	}

	/** {@inheritDoc}. */
	@Override
	public FindingReport deepClone() throws DeepCloneException {
		return new FindingReport(this);
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "Finding Report ["
				+ StringUtils.concat(categories.keySet(), ", ") + "]";
	}

	/**
	 * Copies all findings from the source to the target report, unless they
	 * already exist there. Existence is checked by using the finding's id.
	 */
	public static void copyAll(FindingReport source, FindingReport target)
			throws DeepCloneException {

		for (FindingCategory sourceCategory : source.getChildren()) {
			FindingCategory targetCategory = target
					.getOrCreateCategory(sourceCategory.getName());

			for (FindingGroup sourceGroup : sourceCategory.getChildren()) {
				FindingGroup targetGroup = targetCategory
						.getOrCreateFindingGroup(sourceGroup.getName());
				if (targetGroup.getGroupInfo() == null
						&& sourceGroup.getGroupInfo() != null) {
					targetGroup.setGroupInfo(sourceGroup.getGroupInfo());
				}

				for (Finding finding : sourceGroup.getChildren()) {
					if (targetGroup.getFindingById(finding.id) == null) {
						targetGroup.copyFinding(finding);
					}
				}
			}
		}

	}

	/** Returns the current value of the remove counter. */
	/* package */long getRemoveCounter() {
		return removeCounter;
	}

	/** Increments the remove counter. */
	/* package */void incrementRemoveCounter() {
		removeCounter += 1;
	}
}