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
package org.conqat.engine.commons.node;

import org.conqat.engine.commons.findings.FindingReport;

/**
 * Common constants used for the keys in the IConQATNode key value pairs. For
 * many of these there are convenience methods in
 * {@link org.conqat.engine.commons.node.NodeUtils}.
 * 
 * @author Florian Deissenboeck
 * @author Benjamin Hummel
 * @author Tilman Seifert
 * @author $Author: pfaller $
 * 
 * @version $Rev: 37466 $
 * @ConQAT.Rating GREEN Hash: CADDA5899FF86F2B5A993099EFE8E4B8
 */
public class NodeConstants {

	/**
	 * The display list is appended to the root node of an IConQATONode
	 * hierarchy and holds a {@link DisplayList} containing the keys relevant
	 * for the presentation layer.
	 */
	public static final String DISPLAY_LIST = "display-list";

	/**
	 * A java.util.Comparator that should be used for sorting the children of
	 * this node if sorting is required (e.g. in the presentation).
	 */
	public static final String COMPARATOR = "comparator";

	/**
	 * An {@link org.conqat.lib.commons.assessment.Assessment} for the node.
	 */
	public static final String SUMMARY = "summary";

	/**
	 * This can be attached to the root of node hierarchy to signal not to
	 * display the root node.
	 */
	public static final String HIDE_ROOT = "hide-root";

	/** A {@link FindingReport} for the root node. */
	public static final String FINDINGS_REPORT = "findings-report";

	/**
	 * Key for rules identifiers as they are typically used by tools like PMD or
	 * FindBugs.
	 */
	public static String RULE_IDENTIFIER_KEY = "rule-identifier";

	/**
	 * Key in which processors for different languages store the length of the
	 * longest method.
	 */
	public static final String LONGEST_METHOD_KEY = "LML";
}