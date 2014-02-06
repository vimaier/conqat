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
package org.conqat.engine.commons.findings.xml;

import static org.conqat.engine.commons.findings.xml.EFindingAttributes.DESCRIPTION;
import static org.conqat.engine.commons.findings.xml.EFindingAttributes.END_LINE_NUMBER;
import static org.conqat.engine.commons.findings.xml.EFindingAttributes.END_POSITION;
import static org.conqat.engine.commons.findings.xml.EFindingAttributes.KEY;
import static org.conqat.engine.commons.findings.xml.EFindingAttributes.LOCATION_HINT;
import static org.conqat.engine.commons.findings.xml.EFindingAttributes.NAME;
import static org.conqat.engine.commons.findings.xml.EFindingAttributes.START_LINE_NUMBER;
import static org.conqat.engine.commons.findings.xml.EFindingAttributes.START_POSITION;
import static org.conqat.engine.commons.findings.xml.EFindingAttributes.TIME;
import static org.conqat.engine.commons.findings.xml.EFindingAttributes.UNIFORM_PATH;
import static org.conqat.engine.commons.findings.xml.EFindingAttributes.XMLNS;
import static org.conqat.engine.commons.findings.xml.EFindingElements.ELEMENT;
import static org.conqat.engine.commons.findings.xml.EFindingElements.FINDING;
import static org.conqat.engine.commons.findings.xml.EFindingElements.FINDING_CATEGORY;
import static org.conqat.engine.commons.findings.xml.EFindingElements.FINDING_GROUP;
import static org.conqat.engine.commons.findings.xml.EFindingElements.FINDING_REPORT;
import static org.conqat.engine.commons.findings.xml.EFindingElements.KEY_VALUE_PAIR;
import static org.conqat.engine.commons.findings.xml.EFindingElements.QUALIFIED_NAME;
import static org.conqat.engine.commons.findings.xml.EFindingElements.MODEL_PART;
import static org.conqat.engine.commons.findings.xml.EFindingElements.MODEL_ELEMENT_ID;
import static org.conqat.engine.commons.findings.xml.EFindingElements.TEXT_REGION;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.conqat.engine.commons.findings.EFindingKeys;
import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.findings.FindingCategory;
import org.conqat.engine.commons.findings.FindingGroup;
import org.conqat.engine.commons.findings.FindingReport;
import org.conqat.engine.commons.findings.location.ElementLocation;
import org.conqat.engine.commons.findings.location.ModelPartLocation;
import org.conqat.engine.commons.findings.location.QualifiedNameLocation;
import org.conqat.engine.commons.findings.location.TextRegionLocation;
import org.conqat.engine.commons.findings.util.SmartFindingComparator;
import org.conqat.engine.commons.node.ConQATNodeBase;
import org.conqat.engine.commons.node.NodeConstants;
import org.conqat.engine.commons.sorting.NodeIdComparator;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.commons.xml.XMLWriter;

/**
 * Code for writing finding reports. This is package visible and only used by
 * the {@link FindingReportIO} class.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 45776 $
 * @ConQAT.Rating GREEN Hash: 90BA131F79F5F947D719CC4B22DA71C3
 */
