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

import static org.conqat.lib.commons.string.StringUtils.CR;

import java.io.File;
import java.io.IOException;

import org.conqat.engine.commons.pattern.PatternTransformationList;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.graph.dot.ConQATGraphClusteredDOTConverter;
import org.conqat.engine.graph.dot.ConQATGraphDOTConverter;
import org.conqat.engine.graph.nodes.ConQATGraph;
import org.conqat.lib.commons.graph.EGraphvizOutputFormat;
import org.conqat.lib.commons.graph.GraphvizException;
import org.conqat.lib.commons.graph.GraphvizGenerator;

/**
 * Generate a graph from an {@link ConQATGraph} with DOT.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 76818A0E14164BE725FA8C3A03047BC7
 */
@AConQATProcessor(description = "Generate a graph from an {@link CQGraph} "
		+ "with DOT. It is recommended to use this "
		+ "class with the FilePresentation.")
public class GraphWriter extends InputFileWriterBase<ConQATGraph> {

	/** further DOT layout statements */
	private final StringBuilder additionalHeaders = new StringBuilder();

	/** Name transformation. */
	private PatternTransformationList transformation = null;

	/** Output format. */
	private EGraphvizOutputFormat format;

	/** Show clusters in graph? */
	private boolean showClusters = true;

	/** Set the output format. */
	@AConQATParameter(name = "output", minOccurrences = 1, maxOccurrences = 1, description = "The output format used.")
	public void setOutputFormat(
			@AConQATAttribute(name = "format", description = "Valid DOT output format.") EGraphvizOutputFormat format) {

		this.format = format;
	}

	/** Set the transformations for the graph node names. */
	@AConQATParameter(name = "transform", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Definition of transformations appllied to the node IDs before useing them as labels.")
	public void setAbbreviation(
			@AConQATAttribute(name = "list", description = "Transformation list") PatternTransformationList list) {

		transformation = list;
	}

	/** Enable left right orientation. */
	@AConQATParameter(name = "leftRightOrientation", minOccurrences = 0, maxOccurrences = 1, description = "Enable left right orientation")
	public void setLeftRightOrientation(
			@AConQATAttribute(name = "value", description = "If true, graph is "
					+ "layouted from left to right, otherwise from top to bottom") boolean leftRightOrientation) {

		if (leftRightOrientation) {
			additionalHeaders.append("    rankdir = \"LR\";" + CR);
		}
	}

	/** Add further layout information */
	@AConQATParameter(name = "dot", description = "Add header lines used by dot.")
	public void addHeaderLine(
			@AConQATAttribute(name = "header", description = "header line for the DOT source") String headerLine) {
		additionalHeaders.append(headerLine + CR);
	}

	/** Enable/disable clustering. */
	@AConQATParameter(name = "clusters", minOccurrences = 0, maxOccurrences = 1, description = "Enable/disable clustering")
	public void setShowClusters(
			@AConQATAttribute(name = "show", description = "Enable/disable clustering [true]") boolean showClusters) {
		this.showClusters = showClusters;
	}

	/** {@inheritDoc} */
	@Override
	protected void writeToFile(ConQATGraph input, File file)
			throws ConQATException, IOException {
		try {
			ConQATGraphDOTConverter exporter;
			if (showClusters) {
				exporter = new ConQATGraphClusteredDOTConverter();
			} else {
				exporter = new ConQATGraphDOTConverter();
			}

			exporter.setUseNameAsLabel(true);
			exporter.setTransformation(transformation);
			exporter.setAdditionalHeaders(additionalHeaders.toString());

			GraphvizGenerator generator = new GraphvizGenerator();
			generator.generateFile(exporter.convertToDot(input), file, format);
		} catch (GraphvizException e) {
			throw new ConQATException("IO problems during graph export: "
					+ e.getMessage());
		}
	}
}