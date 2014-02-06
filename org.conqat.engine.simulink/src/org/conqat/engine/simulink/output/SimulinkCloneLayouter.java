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
package org.conqat.engine.simulink.output;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.conqat.lib.commons.collections.UnmodifiableList;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.graph.EGraphvizOutputFormat;
import org.conqat.lib.commons.graph.GraphvizException;
import org.conqat.lib.commons.graph.GraphvizGenerator;
import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.html_presentation.HTMLPresentation;
import org.conqat.engine.html_presentation.util.HTMLLink;
import org.conqat.engine.html_presentation.util.ResourcesManager;
import org.conqat.engine.simulink.clones.normalize.ISimulinkNormalizer;
import org.conqat.engine.simulink.clones.result.SimulinkClone;
import org.conqat.engine.simulink.clones.result.SimulinkCloneResultNode;
import org.conqat.lib.simulink.model.SimulinkBlock;
import org.conqat.lib.simulink.model.SimulinkLine;

/**
 * This processor layouts all clones as small graphs. If the resulting tree is
 * layouted as a table, the graphs are shown as links.
 * 
 * @author hummelb
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: C1334CB2404D3A76EDC544CEC4A2D683
 */
@AConQATProcessor(description = "This processor layouts all clones as small graphs. "
		+ "If the resulting tree is layouted as a table, the graphs are shown as links.")
public class SimulinkCloneLayouter extends
		ConQATPipelineProcessorBase<SimulinkCloneResultNode> {

	/** The list of links pointing to the layouted graphs. */
	@AConQATKey(description = "The list of links pointing to the layouted graphs.", type = "java.util.List<HTMLLink>")
	public static final String GRAPH_KEY = "Layouted Clone";

	/** The output directory used. */
	private File outputDirectory;

	/** Maximal number of graphs to layout. */
	private int maxDraw = Integer.MAX_VALUE;

	/** The normalizer used (if any). */
	private ISimulinkNormalizer normalizer = null;

	/**
	 * Set output directory. Must be the same as specified for
	 * {@link HTMLPresentation}.
	 */
	@AConQATParameter(name = "output", minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "Output directory; should be the same as specified for HTMLPresentation.")
	public void setOutputDirectory(
			@AConQATAttribute(name = "dir", description = "Name of the output directory")
			String outputDirectoryName) {

		outputDirectory = new File(outputDirectoryName);
	}

	/** Set additional dot parameters. */
	@AConQATParameter(name = "dot", maxOccurrences = 1, description = ""
			+ "Limit number of DOT runs, if there are many clones.")
	public void setMaxDotRuns(
			@AConQATAttribute(name = "runs", description = "The max amount of dot runs (default is unlimited)")
			int runs) {
		maxDraw = runs;
	}

	/** Set normalization. */
	@AConQATParameter(name = "norm", maxOccurrences = 1, description = ""
			+ "If provided this normalizer is used to append the weight to the block labels.")
	public void setNormalizer(
			@AConQATAttribute(name = "ref", description = "Reference to the normalizer")
			ISimulinkNormalizer normalizer) {
		this.normalizer = normalizer;
	}

	/** Layout all clones. */
	@Override
	protected void processInput(SimulinkCloneResultNode input)
			throws ConQATException {
		NodeUtils.addToDisplayList(input, GRAPH_KEY);

		for (SimulinkClone clone : input.getChildren()) {
			int count = clone.getBlockLists().size();
			if (maxDraw < count) {
				break;
			}
			maxDraw -= count;
			layoutClone(clone);
		}
	}

	/** Layout the given clone and append link list to value map. */
	private void layoutClone(SimulinkClone clone) throws ConQATException {
		List<HTMLLink> links = new ArrayList<HTMLLink>();

		for (int i = 0; i < clone.getBlockLists().size(); ++i) {

			String filename = getProcessorInfo().getName() + "-"
					+ clone.getId() + "-" + i + "."
					+ EGraphvizOutputFormat.PNG.getFileExtension();

			writeSimulinkClone(FileSystemUtils.newFile(outputDirectory,
					ResourcesManager.IMAGES_DIRECTORY_NAME, filename), clone
					.getBlockLists().get(i), clone.getLineLists().get(i));

			links.add(new HTMLLink("Clone " + i,
					ResourcesManager.IMAGES_DIRECTORY_NAME + "/" + filename));
		}

		clone.setValue(GRAPH_KEY, links);
	}

	/**
	 * Writes the simulink clone defined by given lists of blocks and lines to
	 * the provided file.
	 */
	private void writeSimulinkClone(File file,
			UnmodifiableList<SimulinkBlock> blocks,
			UnmodifiableList<SimulinkLine> lines) throws ConQATException {

		try {
			FileSystemUtils.ensureParentDirectoryExists(file);
			String dotSource = SimulinkDotVisualizer.visualize(blocks, lines,
					normalizer, true, true);
			new GraphvizGenerator().generateFile(dotSource, file,
					EGraphvizOutputFormat.PNG);
		} catch (IOException e) {
			getLogger().warn(e);
		} catch (GraphvizException e) {
			getLogger().warn(e);
		}
	}
}