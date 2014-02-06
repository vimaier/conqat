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
package org.conqat.engine.code_clones.result.annotation;

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.code_clones.detection.CloneDetectionResultElement;
import org.conqat.engine.code_clones.detection.UnitProcessorBase;
import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.lib.commons.algo.ObjectUnionFind;
import org.conqat.lib.commons.collections.CollectionUtils;

/**
 * {@ConQAT.Doc}
 * 
 * This processor requires the number of units to be annotated at each element.
 * Since this annotation is performed during the execution of the clone
 * detection, the unit count is available in every
 * {@link CloneDetectionResultElement}.
 * 
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: 3E590CCD5FA0DC171261F8C43CD33075
 */
@AConQATProcessor(description = ""
		+ "Annotates elements with Redundancy-Free Source Units. "
		+ "If this processor is executed in a clone detection that uses "
		+ "statement normalization, Redundancy Free Source Statements are annotated.")
public class RfssAnnotator extends
		ConQATPipelineProcessorBase<CloneDetectionResultElement> {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Redundancy free source statements", type = "java.lang.Integer")
	public static final String RFSS_KEY = "RFSS";

	/** Keeps track of clone relations between statements */
	private final ObjectUnionFind<UnitKey> cloneRelationships = new ObjectUnionFind<UnitKey>();

	/** For lookups don't generate a new key */
	private final UnitKey lookupStatementKey = new UnitKey(null, 0);

	/** {@inheritDoc} */
	@Override
	protected void processInput(CloneDetectionResultElement input)
			throws ConQATException {
		NodeUtils.addToDisplayList(input, RFSS_KEY);
		computeCloneRelationships(input);

		for (ITextElement element : ResourceTraversalUtils
				.listTextElements(input.getRoot())) {
			processElement(element);
		}
	}

	/**
	 * Computes the clone relationship between individual statements for all
	 * clone classes contained in a detection result.
	 */
	public void computeCloneRelationships(CloneDetectionResultElement input) {
		for (CloneClass cloneClass : input.getList()) {
			Clone representative = CollectionUtils.getAny(cloneClass
					.getClones());
			for (Clone clone : cloneClass.getClones()) {
				if (clone != representative) {
					computeCloneRelationships(clone, representative);
				}
			}
		}
	}

	/**
	 * Computes the clone relationship between individual statements for two
	 * clones
	 */
	private void computeCloneRelationships(Clone clone1, Clone clone2) {
		String origin1 = clone1.getUniformPath();
		String origin2 = clone2.getUniformPath();
		int offset1 = clone1.getStartUnitIndexInElement();
		int offset2 = clone2.getStartUnitIndexInElement();
		for (int i = 0; i < clone1.getLengthInUnits(); i++) {
			UnitKey clone1UnitKey = createUnitKey(origin1, offset1 + i);
			UnitKey clone2UnitKey = createUnitKey(origin2, offset2 + i);

			cloneRelationships.union(clone1UnitKey, clone2UnitKey);
		}
	}

	/**
	 * Annotate RFSS at elements
	 * 
	 * @throws ConQATException
	 *             if the number of units of an element cannot be retrieved
	 */
	private void processElement(ITextElement element) throws ConQATException {
		int units = (int) NodeUtils.getDoubleValue(element,
				UnitProcessorBase.UNITS_KEY);
		double rfss = 0;

		for (int unitIndex = 0; unitIndex < units; unitIndex++) {
			lookupStatementKey
					.assumeValues(element.getUniformPath(), unitIndex);
			int unionOccurrence = cloneRelationships
					.getClusterSize(lookupStatementKey);
			rfss += 1.0 / unionOccurrence;
		}

		element.setValue(RFSS_KEY, rfss);
	}

	/** Computes key for a unit */
	private UnitKey createUnitKey(String origin, int unitIndex) {
		return new UnitKey(origin, unitIndex);
	}

	/**
	 * Optimized class for keys to the union-find-structure. This class is
	 * mutable by design. It was introduced as it speeds up the union find part
	 * and reduces the memory foot print, especially for large systems.
	 */
	private static class UnitKey {

		/** Origin of unit */
		private String origin;
		/** Index of unit */
		private int unitIndex;

		/** Constructor */
		public UnitKey(String origin, int unitIndex) {
			this.origin = origin;
			this.unitIndex = unitIndex;
		}

		/** {@inheritDoc} */
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof UnitKey)) {
				return false;
			}
			UnitKey other = (UnitKey) obj;
			return unitIndex == other.unitIndex && origin.equals(other.origin);
		}

		/** {@inheritDoc} */
		@Override
		public int hashCode() {
			return origin.hashCode() ^ unitIndex;
		}

		/** Set values */
		public void assumeValues(String origin, int unitIndex) {
			this.origin = origin;
			this.unitIndex = unitIndex;
		}
	}

}