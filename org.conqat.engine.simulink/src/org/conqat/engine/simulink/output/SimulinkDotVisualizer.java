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

import static org.conqat.lib.commons.string.StringUtils.CR;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.conqat.lib.commons.collections.IdentityHashSet;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.graph.dot.ConQATGraphDOTConverter;
import org.conqat.engine.simulink.clones.normalize.ISimulinkNormalizer;
import org.conqat.lib.simulink.model.SimulinkBlock;
import org.conqat.lib.simulink.model.SimulinkLine;
import org.conqat.lib.simulink.model.SimulinkModel;

/**
 * Code for converting a set of simulink blocks to DOT format.
 * 
 * @author hummelb
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: 2BAD6D793B151D42681A1666891B45D8
 */
public class SimulinkDotVisualizer {

	/** The header for DOT files. */
	private final static String DEFAULT_HEADER = ConQATGraphDOTConverter.HEADER
			+ "  rankdir = \"LR\";" + CR + "  charset = \"latin1\"" + CR;

	/** Counter used for ID generation. */
	private int counter;

	/** Determines whether to show the hierarchy (= clusters). */
	private final boolean showHierarchy;

	/** The normalizer used (optional). */
	private final ISimulinkNormalizer normalizer;

	/** Whether to suppress the name of the block. */
	private final boolean suppressName;

	/** Map for managing block ids. */
	private final Map<SimulinkBlock, String> blockIDs = new IdentityHashMap<SimulinkBlock, String>();

	/** The lines encountered. */
	private final Set<SimulinkLine> linesFound = new IdentityHashSet<SimulinkLine>();

	/** The string builder for creating the code. */
	private final StringBuilder sb = new StringBuilder();

	/** DOT header. */
	private final String header;

	/** Private constructor. */
	private SimulinkDotVisualizer(boolean showHierarchy, boolean suppressName,
			ISimulinkNormalizer normalizer, String dotHeader) {
		this.showHierarchy = showHierarchy;
		this.suppressName = suppressName;
		this.normalizer = normalizer;
		if (dotHeader == null) {
			header = DEFAULT_HEADER;
		} else {
			header = DEFAULT_HEADER + CR + dotHeader;
		}
	}

	/** Open the DOT graph. */
	private void startDot() {
		sb.append(header);
	}

	/**
	 * Dumps the given block to DOT format. This also makes its subblocks to be
	 * layouted. Subsystems are only visible (as clusters) if the hierarchy is
	 * shown ({@link #showHierarchy}). During this process all lines are
	 * collected as well.
	 */
	private void dumpBlock(SimulinkBlock block) throws ConQATException {

		String id = getBlockID(block);
		String label = determineBlockLabel(block);

		if (!block.hasSubBlocks()) {
			sb.append("  " + id + " [label=\"" + label + "\"];" + CR);
		} else {
			if (showHierarchy) {
				sb.append("  subgraph cluster" + ++counter + " {" + CR);
				sb.append("    label=\"" + label + "\";" + CR);
			}
			for (SimulinkBlock child : block.getSubBlocks()) {
				dumpBlock(child);
			}
			if (showHierarchy) {
				sb.append("}" + CR);
			}
		}

		for (SimulinkLine line : block.getInLines()) {
			linesFound.add(line);
		}
		for (SimulinkLine line : block.getOutLines()) {
			linesFound.add(line);
		}
	}

	/** Returns the label to be used for the block. */
	private String determineBlockLabel(SimulinkBlock block)
			throws ConQATException {
		String label = "";
		if (!suppressName) {
			label = ConQATGraphDOTConverter.dotEscape(block.getName()) + "\\n";
		}
		label += ConQATGraphDOTConverter.dotEscape(block.getResolvedType());

		if (normalizer != null) {
			label += " (" + normalizer.determineWeight(block) + ")";
		}
		return label;
	}

	/** Returns a unique ID for the given block. */
	private String getBlockID(SimulinkBlock block) {
		String id = blockIDs.get(block);
		if (id == null) {
			id = "block" + counter++;
			blockIDs.put(block, id);
		}
		return id;
	}

