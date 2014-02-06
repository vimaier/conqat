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
package org.conqat.engine.code_clones.detection;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.code_clones.core.Unit;
import org.conqat.engine.resource.scope.TextResourceParameterObjectListBase;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.lib.commons.clone.DeepCloneException;
import org.conqat.lib.commons.collections.CollectionUtils;

/**
 * Parameter object that bundles a resource tree, a list of clone classes and
 * the units on which detection was performed into a single object.
 * 
 * @author $Author: goede $
 * @version $Rev: 43552 $
 * @ConQAT.Rating GREEN Hash: 53122CE7F54D5E7538C0801FF4D5230F
 */
public class CloneDetectionResultElement extends
		TextResourceParameterObjectListBase<CloneClass> {

	/** Date denoting the system version on which clone detection was performed */
	private final Date systemDate;

	/**
	 * Map from element uniform path to array of units on which detection was
	 * performed
	 */
	private final Map<String, Unit[]> unitsMap;

	/**
	 * Constructor that does not store the units.
	 * 
	 * @param systemDate
	 *            Timestamp at which detection was performed
	 * @param root
	 *            Root of the resource tree
	 * @param cloneClasses
	 *            List of clone classes
	 */
	public CloneDetectionResultElement(Date systemDate, ITextResource root,
			List<CloneClass> cloneClasses) {
		this(systemDate, root, cloneClasses, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param systemDate
	 *            Timestamp at which detection was performed
	 * @param root
	 *            Root of the resource tree
	 * @param cloneClasses
	 *            List of clone classes
	 * @param unitsMap
	 *            Map from origin to array of units on which detection was
	 *            performed
	 */
	public CloneDetectionResultElement(Date systemDate, ITextResource root,
			List<CloneClass> cloneClasses, Map<String, Unit[]> unitsMap) {
		super(root, cloneClasses);
		this.systemDate = systemDate;
		this.unitsMap = unitsMap;
	}

	/** Copy constructor */
	public CloneDetectionResultElement(CloneDetectionResultElement element)
			throws DeepCloneException {
		super(element);
		systemDate = (Date) element.getSystemDate().clone();

		// we do not deep-clone no purpose to save memory. Furthermore, units
		// are immutable.
		if (element.unitsMap != null) {
			unitsMap = new HashMap<String, Unit[]>(element.unitsMap);
		} else {
			unitsMap = null;
		}
	}

	/** Returns Timestamp at which detection was started */
	public Date getSystemDate() {
		return new Date(systemDate.getTime());
	}

	/** {@inheritDoc} */
	@Override
	public CloneDetectionResultElement deepClone() throws DeepCloneException {
		return new CloneDetectionResultElement(this);
	}

	/**
	 * Returns map from element uniform path to array of units on which
	 * detection was performed. Returns null if units are not stored.
	 */
	public Map<String, Unit[]> getUnits() {
		if (unitsMap == null) {
			return null;
		}
		return CollectionUtils.asUnmodifiable(unitsMap);
	}

}