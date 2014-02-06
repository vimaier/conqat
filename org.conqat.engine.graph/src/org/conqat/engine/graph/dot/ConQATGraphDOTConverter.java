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
package org.conqat.engine.graph.dot;

import static org.conqat.lib.commons.string.StringUtils.CR;

import java.awt.Color;

import org.conqat.engine.commons.pattern.PatternTransformationList;
import org.conqat.engine.graph.nodes.ConQATGraph;
import org.conqat.engine.graph.nodes.ConQATVertex;
import org.conqat.engine.graph.nodes.IConQATGraphNode;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.utils.UserData;

/**
 * A class for converting a {@link ConQATGraph} to input suitable for the DOT
 * program.
 * 
 * @author Florian Deissenboeck
 * @author Tilman Seifert
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * 
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: FB27CDB2E5EEA05E3533ECE22D2D698F
 */
public class ConQATGraphDOTConverter {

	/** The header for DOT files. */
	public final static String HEADER = "digraph G {" + CR
			+ "  edge [  fontname = \"Helvetica\"," + CR
			+ "          color = \"#639CCE\", fontsize = 8 ];" + CR
			+ "  node [  shape = polygon," + CR + "          sides = 4," + CR
			+ "          color = \"#639CCE\"," + CR
			+ "          fontname = \"Helvetica\"," + CR
			+ "          fontsize    = 9," + CR + "          height=0.25];"
			+ CR;

	/** The transformation patterns for vertex names. */
	private PatternTransformationList transformation = null;

	/** Additional headers included. */
	private String additionalHeaders = null;

	/** Flag for label generation based on names. */
	private boolean useNameAsLabel = false;

	/** Key in which colors are stored */
	private String colorKey = "color";

	/** Sets additional headers to be used. */
	public void setAdditionalHeaders(String additionalHeaders) {
		this.additionalHeaders = additionalHeaders;
	}

	/** Sets the transforation used for changing node labels. */
	public void setTransformation(PatternTransformationList transformation) {
		this.transformation = transformation;
	}

	/** Set the key in which the color is stored */
	public void setColorKey(String colorKey) {
		this.colorKey = colorKey;
	}

	/**
	 * Specify label generation.
	 * <p>
	 * <code>true</code>: node name, <code>false</code>: node id.
	 */
	public void setUseNameAsLabel(boolean useNameAsLabel) {
		this.useNameAsLabel = useNameAsLabel;
	}

	/** Returns the DOT input description for the provided graph. */
	public String convertToDot(ConQATGraph graph) {
		StringBuilder result = new StringBuilder();
		result.append(HEADER);
		if (additionalHeaders != null) {
			result.append(additionalHeaders);
		}
		result.append(createVertexDescription(graph));
		result.append(createEdgeDescription(graph));
		result.append("}" + CR);
		return result.toString();
	}

	/** Returns the description for all vertices. */
	protected String createVertexDescription(ConQATGraph graph) {
		StringBuilder result = new StringBuilder();
		for (ConQATVertex vertex : graph.getVertices()) {
			result.append(createVertex(vertex));
		}
		return result.toString();
	}

	/** Returns the description for a single vertex. */
	protected String createVertex(ConQATVertex node) {
		StringBuilder result = new StringBuilder();
		result.append(makeId(node));
		result.append(" [label=\"" + makeLabel(node) + "\"");
		createColorDescription(node, result);
		result.append("];" + CR);
		return result.toString();
	}

	/** Inserts a color tag into a string builder */
	private void createColorDescription(UserData node, StringBuilder result) {
		Object color = node.getUserDatum(colorKey);
		if (color instanceof Color) {
			result.append(String.format("color=\"#%06X\"", ((Color) color)
					.getRGB() & 0xffffff));
		}
	}

	/** Create the description of all edges. */
	protected String createEdgeDescription(ConQATGraph graph) {
		StringBuilder result = new StringBuilder();
		for (Object o : graph.getGraph().getEdges()) {
			DirectedSparseEdge edge = (DirectedSparseEdge) o;
			result.append(createEdge(edge));
		}
		return result.toString();
	}

	/** Create the description of a single edge. */
	protected String createEdge(DirectedSparseEdge edge) {
		StringBuilder result = new StringBuilder();
		ConQATVertex sourceVertex = (ConQATVertex) edge.getSource();
		ConQATVertex targetVertex = (ConQATVertex) edge.getDest();

		result.append(makeId(sourceVertex));
		result.append(" -> ");
		result.append(makeId(targetVertex));
		result.append(" [");

		createColorDescription(edge, result);

		result.append("];" + CR);
		return result.toString();
	}

	/** Create a DOT-compatible id for vertices from a vertex description. */
	protected String makeId(IConQATGraphNode vertex) {
		return "\"" + dotEscape(vertex.getId()) + "\"";
	}

	/**
	 * Returns the input string with suitable escape characters to be used for
	 * DOT. The surrounding quotes are not added.
	 */
	public static String dotEscape(String name) {
		return name.replaceAll("([\\\\\\\"])", "\\$1");
	}

	/** Returns a label for the vertex. The nodes ID is used and transformed. */
	protected String makeLabel(IConQATGraphNode node) {

		String label;
		if (useNameAsLabel) {
			label = node.getName();
		} else {
			label = node.getId();
		}

		if (transformation != null) {
			label = transformation.applyTransformation(label);
		}
		return label.replaceAll("\\\\", "\\\\\\\\");
	}

}