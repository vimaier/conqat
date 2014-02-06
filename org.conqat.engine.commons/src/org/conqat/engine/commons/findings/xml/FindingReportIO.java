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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Locale;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.xml.IXMLResolver;
import org.conqat.lib.commons.xml.LowercaseResolver;
import org.conqat.lib.commons.xml.XMLUtils;
import org.conqat.engine.commons.findings.FindingReport;

/**
 * Class used for performing input and output of {@link FindingReport}s.
 * 
 * @author hummelb
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: F8503664F231BAC487CDD1BAACC325C0
 */
public class FindingReportIO {

	/** The date format used. */
	/* package */static final DateFormat DATE_FORMAT = DateFormat
			.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.US);

	/** Resolver used for element and attribute names. */
	/* package */static final IXMLResolver<EFindingElements, EFindingAttributes> XML_RESOLVER = new LowercaseResolver<EFindingElements, EFindingAttributes>(
			EFindingAttributes.class);

	/** The name of the schema used for validation. */
	private static final String SCHEMA_NAME = "findings.xsd";

	/** Writes a report to a file. */
	public static void writeReport(FindingReport report, File file)
			throws IOException {
		writeReport(report, new FileOutputStream(file));
	}

	/** Writes a report to a stream. */
	public static void writeReport(FindingReport report, OutputStream out) {
		new FindingReportWriter(out).write(report);
	}

	/** Reads a report from a file. */
	public static FindingReport readReport(File file) throws IOException {
		return readReport(new FileInputStream(file));
	}

	/** Writes a report to a stream. */
	public static FindingReport readReport(InputStream in) throws IOException {
		try {
			FindingReportReaderHandler handler = new FindingReportReaderHandler();
			XMLUtils.parseSAX(new InputSource(FileSystemUtils
					.autoDecompressStream(in)), FindingReportIO.class
					.getResource(SCHEMA_NAME), handler);
			return handler.getReport();
		} catch (SAXException e) {
			throw new IOException("Parsing error: " + e.getMessage());
		}
	}
}