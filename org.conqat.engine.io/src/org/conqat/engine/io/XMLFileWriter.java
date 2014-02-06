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
package org.conqat.engine.io;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.List;

import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.io.format.EXMLAttribute;
import org.conqat.engine.io.format.EXMLElement;
import org.conqat.lib.commons.assessment.Assessment;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.xml.XMLResolver;
import org.conqat.lib.commons.xml.XMLWriter;

/**
 * This processor writes an IConQATNode to an XML file.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 299E4728C4A7AC76CDA0247A658F580E
 */
@AConQATProcessor(description = "This processor writes an IConQATNode to an XML file.")
public class XMLFileWriter extends InputFileWriterBase<IConQATNode> {

	/** XML writer to write file. */
	private XMLWriter<EXMLElement, EXMLAttribute> writer;

	/** {@inheritDoc} */
	@Override
	protected void writeToFile(IConQATNode input, File file) throws IOException {
		writer = new XMLWriter<EXMLElement, EXMLAttribute>(
				new PrintStream(file, FileSystemUtils.UTF8_ENCODING),
				new XMLResolver<EXMLElement, EXMLAttribute>(EXMLAttribute.class));
		write(input);
		writer.close();
	}

	/** Write whole tree. */
	private void write(IConQATNode input) {
		writer.addHeader("1.0", FileSystemUtils.UTF8_ENCODING);

		writer.openElement(EXMLElement.result, EXMLAttribute.xmlns,
				"http://conqat.cs.tum.edu/ns/node", EXMLAttribute.hideRoot,
				NodeUtils.getHideRoot(input));

		List<String> keyList = NodeUtils.getDisplayList(input).getKeyList();

		writeKeyList(keyList);
		writeNode(input, keyList);

		writer.closeElement(EXMLElement.result);
	}

	/** Recursively write a node. */
	private void writeNode(IConQATNode node, List<String> displayList) {

		writer.openElement(EXMLElement.node, EXMLAttribute.id, node.getId());

		writer.addClosedTextElement(EXMLElement.description, node.getName());

		for (String key : displayList) {
			writeKeyValue(node, key);
		}

		if (node.hasChildren()) {
			for (IConQATNode child : NodeUtils.getSortedChildren(node)) {
				writeNode(child, displayList);
			}
		}

		writer.closeElement(EXMLElement.node);
	}

	/** Write a single key with its value. */
	private void writeKeyValue(IConQATNode node, String key) {
		writer.openElement(EXMLElement.value, EXMLAttribute.key, key);
		writeValue(node.getValue(key));
		writer.closeElement(EXMLElement.value);
	}

	/** Write a value. */
	private void writeValue(Object value) {
		if (value == null) {
			return;
		}
		if (value instanceof Collection<?>) {
			writeCollection((Collection<?>) value);
			return;
		}
		if (value instanceof Assessment) {
			Assessment assessment = (Assessment) value;
			writer.addText(assessment.getDominantColor().name());
			return;
		}
		if (value instanceof Number) {
			writer.addText(numberFormatter.format(value));
			return;
		}
		if (value instanceof Color) {
			writer.addText("#"
					+ Integer.toHexString(((Color) value).getRGB())
							.substring(2));
			return;
		}
		writer.addText(value.toString());
	}

	/** Write a collection. */
	private void writeCollection(Collection<?> collection) {
		writer.openElement(EXMLElement.collection);
		for (Object item : collection) {
			writer.openElement(EXMLElement.item);
			writeValue(item);
			writer.closeElement(EXMLElement.item);
		}
		writer.closeElement(EXMLElement.collection);
	}

	/** Write list of keys. */
	private void writeKeyList(List<String> displayList) {
		for (String key : displayList) {
			writer.openElement(EXMLElement.key);
			writer.addText(key);
			writer.closeElement(EXMLElement.key);
		}
	}
}