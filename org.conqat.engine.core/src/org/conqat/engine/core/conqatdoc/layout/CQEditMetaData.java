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
package org.conqat.engine.core.conqatdoc.layout;

import java.awt.Point;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.conqat.lib.commons.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * This class manages the access to meta-data used by the cq.edit graphical
 * editor.
 * <p>
 * The current implementation performs a full inspection of the meta-data at
 * creation time and then answers all queries from a prepared {@link HashMap}.
 * While a solution using XPath (which we used before) might seem cleaner from
 * an XML perspective, it is more than ten times slower which severely impacts
 * the performance of configuration traversal of large configurations.
 * 
 * @author $Author: heineman $
 * @version $Rev: 39917 $
 * @ConQAT.Rating GREEN Hash: 45CB463B81133B3B678C4C086C914CA1
 */
public class CQEditMetaData {

	/** XML element <code>entry</code>. This stores a key value pair. */
	protected final static String XML_ELEMENT_ENTRY = "entry";

	/**
	 * XML element <code>entries</code>. This holds all <code>entry</code>
	 * elements for a namespace.
	 */
	protected final static String XML_ELEMENT_ENTRIES = "entries";

	/** XML attribute of {@link #XML_ELEMENT_ENTRIES}. */
	protected final static String XML_ATTRIBUTE_NAMESPACE = "namespace";

	/** XML attribute of {@link #XML_ELEMENT_ENTRY}. */
	protected final static String XML_ATTRIBUTE_KEY = "key";

	/** XML attribute of {@link #XML_ELEMENT_ENTRY}. */
	protected final static String XML_ATTRIBUTE_VALUE = "value";

	/** Key used for storing the position. */
	public final static String KEY_POSITION = "pos";

	/** Key used for storing visibility of edges from this source */
	public final static String KEY_SOURCE_EDGES_INVISIBLE = "edges_invisible";

	/** Meta-data type used by the editor. */
	public final static String CQEDIT_META_DATA_TYPE = "cq.edit";

	/**
	 * Stores the meta data using a key created from the namespace and key using
	 * {@link #qualifiedKey(String, String)}.
	 */
	private final Map<String, String> metaData = new HashMap<String, String>();

	/** Clears the stored meta data and reloads it */
	public void clearAndLoadFromElement(Element metaDataElement) {
		metaData.clear();

		List<Element> entriesElements = XMLUtils.getNamedChildren(
				metaDataElement, XML_ELEMENT_ENTRIES);
		for (Element entries : entriesElements) {
			String namespace = entries.getAttribute(XML_ATTRIBUTE_NAMESPACE);
			List<Element> entryElement = XMLUtils.getNamedChildren(entries,
					XML_ELEMENT_ENTRY);
			for (Element entry : entryElement) {
				String key = qualifiedKey(namespace,
						entry.getAttribute(XML_ATTRIBUTE_KEY));
				metaData.put(key, entry.getAttribute(XML_ATTRIBUTE_VALUE));
			}
		}
	}

	/** Creates the key used for storing a namespace/key pair. */
	private static String qualifiedKey(String namespace, String key) {
		return namespace + "#-#" + key;
	}

	/**
	 * Get meta-data value or <code>null</code> if no value exists.
	 * 
	 * @param namespace
	 *            meta-data namespace, e.g. the unit name
	 * @param key
	 *            meta-data key
	 */
	public String getMetaData(String namespace, String key) {
		return metaData.get(qualifiedKey(namespace, key));
	}

	/**
	 * Returns the position stored in the meta-data for the given namespace or
	 * <code>null</code> if no meta data could be extracted or the meta-data
	 * does not contain a valid position.
	 */
	public Point getPosition(String namespace) {
		String posString = getMetaData(namespace, KEY_POSITION);
		if (posString == null) {
			return null;
		}

		String[] pos = posString.split(",");
		if (pos.length != 2) {
			return null;
		}

		try {
			return new Point(Integer.parseInt(pos[0]), Integer.parseInt(pos[1]));
		} catch (NumberFormatException ex) {
			return null;
		}
	}

	/** Returns source edge visibility for the given element. */
	public boolean getSourceEdgesInvisible(String namespace) {
		return Boolean.valueOf(getMetaData(namespace,
				KEY_SOURCE_EDGES_INVISIBLE));
	}
}