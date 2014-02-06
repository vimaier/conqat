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

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.engine.resource.util.ResourceUtils;

/**
 * Base class for metrics that can be calculated on the text level.
 * 
 * @author $Author: goede $
 * @version $Rev: 41801 $
 * @ConQAT.Rating GREEN Hash: B7235F8AC226CCDB4EEE9521342A1190
 */
public abstract class TextMetricAnalyzerBase extends
		NumericMetricAnalyzerBase<ITextResource, ITextElement> {

	/** {@inheritDoc}. */
	@Override
	protected Class<ITextElement> getElementClass() {
		return ITextElement.class;
	}

	/** Reports a metric value. The location is one specific (filtered) line. */
	protected void reportMetricValueForFilteredLine(double value,
			int filteredLine) throws ConQATException {
		reportMetricValue(value,
				ResourceUtils.createTextRegionLocationForFilteredLines(
						currentElement, filteredLine, filteredLine));
	}

	/** Reports a metric value. The location is a region of (filtered) lines. */
	protected void reportMetricValueForFilteredLines(double value,
			int filteredStartLine, int filteredEndLine) throws ConQATException {
		reportMetricValue(value,
				ResourceUtils.createTextRegionLocationForFilteredLines(
						currentElement, filteredStartLine, filteredEndLine));
	}

	/**
	 * Reports a metric value. The location is a region denoted by (filtered)
	 * offsets.
	 */
	protected void reportMetricValueForFilteredOffsets(double value,
			int filteredStartOffset, int filteredEndOffset)
			throws ConQATException {
		reportMetricValue(value,
				ResourceUtils.createTextRegionLocationForFilteredOffsets(
						currentElement, filteredStartOffset, filteredEndOffset));
	}
}