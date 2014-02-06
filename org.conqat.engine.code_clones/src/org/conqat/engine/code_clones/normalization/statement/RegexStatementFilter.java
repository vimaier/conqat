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
package org.conqat.engine.code_clones.normalization.statement;

import org.conqat.engine.code_clones.core.Unit;
import org.conqat.engine.code_clones.normalization.UnitProviderBase;
import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.sourcecode.resource.ITokenResource;

/**
 * A filter that discards statements based on regular expressions.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 36296 $
 * @ConQAT.Rating GREEN Hash: 36ADC0D89F1788B4001F453F2BEA13AB
 */
public class RegexStatementFilter extends StatementFilterBase {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** List of patterns used to ignore statements. */
	private final PatternList ignoreList;

	/** Constructor. */
	public RegexStatementFilter(
			UnitProviderBase<ITokenResource, Unit> provider,
			PatternList ignoreList) {
		super(provider);
		this.ignoreList = ignoreList;
	}

	/** {@inheritDoc} */
	@Override
	protected boolean isFiltered(Unit unit) {
		return ignoreList.matchesAny(unit.getUnnormalizedContent());
	}

}