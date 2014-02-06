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
package org.conqat.engine.code_clones.normalization;

import org.conqat.engine.code_clones.core.Unit;
import org.conqat.engine.code_clones.normalization.statement.RegexStatementFilter;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.sourcecode.resource.ITokenResource;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: juergens $
 * @version $Rev: 35928 $
 * @ConQAT.Rating GREEN Hash: F012EB21CF57A5152F503F06FFFE67A3
 */
@AConQATProcessor(description = "A filter that ignores all statements whose textual content matches one of the given pattern.")
public class RegexStatementFilterFactory extends ConQATProcessorBase {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.INPUT_NAME, attribute = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_DESC)
	public UnitProviderBase<ITokenResource, Unit> provider;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "pattern", attribute = "ignore", description = "The pattern used to match statements that should be ignored.")
	public PatternList ignoreList;

	/** {@inheritDoc} */
	@Override
	public UnitProviderBase<ITokenResource, Unit> process() {
		return new RegexStatementFilter(provider, ignoreList);
	}

}
