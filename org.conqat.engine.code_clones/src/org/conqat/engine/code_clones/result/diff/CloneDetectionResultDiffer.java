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
package org.conqat.engine.code_clones.result.diff;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.code_clones.result.CloneReportWriterProcessor;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.IConQATLogger;
import org.conqat.engine.resource.library.FileLibrary;

/**
 * Computes the differences between two clone detection results. One detection
 * result is termed <i>before</i> and one <i>after</i>. The after detection
 * result is compared against the before result.
 * <p>
 * As result, three clone reports are generated: One for added, one for
 * unchanged and one for removed clone classes. Equality of clone classes is
 * determines on the basis of the {@link CloneClass#equals(Object)} method,
 * which uses clone class fingerprints to determine equality.
 * <p>
 * The clone reports containing unchanged and removed clones are written w.r.t
 * to files in the "before" clone report. Added clones are written w.r.t. to
 * files in the "after" report.
 * <p>
 * This processor is a sink and thus returns nothing
 * 
 * @author juergens
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: CDFF8D383C98DED97B0094F9B0A66369
 */
@AConQATProcessor(description = ""
		+ "Computes the differences between two clone detection results. One detection"
		+ "result is termed 'before' and one 'after'. The after detection"
		+ "result is compared against the before report."
		+ "The clone reports containing unchanged and removed clones are written w.r.t"
		+ "to files in the 'before' clone report. Added clones are written w.r.t to"
		+ "files in the 'after' report."
		+ "As a result, three clone reports are generated: One for added, one for"
		+ "unchanged and one for removed clone classes. Equality of clone classes is"
		+ "determines on the basis of the {@link CloneClass#equals(Object)} method,"
		+ "which uses clone class fingerprints to determine equality."
		+ "This processor is a sink and thus returns nothing (null).")
public class CloneDetectionResultDiffer extends CloneDetectionResultDifferBase {

	/** Name of the clone report file into which removed clones are written */
	private static final String REMOVED_CLONES_REPORT_NAME = "removed_clones.xml";

	/** Name of the clone report file into which added clones are written */
	private static final String ADDED_CLONES_REPORT_NAME = "added_clones.xml";

	/** Name of the clone report file into which unchanged clones are written */
	private static final String UNCHANGED_CLONES_REPORT_NAME = "unchanged_clones.xml";

	/** Directory into which reports containing difference results are written */
	private String outputDir;

	/** ConQAT Parameter */
	@AConQATParameter(name = "output", minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "Directory into which reports containing diff results are written")
	public void setOutputDirectory(
			@AConQATAttribute(name = "dir", description = "Gets created if necessary.") String outputDir) {
		this.outputDir = outputDir;
	}

	/** {@inheritDoc} */
	@Override
	public Object process() throws ConQATException {
		// create lists for diff results
		List<CloneClass> unchanged = new ArrayList<CloneClass>();
		List<CloneClass> added = new ArrayList<CloneClass>();
		List<CloneClass> removed = new ArrayList<CloneClass>();

		// fill lists
		computeDifferences(unchanged, added, removed);

		// write result reports
		writeResults(unchanged, added, removed, after.getSystemDate());

		// This processor is a sink => returns nothing
		return null;
	}

	/**
	 * Determines clone classes that have been added or removed or have remained
	 * unchanged between before and after detection results
	 */
	private void computeDifferences(List<CloneClass> unchanged,
			List<CloneClass> added, List<CloneClass> removed) {

		Set<CloneClass> cloneClassesAfter = new HashSet<CloneClass>(
				after.getList());

		for (CloneClass cloneClass : before.getList()) {
			if (cloneClassesAfter.contains(cloneClass)) {
				unchanged.add(cloneClass);
				cloneClassesAfter.remove(cloneClass);
			} else {
				removed.add(cloneClass);
			}
		}

		added.addAll(cloneClassesAfter);
	}

	/** Creates reports containing diff results */
	private void writeResults(List<CloneClass> unchanged,
			List<CloneClass> added, List<CloneClass> removed, Date systemDate)
			throws ConQATException {
		FileLibrary.ensureDirectoryExists(outputDir);

		IConQATLogger logger = getLogger();
		CloneReportWriterProcessor.writeReport(unchanged, before, systemDate,
				new File(outputDir, UNCHANGED_CLONES_REPORT_NAME), logger);
		CloneReportWriterProcessor.writeReport(added, after, systemDate,
				new File(outputDir, ADDED_CLONES_REPORT_NAME), logger);
		CloneReportWriterProcessor.writeReport(removed, before, systemDate,
				new File(outputDir, REMOVED_CLONES_REPORT_NAME), logger);
	}
}