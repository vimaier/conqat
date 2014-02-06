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

import java.io.Serializable;
import java.util.Map;

import org.conqat.engine.code_clones.core.CloneDetectionException;
import org.conqat.engine.code_clones.core.Unit;
import org.conqat.engine.code_clones.normalization.UnitProviderBase;
import org.conqat.engine.code_clones.normalization.provider.IUnitProvider;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.engine.resource.util.ResourceTraversalUtils;

/**
 * Inserts sentinels between units that originate from different files.
 * <p>
 * This way, we cannot find clones that span several files.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43764 $
 * @ConQAT.Rating GREEN Hash: A9E75E7CC2D14B1729201750C5052705
 */
/* package */class Sentinelizer extends UnitProviderBase<ITextResource, Unit>
		implements Serializable {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** Flag that indicates that the last returned unit was a sentinel */
	private boolean lastUnitWasSentinel = false;

	/** Reference to unit that has been returned last */
	private Unit lastReturnedUnit = null;

	/** Provides units without sentinels */
	private final IUnitProvider<ITextResource, Unit> unitPovider;

	/** Map from uniform paths to elements */
	private Map<String, ITextElement> uniformPathToElement;

	/** Constructor */
	public Sentinelizer(IUnitProvider<ITextResource, Unit> unitPovider) {
		this.unitPovider = unitPovider;
	}

	/** Forward initialization to unit provider */
	@Override
	protected void init(ITextResource root) throws CloneDetectionException {
		unitPovider.init(root, getLogger());
		// init map that maps from element id to set of filtered regions
		uniformPathToElement = ResourceTraversalUtils
				.createUniformPathToElementMap(root, ITextElement.class);
	}

	/** {@inheritDoc} */
	@Override
	protected Unit provideNext() throws CloneDetectionException {
		Unit next = unitPovider.lookahead(1);

		// no more units - return null
		if (next == null) {
			if (!lastUnitWasSentinel) {
				lastUnitWasSentinel = true;
				return createSentinel();
			}
			return null;
		}

		// if last unit was sentinel, simply return next unit
		if (lastUnitWasSentinel) {
			lastUnitWasSentinel = false;
			lastReturnedUnit = unitPovider.getNext();
			return lastReturnedUnit;
		}

		if (lastReturnedUnit != null && !next.isSynthetic()
				&& isFilterGapBefore(next)) {
			lastUnitWasSentinel = true;
			return createSentinel();
		}

		// if we are still in same file, simply return next unit
		if (stillInSameFile(next)) {
			lastReturnedUnit = unitPovider.getNext();
			return lastReturnedUnit;
		}

		// if we change files, return sentinel
		lastUnitWasSentinel = true;
		return createSentinel();
	}

	/** Checks for a filter gap between the last returned and the next unit */
	private boolean isFilterGapBefore(Unit next) throws CloneDetectionException {
		ITextElement element = uniformPathToElement.get(next
				.getElementUniformPath());
		if (!stillInSameFile(next)) {
			return false;
		}

		try {
			return element.isFilterGapBetween(
					lastReturnedUnit.getFilteredEndOffset(),
					next.getFilteredStartOffset());
		} catch (ConQATException e) {
			throw new CloneDetectionException("Could not determine filter gap",
					e);
		}
	}

	/** Creates a sentinel unit */
	private SentinelUnit createSentinel() {
		if (lastReturnedUnit == null) {
			return null;
		}
		return new SentinelUnit(lastReturnedUnit.getElementUniformPath());
	}

	/** Returns true, if unit and last returned unit stem from same file */
	private boolean stillInSameFile(Unit unit) {
		return lastReturnedUnit == null
				|| lastReturnedUnit.getElementUniformPath().equals(
						unit.getElementUniformPath());
	}

}