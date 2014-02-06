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
package org.conqat.engine.code_clones.detection.filter;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.code_clones.core.utils.CloneUtils;
import org.conqat.engine.code_clones.detection.CloneDetectionResultElement;
import org.conqat.engine.code_clones.result.CloneReportWriterProcessor;
import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.ITextResource;

/**
 * Base class for clone class filters.
 * <p>
 * It writes a clone report that contains the clone classes that have been
 * filtered out, if the output-directory parameter is set.
 * 
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: 193C3FD9BE897AC25990F804124BDA9C
 */
public abstract class CloneClassFilterBase extends
		ConQATPipelineProcessorBase<CloneDetectionResultElement> {

	/** File into which a report with the filtered clone classes gets written */
	private File targetFile = null;

	/** Creates the target file from directory and filename parameters */
	@AConQATParameter(name = "report", maxOccurrences = 1, description = ""
			+ "Clone report containing the clone classes that have been filtered out by this processor. If this optional parameter is not set, no report gets written ")
	public void setOutputDirectory(
			@AConQATAttribute(name = "dir", description = "Name of the output directory") String outputDir,
			@AConQATAttribute(name = "report-name", description = "Name of the report file") String reportName) {

		targetFile = new File(outputDir, reportName);
	}

	/** {@inheritDoc} */
	@Override
	protected void processInput(CloneDetectionResultElement input)
			throws ConQATException {
		setUp(input);

		List<CloneClass> unfilteredCloneClasses = input.getList();
		sort(unfilteredCloneClasses);

		input.setList(filterCloneClasses(unfilteredCloneClasses,
				input.getRoot(), input.getSystemDate()));
	}

	/** Hook method that allows deriving classes to sort list before filtering */
	protected void sort(
			@SuppressWarnings("unused") List<CloneClass> cloneClasses) {
		// Default implementation is not to sort
	}

	/**
	 * This method is called before any filtering is performed. It allows
	 * subclasses to perform initializations.
	 * <p>
	 * This is an empty implementation, so subclasses do not have to implement
	 * it themselves.
	 */
	@SuppressWarnings("unused")
	protected void setUp(CloneDetectionResultElement input)
			throws ConQATException {
		// nothing to do here
	}

	/** Performs filtering of the clone classes */
	private List<CloneClass> filterCloneClasses(
			List<CloneClass> unfilteredCloneClasses, ITextResource root,
			Date systemDate) throws ConQATException {
		List<CloneClass> filteredCloneClasses = new ArrayList<CloneClass>();
		List<CloneClass> removedCloneClasses = new ArrayList<CloneClass>();

		for (CloneClass cloneClass : unfilteredCloneClasses) {
			if (!filteredOut(cloneClass)) {
				filteredCloneClasses.add(cloneClass);
			} else {
				removedCloneClasses.add(cloneClass);
			}
		}

		writeLogMessage(filteredCloneClasses, unfilteredCloneClasses);
		writeRemovedCloneClassesReport(root, removedCloneClasses, systemDate);

		return filteredCloneClasses;
	}

	/**
	 * Template method that allows deriving classes to implement their filter
	 * criteria. It this returns true, the clone class will be remove by this
	 * filter.
	 */
	protected abstract boolean filteredOut(CloneClass cloneClass)
			throws ConQATException;

	/** Log how many clone classes have been removed */
	private void writeLogMessage(List<CloneClass> filteredCloneClasses,
			List<CloneClass> unfilteredCloneClasses) {

		int filteredClones = CloneUtils.countClones(filteredCloneClasses);
		int unfilteredClones = CloneUtils.countClones(unfilteredCloneClasses);
		int removedClones = unfilteredClones - filteredClones;

		int filteredClasses = filteredCloneClasses.size();
		int unfilteredClasses = unfilteredCloneClasses.size();
		int removedClasses = unfilteredClasses - filteredClasses;

		getLogger().info(
				"Removed " + removedClasses + " from " + unfilteredClasses
						+ " clone classes, " + filteredClasses
						+ " clone classes left." + " (Removed " + removedClones
						+ " from " + unfilteredClones + " clones, "
						+ filteredClones + " clones left.)");
	}

	/** Writes a report that contains the filtered clone classes. */
	private void writeRemovedCloneClassesReport(ITextResource root,
			List<CloneClass> removedCloneClasses, Date systemDate)
			throws ConQATException {

		// don't write anything, if targetfile hasn't been set
		if (targetFile == null) {
			return;
		}

		CloneReportWriterProcessor.writeReport(removedCloneClasses, root,
				systemDate, targetFile, getLogger());
	}

}