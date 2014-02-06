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
package org.conqat.engine.commons.pattern;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

import org.conqat.engine.commons.CommonUtils;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.collections.BasicPatternList;

/**
 * A list of RegEx pattern. This is useful for defining black lists, etc. This
 * is a {@link BasicPatternList} with engine specific extensions.
 * 
 * @author $Author: heineman $
 * @version $Rev: 41572 $
 * @ConQAT.Rating GREEN Hash: 50FEB81E1C34B92F81D7D38FEAB1CCDF
 * 
 * @see PatternListDef
 */
public class PatternList extends BasicPatternList {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/**
	 * This raises an exception if the pattern list is empty. We introduced this
	 * method as this check is frequently used by processors dealing with
	 * pattern lists.
	 */
	public static void checkIfEmpty(PatternList patternList)
			throws ConQATException {
		if (patternList.isEmpty()) {
			throw new ConQATException(
					"Empty pattern list. This is probably not intended.");
		}
	}

	/** Creates an empty {@link PatternList} */
	public PatternList() {
		// do nothing
	}

	/**
	 * Creates a {@link PatternList} with a single pattern compiled from the
	 * given regular expression.
	 */
	public PatternList(String regex) throws ConQATException {
		this(CommonUtils.compilePattern(regex));
	}

	/** Creates a {@link PatternList} with the collection of patterns */
	public PatternList(Collection<? extends Pattern> patterns) {
		super(patterns);
	}

	/** Creates a pattern list for the specified patterns. */
	public PatternList(Pattern... patterns) {
		this(Arrays.asList(patterns));
	}

	/** {@inheritDoc} */
	@Override
	public PatternList deepClone() {
		return new PatternList(this);
	}
}