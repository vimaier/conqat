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
package org.conqat.engine.code_clones.normalization.repetition;

import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 37519 $
 * @ConQAT.Rating GREEN Hash: E91ABC52E801C303ED3B682286094AAC
 */
@AConQATProcessor(description = "Marks regions of repetitive tokens. "
		+ RepetitiveRegionMarkerBase.DOC)
public class RepetitiveTokensRegionMarker extends RepetitiveRegionMarkerBase {

	/** {@inheritDoc} */
	@Override
	protected RepetitiveTokensRegionMarkerStrategy createStrategy() {
		return new RepetitiveTokensRegionMarkerStrategy();
	}
}