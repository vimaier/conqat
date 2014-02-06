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

import static org.conqat.engine.code_clones.core.report.ECloneReportAttribute.systemdate;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.conqat.engine.code_clones.core.Clone;
import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.code_clones.core.KeyValueStoreBase;
import org.conqat.engine.commons.findings.location.TextRegionLocation;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.enums.EnumUtils;
import org.conqat.lib.commons.reflect.TypeConversionException;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.commons.xml.XMLUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Reads clone reports. This reader is used both by the engine and the IDE.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43764 $
 * @ConQAT.Rating GREEN Hash: 5EF6950FE3856F711765FBEE5F43B3CC
 */
public class CloneReportReader {

	/** Name of schema file. */
	private static final String CLONEREPORT_SCHEMA_NAME = "clonereport.xsd";

	/** Descriptors for all source elements on which detection was performed. */
	private final List<SourceElementDescriptor> sourceElementDescriptors = new ArrayList<SourceElementDescriptor>();

	/** List of the clone classes in the report */
	private final List<CloneClass> cloneClasses = new ArrayList<CloneClass>();

	/**
	 * Maps source file ids to {@link SourceElementDescriptor}s in order to
	 * resolve source elements of clones.
	 */
	private final Map<Long, SourceElementDescriptor> sourceElementDescriptorsById = new HashMap<Long, SourceElementDescriptor>();

	/** Date denoting the system version on which clone detection was performed. */
	private Date systemDate;

	/** The root values storing key/value for the entire report. */
	private final RootValues rootValues = new RootValues();

	/**
	 * Reads the clone report.
	 * 
	 * @throws ConQATException
	 *             if report could not be parsed
	 */
	public CloneReportReader(File report) throws ConQATException {
		try {
			URL schema = CloneReportReader.class
					.getResource(CLONEREPORT_SCHEMA_NAME);
			CCSMAssert.isFalse(schema == null, "Schema not found.");

			XMLUtils.parseSAX(report, schema, new CloneReportHandler());
		} catch (SAXException e) {
			throw new ConQATException("Could not parse file '" + report + ": "
					+ e.getMessage(), e);
		} catch (IOException e) {
			throw new ConQATException("Could not read file: " + e.getMessage(),
					e);
		}
	}

	/** Get list of {@link SourceElementDescriptor}s contained in report */
	public List<SourceElementDescriptor> getSourceElementDescriptors() {
		return sourceElementDescriptors;
	}

	/** Get list of clone classes contained in report */
	public List<CloneClass> getCloneClasses() {
		return cloneClasses;
	}

	/**
	 * Get the date denoting the system version on which clone detection was
	 * performed
	 */
	public Date getSystemDate() {
		return systemDate;
	}

	/**
	 * Returns an object that represents the key/value pairs stored at the
	 * report's root.
	 */
	public RootValues getRootValues() {
		return rootValues;
	}

	/** Adds a {@link SourceElementDescriptor}. */
	private void addSourceElementDescriptor(SourceElementDescriptor descriptor) {
		sourceElementDescriptorsById.put(descriptor.getId(), descriptor);
		sourceElementDescriptors.add(descriptor);
	}

	/** Parses a {@link SourceElementDescriptor} from given attributes. */
	private static SourceElementDescriptor parseSourceFileDescriptor(
			Attributes attributes) {
		int id = intAttribute(ECloneReportAttribute.id, attributes);
		String uniformPath = stringAttribute(ECloneReportAttribute.path,
				attributes);

		// read (optional) location
		String location = stringAttribute(ECloneReportAttribute.location,
				attributes);
		if (StringUtils.isEmpty(location)) {
			location = uniformPath;
		}

		int length = intAttribute(ECloneReportAttribute.length, attributes);
		String fingerprint = stringAttribute(ECloneReportAttribute.fingerprint,
				attributes);

		return new SourceElementDescriptor(id, location, uniformPath, length,
				fingerprint);
	}

	/** Parses a {@link CloneClass} from given attributes. */
	private static CloneClass parseCloneClass(Attributes attributes) {
		int normalizedLength = intAttribute(
				ECloneReportAttribute.normalizedLength, attributes);
		int id = intAttribute(ECloneReportAttribute.id, attributes);
		return new CloneClass(normalizedLength, id);
	}

