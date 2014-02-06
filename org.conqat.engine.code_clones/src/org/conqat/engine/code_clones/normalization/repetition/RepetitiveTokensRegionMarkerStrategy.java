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

import org.conqat.engine.code_clones.core.TokenUnit;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.lib.commons.equals.DefaultEquator;
import org.conqat.lib.commons.region.RegionSet;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46755 $
 * @ConQAT.Rating GREEN Hash: 4B9B189E655E6F3C6CEFC5D0EE786443
 */
@AConQATProcessor(description = ""
		+ "Marks regions of repetitive tokens for context sensitive normalization.")
public class RepetitiveTokensRegionMarkerStrategy extends
		RepetitiveRegionMarkerStrategyBase {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** {@inheritDoc} */
	@Override
	protected void findRegionsForElement(ITokenElement element, RegionSet result)
			throws ConQATException {
		TokenUnit[] sequence = RepetitionUtils.getTokens(element, getLogger());

		RepetitionFinder<TokenUnit> detector = new RepetitionFinder<TokenUnit>(
				sequence, DefaultEquator.INSTANCE,
				repetitionParameters.getMinLength(),
				repetitionParameters.getMinMotifInstances());

		markRegions(result, detector);
	}
}