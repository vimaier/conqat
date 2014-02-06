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
package org.conqat.engine.sourcecode.pattern;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;

import org.conqat.lib.commons.collections.IntList;

/**
 * Matcher for {@link EnumPattern}.
 * 
 * @author herrmama
 * @author $Author: juergens $
 * @version $Rev: 35204 $
 * @ConQAT.Rating GREEN Hash: FCA80613EB50670A95EF590D1D8D14BA
 */
public class EnumPatternMatcher implements MatchResult {

	/** {@link Matcher} to which this matcher delegates. */
	private final Matcher matcher;

	/** Mapping of positions. */
	private final IntList positions;

	/**
	 * Constructor.
	 * 
	 * @param pattern
	 *            The enum pattern on which this matcher is based.
	 * @param string
	 *            The string that needs to be matched by the string pattern.
	 * @param positions
	 *            The mapping of string positions to positions in the element
	 *            list.
	 */
	EnumPatternMatcher(@SuppressWarnings("rawtypes") EnumPattern pattern,
			String string, IntList positions) {
		this.positions = positions;
		matcher = pattern.getPattern().matcher(string);
	}

	/**
	 * Map a position in the string to a position in the element list. Returns
	 * -1 if the string position cannot be mapped to a position in the element
	 * list.
	 */
	private int getPosition(int stringPosition) {
		if (isIdentityMapping()) {
			return stringPosition;
		}
		for (int i = 0, n = positions.getSize(); i < n; i++) {
			Integer position = positions.get(i);
			if (stringPosition <= position) {
				return i;
			}
		}
		return -1;
	}

	/** Check whether the matcher has an identity mapping. */
	private boolean isIdentityMapping() {
		return positions == null;
	}

	/** see {@link Matcher#find()} */
	public boolean find() {
		return matcher.find();
	}

	/** see {@link Matcher#matches()} */
	public boolean matches() {
		return matcher.matches();
	}

	/** see {@link Matcher#end()} */
	@Override
	public int end() {
		int end = matcher.end();
		return getPosition(end);
	}

	/** see {@link Matcher#end(int)} */
	@Override
	public int end(int group) {
		return getPosition(matcher.end(group));
	}

	/** see {@link Matcher#group()} */
	@Override
	public String group() {
		return matcher.group();
	}

	/** see {@link Matcher#group(int)} */
	@Override
	public String group(int group) {
		return matcher.group(group);
	}

	/** see {@link Matcher#groupCount()} */
	@Override
	public int groupCount() {
		return matcher.groupCount();
	}

	/** see {@link Matcher#start()} */
	@Override
	public int start() {
		int start = matcher.start();
		return getPosition(start);
	}

	/** see {@link Matcher#start(int)} */
	@Override
	public int start(int group) {
		return getPosition(matcher.start(group));
	}
}