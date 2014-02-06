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
package org.conqat.engine.commons.mark;

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.commons.CommonUtils;
import org.conqat.engine.commons.logging.IncludeExcludeListLogMessage;
import org.conqat.engine.commons.logging.StructuredLogTags;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.commons.traversal.NodeTraversingProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;

/**
 * Base class for processors that mark nodes.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: C9C77011A9D13AFBC3FC88322AB69A1A
 */
public abstract class MarkerBase<T extends IConQATNode> extends
		NodeTraversingProcessorBase<T> {

	/** The list of patterns to match against. */
	protected PatternList patternList;

	/** Key into which match result is stored */
	protected String writeKey;

	/** Value written into key of objects that match */
	protected Object value;

	/** Keeps track of all elements marked as matching. */
	protected final List<String> matchingNodeLogDetails = new ArrayList<String>();

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "log-caption", attribute = "value", optional = true, description = "Caption used in log messages. If not set, default is used")
	public String logCaption = defaultLogCaption();

	/** {@ConQAT.Doc}. */
	@AConQATParameter(name = "pattern", minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "Regular expressions used for element matching.")
	public void setPatternList(
			@AConQATAttribute(name = "list", description = "Reference to pattern list.") PatternList patternList) {
		this.patternList = patternList;
		if (!patternList.isEmpty()) {
			getLogger().info(
					new IncludeExcludeListLogMessage("patterns", logCaption,
							patternList.asStringList(),
							StructuredLogTags.PATTERN));
		}
	}

	/** {@ConQAT.Doc}. */
	@AConQATFieldParameter(parameter = "log", attribute = "purpose", optional = true, description = ""
			+ "Purpose description used to create user-log message. Default is 'Marked as generated'")
	public String logPurpose = "Marked as generated";

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "mark", minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "Value that gets stored under key in matching nodes")
	public void setMarkValue(
			@AConQATAttribute(name = "key", description = "Name of key under which value gets stored") String writeKey,
			@AConQATAttribute(name = "value", description = "Value that gets stored for matching nodes") String valueString,
			@AConQATAttribute(name = "type", description = "Type of value that gets stored") String typeName)
			throws ConQATException {
		this.writeKey = writeKey;
		this.value = CommonUtils.convertTo(valueString, typeName);
	}

	/**
	 * Template method that deriving classes override to provide a caption for
	 * their log messages.
	 */
	protected abstract String defaultLogCaption();

	/**
	 * Template method that deriving classes override to provide the string
	 * against which is matched
	 */
	protected abstract String getNodeStringToMatch(T node)
			throws ConQATException;

	/** {@inheritDoc} */
	@Override
	protected void finish(T root) {
		getLogger().info(
				new IncludeExcludeListLogMessage(StructuredLogTags.FILES,
						logPurpose, matchingNodeLogDetails,
						StructuredLogTags.FILES));
	}

	/** {@inheritDoc} */
	@Override
	public void visit(T node) {
		if (skip(node)) {
			return;
		}
		try {
			boolean matches = patternList
					.findsAnyIn(getNodeStringToMatch(node));
			if (matches) {
				node.setValue(writeKey, value);
				matchingNodeLogDetails.add(logDetail(node));
			}
		} catch (ConQATException e) {
			getLogger().warn(
					"Could not mark: " + logDetail(node) + ": "
							+ e.getMessage());
		}
	}

	/** Template method that deriving classes override to provide log details */
	protected String logDetail(T node) {
		return node.getId();
	}

	/**
	 * Template method that deriving classes override to determine which nodes
	 * to operate on
	 */
	@SuppressWarnings("unused")
	protected boolean skip(T node) {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	protected ETargetNodes getTargetNodes() {
		return ETargetNodes.ALL;
	}

}