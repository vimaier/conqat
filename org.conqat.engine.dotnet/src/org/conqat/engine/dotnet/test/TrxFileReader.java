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
package org.conqat.engine.dotnet.test;

import static org.conqat.engine.dotnet.test.ETrxXmlAttribute.duration;
import static org.conqat.engine.dotnet.test.ETrxXmlAttribute.endTime;
import static org.conqat.engine.dotnet.test.ETrxXmlAttribute.outcome;
import static org.conqat.engine.dotnet.test.ETrxXmlAttribute.startTime;
import static org.conqat.engine.dotnet.test.ETrxXmlAttribute.testId;

import java.util.HashSet;
import java.util.Set;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.commons.node.ListNode;
import org.conqat.engine.commons.node.NodeConstants;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.engine.resource.util.TextElementXMLReader;
import org.conqat.lib.commons.assessment.Assessment;
import org.conqat.lib.commons.assessment.ETrafficLightColor;
import org.conqat.lib.commons.xml.IXMLElementProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 44256 $
 * @ConQAT.Rating YELLOW Hash: DEBA3775C15FF7837C9C162CB518B315
 */
@AConQATProcessor(description = "This processor reads .trx files that contain the test results of NUnit tests")
public class TrxFileReader extends ConQATProcessorBase {

	/** {@ConQAT.Doc} */
	@AConQATKey(type = "java.lang.String", description = "The duration of the test run")
	public static final String DURATION_KEY = "duration";

	/** {@ConQAT.Doc} */
	@AConQATKey(type = "java.lang.String", description = "The id of the test case")
	public static final String ID_KEY = "id";

	/** {@ConQAT.Doc} */
	@AConQATKey(type = "java.lang.String", description = "The outcome of the test run (Passed, Failed, Error)")
	public static final String OUTCOME_KEY = "outcome";

	/** {@ConQAT.Doc} */
	@AConQATKey(type = "java.lang.String", description = "The assessment of the outcome of the test run")
	public static final String ASSESSMENT_KEY = "assessment";

	/** {@ConQAT.Doc} */
	@AConQATKey(type = "java.lang.String", description = "The start time the test run")
	public static final String STARTTIME_KEY = "start time";

	/** {@ConQAT.Doc} */
	@AConQATKey(type = "java.lang.String", description = "The end time of the test run")
	public static final String ENDTIME_KEY = "end time";

	/** Set of TRX elements that gets read */
	private final Set<ITextElement> trxElements = new HashSet<ITextElement>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "reports", description = "Text element hierarchy that contains the .trx reports", minOccurrences = 0, maxOccurrences = -1)
	public void setReportFilesRoot(
			@AConQATAttribute(name = "root", description = "All text elements in this hierarchy are treated as .trx reports") ITextResource root) {
		for (ITextElement trxElement : ResourceTraversalUtils
				.listTextElements(root)) {
			trxElements.add(trxElement);
		}
	}

	/** {@inheritDoc} */
	@Override
	public ListNode process() throws ConQATException {
		ListNode rootNode = createRootNode();
		if (trxElements.size() == 0) {
			getLogger().warn("No trx elements found.");
			return rootNode;
		}

		for (ITextElement trxElement : trxElements) {
			TrxParser parser = new TrxParser(trxElement);
			parser.parseTrxReport(rootNode);
		}

		return rootNode;
	}

	/** Creates a root node and configures its display list */
	private ListNode createRootNode() {
		ListNode rootNode = new ListNode("Unit Tests");
		// we don't display the other keys by
		// default, as they clutter the output. We still store the data,
		// since it is used by other processors
		NodeUtils.addToDisplayList(rootNode, OUTCOME_KEY, DURATION_KEY,
				ASSESSMENT_KEY);

		rootNode.setValue(NodeConstants.HIDE_ROOT, true);
		return rootNode;
	}

	/** Xml parser that performs the actual XML processing. */
	protected class TrxParser
			extends
			TextElementXMLReader<ETrxXmlElement, ETrxXmlAttribute, ConQATException> {

		/** Constructor */
		public TrxParser(ITextElement element) throws ConQATException {
			super(element, ETrxXmlAttribute.class);
		}

		/** Reads the relevant data from the .trx report. */
		public void parseTrxReport(ListNode rootNode) throws ConQATException {
			parseAndWrapExceptions();
			processDecendantElements(new UnitTestResultProcessor(rootNode));
		}

		/** Processor for UnitTest in .trx-files elements */
		private class UnitTestResultProcessor implements
				IXMLElementProcessor<ETrxXmlElement, ConQATException> {

			/** Root node */
			private final ListNode rootNode;

			/** Constructor */
			public UnitTestResultProcessor(ListNode rootNode) {
				this.rootNode = rootNode;
			}

			/** {@inheritDoc} */
			@Override
			public ETrxXmlElement getTargetElement() {
				return ETrxXmlElement.UnitTestResult;
			}

			/** {@inheritDoc} */
			@Override
			public void process() {
				String name = getStringAttribute(ETrxXmlAttribute.testName);

				ListNode node = new ListNode(name);
				rootNode.addChild(node);

				parseAndStoreAttribute(node, ID_KEY, testId);
				parseAndStoreAttribute(node, STARTTIME_KEY, startTime);
				parseAndStoreAttribute(node, ENDTIME_KEY, endTime);
				parseAndStoreAttribute(node, DURATION_KEY, duration);

				parseOutcome(node);
			}

			/** Retrieve string attribute and store in node */
			private String parseAndStoreAttribute(ListNode node, String key,
					ETrxXmlAttribute attribute) {
				String value = getStringAttribute(attribute);
				node.setValue(key, value);
				return value;
			}

			/** Parse and assess outcome value */
			private void parseOutcome(ListNode node) {
				String testOutcome = parseAndStoreAttribute(node, OUTCOME_KEY,
						outcome);

				if (testOutcome.equals("Passed")
						|| testOutcome.equals("PassedButRunAborted")) {
					node.setValue(ASSESSMENT_KEY, new Assessment(
							ETrafficLightColor.GREEN));
				} else if (testOutcome.equals("Failed")) {
					node.setValue(ASSESSMENT_KEY, new Assessment(
							ETrafficLightColor.RED));
				} else {
					node.setValue(ASSESSMENT_KEY, new Assessment(
							ETrafficLightColor.YELLOW));
				}
			}
		}
	}
}