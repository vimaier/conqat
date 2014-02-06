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
package org.conqat.engine.cpp.pclint;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.base.ReportReaderBase;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.util.ResourceUtils;
import org.conqat.engine.resource.util.TextElementXMLReader;
import org.conqat.lib.commons.filesystem.CanonicalFile;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.xml.IXMLElementProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 46815 $
 * @ConQAT.Rating GREEN Hash: 145F4D2AC52E57B013EFED0CC870621A
 */
@AConQATProcessor(description = "Reads a PCLint report and attaches the findings "
		+ "to the provided resource tree. "
		+ ReportReaderBase.DOC
		+ "PCLint must be configured so that the report contains absolute filenames.")
public class PCLintReportReader extends ReportReaderBase {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Key for findings", type = "org.conqat.engine.commons.findings.FindingsList")
	public static final String PC_LINT = "PC-Lint";

	/** The categories included. */
	private final Set<String> categories = new HashSet<String>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "category", minOccurrences = 0, description = "Adds a category to include when loading the report. "
			+ "If no categories are specified, all categories will be allowed.")
	public void addCategory(
			@AConQATAttribute(name = "name", description = "Known categories: error"
					+ "warning") String category) {
		categories.add(category);
	}

	/** {@inheritDoc} */
	@Override
	protected void loadReport(ITextElement report) throws ConQATException {
		new DocReader(report).load();
	}

	/** Returns null as we don't have rule descriptions for PCLint. */
	@Override
	protected String obtainRuleDescription(String ruleId) {
		return null;
	}

	/** Class used for reading PCLint reports. */
	private final class DocReader
			extends
			TextElementXMLReader<EPCLintElements, EPCLintAttributes, ConQATException> {

		/** Constructor. */
		private DocReader(ITextElement report) throws ConQATException {
			super(report, EPCLintAttributes.class);
		}

		/** Reads the report and loads its contents into the findings report. */
		public void load() throws ConQATException {
			parseAndWrapExceptions();
			processDecendantElements(new MessageProcessor());
		}

		/** Processor for bug instances. */
		private final class MessageProcessor implements
				IXMLElementProcessor<EPCLintElements, ConQATException> {

			/** {@inheritDoc} */
			@Override
			public EPCLintElements getTargetElement() {
				return EPCLintElements.message;
			}

			/** {@inheritDoc} */
			@Override
			public void process() throws ConQATException {

				String locationInReport = getChildText(EPCLintElements.file);

				if (!FileSystemUtils.isAbsolutePath(locationInReport)) {
					// interpret locations relative to report directory
					CanonicalFile reportLocation = ResourceUtils
							.getFileFromLocation(element.getLocation());
					if (reportLocation == null) {
						throw new ConQATException("PCLint report not found at "
								+ element.getLocation());
					}
					locationInReport = new File(reportLocation.getParentFile(),
							locationInReport).getAbsolutePath();
				} else {
					locationInReport = new File(locationInReport)
							.getAbsolutePath();
				}

				String category = getChildText(EPCLintElements.type);
				if (!(categories.isEmpty() || categories.contains(category))) {
					return;
				}

				String code = getChildText(EPCLintElements.code);
				if (ignoreFindingType(code)) {
					return;
				}

				String message = getChildText(EPCLintElements.desc);

				String lineAsString = getChildText(EPCLintElements.line);
				try {
					int line = Integer.valueOf(lineAsString);
					createLineFinding(code, message, locationInReport, line);
				} catch (NumberFormatException e) {
					getLogger().warn(
							"Error parsing line for finding at " + lineAsString
									+ " in report " + element.getLocation());
					createFindingForFileLocation(code, message,
							locationInReport);
				}
			}
		}
	}

}