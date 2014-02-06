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
package org.conqat.engine.dotnet.types;

import java.util.ArrayList;
import java.util.List;

import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableList;

/**
 * Base class for code entities
 * 
 * @author $Author: goede $
 * @version $Rev: 40974 $
 * @ConQAT.Rating GREEN Hash: C37B51113B8753D5021A2FAB7958B640
 */
public abstract class CodeEntityBase {

	/** Children of the code entity */
	protected final List<CodeEntityBase> children = new ArrayList<CodeEntityBase>();

	/** String that separates children */
	private final String childSeparator;

	/** Constructor */
	protected CodeEntityBase(String childSeparator) {
		this.childSeparator = childSeparator;
	}

	/** Add child */
	public void addChild(NamedCodeEntity child) {
		children.add(child);
	}

	/** Get children */
	public UnmodifiableList<CodeEntityBase> getChildren() {
		return CollectionUtils.asUnmodifiable(children);
	}

	/** Get fully qualified name. Can be null, if entity has no name */
	public String getFqName() {
		return null;
	}

	/** String that separates children */
	public String getChildSeparator() {
		return childSeparator;
	}

	/** Write fully qualified type names in DFS order to list */
	public List<String> collectTypeNames() {
		List<String> typeFyNames = new ArrayList<String>();
		collectTypeNames(this, typeFyNames);
		return typeFyNames;
	}

	/** Write fully qualified type names in DFS order to list */
	private void collectTypeNames(CodeEntityBase type, List<String> typeFqNames) {
		for (CodeEntityBase child : type.getChildren()) {
			String fqName = child.getFqName();
			if (fqName != null) {
				typeFqNames.add(fqName);
				collectTypeNames(child, typeFqNames);
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return getFqName();
	}

}