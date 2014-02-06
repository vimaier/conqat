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

import java.awt.Color;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.conqat.lib.commons.collections.IdentityHashSet;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.html_presentation.util.HTMLLink;
import org.conqat.engine.simulink.clones.result.SimulinkClone;
import org.conqat.engine.simulink.clones.result.SimulinkCloneResultNode;
import org.conqat.lib.simulink.model.SimulinkBlock;
import org.conqat.lib.simulink.model.SimulinkModel;
import org.conqat.lib.simulink.util.SimulinkUtils;

/**
 * A processor for writing Matlab M-files which can be used to color the nodes
 * in this clone.
 * 
 * @author hummelb
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: 70845BD65D7B1AEC9EDB1A93049F8E04
 */
@AConQATProcessor(description = "This processor writes Matlab M-files which can "
		+ "be used to color the nodes of the clones in matlab.")
public class MatlabColorMFileWriter extends
		ConQATPipelineProcessorBase<SimulinkCloneResultNode> {

	/** The color used for uncolored blocks. */
	private static final String WHITE_COLOR = "white";

	/** The color used for parents of the "cloned" blocks. */
	private static final String HIERARCHY_COLOR = makeMatlabColor(Color.GRAY);

	/** The key used for storing the file. */
	@AConQATKey(description = "The M-file containing Matlab commands for coloring the clones.", type = "java.lang.String")
	public static final String MFILE_KEY = "color-file";

	/** The directory to write the files into. */
	private File outputDirectory;

	/** The maximal number of files being written. */
	private int maxWrites = Integer.MAX_VALUE;

	/** Set the output directory. */
	@AConQATParameter(name = "output", minOccurrences = 1, maxOccurrences = 1, description = "The directory to write the M-files into.")
	public void setOutputDirectory(
			@AConQATAttribute(name = "dir", description = "The name of the directory.")
			String dir) throws ConQATException {
		outputDirectory = new File(dir);
		try {
			FileSystemUtils.ensureDirectoryExists(outputDirectory);
		} catch (IOException e) {
			throw new ConQATException("Could not create output directory!", e);
		}
		if (!outputDirectory.isDirectory()) {
			throw new ConQATException("Could not create output directory!");
		}
	}

	/** Set the maximal number of writes. */
	@AConQATParameter(name = "writes", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Sets the maximal number of files written (to save disk space in case of many clones).")
	public void setMaxWrites(
			@AConQATAttribute(name = "max", description = "The maximal number of files (default is unlimited).")
			int maxWrites) {
		this.maxWrites = maxWrites;
	}

	/** {@inheritDoc} */
	@Override
	protected void processInput(SimulinkCloneResultNode input)
			throws ConQATException {
		NodeUtils.addToDisplayList(input, MFILE_KEY);

		for (SimulinkClone clone : input.getChildren()) {
			if (maxWrites <= 0) {
				break;
			}
			--maxWrites;

			String filename = getProcessorInfo().getName().replace('.', '_')
					+ "_" + clone.getId().replaceAll("\\s", "_") + ".m";
			File mFile = new File(outputDirectory, filename);
			try {
				writeMFile(clone, mFile);
			} catch (IOException e) {
				throw new ConQATException("Could not write "
						+ mFile.getAbsolutePath(), e);
			}
			clone.setValue(MFILE_KEY, new HTMLLink("m-file", filename));
		}
	}

	/** Writes the color commands for the given clone to the file. */
	private void writeMFile(SimulinkClone clone, File file) throws IOException {
		int size = clone.getBlockLists().size();
		String[] colors = generateColors(size);
		Map<SimulinkBlock, String> colorMap = new IdentityHashMap<SimulinkBlock, String>();
		Set<SimulinkModel> models = new IdentityHashSet<SimulinkModel>();
		for (int i = 0; i < size; ++i) {
			UnmodifiableList<SimulinkBlock> blocks = clone.getBlockLists().get(
					i);
			models.add(blocks.get(0).getModel());
			for (SimulinkBlock block : blocks) {
				colorMap.put(block, colors[i]);
			}
			for (SimulinkBlock block : SimulinkUtils.calculateParentSet(blocks)) {
				if (!colorMap.containsKey(block)) {
					colorMap.put(block, HIERARCHY_COLOR);
				}
			}
		}

		Set<SimulinkBlock> all = new IdentityHashSet<SimulinkBlock>();
		for (SimulinkModel model : models) {
			all.addAll(SimulinkUtils.listBlocksDepthFirst(model));
			all.remove(model);
		}

		dumpColorMFile(file, all, colorMap);
	}

	/**
	 * Creates the m-file according to a list of all nodes (which are white by
	 * default) and a color lookup map.
	 */
	private void dumpColorMFile(File mFile, Set<SimulinkBlock> allBlocks,
			Map<SimulinkBlock, String> colorMap) throws IOException {
		PrintWriter pw = new PrintWriter(new FileWriter(mFile));
		for (SimulinkBlock block : allBlocks) {
			String color = colorMap.get(block);
			if (color == null) {
				color = WHITE_COLOR;
			}
			pw.println("set_param(sprintf('" + block.getId()
					+ "'), 'BackgroundColor', '" + color + "');");
		}
		pw.close();
	}

	/** Generate the given number of matlab colors. */
	private static String[] generateColors(int num) {
		String[] result = new String[num];
		for (int i = 0; i < num; ++i) {
			result[i] = makeMatlabColor(Color
					.getHSBColor((float) i / num, 1, 1));
		}
		return result;
	}

	/**
	 * Creates a color string ,which can be used in Matlab, from the given
	 * color.
	 */
	private static String makeMatlabColor(Color color) {
		return String.format(Locale.US, "[%.6f, %.6f, %.6f]",
				color.getRed() / 255., color.getGreen() / 255.,
				color.getBlue() / 255.);
	}
}