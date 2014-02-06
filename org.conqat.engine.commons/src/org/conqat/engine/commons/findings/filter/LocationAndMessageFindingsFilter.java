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
package org.conqat.engine.commons.findings.filter;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.conqat.engine.commons.CommonUtils;
import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.collections.PairList;
import org.conqat.lib.commons.filesystem.AntPatternUtils;

/**
 * {ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating GREEN Hash: 0AF99ECECAE26A20236CC61E25B3BAFB
 */
@AConQATProcessor(description = "Filters findings based on regular expressions "
		+ "that match any location of the finding, the message of the finding, "
		+ "or both.")
public class LocationAndMessageFindingsFilter extends FindingsFilterBase {

	/** Pattern for the uniform paths and messaged of filtered findings. */
	private PairList<Pattern, Pattern> filterPatterns = new PairList<Pattern, Pattern>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "finding-regex-filter", description = "Regex pattern for filtering findings.")
	public void setLocationRegexPattern(
			@AConQATAttribute(name = "uniform-path-regex", description = "The uniform path regex pattern.") String uniformPath,
			@AConQATAttribute(name = "message-regex", description = "The message regex pattern.") String message)
			throws ConQATException {
		filterPatterns.add(CommonUtils.compilePattern(uniformPath),
				CommonUtils.compilePattern(message));
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "finding-ant-pattern-filter", description = "Ant pattern for filtering findings.")
	public void setLocationAntPattern(
			@AConQATAttribute(name = "uniform-path-ant-pattern", description = "The uniform path ant pattern.") String uniformPath,
			@AConQATAttribute(name = "message-regex", description = "The message regex pattern.") String message,
			@AConQATAttribute(name = "case-sensitive", description = "Flag for disabling case-sensitivity for the path ant pattern.", defaultValue = "true") boolean caseSensitive)
			throws ConQATException {
		try {
			Pattern convertPattern = AntPatternUtils.convertPattern(
					uniformPath, caseSensitive);
			filterPatterns.add(convertPattern,
					CommonUtils.compilePattern(message));
		} catch (PatternSyntaxException e) {
			throw new ConQATException(e);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected boolean isFiltered(IConQATNode node, Finding finding) {
		for (int i = 0; i < filterPatterns.size(); i++) {
			Pattern uniformPathPatterns = filterPatterns.getFirst(i);
			Pattern messagePatterns = filterPatterns.getSecond(i);
			if (uniformPathMatches(finding, uniformPathPatterns)
					&& messageMatches(finding, messagePatterns))
				return true;
		}
		return false;
	}

	/**
	 * Checks whether the finding's location's uniform path matches the pattern.
	 */
	private boolean uniformPathMatches(Finding finding,
			Pattern uniformPathPattern) {
		return uniformPathPattern.matcher(
				finding.getLocation().getUniformPath()).matches();
	}

	/** Checks whether the finding's message matches the pattern. */
	private boolean messageMatches(Finding finding, Pattern messagePattern) {
		return messagePattern.matcher(finding.getMessage()).matches();
	}

}
