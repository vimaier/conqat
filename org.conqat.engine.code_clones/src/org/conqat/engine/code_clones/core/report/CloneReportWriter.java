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
package org.conqat.engine.code_clones.core.report;

import static org.conqat.engine.code_clones.core.report.ECloneReportAttribute.deltaInUnits;
import static org.conqat.engine.code_clones.core.report.ECloneReportAttribute.endLine;
import static org.conqat.engine.code_clones.core.report.ECloneReportAttribute.endOffset;
import static org.conqat.engine.code_clones.core.report.ECloneReportAttribute.fingerprint;
import static org.conqat.engine.code_clones.core.report.ECloneReportAttribute.id;
import static org.conqat.engine.code_clones.core.report.ECloneReportAttribute.length;
import static org.conqat.engine.code_clones.core.report.ECloneReportAttribute.lengthInUnits;
import static org.conqat.engine.code_clones.core.report.ECloneReportAttribute.location;
import static org.conqat.engine.code_clones.core.report.ECloneReportAttribute.normalizedLength;
import static org.conqat.engine.code_clones.core.report.ECloneReportAttribute.path;
import static org.conqat.engine.code_clones.core.report.ECloneReportAttribute.sourceFileId;
import static org.conqat.engine.code_clones.core.report.ECloneReportAttribute.startLine;
import static org.conqat.engine.code_clones.core.report.ECloneReportAttribute.startOffset;
import static org.conqat.engine.code_clones.core.report.ECloneReportAttribute.startUnitIndexInFile;
import static org.conqat.engine.code_clones.core.report.ECloneReportAttribute.systemdate;
import static org.conqat.engine.code_clones.core.report.ECloneReportAttribute.xmlns;
import static org.conqat.engine.code_clones.core.report.ECloneReportElement.cloneReport;
import static org.conqat.engine.code_clones.core.report.ECloneReportElement.sourceFile;
import static org.conqat.engine.code_clones.core.report.ECloneReportElement.value;
import static org.conqat.engine.code_clones.core.report.ECloneReportElement.values;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.code_clones.core.KeyValueStoreBase;
import org.conqat.engine.code_clones.core.utils.StableCloneClassComparator;
import org.conqat.engine.code_clones.core.utils.StableCloneComparator;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.xml.XMLResolver;
import org.conqat.lib.commons.xml.XMLWriter;

/**
 * Output class for the XML format clone reports.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43764 $
 * @ConQAT.Rating GREEN Hash: FFD81447C656E3D3B1891C361334BAE0
 */
public class CloneReportWriter {

	/** Pattern used to write dates to string */
	public static final String DATE_PATTERN = "yyyy.MM.dd HH:mm:ss:SSSZ";

	/** XMLWriter that performs the XML processing */
	private final XMLWriter<ECloneReportElement, ECloneReportAttribute> writer;

	/** Comparator used to sort clones in the report */
	private final StableCloneComparator comparator;

	/**
	 * Method object method: Creates a {@link CloneReportWriter} and writes a
	 * clone report file.
	 * 
	 * @param cloneClasses
	 *            Result clone classes that get written to the report
	 * 
	 * @param sourceFileDescriptorsMap
	 *            Map that maps from uniform path to the corresponding
	 *            {@link SourceElementDescriptor}
	 * @param targetFile
	 *            File into which report gets written
	 * 
	 * @throws ConQATException
	 *             If report creation fails.
	 */
	public static void writeReport(List<CloneClass> cloneClasses,
			Map<String, SourceElementDescriptor> sourceFileDescriptorsMap,
			Date systemDate, File targetFile, boolean lenient)
			throws ConQATException {
		writeReport(cloneClasses, sourceFileDescriptorsMap, null, systemDate,
				targetFile, lenient);
	}

	/**
	 * Method object method: Creates a {@link CloneReportWriter} and writes a
	 * clone report file in strict mode.
	 * 
	 * @see #writeReport(List, Map, Date, File, boolean)
	 */
	public static void writeReport(List<CloneClass> cloneClasses,
			Map<String, SourceElementDescriptor> sourceFileDescriptorsMap,
			Date systemDate, File targetFile) throws ConQATException {
		writeReport(cloneClasses, sourceFileDescriptorsMap, null, systemDate,
				targetFile, false);
	}

