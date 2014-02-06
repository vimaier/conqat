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

import java.text.ParseException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import org.conqat.engine.commons.findings.EFindingKeys;
import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.findings.FindingCategory;
import org.conqat.engine.commons.findings.FindingGroup;
import org.conqat.engine.commons.findings.FindingReport;
import org.conqat.engine.commons.findings.location.ElementLocation;
import org.conqat.engine.commons.findings.location.ModelPartLocation;
import org.conqat.engine.commons.findings.location.QualifiedNameLocation;
import org.conqat.engine.commons.findings.location.TextRegionLocation;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.enums.EnumUtils;
import org.conqat.lib.commons.reflect.ReflectionUtils;
import org.conqat.lib.commons.reflect.TypeConversionException;
import org.conqat.lib.commons.string.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Code for reading finding reports. This is package visible and only used by
 * the {@link FindingReportIO} class.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating GREEN Hash: 742AF9A1CA0D4A7A003A3B40AC6AB32D
 */
/* package */class FindingReportReaderHandler extends DefaultHandler {

	/** Mapping from element names to element enum values */
	private static final Map<String, EFindingElements> ELEMENTS_BY_NAME = new HashMap<String, EFindingElements>();

	/** Mapping from attribute names to attribute enum values */
	private static final Map<String, EFindingAttributes> ATTRIBUTES_BY_NAME = new HashMap<String, EFindingAttributes>();

	static {
		for (EFindingElements element : EFindingElements.values()) {
			ELEMENTS_BY_NAME.put(
					FindingReportIO.XML_RESOLVER.resolveElementName(element),
					element);
		}
		for (EFindingAttributes attribute : EFindingAttributes.values()) {
			ATTRIBUTES_BY_NAME.put(FindingReportIO.XML_RESOLVER
					.resolveAttributeName(attribute), attribute);
		}
	}

	/** The report. */
	private FindingReport report;

	/** The current category. */
	private FindingCategory currentCategory;

	/** The current group. */
	private FindingGroup currentGroup;

	/** The last location found. */
	private ElementLocation lastLocation;

	/** The stack of key/value pairs found in the XML (for group or finding). */
	private Stack<Map<String, Object>> keyValueStack = new Stack<Map<String, Object>>();

	/** Current key for key/value data. */
	private String currentKey;

	/** Buffer for text content in a key/value pair. */
	private StringBuilder textContent = new StringBuilder();

	/** Returns the report. */
	/* package */FindingReport getReport() {
		return report;
	}

	/** {@inheritDoc} */
	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		EFindingElements element = determineElement(localName);
		Map<EFindingAttributes, String> attributeMap = determineAttributes(attributes);
		try {

			switch (element) {
			case FINDING_REPORT:
				try {
					report = new FindingReport(
							FindingReportIO.DATE_FORMAT.parse(attributeMap
									.get(EFindingAttributes.TIME)));
				} catch (ParseException e) {
					throw new SAXException("Invalid date format: "
							+ attributeMap.get(EFindingAttributes.TIME), e);
				}
				break;

			case FINDING_CATEGORY:
				currentCategory = report.getOrCreateCategory(attributeMap
						.get(EFindingAttributes.NAME));

				break;

			case FINDING_GROUP:
				currentGroup = currentCategory.createFindingGroup(attributeMap
						.get(EFindingAttributes.DESCRIPTION));
				keyValueStack.push(new HashMap<String, Object>());
				break;

			case FINDING:
				keyValueStack.push(new HashMap<String, Object>());
				break;

			case KEY_VALUE_PAIR:
				currentKey = attributeMap.get(EFindingAttributes.KEY);
				textContent.setLength(0);
				break;

			case ELEMENT:
				lastLocation = new ElementLocation(
						getLocationHint(attributeMap),
						getUniformPath(attributeMap));
				break;

			case TEXT_REGION:
				lastLocation = extractTextRegionLocation(
						getUniformPath(attributeMap),
						getLocationHint(attributeMap), attributeMap);
				break;

			case QUALIFIED_NAME:
				lastLocation = new QualifiedNameLocation(
						attributeMap.get(EFindingAttributes.NAME),
						getLocationHint(attributeMap),
						getUniformPath(attributeMap));
				break;

			case MODEL_PART:
				lastLocation = new ModelPartLocation(
						getUniformPath(attributeMap),
						getLocationHint(attributeMap));
				break;

			case MODEL_ELEMENT_ID:
				textContent.setLength(0);
				break;

			default:
				CCSMAssert.fail("Unknown element!");
			}
		} catch (NumberFormatException e) {
			throw new SAXException("Invalid number!", e);
		}
	}

	/** Extracts the location hint from the attributes. */
	private String getLocationHint(Map<EFindingAttributes, String> attributeMap) {
		String locationHint = attributeMap
				.get(EFindingAttributes.LOCATION_HINT);
		if (locationHint == null) {
			return StringUtils.EMPTY_STRING;
		}
		return locationHint;
	}

	/** Extracts the uniform path from the attributes. */
	private String getUniformPath(Map<EFindingAttributes, String> attributeMap) {
		return attributeMap.get(EFindingAttributes.UNIFORM_PATH);
	}

	/** Extracts a {@link TextRegionLocation} from the XML. */
	private TextRegionLocation extractTextRegionLocation(String uniformPath,
			String locationHint, Map<EFindingAttributes, String> attributeMap) {
		return new TextRegionLocation(locationHint, uniformPath,
				Integer.parseInt(attributeMap
						.get(EFindingAttributes.START_LINE_NUMBER)),
				Integer.parseInt(attributeMap
						.get(EFindingAttributes.END_LINE_NUMBER)),
				Integer.parseInt(attributeMap
						.get(EFindingAttributes.START_POSITION)),
				Integer.parseInt(attributeMap
						.get(EFindingAttributes.END_POSITION)));
	}

	/** {@inheritDoc} */
	@Override
	public void characters(char[] ch, int start, int length) {
		textContent.append(ch, start, length);
	}

	/** {@inheritDoc} */
	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		EFindingElements element = determineElement(localName);

		switch (element) {
		case KEY_VALUE_PAIR:
			parseAndStoreKeyValuePair();
			break;

		case MODEL_ELEMENT_ID:
			((ModelPartLocation) lastLocation).addElementId(textContent
					.toString());
			break;

		case FINDING_GROUP:
			copyCurrentKeyValuesInto(currentGroup);
			break;

		case FINDING:
			Finding finding = currentGroup.createFinding(lastLocation);
			copyCurrentKeyValuesInto(finding);
			break;
		}
	}

	/**
	 * Copies the key/value pairs from the top of {@link #keyValueStack} into
	 * the given node and pops the stack.
	 */
	private void copyCurrentKeyValuesInto(IConQATNode node) {
		Map<String, Object> map = keyValueStack.pop();
		for (Entry<String, Object> entry : map.entrySet()) {
			node.setValue(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Parses a key/value pair an stores it into the current top of
	 * {@link #keyValueStack}.
	 */
	private void parseAndStoreKeyValuePair() throws SAXException {
		Object value = textContent.toString();
		EFindingKeys key = EnumUtils.valueOfIgnoreCase(EFindingKeys.class,
				currentKey);
		if (key != null) {
			try {
				value = ReflectionUtils.convertString(value.toString(),
						key.getType());
			} catch (TypeConversionException e) {
				throw new SAXException("Unexpected value " + value
						+ " for key " + key);
			}
		}
		keyValueStack.peek().put(currentKey, value);
	}

	/** Returns the element for the local name. */
	private static EFindingElements determineElement(String localName)
			throws SAXException {
		EFindingElements element = ELEMENTS_BY_NAME.get(localName);
		if (element == null) {
			throw new SAXException("Could not find element for " + localName);
		}
		return element;
	}

	/** Returns the attributes as a map from enum value to string. */
	private Map<EFindingAttributes, String> determineAttributes(
			Attributes attributes) throws SAXException {
		Map<EFindingAttributes, String> result = new EnumMap<EFindingAttributes, String>(
				EFindingAttributes.class);
		for (int i = 0; i < attributes.getLength(); ++i) {
			EFindingAttributes attribute = ATTRIBUTES_BY_NAME.get(attributes
					.getLocalName(i));
			if (attribute == null) {
				throw new SAXException("Could not find attribute for "
						+ attributes.getLocalName(i));
			}
			result.put(attribute, attributes.getValue(i));
		}
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public void error(SAXParseException e) throws SAXException {
		throw e;
	}

	/** {@inheritDoc} */
	@Override
	public void fatalError(SAXParseException e) throws SAXException {
		throw e;
	}

	/** {@inheritDoc} */
	@Override
	public void warning(SAXParseException e) throws SAXException {
		throw e;
	}
}