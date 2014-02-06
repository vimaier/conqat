/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.conqat.lib.commons.clone.IDeepCloneable;

/**
 * Stores meta-information on a {@link FindingGroup}. The information is
 * self-contained, i.e. group and category name are repeated.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46037 $
 * @ConQAT.Rating GREEN Hash: 7FBBE6F34C28BF7EB7E97BF27677750C
 */
public class FindingGroupInfo implements Serializable, IDeepCloneable {

	/** Version for serialization. */
	private static final long serialVersionUID = 1;

	/** The name of the category. */
	private final String categoryName;

	/** The name of the group. */
	private final String groupName;

	/**
	 * A description of the finding. This is a string that can be shown to the
	 * user to explain why findings in this group are a problem and may also
	 * contain hints on removing such a finding. The string may contain HTML
	 * markup (e.g. to display tables or bullet lists).
	 * <p>
	 * The HTML content is in part to ease integration of tools like Findbugs,
	 * which already provide databases with descriptions but only with (simple)
	 * HTML markup. As removal/conversion of this HTML is hard, we rather deal
	 * with it.
	 */
	private final String description;

	/**
	 * Contains for each property for findings in the described group a brief
	 * explanation.
	 */
	private final Map<String, String> propertyDescriptions = new HashMap<String, String>();

	/** Constructor. */
	public FindingGroupInfo(String categoryName, String groupName,
			String description) {
		this.categoryName = categoryName;
		this.groupName = groupName;
		this.description = description;
	}

	/** Constructor. */
	public FindingGroupInfo(FindingGroup group, String description) {
		this(group.getParent().getName(), group.getName(), description);
	}

	/** Constructor. */
	public FindingGroupInfo(FindingGroupInfo other) {
		this(other.categoryName, other.groupName, other.description);
		propertyDescriptions.putAll(other.propertyDescriptions);
	}

	/** Returns the name of the category. */
	public String getCategoryName() {
		return categoryName;
	}

	/** Returns name of the group. */
	public String getGroupName() {
		return groupName;
	}

	/**
	 * Returns a description of the finding. This is a string that can be shown
	 * to the user to explain why findings in this group are a problem and may
	 * also contain hints on removing such a finding. The string may contain
	 * HTML markup (e.g. to display tables or bullet lists).
	 */
	public String getDescription() {
		return description;
	}

	/** Sets a property description. */
	public void setPropertyDescription(String propertyName, String description) {
		propertyDescriptions.put(propertyName, description);
	}

	/** Returns the description for a property (or null). */
	public String getPropertyDescription(String propertyName) {
		return propertyDescriptions.get(propertyName);
	}

	/** {@inheritDoc} */
	@Override
	public FindingGroupInfo deepClone() {
		return new FindingGroupInfo(this);
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return categoryName + "/" + groupName + ": " + description;
	}
}
