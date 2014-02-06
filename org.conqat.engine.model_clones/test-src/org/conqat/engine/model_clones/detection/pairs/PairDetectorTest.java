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
package org.conqat.engine.model_clones.detection.pairs;

import org.conqat.engine.model_clones.detection.ModelCloneReporterMock;
import org.conqat.engine.model_clones.detection.util.AugmentedModelGraph;
import org.conqat.engine.model_clones.model.IModelGraph;
import org.conqat.engine.core.logging.testutils.LoggerMock;

/**
 * Test for the {@link PairDetector}.
 * 
 * @author hummelb
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: A41CBF1CCE71E72D96B5C947CD49CB81
 */
public class PairDetectorTest extends PairDetectorTestBase {

	/** {@inheritDoc} */
	@Override
	protected ModelCloneReporterMock runDetection(IModelGraph graph,
			int minCloneSize, int minCloneWeight) throws Exception {
		ModelCloneReporterMock result = new ModelCloneReporterMock();
		new PairDetector(new AugmentedModelGraph(graph), minCloneSize,
				minCloneWeight, true/* early exit */, result, new LoggerMock())
				.execute();
		return result;
	}
}