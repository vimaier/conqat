/*--------------------------------------------------------------------------+
$Id: CSVWriter.java 45262 2013-06-23 20:24:09Z hummelb $
|                                                                          |
| Copyright 2005-2010 Technische Universitaet Muenchen                     |
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
+--------------------------------------------------------------------------*/
package org.conqat.engine.io;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.sorting.NodeIdComparator;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Base class for processors that write CSV files.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 45262 $
 * @ConQAT.Rating GREEN Hash: 0CB37C35D5B5A1EA00E2AA6C1CF027D9
 */
@AConQATProcessor(description = "Writes the nodes of a ConQAT node hierarchy to a CSV file. "
		+ "Each node is written to a single line and each line contains the ID of the node and the node's values. "
		+ "The first line contains a field description. The keys respected during writing are determined by the display list.")
public class CSVWriter extends InputFileWriterBase<IConQATNode> {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "separator", attribute = "separator", optional = true, description = ""
			+ "String used to split columns (default is semicolon).")
	public String separator = ";";

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "target", attribute = "nodes", optional = true, description = ""
			+ "The target nodes to operate on (default is leaves).")
	public ETargetNodes targetNodes = ETargetNodes.LEAVES;

	/** {@inheritDoc} */
	@Override
	protected void writeToFile(IConQATNode input, File file) throws IOException {

		List<String> keys = getKeys(input);

		PrintWriter pw = new PrintWriter(file);
		try {
			pw.print("id");
			pw.print(separator);
			pw.println(StringUtils.concat(keys, separator));

			List<IConQATNode> nodes = obtainNodes(input);
			// sort to get stable CSV files
			sortNodes(input, nodes);

			for (IConQATNode node : nodes) {
				pw.print(node.getId());
				for (String key : keys) {
					pw.print(separator);
					pw.print(getValue(node, key));
				}
				pw.println();
			}
		} finally {
			FileSystemUtils.close(pw);
		}
	}

	/**
	 * Sort using comparator stored in root node, or by node id if no comparator
	 * is found.
	 */
	@SuppressWarnings("unchecked")
	private void sortNodes(IConQATNode input, List<IConQATNode> nodes) {
		Comparator<IConQATNode> comparator = (Comparator<IConQATNode>) NodeUtils
				.getComparator(input);
		if (comparator == null) {
			comparator = NodeIdComparator.INSTANCE;
		}
		Collections.sort(nodes, comparator);
	}

	/**
	 * Template method that allows deriving classes to overwrite the way they
	 * retrieve values from nodes
	 */
	protected Object getValue(IConQATNode node, String key) {
		return node.getValue(key);
	}

	/**
	 * Returns all nodes from the input that are to be written to the CSV file.
	 * Deriving classes should override this method, if they need to customize
	 * this.
	 */
	@SuppressWarnings("unchecked")
	protected <E extends IConQATNode> List<E> obtainNodes(IConQATNode input) {
		return (List<E>) TraversalUtils.listDepthFirst(input, targetNodes);
	}

	/**
	 * Method that deriving classes can override to determine the keys to write
	 * to the CSV file
	 */
	protected List<String> getKeys(IConQATNode input) {
		return NodeUtils.getDisplayList(input).getKeyList();
	}
}