	/**
	 * Writes the given lines into the DOT string. The given attributes are
	 * included, but if the string is non-empty it should also contain the
	 * brackets. If one of the blocks for a line is a subsystem or has not been
	 * layouted before, the corresponding line is skipped.
	 */
	private void dumpLines(Collection<SimulinkLine> lines, String attributes) {
		for (SimulinkLine line : lines) {

			if (attributes == null) {
				attributes = "";
			}

			// skip lines connected to subsystems
			if (line.getSrcPort().getBlock().hasSubBlocks()
					|| line.getDstPort().getBlock().hasSubBlocks()) {
				continue;
			}

			String startId = blockIDs.get(line.getSrcPort().getBlock());
			String endId = blockIDs.get(line.getDstPort().getBlock());

			// only show, if corresponding blocks have been layouted before
			if (startId != null && endId != null) {
				sb.append("  " + startId + " -> " + endId + attributes + ";"
						+ CR);
			}
		}
	}

	/** Close the dot graph. */
	private void closeDot() {
		sb.append("}" + CR);
	}

	/**
	 * Returns the DOT code for the given simulink model.
	 * 
	 * @param model
	 *            The model to visualize.
	 * @param normalizer
	 *            the normalizer used for determining the weight of the blocks.
	 *            If this is null, the weights are not shown.
	 * @param showHierarchy
	 *            if this is true, subsystems will be layouted as clusters.
	 *            Otherwise subsystems are not shown at all.
	 * @param suppressName
	 *            is this is true, the name of the block is not included in the
	 *            labels and only the type is shown.
	 * @return the DOT representation of the model.
	 */
	public static String visualize(SimulinkModel model,
			ISimulinkNormalizer normalizer, boolean showHierarchy,
			boolean suppressName) throws ConQATException {

		SimulinkDotVisualizer vis = new SimulinkDotVisualizer(showHierarchy,
				suppressName, normalizer, null);
		vis.startDot();
		for (SimulinkBlock block : model.getSubBlocks()) {
			vis.dumpBlock(block);
		}
		vis.dumpLines(vis.linesFound, null);
		vis.closeDot();

		return vis.sb.toString();
	}

	/**
	 * Returns the DOT code for the given simulink blocks.
	 * 
	 * @param blocks
	 *            the blocks to be layouted.
	 * @param lines
	 *            an additional set of lines which is drawn in an extra color.
	 * @param normalizer
	 *            the normalizer used for determining the weight of the blocks.
	 *            If this is null, the weights are not shown.
	 * @param suppressName
	 *            is this is true, the name of the block is not included in the
	 *            labels and only the type is shown.
	 * @param drawImpliedLines
	 *            if this is true, all lines existing between any of the given
	 *            blocks will be shown. Otherwise only the lines explicitly
	 *            given are used.
	 * @return the DOT representation of the given blocks.
	 */
	public static String visualize(List<SimulinkBlock> blocks,
			List<SimulinkLine> lines, ISimulinkNormalizer normalizer,
			boolean suppressName, boolean drawImpliedLines)
			throws ConQATException {

		return visualize(blocks, lines, normalizer, suppressName,
				drawImpliedLines, null);
	}

	/**
	 * Returns the DOT code for the given simulink blocks.
	 * 
	 * @param blocks
	 *            the blocks to be layouted.
	 * @param lines
	 *            an additional set of lines which is drawn in an extra color.
	 * @param normalizer
	 *            the normalizer used for determining the weight of the blocks.
	 *            If this is null, the weights are not shown.
	 * @param suppressName
	 *            is this is true, the name of the block is not included in the
	 *            labels and only the type is shown.
	 * @param drawImpliedLines
	 *            if this is true, all lines existing between any of the given
	 *            blocks will be shown. Otherwise only the lines explicitly
	 *            given are used.
	 * @param dotHeader
	 *            additional header information to be included in the generated
	 *            dot file.
	 * @return the DOT representation of the given blocks.
	 */
	public static String visualize(List<SimulinkBlock> blocks,
			List<SimulinkLine> lines, ISimulinkNormalizer normalizer,
			boolean suppressName, boolean drawImpliedLines, String dotHeader)
			throws ConQATException {

		SimulinkDotVisualizer vis = new SimulinkDotVisualizer(false,
				suppressName, normalizer, dotHeader);
		vis.startDot();
		for (SimulinkBlock block : blocks) {
			vis.dumpBlock(block);
		}

		vis.dumpLines(lines, "[color=gray]");
		if (drawImpliedLines) {
			vis.linesFound.removeAll(lines);
			vis.dumpLines(lines, null);
		}
		vis.closeDot();

		return vis.sb.toString();
	}
}