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
package org.conqat.engine.code_clones.result;

import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.code_clones.core.report.CloneReportWriter;
import org.conqat.engine.code_clones.core.report.SourceElementDescriptor;
import org.conqat.engine.code_clones.core.utils.CloneUtils;
import org.conqat.engine.code_clones.detection.CloneDetectionResultElement;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.sorting.NodeIdComparator;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.IConQATLogger;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.engine.resource.text.TextElementUtils;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.lib.commons.digest.Digester;
import org.conqat.lib.commons.string.StringUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: poehlmann $
 * @version $Revision: 45195 $
 * @ConQAT.Rating GREEN Hash: E66869602085AE7A38AD821C3E873A91
 */
@AConQATProcessor(description = "Processor that writes a clone detection result file in xml format."
		+ "The actual xml processing is performed in class {@link CloneReportWriter}."
		+ "The main job of this class is to make {@link CloneReportWriter} accessible in"
		+ "a ConQAT clone detection configuration. This separation allows for the use of"
		+ "{@link CloneReportWriter} outside of ConQAT.")
public class CloneReportWriterProcessor extends CloneReportWriterProcessorBase {

	/** Clone detection result for which report gets written */
	private CloneDetectionResultElement detectionResult;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "detection-result", description = ConQATParamDoc.INPUT_DESC, minOccurrences = 1, maxOccurrences = 1)
	public void setDetectionResult(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) CloneDetectionResultElement detectionResult) {
		this.detectionResult = detectionResult;
	}

	/** {@inheritDoc} */
	@Override
	protected void doWriteReport() throws ConQATException {
		List<CloneClass> cloneClasses = detectionResult.getList();
		writeReport(cloneClasses, detectionResult.getRoot(),
				detectionResult.getSystemDate(), targetFile, getLogger());

		getLogger().info("Clone classes: " + cloneClasses.size());
		getLogger().info("Clones: " + CloneUtils.countClones(cloneClasses));
	}

	/**
	 * Writes a clone report file using a {@link CloneReportWriter}.
	 * 
	 * @param cloneClasses
	 *            Result clone classes that get written to the report
	 * @param root
	 *            Root of the resource tree on which detection has been
	 *            performed
	 * @param systemDate
	 *            Date denoting the system version on which clone detection was
	 *            performed.
	 * @param targetFile
	 *            File into which report gets written
	 * @param logger
	 *            Logger used to log errors occurring during report writing
	 * 
	 * @throws ConQATException
	 *             If report creation fails.
	 */
	public static void writeReport(List<CloneClass> cloneClasses,
			ITextResource root, Date systemDate, File targetFile,
			IConQATLogger logger) throws ConQATException {
		CloneReportWriter.writeReport(cloneClasses,
				createSourceElementDescriptors(root, logger), systemDate,
				targetFile);
	}

	/**
	 * Creates a Map from clone uniform paths to {@link SourceElementDescriptor}
	 * s.
	 */
	private static Map<String, SourceElementDescriptor> createSourceElementDescriptors(
			ITextResource root, IConQATLogger logger) {
		Map<String, SourceElementDescriptor> sourceElementInfos = new HashMap<String, SourceElementDescriptor>();

		int sourceElementIdCounter = 0;
		List<ITextElement> elements = ResourceTraversalUtils
				.listTextElements(root);

		// the order in which we create the SourceElementDescriptors in this
		// loop affects their ids. sort to make ids stable
		Collections.sort(elements, new NodeIdComparator());
		for (ITextElement element : elements) {
			int length = -1;
			String fingerprint = StringUtils.EMPTY_STRING;
			try {
				length = TextElementUtils.countUnfilteredLOC(element);
				fingerprint = Digester
						.createMD5Digest(element.getUnfilteredTextContent());
			} catch (ConQATException e) {
				logger.warn("Could not read element " + element.getLocation()
						+ " in order to compute length and fingerprint: "
						+ e.getMessage());
			}

			sourceElementInfos.put(element.getUniformPath(),
					new SourceElementDescriptor(sourceElementIdCounter++,
							element.getLocation(), element.getUniformPath(),
							length, fingerprint));
		}
		return sourceElementInfos;
	}

}