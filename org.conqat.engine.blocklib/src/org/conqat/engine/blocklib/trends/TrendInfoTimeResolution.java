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
package org.conqat.engine.blocklib.trends;

import org.conqat.engine.commons.util.ConQATInputProcessorBase;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.html_presentation.chart.ETimeResolution;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: pfaller $
 * @version $Rev: 37524 $
 * @ConQAT.Rating GREEN Hash: CCFF10C347465650F0401C15F14633CA
 */
@AConQATProcessor(description = "Extracts the time resolution from a trend info.")
public class TrendInfoTimeResolution extends
		ConQATInputProcessorBase<TrendInfo> {

	/** {@inheritDoc} */
	@Override
	public ETimeResolution process() {
		return input.getTimeResolution();
	}
}
