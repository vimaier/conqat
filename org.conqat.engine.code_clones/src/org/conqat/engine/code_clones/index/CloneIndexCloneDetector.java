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
package org.conqat.engine.code_clones.index;

import org.conqat.engine.code_clones.core.constraint.ConstraintList;
import org.conqat.engine.code_clones.core.constraint.ICloneClassConstraint;
import org.conqat.engine.code_clones.detection.CloneDetectionResultElement;
import org.conqat.engine.code_clones.index.report.ConstraintAwareCollectingCloneClassReporter;
import org.conqat.engine.code_clones.index.store.ICloneIndexStore;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.util.ConQATInputProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.engine.resource.util.ResourceTraversalUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: AF93E0478390C4B1AF53D90500C5DC50
 */
@AConQATProcessor(description = "Performs clone detection on an existing clone index. "
		+ "The index must already be filled.")
public class CloneIndexCloneDetector extends
		ConQATInputProcessorBase<ITextResource> {

	/** The store factory. */
	private ICloneIndexStore store;

	/**
	 * Number of units that a clone must at least comprise. If it has less, it
	 * gets filtered out.
	 */
	private int minLength = -1;

	/** List of constraints that all detected clone classes must satisfy */
	private final ConstraintList constraints = new ConstraintList();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "store", description = "The store factory used to access and persist the clone index.", minOccurrences = 1, maxOccurrences = 1)
	public void setStoreFactory(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) ICloneIndexStore store) {
		// whether the index is valid (contains data) is implicitly checked in
		// the process() method
		this.store = store;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "constraint", minOccurrences = 0, maxOccurrences = -1, description = ""
			+ "Adds a constraint that each detected clone class must satisfy")
	public void addConstraint(
			@AConQATAttribute(name = "type", description = "Clone classes that do not match the constraint are filtered") ICloneClassConstraint constraint) {
		constraints.add(constraint);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "clonelength", description = "Minimal length of Clone. If none is set, all clones will be reported (limited by chunk size of the index).", minOccurrences = 0, maxOccurrences = 1)
	public void setMinLength(
			@AConQATAttribute(name = "min", description = "Minimal length of Clone") int minLength) {
		this.minLength = minLength;
	}

	/** {@inheritDoc} */
	@Override
	public CloneDetectionResultElement process() throws ConQATException {
		int chunkLength = new PersistedOptions(store).getChunkLength();
		if (minLength < 0) {
			minLength = chunkLength;
		}
		if (minLength < chunkLength) {
			throw new ConQATException("The minimal clone length of "
					+ minLength + " is smaller than the chunk length of "
					+ chunkLength
					+ " stored in the index, which is not supported.");
		}

		CloneIndex index = new CloneIndex(store, getLogger());
		ConstraintAwareCollectingCloneClassReporter reporter = new ConstraintAwareCollectingCloneClassReporter(
				minLength, constraints);

		for (ITextElement element : ResourceTraversalUtils
				.listTextElements(input)) {
			index.reportClones(element.getUniformPath(), reporter, true,
					minLength);
		}

		getLogger().info("Overall performance: " + index.getPerformanceInfo());

		return new CloneDetectionResultElement(reporter.getBirthDate(), input,
				reporter.getCloneClasses(), null);
	}
}