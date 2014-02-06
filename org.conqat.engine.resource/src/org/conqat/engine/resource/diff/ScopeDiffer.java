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
package org.conqat.engine.resource.diff;

import java.util.HashSet;
import java.util.Set;

import org.conqat.engine.commons.logging.ListStructuredLogMessage;
import org.conqat.engine.commons.logging.StructuredLogTags;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: pfaller $
 * @version $Rev: 45872 $
 * @ConQAT.Rating YELLOW Hash: 81124D25CFEB7B9F33CE31F4EC9C3AF7
 */
@AConQATProcessor(description = "This processor analyzes the difference between two scopes. It is assumed "
		+ "that main scope is more recent than the 'comparee' scope. The mapping of "
		+ "the elements is based on uniform pathes. The diff calculation excludes "
		+ "lines consisting of whitespace only.")
public class ScopeDiffer extends ScopeDifferBase {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Relative Number of (non-whitespace) lines added, deleted or modified.", type = "java.lang.Double")
	public static final String KEY_RELATIVE_CHURN = "Relative Churn";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Number of (non-whitespace) lines added, deleted or modified.", type = "java.lang.Integer")
	public static final String KEY_CHURN_LINES = "Churn Lines";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Total number of (non-whitespace) lines.", type = "java.lang.Integer")
	public static final String KEY_NORMALIZED_LINES = "Relative Churn Base";

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Elements present in the main scope but "
			+ "not in the comparee are flagged with 'true'.", type = "java.lang.Boolean")
	public static final String KEY_NEW = "New";

	/** Collects uniform paths for logging new elements */
	private Set<String> newElements = new HashSet<String>();

	/** Collects uniform paths for logging modified elements */
	private Set<String> modifiedElements = new HashSet<String>();

	/** Collects uniform paths for logging unchanged elements */
	private Set<String> unchangedElements = new HashSet<String>();

	/** Collects uniform paths for logging of removed elements */
	private Set<String> removedElements = new HashSet<String>();

	/** {@inheritDoc} */
	@Override
	protected void setUp(ITextResource root) throws ConQATException {
		super.setUp(root);
		NodeUtils.addToDisplayList(root, KEY_RELATIVE_CHURN, KEY_NEW,
				KEY_CHURN_LINES);
	}

	/** {@inheritDoc} */
	@Override
	protected void determineDiff(ITextElement mainElement,
			ITextElement compareeElement) throws ConQATException {

		CodeChurnMetrics<ITextElement> codeChurnMetrics = new LocCodeChurnMetrics(
				mainElement, compareeElement, getLogger());
		mainElement.setValue(KEY_NEW, codeChurnMetrics.isNew());
		mainElement.setValue(KEY_RELATIVE_CHURN,
				codeChurnMetrics.getRelativeChurn());
		mainElement.setValue(KEY_CHURN_LINES, codeChurnMetrics.getCodeChurn());
		mainElement.setValue(KEY_NORMALIZED_LINES,
				codeChurnMetrics.getMaxLines());

		String uniformPath = mainElement.getUniformPath();
		if (codeChurnMetrics.isNew()) {
			newElements.add(uniformPath);
		} else if (codeChurnMetrics.getCodeChurn() > 0) {
			modifiedElements.add(uniformPath);
		} else {
			unchangedElements.add(uniformPath);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void processUnmatchedCompareeElement(ITextElement value) {
		// just log elements
		removedElements.add(value.getUniformPath());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws ConQATException
	 */
	@Override
	protected void finish(ITextResource root) throws ConQATException {
		super.finish(root);
		getLogger().info(
				new ListStructuredLogMessage("New Elements: "
						+ newElements.size(), newElements,
						StructuredLogTags.FILES));
		getLogger().info(
				new ListStructuredLogMessage("Modified Elements:"
						+ modifiedElements.size(), modifiedElements,
						StructuredLogTags.FILES));
		getLogger().info(
				new ListStructuredLogMessage("Unchanged Elements: "
						+ unchangedElements.size(), unchangedElements,
						StructuredLogTags.FILES));
		getLogger().info(
				new ListStructuredLogMessage("Removed Elements: "
						+ removedElements.size(), removedElements,
						StructuredLogTags.FILES));
	}

}
