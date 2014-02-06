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
package org.conqat.engine.commons.findings.location;

import java.util.HashSet;
import java.util.Set;

import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableSet;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Location identifying a set of model elements by their ids.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating GREEN Hash: 6484EAC1027483E6E28972F05FB378F4
 */
public class ModelPartLocation extends ElementLocation {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** The IDs of the model elements. */
	private final Set<String> elementIds = new HashSet<String>();

	/** Constructor. */
	public ModelPartLocation(String location, String uniformPath) {
		super(location, uniformPath);
	}

	/** Adds an element ID to this location */
	public void addElementId(String elementId) {
		elementIds.add(elementId);
	}

	/** Returns the IDs of the model elements. */
	public UnmodifiableSet<String> getElementIds() {
		return CollectionUtils.asUnmodifiable(elementIds);
	}

	/** {@inheritDoc} */
	@Override
	public String toLocationString() {
		return super.toLocationString() + ":"
				+ StringUtils.concat(elementIds, ", ");
	}
}