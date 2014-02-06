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
package org.conqat.engine.code_clones.core.constraint;

import java.util.Date;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;

/**
 * Base class for constraints that are evaluated against a baseline date.
 * 
 * @author Elmar Juergens
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: A752FF53F7C465DFD8BBD6AD9802D64D
 */
public abstract class DateConstraintBase extends ConstraintBase {

	/** Date relative to which the constraint is evaluated */
	protected Date baselineDate;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "baseline", minOccurrences = 1, maxOccurrences = 1, description = "Date relative to which filtering is performed")
	public void setBaselineDate(
			@AConQATAttribute(name = "date", description = ConQATParamDoc.INPUT_REF_DESC) Date baselineDate) {
		this.baselineDate = baselineDate;
	}

}