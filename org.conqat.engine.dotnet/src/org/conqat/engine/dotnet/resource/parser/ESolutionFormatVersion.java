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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.IConQATLogger;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.TextElementUtils;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Version of solution and project file formats
 * 
 * @author $Author: heinemann $
 * @version $Rev: 45826 $
 * @ConQAT.Rating GREEN Hash: F16E58511C49467EE680ADA5D03509C5
 */
public enum ESolutionFormatVersion {

	/** VS.NET 2003 format */
	VERSION_8,

	/** VS.NET 2005, 2008, 2010, 2012 format */
	VERSION_9;

	/** String that identifies VS.NET 2012 solution */
	private static final String SOLUTION_HEADER_12 = defaultSolutionHeader("12.00");

	/** String that identifies VS.NET 2010 solution */
	private static final String SOLUTION_HEADER_11 = defaultSolutionHeader("11.00");

	/** String that identifies VS.NET 2008 solution */
	private static final String SOLUTION_HEADER_10 = defaultSolutionHeader("10.00");

	/** String that identifies VS.NET 2005 solution */
	private static final String SOLUTION_HEADER_9 = defaultSolutionHeader("9.00");

	/** String that identifies VS.NET 2003 solution */
	private static final String SOLUTION_HEADER_8 = defaultSolutionHeader("8.00");

	/**
	 * Returns the default header of solution files for the specified version
	 * string, e.g. '12.00'.
	 */
	private static String defaultSolutionHeader(String version) {
		return "Microsoft Visual Studio Solution File, Format Version "
				+ version;
	}

	/**
	 * Regular expression used to extract format version number from projects.
	 * Product version is either in the form 'ProductVersion = "7.10.3077"' or
	 * in form '<ProductVersion>8.0.5027</ProductVersion>' (with varying
	 * numbers). This pattern matches both forms. The numbers are matched by
	 * capturing group with index 1.
	 */
	private static final Pattern PRODUCT_VERSION_PATTERN = Pattern
			.compile("<?ProductVersion>?\\s*=?\\s*\"?(\\d+)\\.\\d+\\.\\d+\"?");

	/**
	 * VS.NET 2010 and up files appear to not always have the above patterns.
	 * Instead, we search for the tool version.
	 */
	private static final Pattern TOOLS_VERSION_PATTERN = Pattern
			.compile("<Project[^>]*ToolsVersion=\"(\\d+).\\d+\"");

	/** Determine format for a project element. */
	public static ESolutionFormatVersion determineProjectFormat(
			ITextElement projectElement) throws ConQATException {
		String projectContent = projectElement.getTextContent();
		Matcher productVersionMatcher = PRODUCT_VERSION_PATTERN
				.matcher(projectContent);
		Matcher toolsVersionMatcher = TOOLS_VERSION_PATTERN
				.matcher(projectContent);

		if (productVersionMatcher.find()) {
			String majorVersionString = productVersionMatcher.group(1);
			int major = Integer.parseInt(majorVersionString);

			// unfortunately, the project file format numbers and the VS version
			// numbers are not the same. Thus we need to map individually.
			switch (major) {
			case 7:
				return ESolutionFormatVersion.VERSION_8;
			case 8:
				// fall through to case 9
			case 9:
				return ESolutionFormatVersion.VERSION_9;
			default:
				throwUnknownFormatException(projectElement);
			}
		}

		if (toolsVersionMatcher.find()) {
			String toolVersionString = toolsVersionMatcher.group(1);
			int major = Integer.parseInt(toolVersionString);
			if (major == 4 || major == 3) {
				return ESolutionFormatVersion.VERSION_9;
			}

			throwUnknownFormatException(projectElement);
		}

		// our format assumption is violated since both matchers fail
		throw new ConQATException(
				"Unable to determine format version for project file: "
						+ projectElement.getLocation());
	}

	/** Creates and throws an exception that shows that format is unknown. */
	private static ConQATException throwUnknownFormatException(
			ITextElement projectElement) throws ConQATException {
		throw new ConQATException("Project Element "
				+ projectElement.getLocation() + " has unknown format");
	}

	/**
	 * Asserts that the solution format is known.
	 * 
	 * @throws ConQATException
	 *             If the solution format is not known.
	 */
	public static void checkValidSolutionFormat(ITextElement solutionElement)
			throws ConQATException {

		String[] lines = TextElementUtils.getLines(solutionElement);

		CCSMAssert.isTrue(lines.length >= 2,
				"Solution file shorter than expected");

		if (!ESolutionFormatVersion.startsWithAnyOf(lines, SOLUTION_HEADER_8,
				SOLUTION_HEADER_9, SOLUTION_HEADER_10, SOLUTION_HEADER_11,
				SOLUTION_HEADER_12)) {
			throw new ConQATException("Format of solution "
					+ solutionElement.getLocation() + " unknown");
		}
	}

	/**
	 * Returns true, if the first or the second line in an array starts with any
	 * of the given strings
	 */
	private static boolean startsWithAnyOf(String[] lines, String... prefixes) {
		return StringUtils.startsWithOneOf(lines[0], prefixes)
				|| StringUtils.startsWithOneOf(lines[1], prefixes);
	}

	/**
	 * Factory method that creates a {@link ProjectParser} according to the
	 * solution version.
	 */
	public ProjectParser createProjectParser(IConQATLogger logger) {
		switch (this) {
		case VERSION_9:
			return new ProjectParser9(logger);
		case VERSION_8:
			return new ProjectParser8(logger);
		default:
			CCSMAssert.fail("No reader for this project format implemented!");
			return null;
		}
	}

}