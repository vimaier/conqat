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
package org.conqat.engine.html_presentation.layouters;

import static org.conqat.lib.commons.string.StringUtils.CR;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.commons.pattern.PatternTransformationList;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.graph.nodes.ConQATGraph;
import org.conqat.engine.html_presentation.color.ColorizerBase;
import org.conqat.engine.html_presentation.image.IImageDescriptor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: F06A41E92CC79114AF56387BD0F8DB4C
 */
@AConQATProcessor(description = "Creates graphical representation of ConQAT graphs. "
		+ "Graphs are layout using GraphViz's dot layout engine. Therefore the dot "
		+ "executable must be on the system's path.")
public class GraphCreator extends ConQATProcessorBase {

	/** Name of the graph icon: {@value} */
	public static final String GRAPH_ICON_NAME = "graph.gif";

	/** The graph to display. */
	private ConQATGraph graph;

	/** Key in which colors for nodes and edges are stored */
	private String colorKey = ColorizerBase.COLOR_KEY_DEFAULT;

	/** further DOT layout statements */
	private final StringBuilder additionalDotHeaders = new StringBuilder();

	/** Name transformation. */
	private PatternTransformationList transformation = null;

	/** Show clusters in graph? */
	private boolean showClusters = true;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "dot-executable", attribute = "path", description = "The "
			+ "path to the dot executable.", optional = true)
	public String dotExecutablePath = null;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "transform", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Definition of transformations appllied to the node IDs before useing them as labels.")
	public void setAbbreviation(
			@AConQATAttribute(name = "list", description = "Transformation list") PatternTransformationList list) {

		this.transformation = list;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "graph", minOccurrences = 1, maxOccurrences = 1, description = "Graph to visualize.")
	public void setInput(
			@AConQATAttribute(name = "ref", description = "Reference to graph generating processor.") ConQATGraph graph) {

		this.graph = graph;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "leftRightOrientation", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Enable left right orientation")
	public void setLeftRightOrientation(
			@AConQATAttribute(name = "value", description = "If true, graph is "
					+ "layouted from left to right, otherwise from top to bottom") boolean leftRightOrientation) {

		if (leftRightOrientation) {
			additionalDotHeaders.append("    rankdir = \"LR\";" + CR);
		}
	}

	/**
	 * {@ConQAT.Doc}
	 * 
	 * @param headerLine
	 *            DOT headerline, e.g. rankdir or ranksep commands.
	 */
	@AConQATParameter(name = "dot", description = "DOT command for layout information")
	public void addHeaderLine(
			@AConQATAttribute(name = "header", description = "header line for the DOT source") String headerLine) {
		additionalDotHeaders.append(headerLine + CR);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "clusters", minOccurrences = 0, maxOccurrences = 1, description = "Enable/disable clustering")
	public void setShowClusters(
			@AConQATAttribute(name = "show", description = "Enable/disable clustering [true]") boolean showClusters) {
		this.showClusters = showClusters;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "color", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Set key in which colors for keys and nodes are stored")
	public void setColorKey(
			@AConQATAttribute(name = "key", description = "Name of the key") String colorKey) {
		this.colorKey = colorKey;
	}

	/** {@inheritDoc} */
	@Override
	public IImageDescriptor process() {
		return new GraphImageDescriptor(graph, showClusters, transformation,
				additionalDotHeaders.toString(), colorKey, dotExecutablePath);
	}
}