	/**
	 * Method object method: Creates a {@link CloneReportWriter} and writes a
	 * clone report file.
	 * 
	 * @param cloneClasses
	 *            Result clone classes that get written to the report
	 * 
	 * @param sourceFileDescriptorsMap
	 *            Map that maps from uniform path to the corresponding
	 *            {@link SourceElementDescriptor}
	 * @param rootValues
	 *            key/values stored at the report's root. May be null.
	 * @param targetFile
	 *            File into which report gets written
	 * 
	 * @throws ConQATException
	 *             If report creation fails.
	 */
	public static void writeReport(List<CloneClass> cloneClasses,
			Map<String, SourceElementDescriptor> sourceFileDescriptorsMap,
			RootValues rootValues, Date systemDate, File targetFile,
			boolean lenient) throws ConQATException {
		try {
			CloneReportWriter writer = new CloneReportWriter(targetFile,
					lenient);
			writer.writeReport(cloneClasses, sourceFileDescriptorsMap,
					rootValues, systemDate);
		} catch (IOException e) {
			throw new ConQATException("Could not write report: "
					+ e.getMessage());
		}
	}

	/**
	 * Method object method: Creates a {@link CloneReportWriter} and writes a
	 * clone report file in strict mode. Use this for clones detected by ConQAT.
	 * 
	 * @see #writeReport(List, Map, RootValues, Date, File, boolean)
	 */
	public static void writeReport(List<CloneClass> cloneClasses,
			Map<String, SourceElementDescriptor> sourceFileDescriptorsMap,
			RootValues rootValues, Date systemDate, File targetFile)
			throws ConQATException {
		writeReport(cloneClasses, sourceFileDescriptorsMap, rootValues,
				systemDate, targetFile, false);
	}

	/** Private constructor to enforce use of static method */
	private CloneReportWriter(File targetFile, boolean lenient)
			throws IOException {
		FileSystemUtils.ensureParentDirectoryExists(targetFile);
		writer = new XMLWriter<ECloneReportElement, ECloneReportAttribute>(
				new PrintStream(targetFile, FileSystemUtils.UTF8_ENCODING),
				new XMLResolver<ECloneReportElement, ECloneReportAttribute>(
						ECloneReportAttribute.class));
		if (lenient) {
			comparator = StableCloneComparator.LENIENT_INSTANCE;
		} else {
			comparator = StableCloneComparator.INSTANCE;
		}
	}

	/** Create XML clone reports. */
	private void writeReport(List<CloneClass> cloneClasses,
			Map<String, SourceElementDescriptor> sourceFileDescriptorsMap,
			RootValues rootValues, Date systemDate) {

		writer.addHeader("1.0", FileSystemUtils.UTF8_ENCODING);
		writer.openElement(cloneReport, xmlns,
				"http://conqat.cs.tum.edu/ns/clonereport");
		if (systemDate != null) {
			writer.addAttribute(systemdate,
					new SimpleDateFormat(DATE_PATTERN).format(systemDate));
		}

		if (rootValues != null) {
			writeValues(rootValues);
		}

		writeSourceFileDescriptors(sourceFileDescriptorsMap.values());

		// sort for stable clone reports
		Collections.sort(cloneClasses, StableCloneClassComparator.INSTANCE);
		for (CloneClass cloneClass : cloneClasses) {
			writeCloneClass(cloneClass, sourceFileDescriptorsMap);
		}

		writer.closeElement(cloneReport);
		writer.close();
	}

	/** Writes a clone class to xml */
	private void writeCloneClass(CloneClass cloneClass,
			Map<String, SourceElementDescriptor> sourceFileDescriptorsMap) {
		writer.openElement(ECloneReportElement.cloneClass, normalizedLength,
				cloneClass.getNormalizedLength(), id, cloneClass.getId(),
				fingerprint, cloneClass.getFingerprint());

		writeValues(cloneClass);

		for (Clone clone : CollectionUtils.sort(cloneClass.getClones(),
				comparator)) {
			writeClone(clone, sourceFileDescriptorsMap);
		}

		writer.closeElement(ECloneReportElement.cloneClass);
	}

