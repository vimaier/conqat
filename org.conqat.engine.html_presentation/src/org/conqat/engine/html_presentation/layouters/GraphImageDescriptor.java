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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;

import org.conqat.engine.commons.pattern.PatternTransformationList;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.graph.dot.ConQATGraphClusteredDOTConverter;
import org.conqat.engine.graph.dot.ConQATGraphDOTConverter;
import org.conqat.engine.graph.nodes.ConQATGraph;
import org.conqat.engine.html_presentation.image.IImageDescriptor;
import org.conqat.engine.html_presentation.image.ITooltipDescriptor;
import org.conqat.engine.html_presentation.image.ImageDescriptorBase;
import org.conqat.engine.html_presentation.image.ImageDescriptorUtils;
import org.conqat.lib.commons.graph.GraphvizException;
import org.conqat.lib.commons.graph.GraphvizGenerator;

/**
 * Image descriptor for DOT graphs. If the generated DOT graph is larger than
 * the specified drawing width, the image is scaled.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: D6F503A8E85E0A54286EB7D7B27BEA47
 */
public class GraphImageDescriptor extends ImageDescriptorBase {

	/** Name of the graph icon: {@value} */
	public static final String GRAPH_ICON_NAME = "graph.gif";

	/** The graph to display. */
	private final ConQATGraph graph;

	/** Show clusters in graph? */
	private final boolean showClusters;

	/** Name transformation. */
	private final PatternTransformationList transformation;

	/** Further DOT layout statements. */
	private final String additionalDotHeaders;

	/** Key in which colors for nodes and edges are stored. */
	private final String colorKey;

	/** The path the dot executable */
	private final String dotExecutablePath;

	/**
	 * Constructor.
	 * 
	 * @param graph
	 *            the graph to display.
	 * @param showClusters
	 *            show clusters in graph?
	 * @param transformation
	 *            Name transformation.
	 * @param additionalDotHeaders
	 *            further DOT layout statements
	 * @param colorKey
	 *            key in which colors for nodes and edges are stored.
	 * @param dotExecutablePath
	 *            path to dot executable, if <code>null</code>, dot must be on
	 *            the path
	 */
	public GraphImageDescriptor(ConQATGraph graph, boolean showClusters,
			PatternTransformationList transformation,
			String additionalDotHeaders, String colorKey,
			String dotExecutablePath) {
		super(GRAPH_ICON_NAME);
		this.graph = graph;
		this.showClusters = showClusters;
		this.transformation = transformation;
		this.additionalDotHeaders = additionalDotHeaders;
		this.colorKey = colorKey;
		this.dotExecutablePath = dotExecutablePath;
	}

	/** {@inheritDoc} */
	@Override
	public Dimension getPreferredSize() throws ConQATException {
		BufferedImage image = createImage();
		return new Dimension(image.getWidth(), image.getHeight());
	}

	/** {@inheritDoc} */
	@Override
	public void draw(Graphics2D graphics, int width, int height)
			throws ConQATException {
		ImageDescriptorUtils.initGraphics(graphics);
		graphics.setPaint(Color.WHITE);
		graphics.fillRect(0, 0, width, height);
		BufferedImage image = createImage();
		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);

		graphics.drawImage(
				image,
				ImageDescriptorUtils.adjust(width, height,
						new Rectangle(image.getWidth(), image.getHeight())),
				null);
	}

	/** Create graph image. */
	private BufferedImage createImage() throws ConQATException {
		ConQATGraphDOTConverter exporter;
		if (showClusters) {
			exporter = new ConQATGraphClusteredDOTConverter();
		} else {
			exporter = new ConQATGraphDOTConverter();
		}
		exporter.setTransformation(transformation);
		exporter.setAdditionalHeaders(additionalDotHeaders);
		exporter.setColorKey(colorKey);

		String dotSource = exporter.convertToDot(graph);

		GraphvizGenerator generator = createGraphvizGenerator();

		try {
			return generator.generateImage(dotSource);
		} catch (IOException e) {
			throw new ConQATException("Could not write to graph file: "
					+ e.getMessage(), e);
		} catch (GraphvizException e) {
			throw new ConQATException("Could not create graph: "
					+ e.getMessage(), e);
		}
	}

	/** Creates the {@link GraphvizGenerator}. */
	private GraphvizGenerator createGraphvizGenerator() {
		if (dotExecutablePath == null) {
			return new GraphvizGenerator();
		}
		return new GraphvizGenerator(dotExecutablePath);
	}

	/** Returns null. */
	@Override
	public ITooltipDescriptor<Object> getTooltipDescriptor(int width, int height) {
		return null;
	}

	/**
	 * Returns false.
	 * <p>
	 * Note: Principally, we could support a vector format as DOT can render to
	 * SVG. However, this is not easy to implement with the
	 * {@link IImageDescriptor}-interface as we would need to make the
	 * distinction based on the concrete type of the {@link Graphics2D} object
	 * provided to {@link #draw(Graphics2D, int, int)}.
	 */
	@Override
	public boolean isVectorFormatSupported() {
		return false;
	}

}