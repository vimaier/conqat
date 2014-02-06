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
package org.conqat.engine.text.identifier;

import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.analysis.LiteralAnalyzerBase;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.lib.commons.collections.CounterSet;
import org.conqat.lib.scanner.IToken;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 41651 $
 * @ConQAT.Rating GREEN Hash: EF2828DC2984276C70DEA1D225AAF7D8
 */
@AConQATProcessor(description = "Produces a counter set containing the text of all literals.")
public class LiteralCounter extends LiteralAnalyzerBase {

	/** Counts literals. */
	private final CounterSet<String> literals = new CounterSet<String>();

	/** {@inheritDoc} */
	@Override
	protected void analyzeLiteral(IToken token, ITokenElement element) {
		literals.inc(token.getText());
	}

	/** {@inheritDoc} */
	@Override
	public CounterSet<String> process() throws ConQATException {
		analyzeLiterals();
		return literals;
	}
}
