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
package org.conqat.engine.commons.util;

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.commons.format.EValueFormatter;
import org.conqat.engine.commons.format.IValueFormatter;
import org.conqat.engine.commons.format.Summary;
import org.conqat.engine.commons.node.DisplayList;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeConstants;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 38374 $
 * @ConQAT.Rating GREEN Hash: E0C082AB5CA3457E1C62FFF96A87572A
 */
@AConQATProcessor(description = "This processor can be used for modifying the display list of a ConQAT node.")
public class DisplayListEditor extends ConQATPipelineProcessorBase<IConQATNode> {

	/** Determines whether all keys are removed from the display list */
	private boolean removeAll = false;

	/** Determines whether the Assessment summary at the root node is removed */
	private boolean removeAssessmentSummary = false;

	/** Names of keys to remove. */
	private final List<String> removeKeys = new ArrayList<String>();

	/** Names of keys to add (at the end). */
	private final DisplayList addKeys = new DisplayList();

	/** Names of keys to insert (at the beginning). */
	private final DisplayList insertKeys = new DisplayList();

	/** Names of keys for which to change the format. */
	private final DisplayList formatKeys = new DisplayList();

	/**
	 * Flag to indicate if root should be hidden or not. We use the
	 * non-primitive to here to use the third state (<code>null</code>) to
	 * encode the default value that does not change the root hiding.
	 */
	private Boolean hideRoot = null;

	/** Formatter for summary. */
	private EValueFormatter summaryFormatter;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "clear", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Removes all keys")
	public void setClearAll(
			@AConQATAttribute(name = "all", description = "Default is false") boolean removeAll) {
		this.removeAll = removeAll;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "assessment-summary", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Remove assessment summary")
	public void setRemoveAssessmentSummary(
			@AConQATAttribute(name = "remove", description = "Default is false") boolean removeAssessmentSummary) {
		this.removeAssessmentSummary = removeAssessmentSummary;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "add", description = "Add a key to the end of the display list.")
	public void addKey(
			@AConQATAttribute(name = "key", description = "The name of the key") String key,
			@AConQATAttribute(name = "formatter", description = "The formatter to be used", defaultValue = EValueFormatter.DEFAULT_STRING) EValueFormatter formatter) {
		addKeys.addKey(key, formatter.getFormatter());
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "insert", description = "Add a key to the beginning of the display list.")
	public void insertKey(
			@AConQATAttribute(name = "key", description = "The name of the key") String key,
			@AConQATAttribute(name = "formatter", description = "The formatter to be used", defaultValue = EValueFormatter.DEFAULT_STRING) EValueFormatter formatter) {
		insertKeys.addKey(key, formatter.getFormatter());
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "remove", description = "Remove a key from the display list.")
	public void removeKey(
			@AConQATAttribute(name = "key", description = "The name of the key") String key) {
		removeKeys.add(key);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "format", description = "Changes the format used for a key from the display list.")
	public void setFormatter(
			@AConQATAttribute(name = "key", description = "The name of the key") String key,
			@AConQATAttribute(name = "formatter", description = "The formatter to be used") EValueFormatter formatter) {
		formatKeys.addKey(key, formatter.getFormatter());
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "external-format", description = "Changes the format used for a key from the display list using a processor provided formatter (instead of one from the default enum).")
	public void setFormatter(
			@AConQATAttribute(name = "key", description = "The name of the key") String key,
			@AConQATAttribute(name = "formatter", description = "The formatter to be used") IValueFormatter formatter) {
		formatKeys.addKey(key, formatter);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "summary-format", description = "Changes the format used for the summary")
	public void setSummaryFormatter(
			@AConQATAttribute(name = "formatter", description = "The formatter to be used") EValueFormatter formatter) {
		summaryFormatter = formatter;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "hide", minOccurrences = 0, maxOccurrences = 1, description = "Hide/unhide root. Default is to leave unchanged.")
	public void hideRoot(
			@AConQATAttribute(name = "root", description = "True to hide root. False to unhide root.") boolean hideRoot) {
		this.hideRoot = hideRoot;
	}

	/** {@inheritDoc} */
	@Override
	protected void processInput(IConQATNode input) {
		if (removeAssessmentSummary) {
			input.setValue(NodeConstants.SUMMARY, null);
		}

		if (summaryFormatter != null) {
			setSummaryFormatter(input);
		}

		if (hideRoot != null) {
			input.setValue(NodeConstants.HIDE_ROOT, hideRoot);
		}

		DisplayList newDisplayList = insertKeys;

		if (!removeAll) {
			DisplayList reducedDisplayList = NodeUtils.getDisplayList(input);
			reducedDisplayList.removeKeys(removeKeys);
			newDisplayList.addAll(reducedDisplayList);
		}
		newDisplayList.addAll(addKeys);

		for (String key : formatKeys.getKeyList()) {
			if (!newDisplayList.containsKey(key)) {
				getLogger().error(
						"Tried to change format for key " + key
								+ " which is not in the display list anymore!");
				continue;
			}
			newDisplayList.addKey(key, formatKeys.getFormatter(key));
		}

		input.setValue(NodeConstants.DISPLAY_LIST, newDisplayList);
	}

	/** Set the formatter for the summary. */
	private void setSummaryFormatter(IConQATNode input) {
		Object summary = input.getValue(NodeConstants.SUMMARY);
		if (summary == null) {
			return;
		}
		if (summary instanceof Summary) {
			summary = ((Summary) summary).getValue();
		}
		input.setValue(NodeConstants.SUMMARY, new Summary(summary,
				summaryFormatter));
	}
}