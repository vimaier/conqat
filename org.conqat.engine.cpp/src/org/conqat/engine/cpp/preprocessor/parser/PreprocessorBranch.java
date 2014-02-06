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
package org.conqat.engine.cpp.preprocessor.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.conqat.lib.scanner.IToken;

/**
 * A preprocessor branch. This represents the condition (#if, #elif, #else) and
 * the code following the condition.
 * 
 * @author $Author: feilkas $
 * @version $Rev: 41746 $
 * @ConQAT.Rating GREEN Hash: F6D93CB0C27D389712BD9AAA514A47EA
 */
public class PreprocessorBranch {

	/** The token representing the condition, i.e. the preprocessor directive. */
	private final IToken conditionToken;

	/** The condition itself as a string. This is null for #else. */
	private final String conditionString;

	/** The code contained in the branch. */
	/* package */final List<PreprocessorNodeBase> containedCode = new ArrayList<PreprocessorNodeBase>();

	/** Constructor. */
	/* package */PreprocessorBranch(IToken token) {
		this.conditionToken = token;

		Matcher matcher = PreprocessorParser.IF_OR_ELIF_PATTERN.matcher(token
				.getText());
		if (matcher.matches()) {
			this.conditionString = matcher.group(1).trim();
		} else {
			// must be else or something similar
			this.conditionString = null;
		}
	}

	/**
	 * Returns the token representing the condition, i.e. the preprocessor
	 * directive.
	 */
	public IToken getConditionToken() {
		return conditionToken;
	}

	/** Returns the condition itself as a string. This is null for #else. */
	public String getConditionString() {
		return conditionString;
	}

	/** Returns the contained code/nodes. */
	public UnmodifiableList<PreprocessorNodeBase> getContainedCode() {
		return CollectionUtils.asUnmodifiable(containedCode);
	}
}
