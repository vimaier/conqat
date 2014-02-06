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
package org.conqat.engine.code_clones.normalization.provider;

import java.io.Serializable;

import org.conqat.engine.code_clones.core.Unit;
import org.conqat.engine.core.logging.IConQATLogger;
import org.conqat.engine.resource.text.ITextResource;

/**
 * Mock implementation of {@link IUnitProvider}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43157 $
 * @ConQAT.Rating GREEN Hash: 4B5E52A41123C0F0F714823FACC99BF7
 */
@SuppressWarnings("serial")
public class UnitProviderMock implements IUnitProvider<ITextResource, Unit>,
		Serializable {

	/** Always return null */
	@Override
	public Unit getNext() {
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public void init(ITextResource root, IConQATLogger logger) {
		// Do nothing
	}

	/** Always return null */
	@Override
	public Unit lookahead(int index) {
		return null;
	}
}