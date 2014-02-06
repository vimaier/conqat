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
package org.conqat.engine.text.comments.classification.features;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.conqat.lib.commons.string.StringUtils;

/**
 * Class to detect code snippets in comments
 * 
 * @author $Author: steidl $
 * @version $Rev: 46304 $
 * @ConQAT.Rating YELLOW Hash: D70DFF2838E12723C5000CAA3B4D2F16
 */
public class CodeRecognizer {

	/** Threshold for code recognition. */
	private static final double threshold = 0.1;

	/** Pattern for Java if, for, and while statements */
	private static final String javaControlLoopPattern = "(if|while|for)\\s*\\(.*";

	/** Pattern for method calls in Java */
	private static final String javaMethodCallPattern = "[a-zA-Z]+\\.([a-zA-Z]|_|[1-9])+\\(.*\\)";

	/**
	 * Returns true if at least ten percent of the comment's lines contain code.
	 */
	public static boolean isCommentedOutCode(String comment) {
		List<String> lines = StringUtils.splitLinesAsList(comment);

		int countCodeLines = 0;
		for (String line : lines) {
			if (isCodeLine(line)) {
				countCodeLines++;
			}
		}

		double ratio = (double) countCodeLines / ((double) lines.size());
		return ratio >= threshold;
	}

	/** Returns true if the given string contains code snippets. */
	public static boolean isCodeLine(String commentLine) {
		return matchMethodCallPattern(commentLine)
				|| containsJavaControlLoopPattern(commentLine)
				|| containsCodeCharacteristic(commentLine);
	}

	/**
	 * Returns true if the given line of comment has a method call pattern.
	 */
	private static boolean matchMethodCallPattern(String commentLine) {
		Pattern pattern = Pattern.compile(javaMethodCallPattern);
		Matcher matcher = pattern.matcher(commentLine);
		return matcher.find();
	}

	/**
	 * Returns true if the given string contains a code characteristic such as
	 * "void" or ";".
	 */
	private static boolean containsCodeCharacteristic(String commentline) {
		return (commentline.contains("=") && commentline.contains(";"))
				|| commentline.contains("==") || commentline.contains("void")
				|| commentline.endsWith(";") || commentline.endsWith("{");
	}

	/**
	 * Returns true if the given comment line contains java syntax such as an
	 * "if" or a "while" statement.
	 */
	private static boolean containsJavaControlLoopPattern(String commentline) {
		Pattern pattern = Pattern.compile(javaControlLoopPattern);
		Matcher matcher = pattern.matcher(commentline);
		return matcher.find();
	}

}
