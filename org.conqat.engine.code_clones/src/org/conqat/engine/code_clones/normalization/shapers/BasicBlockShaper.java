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
package org.conqat.engine.code_clones.normalization.shapers;

import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;

/**
 * {@ConQAT.Doc}
 * 
 * @author Elmar Juergens
 * @author $Author: hummelb $
 * 
 * @version $Revision: 36296 $
 * @ConQAT.Rating GREEN Hash: 73664980EC25A31ECBEC4A4EE6123987
 */
@AConQATProcessor(description = ""
		+ "Inserts sentinels after each '{' or '}' character. Clones thus cannot cross block boundaries.")
public class BasicBlockShaper extends ShaperBase {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** Determines whether a token represents a boundary */
	@Override
	protected boolean isBoundary(IToken token) {
		return token.getType() == ETokenType.RBRACE
				|| token.getType() == ETokenType.LBRACE;
	}

}