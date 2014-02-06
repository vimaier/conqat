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
package org.conqat.engine.java.findbugs;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableSet;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.commons.xml.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Manager for findbugs messages. This class reads the findbugs messages.xml
 * located in the bundle resources and provides text messages for all bug types.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 47086 $
 * @ConQAT.Rating GREEN Hash: DE2ED53B4B728976AC3D36C10D192C89
 */
public class FindbugsMessageManager {

	/** Singleton instance. */
	private static FindbugsMessageManager instance = null;

	/** Mapping from bug types to short description strings. */
	private final Map<String, String> shortDescriptions = new HashMap<String, String>();

	/** Mapping from bug types to detailed description strings. */
	private final Map<String, String> detailedDescriptions = new HashMap<String, String>();

	/** Mapping category ID to readable category name. */
	private final Map<String, String> categoryNames = new HashMap<String, String>();

	/** Mapping bug ID to readable category name. */
	private final Map<String, String> bugCategories = new HashMap<String, String>();

	/** Constructor. */
	private FindbugsMessageManager() throws ConQATException {
		try {
			JarFile jarFile = new JarFile(
					FindBugsExecutor.getFindbugsJarDefaultLocation());
			InputStream messages = FileSystemUtils.openJarFileEntry(jarFile,
					"messages.xml");
			InputStream findbugs = FileSystemUtils.openJarFileEntry(jarFile,
					"findbugs.xml");
			try {
				readMessages(XMLUtils.parse(new InputSource(messages)));
				readCategories(XMLUtils.parse(new InputSource(findbugs)));
			} finally {
				FileSystemUtils.close(messages);
				FileSystemUtils.close(findbugs);
			}
		} catch (SAXException e) {
			throw new ConQATException("Parsing error!", e);
		} catch (IOException e) {
			throw new ConQATException("I/O error!", e);
		}
	}

	/** Reads the finding categories. */
	private void readCategories(Document findbugsDoc) {
		CCSMAssert.isFalse(categoryNames.isEmpty(),
				"Must be called after readMessages");
		for (Element bugPattern : XMLUtils.getNamedChildren(
				findbugsDoc.getDocumentElement(), "BugPattern")) {
			String type = bugPattern.getAttribute("type");
			String category = bugPattern.getAttribute("category");
			bugCategories.put(type, categoryNames.get(category));
		}
	}

	/** Reads the relevant messages into this table. */
	private void readMessages(Document doc) {
		for (Element bugPattern : XMLUtils.getNamedChildren(
				doc.getDocumentElement(), "BugPattern")) {
			String type = bugPattern.getAttribute("type");
			shortDescriptions.put(type,
					extractElementText(bugPattern, "ShortDescription"));
			detailedDescriptions.put(type,
					extractElementText(bugPattern, "Details"));
		}

		for (Element bugCategory : XMLUtils.getNamedChildren(
				doc.getDocumentElement(), "BugCategory")) {
			String category = bugCategory.getAttribute("category");
			categoryNames.put(category,
					extractElementText(bugCategory, "Description"));
		}
	}

	/**
	 * Extracts the text content of the first element of given name in the
	 * parent element.
	 */
	private String extractElementText(Element parent, String elementName) {
		Element element = XMLUtils.getNamedChild(parent, elementName);
		if (element == null) {
			return StringUtils.EMPTY_STRING;
		}
		return element.getTextContent();
	}

	/** Returns the singleton instance. */
	public static FindbugsMessageManager getInstance() throws ConQATException {
		if (instance == null) {
			instance = new FindbugsMessageManager();
		}
		return instance;
	}

	/** Returns the set of all rules (as IDs) */
	public UnmodifiableSet<String> getRuleIds() {
		return CollectionUtils.asUnmodifiable(shortDescriptions.keySet());
	}

	/**
	 * Returns the short description for a given bug pattern type or the empty
	 * String if none is available.
	 */
	public String getShortDescription(String type) {
		String result = shortDescriptions.get(type);
		if (result == null) {
			return StringUtils.EMPTY_STRING;
		}
		return result;
	}

	/** Returns the readable category name for the given bug pattern type. */
	public String getCategory(String type) {
		if (bugCategories.containsKey(type)) {
			return bugCategories.get(type);
		}
		return "Other FindBugs Checks";
	}

	/**
	 * Returns the detailed description for a given bug pattern type or the
	 * empty String if none is available.
	 */
	public String getDetailedDescription(String type) {
		String result = detailedDescriptions.get(type);
		if (result == null) {
			return StringUtils.EMPTY_STRING;
		}
		return result;
	}

}