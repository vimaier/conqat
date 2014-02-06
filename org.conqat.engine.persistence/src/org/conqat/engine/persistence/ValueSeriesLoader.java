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
package org.conqat.engine.persistence;

import org.conqat.engine.commons.statistics.DateValueSeries;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;

/**
 * {@ConQAT.Doc}
 * 
 * 
 * @author juergens
 * @author $Author: juergens $
 * @version $Rev: 35197 $
 * @ConQAT.Rating GREEN Hash: CD68220D30DA0DD300611018FC697351
 */
@AConQATProcessor(description = "Loads a value series from a data base. Does not modify value series.")
public class ValueSeriesLoader extends ValueSeriesCreatorBase {

	/** {@inheritDoc} */
	@Override
	public DateValueSeries process() throws ConQATException {
		return createSeries();
	}

}