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
package org.conqat.engine.text.comments.utils;

/**
 * Tags used in the training data set
 * 
 * @author $Author: steidl $
 * @version $Rev: 46589 $
 * @ConQAT.Rating YELLOW Hash: EF0D472E2BC367A339936CEF26747091
 */
public class CommentTags {

	/**
	 * separator tag, comments are tagged like /** &copyright&
	 */
	public static final String separator = "§";

	/** copyright tag */
	public static final String tagCopyright = "copyright";
	/** interface tag */
	public static final String tagInterface = "interface";
	/** header */
	public static final String tagHeader = "header";
	/** inline tag */
	public static final String tagInline = "inline";
	/** code tag */
	public static final String tagCode = "code";
	/** task tag */
	public static final String tagTask = "task";
	/** section tag */
	public static final String tagSection = "section";

	/** Returns true if the given string if a comment tag. */
	public static boolean isTag(String tag) {
		return ((tagCopyright.equals(tag)) || (tagInterface.equals(tag))
				|| (tagHeader.equals(tag)) || (tagInline.equals(tag))
				|| (tagCode.equals(tag)) || (tagTask.equals(tag)) || (tagSection
					.equals(tag)));
	}
}
