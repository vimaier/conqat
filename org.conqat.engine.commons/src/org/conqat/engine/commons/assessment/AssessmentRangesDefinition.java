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
package org.conqat.engine.commons.assessment;

import java.awt.Color;
import java.util.Collection;

import org.conqat.engine.core.core.ConQATException;

/**
 * Default implementation of {@link AssessmentRangesDefinitionBase}.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 6065A545BC2B4F3BB2547648D391D3AB
 */
public class AssessmentRangesDefinition extends
		AssessmentRangesDefinitionBase<AssessmentRangeDefinition> {

	/** Constructor. */
	public AssessmentRangesDefinition(Color defaultColor, String defaultName,
			Collection<AssessmentRangeDefinition> rangeDefinitions) throws ConQATException {
		super(defaultColor, defaultName, rangeDefinitions);
	}

	/** {@inheritDoc} */
	@Override
	protected AssessmentRangeDefinition newRangeDefinition(double value,
			Color color, String name) {
		return new AssessmentRangeDefinition(value, color, name);
	}

	/** {@inheritDoc} */
	@Override
	public AssessmentRangesDefinition deepClone() {
		return this;
	}
}
