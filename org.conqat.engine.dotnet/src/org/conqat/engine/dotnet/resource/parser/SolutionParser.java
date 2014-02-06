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
package org.conqat.engine.dotnet.resource.parser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.IConQATLogger;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.TextElementUtils;

/**
 * Reads Visual Studio.NET Solution files and extracts the project file names.
 * <p>
 * Consult the package documentation for a description of those aspects of the
 * VS.NET solution file format that are relevant for the extraction of project
 * file names.
 * <p>
 * This parser simply iterates the lines of the solution file. Lines that start
 * with {@value #PROJECT_DEFINITION_LINE_PREFIX} contain the
 * solution-file-relative path to the project file in the third string literal.
 * I.e., the line
 * 
 * <pre>
 * Project(&quot;{FAE04EC0-301F-11D3-BF4B-00C04F79EFBC}&quot;) = &quot;ILAnalyzerTest&quot;, &quot;ILAnalyzerTest\ILAnalyzerTest.csproj&quot;, &quot;{291BC6FE-152B-460A-AD21-EFD9AE3D02FC}&quot;
 * </pre>
 * 
 * points to the file <code>ILAnalyzerTest\ILAnalyzerTest.csproj</code>
 * 
 * @author $Author: heinemann $
 * @version $Rev: 45827 $
 * @ConQAT.Rating GREEN Hash: 6DDFBB2C28DFE6D77BAB751A73DD9AFF
 */
public class SolutionParser {

	/** Lines in the solution that define projects start with this prefix */
	private static final String PROJECT_DEFINITION_LINE_PREFIX = "Project(\"{";

	/** Special project names that are excluded */
	private static final PatternList IGNORED_PROJECTS_PATTERN = new PatternList();
	static {
		// matches all strings that do not contain a dot. I.e., all file names
		// without extension. This way, we can exclude vs.net solution folders.
		IGNORED_PROJECTS_PATTERN.add(Pattern.compile("[^\\.]+"));
	}

	/**
	 * Extracts the project element filenames, relative to the solution element
	 * location, from the solution element.
	 * 
	 * @throws ConQATException
	 *             if the format of the solution is not known or solution cannot
	 *             be read.
	 */
	public static Set<String> parse(ITextElement solutionElement,
			IConQATLogger logger) throws ConQATException {
		ESolutionFormatVersion.checkValidSolutionFormat(solutionElement);

		Set<String> relativeProjectElementNames = new HashSet<String>();
		for (String line : TextElementUtils.getLines(solutionElement)) {
			if (line.startsWith(PROJECT_DEFINITION_LINE_PREFIX)) {
				String relativeProjectElementName = retrieveRelativeProjectElementName(line);
				if (!IGNORED_PROJECTS_PATTERN
						.matchesAny(relativeProjectElementName)) {
					relativeProjectElementNames.add(relativeProjectElementName);
				} else {
					logger.debug("Excluding solution item: "
							+ relativeProjectElementName);
				}
			}
		}
		return relativeProjectElementNames;
	}

	/**
	 * Retrieves the relative project name from a project definition line.
	 * <p>
	 * The relative project name is contained in the third string literal.
	 */
	private static String retrieveRelativeProjectElementName(String line) {
		return retrieveStringLiterals(line).get(2).replaceAll("\"", "");
	}

	/** Returns an array of the string literals contained in the project line. */
	private static List<String> retrieveStringLiterals(String projectLine) {
		Pattern stringLiteralPattern = Pattern.compile("\\\"[^\\\"]*\\\"");
		Matcher matcher = stringLiteralPattern.matcher(projectLine);

		List<String> matches = new ArrayList<String>();
		while (matcher.find()) {
			matches.add(matcher.group());
		}

		return matches;
	}

}