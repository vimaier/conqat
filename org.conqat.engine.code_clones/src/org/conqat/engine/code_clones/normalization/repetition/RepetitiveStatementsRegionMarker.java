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
 * @ConQAT.Rating GREEN Hash: 44D2357035279917EC78D53A73E62F03
 */
@AConQATProcessor(description = "Marks regions of repetitive statements. "
		+ RepetitiveRegionMarkerBase.DOC)
public class RepetitiveStatementsRegionMarker extends
		RepetitiveRegionMarkerBase {

	/** {@inheritDoc} */
	@Override
	protected RepetitiveStatementsRegionMarkerStrategy createStrategy() {
		return new RepetitiveStatementsRegionMarkerStrategy();
	}
}