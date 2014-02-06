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
package org.conqat.engine.resource.text.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.resource.text.filter.base.Deletion;
import org.conqat.engine.resource.text.filter.base.TextFilterBase;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 41499 $
 * @ConQAT.Rating GREEN Hash: 2C175957340B08E320B5D075E3A29643
 */
@AConQATProcessor(description = "A filter that removes all occurrences of a regular expression.")
public class RegexTextFilter extends TextFilterBase {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "patterns", attribute = "ref", description = "Patterns that specify text that gets removed.")
	public PatternList removePatterns;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "create-gap", attribute = "value", optional = true, description = ""
			+ "If this is set to true, the filtered regions will introduce filter gaps. Default is false.")
	public boolean gap = false;

	/** {@inheritDoc} */
	@Override
	public List<Deletion> getDeletions(String s, String originUniformPath) {
		List<Deletion> deletions = new ArrayList<Deletion>();

		for (Pattern removePattern : removePatterns) {
			Matcher matcher = removePattern.matcher(s);
			while (matcher.find()) {
				int start = matcher.start();
				int end = matcher.end();
				Deletion deletion = new Deletion(start, end, gap);
				deletions.add(deletion);
				logDeletion(deletion, originUniformPath);
			}
		}

		return deletions;
	}

}