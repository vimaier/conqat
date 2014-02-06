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

import org.conqat.engine.commons.findings.location.ElementLocation;
import org.conqat.engine.commons.node.ConQATNodeBase;
import org.conqat.engine.commons.node.IRemovableConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.clone.DeepCloneException;
import org.conqat.lib.commons.string.StringUtils;

/**
 * A single finding which represents a single issue found during an analysis. A
 * finding has exactly one location.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46035 $
 * @ConQAT.Rating GREEN Hash: 2CB18AF0C29D33E8E0FA4A3E4A943223
 */
public class Finding extends ConQATNodeBase implements IRemovableConQATNode {

	/** The location. */
	private ElementLocation location;

	/** The finding group this finding belongs to. */
	private final FindingGroup findingGroup;

	/**
	 * Flag that determines whether the message should be used as the name (if
	 * present).
	 */
	private boolean useMessageAsName = false;

	/** The id which is unique within the findings group. */
	/* package */final int id;

	/** Hidden constructor. Use factory methods in {@link FindingGroup} instead. */
	/* package */Finding(FindingGroup findingGroup, int id,
			ElementLocation location) {
		CCSMAssert.isNotNull(location);
		CCSMAssert.isNotNull(findingGroup);

		this.findingGroup = findingGroup;
		this.id = id;
		this.location = location;
	}

	/** Copy constructor. */
	/* package */Finding(Finding other, FindingGroup findingGroup)
			throws DeepCloneException {
		super(other);
		this.findingGroup = findingGroup;
		this.id = other.id;
		this.useMessageAsName = other.useMessageAsName;
		this.location = other.location;
	}

	/** Returns the location for this finding. */
	public ElementLocation getLocation() {
		return location;
	}

	/** Sets/replaces the location. */
	public void setLocation(ElementLocation location) {
		CCSMAssert.isNotNull(location);
		this.location = location;
	}

	/** {@inheritDoc} */
	@Override
	public IRemovableConQATNode[] getChildren() {
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public void remove() {
		findingGroup.remove(this);
	}

	/** {@inheritDoc} */
	@Override
	public String getId() {
		return findingGroup.getId() + ":" + getIdSuffix();
	}

	/** {@inheritDoc} */
	@Override
	public String getName() {
		if (useMessageAsName) {
			String message = getMessage();
			if (!StringUtils.isEmpty(message)) {
				return message;
			}
		}

		return getIdSuffix();
	}

	/** Sets whether the message should be used for the name. */
	public void setUseMessageAsName(boolean useMessageAsName) {
		this.useMessageAsName = useMessageAsName;
	}

	/** Returns the suffix used for generating unique IDs. */
	private String getIdSuffix() {
		// make id field a width of 6 to ensure sorting works as expected
		return "finding " + String.format("%06d", id);
	}

	/** {@inheritDoc} */
	@Override
	public FindingGroup getParent() {
		return findingGroup;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Returns false.
	 */
	@Override
	public boolean hasChildren() {
		return false;
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

	/** Returns the message if set. */
	public String getMessage() {
		Object o = getValue(EFindingKeys.MESSAGE.toString());
		if (o instanceof String) {
			return (String) o;
		}
		return StringUtils.EMPTY_STRING;
	}

	/** Returns the dependency source (or null). */
	public String getDependencySource() {
		return NodeUtils.getStringValue(this,
				EFindingKeys.DEPENDENCY_SOURCE.toString(), null);
	}

	/** Returns the dependency target (or null). */
	public String getDependencyTarget() {
		return NodeUtils.getStringValue(this,
				EFindingKeys.DEPENDENCY_TARGET.toString(), null);
	}

	/**
	 * Returns a single line description of the finding's location that is
	 * meaningful to the user.
	 */
	public String getLocationString() {
		return location.toLocationString();
	}

	/** Returns a (modifiable) map of the finding's numeric properties. */
	@SuppressWarnings("unchecked")
	public Map<String, Double> getProperties() {
		Object map = getValue(EFindingKeys.METRICS.name());
		if (!(map instanceof Map)) {
			map = new HashMap<String, Double>();
			setValue(EFindingKeys.METRICS.name(), map);
		}
		return (Map<String, Double>) map;
	}
}