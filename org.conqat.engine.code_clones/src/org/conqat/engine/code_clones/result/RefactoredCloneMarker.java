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
package org.conqat.engine.code_clones.result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.code_clones.core.Unit;
import org.conqat.engine.code_clones.detection.CloneDetectionResultElement;
import org.conqat.engine.code_clones.index.ChunkUtils;
import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.digest.MD5Digest;

/**
 * {@ConQAT.Doc}
 * 
 * @version $Rev: 43640 $
 * @ConQAT.Rating GREEN Hash: 620DAFC362639A7CE8224B2C7A097AB9
 */
@AConQATProcessor(description = "Marks all clones that are the result of refactoring an old clone.")
public class RefactoredCloneMarker extends
		ConQATPipelineProcessorBase<CloneDetectionResultElement> {

	/** The key used for storing the boolean refactored flag in the clone. */
	public static final String CLONE_KEY = "refactored";

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "old-report", attribute = "ref", description = "The report containing the old (baseline) clones.", optional = true)
	public CloneDetectionResultElement oldReport;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "chunk", attribute = "size", optional = true, description = "The size of chunks used for recognizing refactored clones in units.")
	public int chunkSize = 3;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "mark", attribute = "threshold", optional = true, description = "Threshold of chunks that must match for a clone to be marked as refactored from an old one.")
	public double threshold = .8;

	/** {@inheritDoc} */
	@Override
	protected void processInput(CloneDetectionResultElement newReport)
			throws ConQATException {
		if (oldReport == null) {
			return;
		}

		checkParameters(newReport);

		Set<String> oldFingerPrints = extractOldFingerprints();
		Set<MD5Digest> oldHashes = extractOldHashes();

		for (CloneClass cloneClass : newReport.getList()) {
			for (Clone clone : cloneClass.getClones()) {

				// already existed in old system
				if (oldFingerPrints.contains(clone.getFingerprint())) {
					continue;
				}

				List<MD5Digest> hashes = getChunkHashes(clone, newReport);
				int found = countFoundOldHashes(hashes, oldHashes);
				
				boolean refactored = !hashes.isEmpty()
						&& found >= hashes.size() * threshold;
				markClone(clone, refactored);
			}
		}
	}

	/** Checks parameters for validity. */
	private void checkParameters(CloneDetectionResultElement newReport)
			throws ConQATException {
		if (newReport.getUnits() == null || oldReport.getUnits() == null) {
			throw new ConQATException(
					"Must store units in report to use this processor.");
		}

		if (chunkSize < 1) {
			throw new ConQATException("Chunk size must be positive!");
		}

		if (threshold <= 0 || threshold > 1) {
			throw new ConQATException("Mark threshold must be in ]0;1].");
		}
	}

	/** Returns all fingerprints found in the old report. */
	private Set<String> extractOldFingerprints() throws ConQATException {
		Set<String> fingerprints = new HashSet<String>();
		for (CloneClass cloneClass : oldReport.getList()) {
			for (Clone clone : cloneClass.getClones()) {
				if (clone.containsGaps()) {
					throw new ConQATException(
							"This processor does not work for gapped clones!");
				}
				fingerprints.add(clone.getFingerprint());
			}
		}
		return fingerprints;
	}

	/** Extracts all chunk hashes from the old report. */
	private Set<MD5Digest> extractOldHashes() throws ConQATException {
		Set<MD5Digest> oldHashes = new HashSet<MD5Digest>();
		for (CloneClass cloneClass : oldReport.getList()) {
			for (Clone clone : cloneClass.getClones()) {
				oldHashes.addAll(getChunkHashes(clone, oldReport));
			}
		}
		return oldHashes;
	}

	/**
	 * Extracts the chunk hashes for a clone. Chunks containing a sentinel are
	 * skipped.
	 */
	private List<MD5Digest> getChunkHashes(Clone clone,
			CloneDetectionResultElement report) throws ConQATException {
	    Unit[] unitsFromReport = report.getUnits().get(clone.getUniformPath());
		if (unitsFromReport == null) {
			throw new ConQATException("Missing units for element "
					+ clone.getUniformPath());
		}
        List<Unit> units = Arrays.asList(unitsFromReport);

		int start = clone.getStartUnitIndexInElement();
		int end = clone.getLastUnitInElement();

		CCSMAssert.isTrue(end >= start, "End must be after start!");
		CCSMAssert.isTrue(end <= units.size(),
				"Clone refers to non-existing units. Did you accidentally trim units?"
						+ units.size());

		List<MD5Digest> hashes = new ArrayList<MD5Digest>();
		for (int i = start; i + chunkSize - 1 < end; ++i) {
			MD5Digest hash = ChunkUtils.buildChunkHash(units, i, i + chunkSize
					- 1);
			if (hash != null) {
				hashes.add(hash);
			}
		}
		return hashes;
	}

	/** Returns the number of hashes that are found in the old set of hashes. */
	private int countFoundOldHashes(List<MD5Digest> hashes,
			Set<MD5Digest> oldHashes) {
		int found = 0;
		for (MD5Digest hash : hashes) {
			if (oldHashes.contains(hash)) {
				found += 1;
			}
		}
		return found;
	}

	/** Marks a clone as refactored. */
	private static void markClone(Clone clone, boolean refactored) {
		clone.setValue(CLONE_KEY, refactored);
	}
}
