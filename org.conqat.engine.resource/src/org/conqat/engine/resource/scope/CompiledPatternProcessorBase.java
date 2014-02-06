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
package org.conqat.engine.resource.scope;

import java.util.List;

import org.conqat.engine.commons.logging.IncludeExcludeListLogMessage;
import org.conqat.engine.commons.logging.StructuredLogTags;
import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.util.ConQATDirectoryScanner;

/**
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating RED Hash:
 */
public abstract class CompiledPatternProcessorBase extends PatternProcessorBase {

	/**
	 * The ant patterns from {@link #includePatterns} compiled into regex
	 * patterns.
	 */
	protected final PatternList compiledIncludePatterns = new PatternList();
	/**
	 * The ant patterns from {@link #excludePatterns} compiled into regex
	 * patterns.
	 */
	protected final PatternList compiledExcludePatterns = new PatternList();

	/**
	 * 
	 */
	public CompiledPatternProcessorBase() {
		super();
	}

	/** Compiles the patterns */
	protected void compilePatterns() throws ConQATException {
		compileAntPatterns(includePatterns, compiledIncludePatterns);
		compileAntPatterns(excludePatterns, compiledExcludePatterns);
	
		if (!includePatterns.isEmpty()) {
			getLogger().info(
					new IncludeExcludeListLogMessage("patterns", true,
							includePatterns, StructuredLogTags.SCOPE,
							StructuredLogTags.PATTERN));
		}
		if (!excludePatterns.isEmpty()) {
			getLogger().info(
					new IncludeExcludeListLogMessage("patterns", false,
							excludePatterns, StructuredLogTags.SCOPE,
							StructuredLogTags.PATTERN));
		}
	}

	/** Compiles a list of ANT patterns into Java regexes. */
	private void compileAntPatterns(List<String> antPatterns, PatternList target)
			throws ConQATException {
				for (String antPattern : antPatterns) {
					target.add(ConQATDirectoryScanner.convertPattern(antPattern,
							caseSensitive));
				}
			}

	/**
	 * Returns whether the given name should be included according to the
	 * {@link #compiledIncludePatterns} and {@link #compiledExcludePatterns}.
	 */
	protected boolean isIncluded(String name) {
		return (compiledIncludePatterns.isEmpty() || compiledIncludePatterns
				.matchesAny(name)) && !compiledExcludePatterns.matchesAny(name);
	}

}