	/**
	 * Parses a {@link Clone} from given attributes.
	 * 
	 * @param cloneClass
	 *            clone class this clone belongs to
	 */
	@SuppressWarnings("null")
	private Clone parseClone(Attributes attributes, CloneClass cloneClass) {
		int id = intAttribute(ECloneReportAttribute.id, attributes);
		int rawStartLine = intAttribute(ECloneReportAttribute.startLine,
				attributes);
		int rawEndLine = intAttribute(ECloneReportAttribute.endLine, attributes);
		int rawStartOffset = intAttribute(ECloneReportAttribute.startOffset,
				attributes);
		int rawEndOffset = intAttribute(ECloneReportAttribute.endOffset,
				attributes);

		long sourceFileId = intAttribute(ECloneReportAttribute.sourceFileId,
				attributes);
		String gaps = stringAttribute(ECloneReportAttribute.gaps, attributes);
		String fingerprint = stringAttribute(ECloneReportAttribute.fingerprint,
				attributes);
		int startUnitIndexInFile = intAttribute(
				ECloneReportAttribute.startUnitIndexInFile, attributes);
		int lengthInUnits = intAttribute(ECloneReportAttribute.lengthInUnits,
				attributes);
		int deltaInUnits = intAttribute(ECloneReportAttribute.deltaInUnits,
				attributes);

		SourceElementDescriptor sourceElementDescriptor = sourceElementDescriptorsById
				.get(sourceFileId);
		CCSMAssert.isTrue(sourceElementDescriptor != null,
				"Inconsistent clone report: source element id unknown");

		TextRegionLocation location = new TextRegionLocation(
				sourceElementDescriptor.getLocation(),
				sourceElementDescriptor.getUniformPath(), rawStartOffset,
				rawEndOffset, rawStartLine, rawEndLine);
		Clone clone = new Clone(id, cloneClass, location, startUnitIndexInFile,
				lengthInUnits, fingerprint, deltaInUnits);
		ReportUtils.parseGapOffsetString(clone, gaps);

		return clone;
	}

	/**
	 * Parses the attributes of a single value element and stores the result in
	 * the given store.
	 */
	private static void parseValue(Attributes attributes,
			KeyValueStoreBase store) throws SAXException {
		try {
			String key = stringAttribute(ECloneReportAttribute.key, attributes);
			String typeString = stringAttribute(ECloneReportAttribute.type,
					attributes);
			String valueString = stringAttribute(ECloneReportAttribute.value,
					attributes);

			store.setValue(key, valueString, typeString);
		} catch (TypeConversionException e) {
			throw new SAXException("Could not parse clone class value", e);
		} catch (ClassNotFoundException e) {
			throw new SAXException("Could not parse clone class value", e);
		}
	}

	/** Read string attribute from {@link Attributes}. */
	private static String stringAttribute(ECloneReportAttribute attribute,
			Attributes attributes) {
		return attributes.getValue(attribute.name());
	}

	/** Read int attribute from {@link Attributes}. */
	private static int intAttribute(ECloneReportAttribute attribute,
			Attributes attributes) {
		return Integer.valueOf(stringAttribute(attribute, attributes));
	}

	/**
	 * Read date attribute from {@link Attributes}. Returns null, if the
	 * attribute could not be found.
	 */
	private static Date dateAttribute(ECloneReportAttribute attribute,
			Attributes attributes) throws SAXException {
		try {
			String dateString = stringAttribute(attribute, attributes);
			if (StringUtils.isEmpty(dateString)) {
				return null;
			}
			return new SimpleDateFormat(CloneReportWriter.DATE_PATTERN)
					.parse(dateString);
		} catch (ParseException e) {
			throw new SAXException("Problems reading date attribute.", e);
		}
	}

	/** SAX handler used for parsing the clone report. */
	private class CloneReportHandler extends DefaultHandler {

		/** Stack of domain objects representing the current element. */
		private final Stack<Object> currentElement = new Stack<Object>();

		/** Ensure we stop parsing in case of errors. */
		@Override
		public void error(SAXParseException e) throws SAXException {
			throw e;
		}

		/** {@inheritDoc} */
		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			switch (determineElement(localName)) {
			case cloneReport:
				systemDate = dateAttribute(systemdate, attributes);
				setCurrentElement(rootValues);
				break;

			case sourceFile:
				addSourceElementDescriptor(setCurrentElement(parseSourceFileDescriptor(attributes)));
				break;

			case cloneClass:
				cloneClasses
						.add(setCurrentElement(parseCloneClass(attributes)));
				break;

			case clone:
				setCurrentElement(parseClone(attributes,
						getCurrentElement(CloneClass.class)));
				break;

			case value:
				parseValue(attributes,
						getCurrentElement(KeyValueStoreBase.class));
				break;

			case values:
				// no need to handle explicitly
				break;

			default:
				CCSMAssert.fail("Unknown element: "
						+ determineElement(localName));
			}
		}

		/** {@inheritDoc} */
		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			switch (determineElement(localName)) {
			case cloneReport:
			case sourceFile:
			case cloneClass:
			case clone:
				currentElement.pop();

				// all other elements do not create domain elements
			}
		}

		/** Determines the current element from the local name. */
		private ECloneReportElement determineElement(String localName)
				throws SAXException {
			ECloneReportElement element = EnumUtils.valueOf(
					ECloneReportElement.class, localName);
			if (element == null) {
				throw new SAXException("Unknown element: " + localName);
			}
			return element;
		}

		/**
		 * Sets the given element as current element and returns the element for
		 * convenience.
		 */
		private <T> T setCurrentElement(T domainElement) {
			currentElement.add(domainElement);
			return domainElement;
		}

		/**
		 * Checks whether the top-most current element is of the expected type
		 * and returns it.
		 */
		@SuppressWarnings("unchecked")
		private <T> T getCurrentElement(Class<T> clazz) throws SAXException {
			if (currentElement.isEmpty()) {
				throw new SAXException("Inconsistent XML: no current element!");
			}

			Object current = currentElement.peek();
			if (!clazz.isInstance(current)) {
				throw new SAXException(
						"Inconsistent XML: current element is not of type "
								+ clazz + "!");
			}

			return (T) current;
		}
	}
}