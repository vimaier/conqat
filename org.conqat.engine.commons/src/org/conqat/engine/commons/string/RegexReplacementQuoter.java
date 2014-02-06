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
package org.conqat.engine.commons.string;

import java.util.regex.Matcher;

import org.conqat.engine.commons.util.ConQATInputProcessorBase;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: heinemann $
 * @version $Rev: 42997 $
 * @ConQAT.Rating GREEN Hash: 9A1DFA2AD58EE5C536702FC1AE41202E
 */
@AConQATProcessor(description = "Quotes a string to be used as a pattern replacement expression.")
public class RegexReplacementQuoter extends ConQATInputProcessorBase<String> {

	/** {@inheritDoc} */
	@Override
	public String process() {
		return Matcher.quoteReplacement(input);
	}

}
