/*--------------------------------------------------------------------------+
$Id: RegExConverter.java 41751 2012-09-18 11:55:06Z kinnen $
|                                                                          |
| Copyright 2005-2010 by the ConQAT Project                                |
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
+--------------------------------------------------------------------------*/
package org.conqat.engine.architecture.overlap;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides features for translating a standard Java {@link Pattern}
 * to a dk.brics.automaton-style RegEx, and also for checking whether any
 * disallowed Java syntax is being used
 * 
 * @author $Author: beller$
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 33C4CB85D33F9A3D29BA3B6BF8DB25AE
 */
public class RegExConverter {

	/**
	 * Translates a {@link Pattern} to the RegEx format the Automaton Library
	 * needs
	 */
	public static String translatePatternToAutomatonLibraryRegEx(String regex) {
		regex = regex.replace("\\d", "[0-9]");
		regex = regex.replace("\\D", "[^0-9]");
		regex = regex.replace("\\w", "[A-Za-z_0-9]");
		regex = regex.replace("\\W", "[^A-Za-z_0-9]");
		regex = regex.replace("\\s", "[ \\t\\n\\x0B\\f\\r]");
		regex = regex.replace("\\S", "[^ \\t\\n\\x0B\\f\\r]");

		return regex;
	}

	/**
	 * Checks the supplied RegEx for constructs disallowed by automaton library
	 * 
	 * @return An error message or <code>null</code> if RegEx has no unsupported
	 *         syntax
	 */
	public static String hasUnsupportedSyntax(String regex) {
		String errorMessage = hasCharClassUnion(regex);
		if (errorMessage != null) {
			return errorMessage;
		}
		errorMessage = hasCharClassIntersection(regex);
		if (errorMessage != null) {
			return errorMessage;
		}
		errorMessage = hasBackReference(regex);
		if (errorMessage != null) {
			return errorMessage;
		}
		errorMessage = hasBoundaryMatcher(regex);
		if (errorMessage != null) {
			return errorMessage;
		}
		errorMessage = hasPossessiveRegEx(regex);
		if (errorMessage != null) {
			return errorMessage;
		}

		return null;
	}

	/**
	 * Builds an error message resembling the Java RegEx error messages
	 */
	private static String buildErrorMessage(String error, String regex,
			int matchedPosition) {
		String errorMessage = error + " near index " + matchedPosition;
		errorMessage += "\n" + regex + "\n";
		// insert error marker
		for (int i = 0; i < matchedPosition; i++) {
			errorMessage += " ";
		}
		errorMessage += "^";
		return errorMessage;
	}

	/**
	 * Checks whether the supplied regExtoCheck can be matched against
	 * regExPattern. If so, an error message is created with the supplied error.
	 * 
	 * @return An error message, or <code>null</code> if it doesn't have one.
	 */
	public static String matchesRegExPattern(String regExToCheck,
			String regExPattern, String error) {
		Pattern pattern = Pattern.compile(regExPattern);
		Matcher matcher = pattern.matcher(regExToCheck);
		if (matcher.find()) {
			int matchedPosition = matcher.start();
			return buildErrorMessage(error, regExToCheck, matchedPosition);
		}
		return null;
	}

	/**
	 * Checks whether the supplied RegEx has character class union, as in e.g.
	 * "[a[b]]"
	 * 
	 * @return An error message, or <code>null</code> if it doesn't have one.
	 */
	public static String hasCharClassUnion(String regex) {
		// real character class unions (no escaped character classes)
		return matchesRegExPattern(regex,
				"([^\\\\]|\\A)\\[(|[^\\]]*?([^\\]\\\\])?+)\\[",
				"Unsupported character class union");
	}

	/**
	 * Checks whether the supplied RegEx has character class intersection, as in
	 * e.g. "[a&&[ab]]"
	 * 
	 * @return An error message, or <code>null</code> if it doesn't have one.
	 */
	public static String hasCharClassIntersection(String regex) {
		// operations in character classes
		return matchesRegExPattern(regex, "([^\\\\]|\\A)\\[[^\\]]*&&",
				"Unsupported character class intersection");
	}

	/**
	 * Checks whether the supplied RegEx has back reference syntax, as in e.g.
	 * "(a)\1"
	 * 
	 * @return An error message, or <code>null</code> if it doesn't have one.
	 */
	public static String hasBackReference(String regex) {
		return matchesRegExPattern(regex, "\\\\(\\d){1}",
				"Unsupported backreference");
	}

	/**
	 * Checks whether the supplied RegEx uses boundary matchers, as in e.g. "\b"
	 * 
	 * @return An error message, or <code>null</code> if it doesn't have one.
	 */
	public static String hasBoundaryMatcher(String regex) {
		return matchesRegExPattern(regex, "\\\\[bBAGZ]",
				"Unsupported boundary matcher");
	}

	/**
	 * Checks whether the supplied RegEx uses the possessive quantifier, as in
	 * e.g. "a*+".
	 * 
	 * @return An error message, or <code>null</code> if it doesn't have one.
	 */
	public static String hasPossessiveRegEx(String regex) {
		return matchesRegExPattern(regex, "[?*+}]\\+",
				"Unsupported possessive quantifier");
	}

}
