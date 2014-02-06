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
package org.conqat.engine.code_clones.index.report;

import java.util.Date;

import org.conqat.engine.code_clones.core.IdProvider;
import org.conqat.lib.commons.date.DateUtils;

/**
 * Common base class for clone reporter which deals with the birth data and the
 * ID generation.
 * 
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: EB519C2A38D78C3EFAB56EE6AC8FEA2A
 */
public abstract class CloneClassReporterBase implements ICloneClassReporter {

	/** Stores the current date. */
	private final Date now = DateUtils.getNow();

	/** The ID provider used. */
	private final IdProvider idProvider = new IdProvider();

	/** {@inheritDoc} */
	@Override
	public Date getBirthDate() {
		return now;
	}

	/** {@inheritDoc} */
	@Override
	public long provideId() {
		return idProvider.provideId();
	}
}