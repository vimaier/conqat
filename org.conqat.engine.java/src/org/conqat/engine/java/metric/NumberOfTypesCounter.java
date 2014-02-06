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
package org.conqat.engine.java.metric;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.sourcecode.analysis.TokenOccurenceCounterBase;
import org.conqat.lib.scanner.ETokenType;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: juergens $
 * @version $Rev: 35831 $
 * @ConQAT.Rating GREEN Hash: 673C946F90BC8D0A6FE882DC6A69E9F8
 */
@AConQATProcessor(description = "Counts the number of types (i.e. classes, interfaces, enums) by counting the"
		+ " occurrences of the corresponding keywords.")
public class NumberOfTypesCounter extends TokenOccurenceCounterBase {

	/** {@inheritDoc} */
	@Override
	protected Set<ETokenType> getConsideredTokenTypes() {
		return new HashSet<ETokenType>(Arrays.asList(ETokenType.CLASS,
				ETokenType.INTERFACE, ETokenType.ENUM));
	}

}