	/** Writes stored values to xml */
	private void writeValues(KeyValueStoreBase store) {

		// flag used so we only output the values tag if we really need it (i.e.
		// there are non-transient entries).
		boolean hadValuesTag = false;

		for (String key : store.getKeyList()) {
			if (store.getTransient(key)) {
				continue;
			}

			Object valueObject = store.getValue(key);
			CCSMAssert.isNotNull(valueObject, "Value stored under key " + key
					+ " is null");

			if (!hadValuesTag) {
				writer.openElement(values);
				hadValuesTag = true;
			}

			writer.addClosedElement(value, ECloneReportAttribute.key, key,
					ECloneReportAttribute.value, valueObject,
					ECloneReportAttribute.type, valueObject.getClass()
							.getName());
		}

		if (hadValuesTag) {
			writer.closeElement(values);
		}
	}

	/** Writes a clone to xml */
	private void writeClone(Clone clone,
			Map<String, SourceElementDescriptor> sourceFileDescriptorsMap) {
		writer.openElement(ECloneReportElement.clone);

		writer.addAttribute(id, clone.getId());
		writer.addAttribute(fingerprint, clone.getFingerprint());

		writer.addAttribute(startLine, clone.getLocation().getRawStartLine());
		writer.addAttribute(endLine, clone.getLocation().getRawEndLine());
		writer.addAttribute(startOffset, clone.getLocation()
				.getRawStartOffset());
		writer.addAttribute(endOffset, clone.getLocation().getRawEndOffset());

		writer.addAttribute(sourceFileId,
				getSourceFileId(clone, sourceFileDescriptorsMap));
		writer.addAttribute(startUnitIndexInFile,
				clone.getStartUnitIndexInElement());
		writer.addAttribute(lengthInUnits, clone.getLengthInUnits());
		writer.addAttribute(deltaInUnits, clone.getDeltaInUnits());

		writer.addAttribute(ECloneReportAttribute.gaps,
				ReportUtils.createGapOffsetString(clone));

		writeValues(clone);

		writer.closeElement(ECloneReportElement.clone);
	}

	/** Retrieves the source file id for a clone. */
	private long getSourceFileId(Clone clone,
			Map<String, SourceElementDescriptor> sourceFileDescriptorsMap) {
		String uniformPath = clone.getUniformPath();
		SourceElementDescriptor descriptor = sourceFileDescriptorsMap
				.get(uniformPath);
		CCSMAssert.isNotNull(descriptor, "Inconsistent clone data: origin "
				+ uniformPath + " unknown.");
		return descriptor.getId();
	}

	/** Write source file descriptor to xml */
	private void writeSourceFileDescriptors(
			Collection<SourceElementDescriptor> sourceFileDescriptors) {
		for (SourceElementDescriptor sourceFileInfo : sortByUniformPath(sourceFileDescriptors)) {
			writer.openElement(sourceFile, id, sourceFileInfo.getId(), path,
					sourceFileInfo.getUniformPath(), location,
					sourceFileInfo.getLocation(), length,
					sourceFileInfo.getLength(), fingerprint,
					sourceFileInfo.getFingerprint());
			writeValues(sourceFileInfo);
			writer.closeElement(sourceFile);
		}
	}

	/** Sort {@link SourceElementDescriptor}s by name, to get stable report */
	private Collection<SourceElementDescriptor> sortByUniformPath(
			Collection<SourceElementDescriptor> sourceFileDescriptors) {
		return CollectionUtils.sort(sourceFileDescriptors,
				new Comparator<SourceElementDescriptor>() {

					/** {@inheritDoc} */
					@Override
					public int compare(SourceElementDescriptor e1,
							SourceElementDescriptor e2) {
						return e1.getUniformPath().compareTo(
								e2.getUniformPath());
					}
				});
	}
}