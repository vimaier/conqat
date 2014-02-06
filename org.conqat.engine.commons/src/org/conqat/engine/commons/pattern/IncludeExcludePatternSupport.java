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
package org.conqat.engine.commons.pattern;

import java.util.ArrayList;
import java.util.List;

import org.conqat.lib.commons.filesystem.AntPatternUtils;

/**
 * Combines a list of include and exclude patterns. The patterns are specified
 * as an ANT patterns and then converted to Java Regex patterns.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 41092 $
 * @ConQAT.Rating GREEN Hash: 3008EE2727419D4F7CE64ED9EB3EFC6E
 */
public class IncludeExcludePatternSupport {

	/** Case sensitivity */
	private boolean caseSensitive;

	/** The includes */
	private List<String> includes = new ArrayList<String>();

	/** The excludes */
	private List<String> excludes = new ArrayList<String>();

	/** The include patterns */
	private PatternList includePatterns = null;

	/** The exclude patterns */
	private PatternList excludePatterns = null;

	/** Constructs a new case-insensitive {@link IncludeExcludePatternSupport} */
	public IncludeExcludePatternSupport() {
		this(false);
	}

	/** Constructs a new {@link IncludeExcludePatternSupport} */
	public IncludeExcludePatternSupport(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	/** Sets case sensitivity */
	public void setCaseSensitive(boolean caseSensitive) {
		if (includePatterns != null) {
			throw new IllegalStateException(
					"Must not be called after isIncluded has been used (lazy compilation of patterns).");
		}
		this.caseSensitive = caseSensitive;
	}

	/** Adds the given include pattern (ANT pattern notation) */
	public void addIncludePattern(String include) {
		includes.add(include);
	}

	/** Adds the given exclude pattern (ANT pattern notation) */
	public void addExcludePattern(String exclude) {
		excludes.add(exclude);
	}

	/** Returns whether the given name should be included. */
	public boolean isIncluded(String name) {
		// lazy compilation patterns
		if (includePatterns == null) {
			includePatterns = new PatternList();
			excludePatterns = new PatternList();
			for (String include : includes) {
				includePatterns.add(AntPatternUtils.convertPattern(include,
						caseSensitive));
			}
			for (String exclude : excludes) {
				excludePatterns.add(AntPatternUtils.convertPattern(exclude,
						caseSensitive));
			}
		}
		return (includePatterns.emptyOrMatchesAny(name))
				&& !excludePatterns.matchesAny(name);
	}

}
