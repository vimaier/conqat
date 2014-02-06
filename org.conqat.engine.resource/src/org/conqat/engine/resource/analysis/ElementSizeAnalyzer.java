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
package org.conqat.engine.resource.analysis;

import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.IResource;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 44EB959B79F84BCC4ACF0BC6C65192F8
 */
@AConQATProcessor(description = "Determines the size of elements in bytes.")
public class ElementSizeAnalyzer extends
		NumericMetricAnalyzerBase<IResource, IElement> {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Size [byte]", type = "java.lang.Long")
	public static final String KEY = "Size";

	/** {@inheritDoc} */
	@Override
	protected String getKey() {
		return KEY;
	}

	/** {@inheritDoc} */
	@Override
	protected void calculateMetrics(IElement element) throws ConQATException {
		reportMetricValue(element.getContent().length);
	}
}