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
package org.conqat.engine.sourcecode.analysis.shallowparsed;

import java.util.List;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.analysis.TokenMetricAnalyzerBase;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.shallowparser.ShallowParserFactory;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;

/**
 * Base class for processors working on shallow parsed elements.
 * 
 * @author $Author: goede $
 * @version $Rev: 41722 $
 * @ConQAT.Rating GREEN Hash: FEC9BCFDB84EB82EB34C3718C5AC9475
 */
public abstract class ShallowParsedMetricAnalyzerBase extends
		TokenMetricAnalyzerBase {

	/** {@inheritDoc} */
	@Override
	protected final void calculateMetrics(ITokenElement element) {
		try {
			calculateMetrics(element,
					ShallowParserFactory.parse(element, getLogger()));
		} catch (ConQATException e) {
			getLogger().warn(
					"Ignoring element " + element.getLocation() + ": "
							+ e.getMessage());
		}
	}

	/**
	 * Template method for calculating metric values.
	 * 
	 * @see #calculateMetrics(ITokenElement) for reporting details.
	 */
	protected abstract void calculateMetrics(ITokenElement element,
			List<ShallowEntity> entities);
}