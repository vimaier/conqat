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
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.conqat.engine.commons.CommonUtils;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.commons.node.IRemovableConQATNode;
import org.conqat.engine.commons.node.ListNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.reflect.ReflectionUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author juergens
 * @author $Author: feilkas $
 * @version $Rev: 41714 $
 * @ConQAT.Rating GREEN Hash: A10C0F0DC399BC7AA7FDAC92B647E3CF
 */
@AConQATProcessor(description = "Reads CSV (comma separated value) files into a rooted list of ConQAT nodes. "
		+ "For each line in the CSV file, a single {@link IConQATNode} is created. A "
		+ "dedicated column of the CSV file serves as node ids. "
		+ "The first line in the file is expected to contain column names.")
public class CSVReader extends ConQATProcessorBase {

	/** CSV file name */
	private String filename;

	/** Name of the column that is used for IDs */
	private String idColumn;

	/** String used to split columns */
	private String separator = ";";

	/** Converts value strings into typed objects */
	private final TypeConverter converter = new TypeConverter();

	/** Encoding for file read. */
	private Charset encoding = Charset.defaultCharset();

	/** ConQAT Parameter */
	@AConQATParameter(name = "csv", minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "Comma separeted value file that gets read")
	public void setFile(
			@AConQATAttribute(name = "file", description = "Name of the file") String filename,
			@AConQATAttribute(name = "idColumn", description = "Name of the column from which node ids are taken") String idColumn) {
		this.filename = filename;
		this.idColumn = idColumn;
	}

	/** ConQAT Parameter */
	@AConQATParameter(name = "separator", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "String used to split columns.")
	public void setSeparator(
			@AConQATAttribute(name = "separator", defaultValue = ";", description = "Default: \";\"") String separator) {
		this.separator = Pattern.quote(separator);
	}

	/** ConQAT Parameter */
	@AConQATParameter(name = "type", description = "Set the type for a column")
	public void addType(
			@AConQATAttribute(name = "column", description = "Name of the column") String columnName,
			@AConQATAttribute(name = "typename", description = "Name of the type. Allowed values are all java primitives. (int, boolean, long, ...)") String typeName) {

		try {
			Class<?> clazz = ReflectionUtils.resolveType(typeName);
			converter.addTypeAssociation(columnName, clazz);
		} catch (ClassNotFoundException e) {
			getLogger().error("Could not find type: " + typeName);
		}
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.ENCODING_PARAM_NAME, minOccurrences = 0, maxOccurrences = 1, description = ConQATParamDoc.ENCODING_PARAM_DESC)
	public void setEncoding(
			@AConQATAttribute(name = ConQATParamDoc.ENCODING_ATTR_NAME, description = ConQATParamDoc.ENCODING_ATTR_DESC) String encodingName)
			throws ConQATException {
		encoding = CommonUtils.obtainEncoding(encodingName);
	}

	/** {@inheritDoc} */
	@Override
	public IRemovableConQATNode process() throws ConQATException {

		String[] lines;
		try {
			lines = StringUtils.splitLines(FileSystemUtils.readFile(new File(
					filename), encoding.name()));
		} catch (IOException e) {
			throw new ConQATException("Could not read file " + filename + ": "
					+ e.getMessage(), e);
		}
		if (lines.length == 0) {
			throw new ConQATException("CSV file " + filename
					+ " was empty. At least the header was expected");
		}

		String[] columnNames = lines[0].split(separator);
		if (!Arrays.asList(columnNames).contains(idColumn)) {
			throw new ConQATException("Id column '" + idColumn
					+ "' not found in header of file " + filename);
		}

		ListNode listRoot = createNodeList(columnNames, lines);
		NodeUtils.addToDisplayList(listRoot, columnNames);

		return listRoot;
	}

	/**
	 * Creates a rooted list containing a SimpleNode for each line (excluding
	 * the header line)
	 */
	private ListNode createNodeList(String[] columnNames, String[] lines)
			throws ConQATException {
		ListNode listRoot = new ListNode();

		int idColumnIndex = StringUtils.indexOf(columnNames, idColumn);
		for (int lineNumber = 1; lineNumber < lines.length; lineNumber++) {
			String[] values = lines[lineNumber].split(separator);
			ListNode node = new ListNode(values[idColumnIndex]);

			for (int columnIndex = 0; columnIndex < values.length; columnIndex++) {
				if (columnIndex != idColumnIndex) {
					String columnName = columnNames[columnIndex];
					Object typedValue = converter.typedValueFor(columnName,
							values[columnIndex]);
					node.setValue(columnName, typedValue);
				}
			}

			listRoot.addChild(node);
		}
		return listRoot;
	}

}