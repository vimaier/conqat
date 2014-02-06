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
package org.conqat.engine.sourcecode.analysis.plsql;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.analysis.shallowparsed.ShallowParsedFindingAnalyzerBase;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.lib.scanner.ELanguage;

/**
 * Base class for PL/SQL specific findings checks.
 * 
 * @author $Author: goede $
 * @version $Rev: 43229 $
 * @ConQAT.Rating GREEN Hash: DAC549908B1CC15FF47E9A78706E71BE
 */
public abstract class PlsqlFindingAnalyzerBase extends
		ShallowParsedFindingAnalyzerBase {

	/** Category name used. */
	public static final String CATEGORY_NAME = "PL/SQL Checks";

	/** {@inheritDoc} */
	@Override
	protected void analyzeElement(ITokenElement element) throws ConQATException {
		// ignore non-PL/SQL elements
		if (element.getLanguage() != ELanguage.PLSQL) {
			return;
		}

		super.analyzeElement(element);
	}

	/** {@inheritDoc} */
	@Override
	protected String getFindingCategoryName() {
		return CATEGORY_NAME;
	}
}
