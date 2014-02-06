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
package org.conqat.engine.code_clones.normalization.repetition;

import org.conqat.engine.code_clones.core.StatementUnit;
import org.conqat.engine.code_clones.core.TokenUnit;
import org.conqat.lib.commons.equals.IEquator;
import org.conqat.lib.scanner.ETokenType;

/**
 * Compares {@link StatementUnit}s for equality.
 * <p>
 * {@link StatementUnit}s are considered equal by this comparer, if they contain
 * the same number of tokens of the same type in the same order.
 * 
 * 
 * @author Elmar Juergens
 * @author $Author: kinnen $
 * 
 * @version $Revision: 41751 $
 * @ConQAT.Rating GREEN Hash: 4768AFEC3DAA505A5B1A3EECF338F410
 */
public class StatementEquator implements IEquator<StatementUnit> {

	/** Singleton instance */
	private static StatementEquator instance = null;

	/** Singleton accessor */
	public static StatementEquator getInstance() {
		if (instance == null) {
			instance = new StatementEquator();
		}
		return instance;
	}

	/**
	 * Perform comparison.
	 * 
	 * @return <code>true</code>, if both statements are <code>null</code>, or
	 *         both statements contain the same number of tokens with the same
	 *         token types (see {@link ETokenType}) in the same order, else
	 *         <code>false</code>.
	 */
	@Override
	public boolean equals(StatementUnit statement1, StatementUnit statement2) {
		TokenUnit[] tokens1 = statement1.getTokens();
		TokenUnit[] tokens2 = statement2.getTokens();

		// handle nulls
		if (tokens1 == null && tokens2 == null) {
			return true;
		}
		if (tokens1 == null || tokens2 == null) {
			return false;
		}

		// compare length
		if (tokens1.length != tokens2.length) {
			return false;
		}

		// compare elements
		for (int i = 0; i < tokens1.length; i++) {
			if (tokens1[i].getType() != tokens2[i].getType()) {
				return false;
			}
		}
		return true;
	}

}