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

package org.conqat.engine.text.comments.utils;

import static org.conqat.lib.commons.collections.CollectionUtils.asHashSet;

import java.util.Set;

import org.conqat.engine.text.comments.classification.features.CodeRecognizer;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Comment Utilities
 * 
 * @author $Author: steidl $
 * @version $Rev: 46589 $
 * @ConQAT.Rating YELLOW Hash: 490AC73BE3596648349E1E96F5551670
 */
public class CommentUtils {

	/**
	 * Returns true if the comment in a inheritDoc comment, a constructor, a
	 * ConQAT Doc comment or deprecated or only refers to another comment with a
	 * see tag
	 */
	public static boolean isDefaultComment(String comment) {
		return (comment.contains("@inheritDoc")
				|| comment.contains("Constructor")
				|| comment.contains("constructor")
				|| comment.contains("@ConQAT.Doc")
				|| comment.contains("@deprecated") || comment.contains("@see"));
	}

	/**
	 * Returns true if comment contains no other information than java doc tags
	 */
	public static boolean hasOnlyJavaDoc(String comment) {
		comment = CommentUtils.removeCommentIdentifiers(comment);
		comment = CommentUtils.removeJavaDocElements(comment);
		comment = comment.replaceAll("\\s+", "");
		return comment.length() == 0;
	}

	/**
	 * Returns the tags of a comment. Assumes that comment are tagged like
	 * &copyright& or §copyright,de§. This is used for reading the machine
	 * learning training data. Returns an empty string if no tag was found.
	 * 
	 * @return the classification tag
	 */
	public static String getTags(String comment) {
		String[] parts = comment.split(CommentTags.separator);
		if (parts.length != 3) {
			return "";
		}
		return parts[1];
	}

	/**
	 * Returns the class tag of a comment, e.g."header" or "interface".
	 */
	public static String getClassTag(String comment) {
		String[] tags = CommentUtils.getTags(comment).split(",");
		for (String tag : tags) {
			if (!tag.equals("") && CommentTags.isTag(tag))
				return tag;
		}
		return "";
	}

	/**
	 * Returns true if the given comment contains the words copyright or
	 * license.
	 */
	public static boolean isCopyrightComment(String comment) {
		String commentString = comment.toLowerCase();
		return (commentString.contains("copyright") || commentString
				.contains("license"));
	}

	/** Set of java doc tags. */
	private static Set<String> javaDocTags = asHashSet("@param", "@return",
			"@throws", "@since", "@deprecated", "@author", "@see", "@serial");

	/**
	 * Normalizes a comment by removing tags, identifiers, java doc elements,
	 * line breaks and _ and }.
	 */
	private static String normalizeComment(String comment) {
		String result = removeTags(comment);
		result = removeCommentIdentifiers(result);
		result = removeJavaDocElements(result);
		result = removeLineBreaks(result);
		result = result.replaceAll("_", " ");
		result = result.replaceAll("}", "");
		return result;
	}

	/**
	 * Removes all tags that start and end with § in the given comment.
	 */
	private static String removeTags(String comment) {
		return comment.replaceAll(CommentTags.separator + ".*"
				+ CommentTags.separator, "");
	}

	/** Removes comment identifiers. */
	public static String removeCommentIdentifiers(String comment) {
		String result = comment.replaceAll("/\\*\\*", StringUtils.EMPTY_STRING);
		result = result.replaceAll("//", StringUtils.EMPTY_STRING);
		result = result.replaceAll("\\*/", StringUtils.EMPTY_STRING);
		result = result.replaceAll("\\*", StringUtils.EMPTY_STRING);
		result = result.replaceAll("\\\\", StringUtils.EMPTY_STRING);
		result = result.replaceAll("\\n", StringUtils.EMPTY_STRING);
		return result;
	}

	/** Removes all java doc tags. */
	private static String removeJavaDocElements(String comment) {
		String result = "";
		for (String tag : javaDocTags) {
			result = comment.replaceAll(tag, "");
		}
		return result;
	}

	/** Removes line breaks. */
	private static String removeLineBreaks(String comment) {
		return comment.replaceAll("\\n|\n", "");
	}

	/**
	 * Returns the normalized text in comments after removing code snippets
	 */
	public static String getTextInComment(String comment) {
		String result = "";
		String[] lines = comment.split("\n");
		for (int i = 0; i < lines.length; i++) {
			if (!CodeRecognizer.isCodeLine(lines[i])) {
				result = result + "\n" + lines[i];
			}
		}
		return normalizeComment(result);
	}
}
