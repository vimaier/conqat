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
package org.conqat.engine.abap.nugget;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;

import org.conqat.lib.commons.filesystem.CanonicalFile;
import org.conqat.lib.commons.filesystem.FileExtensionFilter;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.commons.xml.XMLUtils;
import org.w3c.dom.Element;

/**
 * Helper class which is used to access various types of source code in elements
 * processed by NuggetSplitter. It allows to distinguish between different
 * languages, such as ABAP code and DynPro flow code.
 * 
 * @author $Author: juergens $
 * @version $Rev: 41786 $
 * @ConQAT.Rating GREEN Hash: 93347985325AB46E0CFDC22E54141BE3
 */
/* package */class NuggetSource {

	/** Constant for the element name identifying DynPro flow code */
	private static final String DYNPRO_FLOW_SOURCE = "dynproflowsource";

	/** Constant holding file extension for ABAP source files */
	public static final String ABAP_FILE_EXTENSION = "abap";

	/** Constant holding file extension for Dynpro flow source files */
	public static final String DYNPRO_FILE_EXTENSION = "dynpro";

	/** Constant for a {@link FileFilter} to filter the source files */
	public static final FileFilter SOURCE_FILE_FILTER = new FileExtensionFilter(
			ABAP_FILE_EXTENSION, DYNPRO_FILE_EXTENSION);

	/** Holds the identified ABAP source */
	private final String abapSource;

	/** Holds the identified DynPro flow source */
	private final String dynproFlowSource;

	/**
	 * Creates a new {@link NuggetSource} object from the given element. The
	 * element is expected to be a nugget-element which holds source code as
	 * text in the leaf elements. All text in the leaf elements is added to the
	 * source code, be default to the ABAP source code. Leaf elements which are
	 * recognized as Dynpro flow source are added to the Dynpro source code.
	 */
	/* package */NuggetSource(Element element) {
		List<Element> leafElements = XMLUtils.leafElementNodes(element);
		StringBuffer abapSourceBuffer = new StringBuffer();
		StringBuffer dynproFlowSourceBuffer = new StringBuffer();
		for (Element leaf : leafElements) {
			if (leaf.getTagName().equals(DYNPRO_FLOW_SOURCE)) {
				addTextToSourceContent(leaf, dynproFlowSourceBuffer);
			} else {
				addTextToSourceContent(leaf, abapSourceBuffer);
			}
		}
		abapSource = abapSourceBuffer.toString().trim();
		dynproFlowSource = dynproFlowSourceBuffer.toString().trim();
	}

	/**
	 * Adds the text in the given leaf element to the given content buffer.
	 */
	private void addTextToSourceContent(Element leaf, StringBuffer content) {
		String text = leaf.getTextContent();
		content.append(text);
		if (text.length() > 0) {
			content.append(StringUtils.CR);
		}
	}

	/** Gets the identified ABAP source code */
	/* package */String getAbapSource() {
		return abapSource;
	}

	/** Gets the identified Dynpro flow source code */
	/* package */String getDynproFlowSource() {
		return dynproFlowSource;
	}

	/**
	 * Writes the source code contents to files. For every language, an
	 * individual file is created.
	 * 
	 * @param fileName
	 *            base name of file without file extension.
	 */
	/* package */void writeContentToFiles(File targetDir, String fileName)
			throws IOException {
		if (!StringUtils.isEmpty(abapSource)) {
			File abapFile = new CanonicalFile(targetDir, fileName + "."
					+ ABAP_FILE_EXTENSION);
			FileSystemUtils.writeFile(abapFile, abapSource);
		}
		if (!StringUtils.isEmpty(dynproFlowSource)) {
			File dynproFile = new CanonicalFile(targetDir, fileName + "."
					+ DYNPRO_FILE_EXTENSION);
			FileSystemUtils.writeFile(dynproFile, dynproFlowSource);
		}
	}

}
