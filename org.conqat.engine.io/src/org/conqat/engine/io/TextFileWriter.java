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

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.commons.assessment.Assessment;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Generate a text file from an annotated {@link IConQATNode}-tree. These files
 * are typically processed with Excel or something similar.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 8E25BA955B7C305D19242313E5A761B8
 */
@AConQATProcessor(description = "Generate a text file from an annotated "
		+ "IConQATNode-tree. These files are typically processed with "
		+ "Excel or something similar. It is recommended to use this "
		+ "class with the FilePresentation.")
public class TextFileWriter extends InputFileWriterBase<IConQATNode> {

	/** Column separator string. */
	private String columnSeparator = StringUtils.TAB;

	/** Indentation character. */
	private char indentCharacter = StringUtils.SPACE_CHAR;

	/** Collection item separator. */
	private String itemSeparator = ",";

	/** Flag for writing the node id. */
	private boolean writeId = false;

	/** Set column separator string. */
	@AConQATParameter(name = "separators", minOccurrences = 0, maxOccurrences = 1, description = "Separator strings.")
	public void setSeparators(
			@AConQATAttribute(name = "column", description = "Column separator string (tab by default).") String columnSeparator,
			@AConQATAttribute(name = "item", description = "Item separator string for collection items (comma by default).") String itemSeparator) {
		this.columnSeparator = columnSeparator;
		this.itemSeparator = itemSeparator;
	}

	/** Set description indentation character. */
	@AConQATParameter(name = "indent", minOccurrences = 0, maxOccurrences = 1, description = "Indentation character for the node description. "
			+ "Indentation can be turned off by providing an empty string.")
	public void setIndentCharacter(
			@AConQATAttribute(name = "char", description = "Description indentation character (space by default).") char indentCharacter) {
		this.indentCharacter = indentCharacter;
	}

	/** Write node id? */
	@AConQATParameter(name = "write-id", minOccurrences = 0, maxOccurrences = 1, description = "Write node id to file?")
	public void setWriteId(
			@AConQATAttribute(name = "value", description = "true/false [false]") boolean writeId) {
		this.writeId = writeId;
	}

	/** {@inheritDoc} */
	@Override
	protected void writeToFile(IConQATNode input, File file) throws IOException {
		if (columnSeparator.equals(itemSeparator)) {
			getLogger().warn("Column separator and item separator are equal.");
		}

		FileSystemUtils.writeFile(file, createContent(input));
	}

	/** Create the entire content. */
	private String createContent(IConQATNode input) {
		StringBuilder content = new StringBuilder();

		List<String> keyList = NodeUtils.getDisplayList(input).getKeyList();
		boolean hideRoot = NodeUtils.getHideRoot(input);

		createHeader(keyList, content);

		if (hideRoot) {
			for (IConQATNode node : NodeUtils.getSortedChildren(input)) {
				appendNode(node, keyList, content, 0);
			}
		} else {
			appendNode(input, keyList, content, 0);
		}

		return content.toString();
	}

	/** Create the header. */
	private void createHeader(List<String> keyList, StringBuilder content) {
		content.append("Description");
		content.append(columnSeparator);

		if (writeId) {
			content.append("Id");
			content.append(columnSeparator);
		}

		content.append(StringUtils.concat(keyList, columnSeparator));
		content.append(StringUtils.CR);
	}

	/**
	 * Recursively add all elements (with all keys).
	 * 
	 * @param node
	 *            node to start with
	 * @param content
	 *            <code>StringBuilder</code> to add content to.
	 * @param depth
	 *            current depth in the tree
	 */
	private void appendNode(IConQATNode node, List<String> keyList,
			StringBuilder content, int depth) {

		// this is for indenting
		content.append(StringUtils.fillString(depth, indentCharacter));
		content.append(StringUtils.replaceLineBreaks(node.getName()));
		content.append(columnSeparator);

		if (writeId) {
			content.append(StringUtils.replaceLineBreaks(node.getId()));
			content.append(columnSeparator);
		}

		appendValues(node, keyList, content);
		content.append(StringUtils.CR);

		if (node.hasChildren()) {
			for (IConQATNode child : NodeUtils.getSortedChildren(node)) {
				appendNode(child, keyList, content, depth + 1);
			}
		}
	}

	/** Append all values for nodes to the table. */
	private void appendValues(IConQATNode node, List<String> keyList,
			StringBuilder content) {
		Iterator<String> it = keyList.iterator();
		while (it.hasNext()) {
			content.append(formatValue(node.getValue(it.next())));
			if (it.hasNext()) {
				content.append(columnSeparator);
			}
		}
	}

	/**
	 * Generate description of value. The following conversions are performed.
	 * 
	 * <ol>
	 * <li>If the value is <code>null</code> a space is returned.</li>
	 * <li>If the value is an assessment, a color string is returned.</li>
	 * <li>If the value is a collection, its values are comma separated.</li>
	 * <li>If the value is a number, it's formatted using {@link NumberFormat}
	 * (US locale).</li>
	 * </ol>
	 */
	protected String formatValue(Object value) {
		if (value == null) {
			return StringUtils.EMPTY_STRING;
		}

		if (value instanceof Assessment) {
			Assessment assessment = (Assessment) value;
			return assessment.getDominantColor().name();
		}

		if (value instanceof Collection<?>) {
			StringBuilder formattedValue = new StringBuilder();
			Collection<?> collection = (Collection<?>) value;
			Iterator<?> it = collection.iterator();

			while (it.hasNext()) {
				formattedValue.append(StringUtils.replaceLineBreaks(it.next()
						.toString()));
				if (it.hasNext()) {
					formattedValue.append(itemSeparator);
				}
			}
			return formattedValue.toString();
		}

		if (value instanceof Number) {
			return numberFormatter.format(value);
		}

		return value.toString();
	}

}