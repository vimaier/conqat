/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
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
package org.conqat.engine.bugzilla.lib;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.conqat.lib.commons.collections.IdentityHashSet;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.commons.xml.XMLUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Parses the RDF format returned by Bugzilla.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 46826 $
 * @ConQAT.Rating GREEN Hash: 0EAC77177F1F7FC2B5920C10E50AB735
 */
public class BugzillaRdfParser {

	/** Prefix of Bugzilla namespace. */
	private static final String BUGZILLA_PREFIX = "bz:";

	/** The name of the bug main element name. */
	private static final String BUG_ELEMENT_NAME = BUGZILLA_PREFIX + "bug";

	/** Parse RDF as returned by the Bugzilla server. */
	public static Set<Bug> parseRDF(InputStream stream)
			throws BugzillaException, IOException {
		InputSource source = new InputSource(stream);
		BugzillaRdfHandler handler = new BugzillaRdfHandler();

		try {
			XMLUtils.parseSAX(source, handler);
		} catch (SAXException e) {
			throw new BugzillaException("Parsing exception", e);
		} finally {
			stream.close();
		}

		return handler.bugs;
	}

	/** The handler used for SAX parsing the RDF. */
	private static class BugzillaRdfHandler extends DefaultHandler {

		/** The resulting set of bugs. */
		private IdentityHashSet<Bug> bugs = new IdentityHashSet<Bug>();

		/**
		 * Mapping from field names to values. This is only non-null while the
		 * parser is within a bug element.
		 */
		private Map<String, String> fields;

		/** Used to collect text content. */
		private final StringBuilder textContent = new StringBuilder();

		/** {@inheritDoc} */
		@Override
		public void error(SAXParseException e) throws SAXException {
			throw e;
		}

		/** {@inheritDoc} */
		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) {

			if (BUG_ELEMENT_NAME.equals(qName)) {
				fields = new HashMap<String, String>();
			} else if (fields != null) {
				// we are within a bug, so each element is a field
				// here we reset the text; we store fields at the closing tag
				textContent.setLength(0);
			}
		}

		/** {@inheritDoc} */
		@Override
		public void characters(char[] ch, int start, int length) {
			// just store in textContent
			textContent.append(ch, start, length);
		}

		/** {@inheritDoc} */
		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			if (BUG_ELEMENT_NAME.equals(qName)) {
				storeBug();
			} else if (fields != null) {
				// we are within a bug, so each element is a field
				// here we actually store the field
				fields.put(StringUtils.stripPrefix(BUGZILLA_PREFIX, qName),
						textContent.toString());
			}
		}

		/**
		 * Creates a bug from the entries of {@link #fields} and stores it in
		 * {@link #bugs}.
		 */
		private void storeBug() throws SAXException {
			String idString = fields.get(EBugzillaField.ID.xmlTag);
			if (idString == null || !idString.matches("\\d+")) {
				throw new SAXException("Could not determine bug id.");
			}

			Bug bug = new Bug(Integer.parseInt(idString));

			for (Entry<String, String> entry : fields.entrySet()) {
				String tagName = entry.getKey();
				String textContent = entry.getValue();

				EBugzillaField field = EBugzillaField.getField(tagName);

				// check if we know about the field
				if (field != null) {
					bug.setField(field, textContent);
				} else {
					bug.setCustomField(tagName, textContent);
				}
			}

			bugs.add(bug);
		}
	}

}
