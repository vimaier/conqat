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
package org.conqat.engine.code_clones.result;

import java.util.Collections;
import java.util.List;

import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.code_clones.detection.CloneDetectionResultElement;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author juergens
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: BE98A6611A5914A208FDEE1B15803E05
 */
@AConQATProcessor(description = "Reduces a clone detection result to a random sample of specified size.")
public class CloneClassSampler extends DetectionResultProcessorBase {

	/** Default sample size */
	private static final int DEFAULT_SAMPLE_SIZE = 1000;

	/** Size of the sample */
	private int sampleSize = DEFAULT_SAMPLE_SIZE;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "sample", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Set size of sample (Number of clone classes that are kept. "
			+ "If less clone classes are contained, all are kept.)")
	public void setSampleSize(
			@AConQATAttribute(name = "size", description = "Default value is "
					+ DEFAULT_SAMPLE_SIZE) int sampleSize) {
		this.sampleSize = sampleSize;
	}

	/** {@inheritDoc} */
	@Override
	public CloneDetectionResultElement process() {
		List<CloneClass> cloneClasses = randomizeCloneClassesOrder();

		cloneClasses = cutToSize(cloneClasses, sampleSize);

		logSampleDetails(cloneClasses);

		return detectionResultForCloneClasses(cloneClasses);
	}

	/** Returns list of clone classes in random order */
	private List<CloneClass> randomizeCloneClassesOrder() {
		List<CloneClass> cloneClasses = detectionResult.getList();
		Collections.shuffle(cloneClasses);
		return cloneClasses;
	}

	/** Returns a list that is truncated after the sample size */
	private List<CloneClass> cutToSize(List<CloneClass> cloneClasses, int size) {
		return cloneClasses.subList(0, Math.min(size, cloneClasses.size()));
	}

	/** Create log messages containing information about truncation */
	private void logSampleDetails(List<CloneClass> sample) {
		double originalSize = detectionResult.getList().size();
		double sampleSize = sample.size();
		getLogger().info(
				"Chose a sample of " + sampleSize + " out of " + originalSize
						+ " clone classes");

		if (originalSize > 0) {
			double ratio = sampleSize / originalSize;
			getLogger().info(
					"Sample has size " + ratio * 100 + "% of original set");
		}
	}

}