/* package */class FindingReportWriter {

	/** The namespace used for the findings XML schema. */
	private static final String XML_NAMESPACE = "http://www.conqat.org/ns/findings";

	/**
	 * Set of keys which are omitted during the export as they have no meaning
	 * for findings but may be added by other processors.
	 */
	private static final Set<String> FILTERED_KEYS = new HashSet<String>(
			Arrays.asList(NodeConstants.COMPARATOR, NodeConstants.HIDE_ROOT,
					NodeConstants.DISPLAY_LIST, NodeConstants.SUMMARY,
					EFindingKeys.METRICS.name()));

	/** The XML writer. */
	private final XMLWriter<EFindingElements, EFindingAttributes> writer;

	/** Constructor. */
	/* package */FindingReportWriter(OutputStream out) {
		this.writer = new XMLWriter<EFindingElements, EFindingAttributes>(
				new PrintWriter(new OutputStreamWriter(out,
						Charset.forName(FileSystemUtils.UTF8_ENCODING))),
				FindingReportIO.XML_RESOLVER);
	}

	/** Writes the report into the writer. */
	/* package */void write(FindingReport report) {
		try {
			writer.addHeader("1.0", FileSystemUtils.UTF8_ENCODING);
			String time = FindingReportIO.DATE_FORMAT.format(report.getTime());
			writer.openElement(FINDING_REPORT, TIME, time, XMLNS, XML_NAMESPACE);

			FindingCategory[] categories = report.getChildren();
			Arrays.sort(categories, NodeIdComparator.INSTANCE);
			for (FindingCategory category : categories) {
				if (category.hasChildren()) {
					writeCategory(category);
				}
			}

			writer.closeElement(FINDING_REPORT);
		} finally {
			writer.close();
		}
	}

	/** Writes the category into the writer. */
	private void writeCategory(FindingCategory category) {
		writer.openElement(FINDING_CATEGORY, NAME, category.getName());

		FindingGroup[] groups = category.getChildren();
		Arrays.sort(groups, NodeIdComparator.INSTANCE);
		for (FindingGroup group : groups) {
			if (group.hasChildren()) {
				writeGroup(group);
			}
		}

		writer.closeElement(FINDING_CATEGORY);
	}

	/** Writes the group into the writer. */
	private void writeGroup(FindingGroup group) {
		writer.openElement(FINDING_GROUP, DESCRIPTION, group.getName());

		writeKeyValues(group);

		Finding[] findings = group.getChildren();
		Arrays.sort(findings, new SmartFindingComparator());
		for (Finding finding : findings) {
			writeFinding(finding);
		}

		writer.closeElement(FINDING_GROUP);
	}

	/** Writes the finding into the writer. */
	private void writeFinding(Finding finding) {
		writer.openElement(FINDING);
		writeKeyValues(finding);
		writeLocation(finding.getLocation());
		writer.closeElement(FINDING);
	}

	/** Writes key/value pairs. */
	private void writeKeyValues(ConQATNodeBase node) {
		for (String key : CollectionUtils.sort(node.getKeys())) {
			if (FILTERED_KEYS.contains(key)) {
				continue;
			}

			Object value = node.getValue(key);
			if (value != null) {
				writer.addClosedTextElement(KEY_VALUE_PAIR, value.toString(),
						KEY, key);
			}
		}
	}

	/** Writes the location to the writer. */
	private void writeLocation(ElementLocation location) {
		if (location instanceof QualifiedNameLocation) {
			writer.openElement(QUALIFIED_NAME, NAME,
					((QualifiedNameLocation) location).getQualifiedName());
			closeLocationElement(location, QUALIFIED_NAME);
		} else if (location instanceof ModelPartLocation) {
			writer.openElement(MODEL_PART, UNIFORM_PATH,
					location.getUniformPath());
			if (!StringUtils.isEmpty(location.getLocation())) {
				writer.addAttribute(LOCATION_HINT, location.getLocation());
			}
			for (String elementId : CollectionUtils
					.sort(((ModelPartLocation) location).getElementIds())) {
				writer.addClosedTextElement(MODEL_ELEMENT_ID, elementId);
			}
			writer.closeElement(MODEL_PART);
		} else if (location instanceof TextRegionLocation) {
			TextRegionLocation textRegionLocation = (TextRegionLocation) location;
			writer.openElement(TEXT_REGION, START_LINE_NUMBER,
					textRegionLocation.getRawStartLine(), END_LINE_NUMBER,
					textRegionLocation.getRawEndLine(), START_POSITION,
					textRegionLocation.getRawStartOffset(), END_POSITION,
					textRegionLocation.getRawEndOffset());
			closeLocationElement(location, TEXT_REGION);
		} else {
			writer.openElement(ELEMENT);
			closeLocationElement(location, ELEMENT);
		}
	}

	/** Closes a location element and adds uniform path and location. */
	private void closeLocationElement(ElementLocation location,
			EFindingElements element) {
		writer.addAttribute(UNIFORM_PATH, location.getUniformPath());
		if (!StringUtils.isEmpty(location.getLocation())) {
			writer.addAttribute(LOCATION_HINT, location.getLocation());
		}
		writer.closeElement(element);
	}
}