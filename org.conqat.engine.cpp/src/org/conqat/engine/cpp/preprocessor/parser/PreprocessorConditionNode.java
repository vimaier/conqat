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

import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.conqat.lib.scanner.IToken;

/**
 * A {@link PreprocessorNodeBase} representing a condition, i.e. an entire #if
 * with all #elif/#else branches.
 * 
 * @author $Author: feilkas $
 * @version $Rev: 41746 $
 * @ConQAT.Rating GREEN Hash: B963722E10556B01ED8256B323BA1187
 */
public class PreprocessorConditionNode extends PreprocessorNodeBase {

	/** The branches of the condition */
	private final List<PreprocessorBranch> branches = new ArrayList<PreprocessorBranch>();

	/** The closing #endif. */
	private IToken endifToken;

	/** Constructor. */
	/* package */PreprocessorConditionNode(IToken ifToken) {
		branches.add(new PreprocessorBranch(ifToken));
	}

	/** Returns the last branch in the condition. */
	/* package */PreprocessorBranch getLastBranch() {
		return CollectionUtils.getLast(branches);
	}

	/** Closes the condition. */
	/* package */void close(IToken endifToken) {
		CCSMAssert.isTrue(this.endifToken == null, "May only close once!");
		this.endifToken = endifToken;
	}

	/** Returns the branches. */
	public UnmodifiableList<PreprocessorBranch> getBranches() {
		return CollectionUtils.asUnmodifiable(branches);
	}

	/** Returns the #endif token (or null if unclosed). */
	public IToken getEndifToken() {
		return endifToken;
	}

	/** Adds another branch to this condition. */
	/* package */void addBranch(IToken token) {
		branches.add(new PreprocessorBranch(token));
	}
}
