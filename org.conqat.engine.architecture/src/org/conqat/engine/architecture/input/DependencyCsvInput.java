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
package org.conqat.engine.architecture.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.conqat.engine.commons.keys.IDependencyListKey;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.node.StringSetNode;
import org.conqat.engine.commons.util.ConQATInputProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.engine.resource.text.TextElementUtils;
import org.conqat.engine.resource.util.ResourceTraversalUtils;

/**
 * {@ConQAT.Doc}
 * <p>
 * This processor can parse CSV files. In the simple case they have
 * columns "source;destination". However, this processor can also handle more
 * complex cases with columns like
 * "source_part1;dest_part2;dest_part1;source_part2". As example of such a case
 * are DB dependencies exported from Oracle, which produces columns
 * "OWNER;NAME;TYPE;REFERENCED_OWNER;REFERENCED_NAME;REFERENCED_TYPE;REFERENCED_LINK_NAME;DEPENDENCY_TYPE"
 * , from which several are concatenated to build the source and destination node
 * names.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: FEAA43222D65A833985D8C051E6F4EFB
 */
@AConQATProcessor(description = "Loads dependency lists from CSV files and creates a node hierarchy that can be used for architecture analysis.")
public class DependencyCsvInput extends ConQATInputProcessorBase<ITextResource>
		implements IDependencyListKey {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "separator", attribute = "string", optional = true, description = ""
			+ "The string that separates entries in a line. The default is a single semicolon.")
	public String csvSeparator = ";";

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "name", attribute = "separator", optional = true, description = ""
			+ "The string that separates parts of a name. The default is a single dot.")
	public String nameSeparator = ".";

	/** The names of columns forming the source. */
	private final List<String> sourceColumns = new ArrayList<String>();

	/** The names of columns forming the target. */
	private final List<String> targetColumns = new ArrayList<String>();

	/** The root node. */
	private final StringSetNode rootNode = new StringSetNode();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "source", minOccurrences = 1, description = ""
			+ "Adds a column to be used for the source name.")
	public void addSourceColumn(
			@AConQATAttribute(name = "column", description = "The name of the column.") String column) {
		sourceColumns.add(column);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "target", minOccurrences = 1, description = ""
			+ "Adds a column to be used for the target name.")
	public void addTargetColumn(
			@AConQATAttribute(name = "column", description = "The name of the column.") String column) {
		targetColumns.add(column);
	}

	/** {@inheritDoc} */
	@Override
	public IConQATNode process() throws ConQATException {
		NodeUtils.addToDisplayList(rootNode, DEPENDENCY_LIST_KEY);

		for (ITextElement element : ResourceTraversalUtils
				.listTextElements(input)) {
			processElement(element);
		}

		return rootNode;
	}

	/** Processes a single text element. */
	protected void processElement(ITextElement element) throws ConQATException {
		String[] lines = TextElementUtils.getLines(element);
		if (lines.length == 0) {
			throw new ConQATException("Empty element: " + element.getLocation());
		}

		Map<String, Integer> columnToIndex = computeColumnToIndexLookup(
				element, lines);
		List<Integer> sourceIndices = columnsToIndices(sourceColumns,
				columnToIndex, element);
		List<Integer> targetIndices = columnsToIndices(targetColumns,
				columnToIndex, element);

		for (int i = 1; i < lines.length; ++i) {
			String[] cells = split(lines[i]);
			List<String> sourceName = buildName(cells, sourceIndices, element,
					i);
			List<String> targetName = buildName(cells, targetIndices, element,
					i);

			StringSetNode sourceNode = getOrCreateNode(sourceName);
			StringSetNode targetNode = getOrCreateNode(targetName);

			NodeUtils.getOrCreateStringList(sourceNode, DEPENDENCY_LIST_KEY)
					.add(targetNode.getId());
		}
	}

	/** Returns a map from column name to index. */
	private Map<String, Integer> computeColumnToIndexLookup(
			ITextElement element, String[] lines) throws ConQATException {
		String[] columns = split(lines[0]);
		Map<String, Integer> columnToIndex = new HashMap<String, Integer>();
		for (int i = 0; i < columns.length; ++i) {
			if (columnToIndex.put(columns[i], i) != null) {
				throw new ConQATException("Duplicate column name '"
						+ columns[i] + "' in element: " + element.getLocation());
			}
		}
		return columnToIndex;
	}

	/**
	 * Returns the node with given name sequences (or creates it if it does not
	 * yet exist).
	 */
	private StringSetNode getOrCreateNode(List<String> name) {
		StringSetNode node = rootNode;
		for (String part : name) {
			StringSetNode child = node.getNamedChild(part);
			if (child == null) {
				String id = part;
				if (node != rootNode) {
					id = node.getId() + nameSeparator + part;
				}

				child = new StringSetNode(id, part);
				node.addChild(child);
			}
			node = child;
		}
		return node;
	}

	/** Extracts the name as a list of parts from cells and indices. */
	private List<String> buildName(String[] cells, List<Integer> indices,
			ITextElement element, int lineIndex) throws ConQATException {
		List<String> name = new ArrayList<String>();
		for (int index : indices) {
			if (index >= cells.length) {
				throw new ConQATException("Column " + index
						+ " not found in line " + lineIndex + " of element: "
						+ element.getLocation());
			}
			String part = cells[index].trim();
			if (!part.isEmpty()) {
				name.add(part);
			}
		}

		if (name.isEmpty()) {
			throw new ConQATException("Only empty name parts found in line "
					+ lineIndex + " of element: " + element.getLocation());
		}

		return name;
	}

	/** Converts a list of column names to a list of column indices. */
	private static List<Integer> columnsToIndices(List<String> columns,
			Map<String, Integer> columnToIndex, ITextElement element)
			throws ConQATException {
		List<Integer> indices = new ArrayList<Integer>();
		for (String column : columns) {
			Integer index = columnToIndex.get(column);
			if (index == null) {
				throw new ConQATException("Missing column '" + column
						+ "' in element: " + element.getLocation());
			}
			indices.add(index);
		}
		return indices;
	}

	/** Splits a single CSV line. */
	private String[] split(String line) {
		return line.split(Pattern.quote(csvSeparator));
	}
}
