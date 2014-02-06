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
package org.conqat.engine.commons.date;

import java.util.Date;

import org.conqat.engine.commons.filter.KeyBasedFilterBase;
import org.conqat.engine.commons.node.IRemovableConQATNode;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;

/**
 * Base class for filters that deal with dates.
 * 
 * @author deissenb
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: EC24F1DBD1D047DB96809C7E3BA12593
 */
public abstract class DateFilterBase extends
		KeyBasedFilterBase<Date, IRemovableConQATNode> {

	/** The date to compare to. */
	protected Date date;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "date", minOccurrences = 1, maxOccurrences = 1, description = "The date to compare to.")
	public void setDate(
			@AConQATAttribute(name = "value", description = "Date") Date date) {
		this.date = date;
	}

}