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
package org.conqat.engine.model_clones.detection;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.IConQATLogger;
import org.conqat.engine.model_clones.detection.util.AugmentedModelGraph;
import org.conqat.engine.model_clones.detection.util.ICloneReporter;

/**
 * Interface for model clone detectors.
 * 
 * @author hummelb
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: 269CEBA3B15B2166C1D4C9690921C21A
 */
public interface IModelCloneDetector {

	/**
	 * Runs the detection algorithm.
	 * 
	 * @param modelGraph
	 *            the graph to run the detection on.
	 * @param cloneReporter
	 *            the class used for reporting detected clones.
	 * @param logger
	 *            the logger used. This should be from the processor using the
	 *            clone detector (not the one creating it), as this is the place
	 *            where the user looks for log messages.
	 */
	void detect(AugmentedModelGraph modelGraph, ICloneReporter cloneReporter,
			IConQATLogger logger) throws ConQATException;
}