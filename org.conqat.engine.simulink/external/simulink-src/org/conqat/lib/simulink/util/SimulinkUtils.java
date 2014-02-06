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
package org.conqat.lib.simulink.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.assertion.PreconditionException;
import org.conqat.lib.commons.collections.IdentityHashSet;
import org.conqat.lib.commons.error.NeverThrownRuntimeException;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.commons.visitor.IVisitor;
import org.conqat.lib.simulink.model.ParameterizedElement;
import org.conqat.lib.simulink.model.SimulinkBlock;
import org.conqat.lib.simulink.model.SimulinkConstants;
import org.conqat.lib.simulink.model.SimulinkInPort;
import org.conqat.lib.simulink.model.SimulinkLine;
import org.conqat.lib.simulink.model.SimulinkModel;
import org.conqat.lib.simulink.model.SimulinkOutPort;
import org.conqat.lib.simulink.model.SimulinkPortBase;
import org.conqat.lib.simulink.model.stateflow.IStateflowElement;
import org.conqat.lib.simulink.model.stateflow.IStateflowNodeContainer;
import org.conqat.lib.simulink.model.stateflow.StateflowBlock;
import org.conqat.lib.simulink.model.stateflow.StateflowChart;
import org.conqat.lib.simulink.model.stateflow.StateflowMachine;
import org.conqat.lib.simulink.model.stateflow.StateflowNodeBase;
import org.conqat.lib.simulink.model.stateflow.StateflowState;
import org.conqat.lib.simulink.model.stateflow.StateflowTarget;

/**
 * Collection of utility methods for Simulink models.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 674FCA6D2098D1D04D92C787F8E773CF
 */
public class SimulinkUtils {

	/** Visitor that stores all blocks in a id->block map. */
	private static class MapVisitor implements
			IVisitor<SimulinkBlock, NeverThrownRuntimeException> {
		/** Maps from block id to block. */
		private final HashMap<String, SimulinkBlock> map = new HashMap<String, SimulinkBlock>();

		/** Visit block */
		@Override
		public void visit(SimulinkBlock block) {
			map.put(block.getId(), block);
		}
	}

	/** Copy parameters from one parameterized element to another. */
	public static void copyParameters(ParameterizedElement source,
			ParameterizedElement target) {
		for (String name : source.getParameterNames()) {
			target.setParameter(name, source.getParameter(name));
		}
	}

	/** Create map that maps from id to block. */
	public static Map<String, SimulinkBlock> createIdToNodeMap(
			SimulinkBlock block) {
		MapVisitor visitor = new MapVisitor();
		visitDepthFirst(block, visitor);
		return visitor.map;
	}

	/** Replaces forward slashes by double forward slashes. */
	public static String escape(String string) {
		return string.replace("/", "//");
	}

	/**
	 * Get Simulink array parameter as array. This raises a
	 * {@link NumberFormatException} if the elements of the array are not
	 * integers.
	 */
	public static int[] getIntParameterArray(String parameter) {
		String[] parts = getStringParameterArray(parameter);
		int[] result = new int[parts.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = Integer.parseInt(parts[i]);
		}
		return result;
	}

	/** Get Simulink array parameter as array. */
	public static String[] getStringParameterArray(String parameter) {
		// remove brackets
		String content = parameter.substring(1, parameter.length() - 1);
		if (StringUtils.isEmpty(content)) {
			return new String[0];
		}
		return content.split("[,;] *");
	}

	/** Checks if a block is a target link block. */
	public static boolean isTargetlinkBlock(SimulinkBlock node) {
		return node.getType().equals(SimulinkConstants.TYPE_Reference)
				&& node.getParameter(SimulinkConstants.PARAM_SourceType)
						.startsWith("TL_");
	}

	/** Split full qualified identifier. */
	public static List<String> splitSimulinkId(String string) {
		ArrayList<String> result = new ArrayList<String>();

		// Simulink names cannot start or end with a slash
		Pattern pattern = Pattern.compile("[^/]/[^/]");
		Matcher matcher = pattern.matcher(string);

		int begin = 0;
		while (matcher.find(begin)) {
			result.add(removeEscapes(string.substring(begin,
					matcher.start() + 1)));
			// pattern is one character longer than the slash
			begin = matcher.end() - 1;
		}
		result.add(removeEscapes(string.substring(begin)));

		return result;
	}

	/**
	 * Create Simulink id from a iteration of names. This takes care of proper
	 * escaping.
	 * 
	 * @throws PreconditionException
	 *             if one of names starts or ends with a slash
	 */
	public static String createSimulinkId(Iterable<String> names) {
		StringBuilder result = new StringBuilder();
		Iterator<String> it = names.iterator();
		while (it.hasNext()) {
			String name = it.next();
			CCSMPre.isFalse(name.startsWith("/") || name.endsWith("/"),
					"Simulink names cannot start or end with a slash.");
			result.append(escape(name));
			if (it.hasNext()) {
				result.append("/");
			}
		}
		return result.toString();
	}

	/**
	 * Visit blocks in a depth first manner.
	 * 
	 * @param <X>
	 *            Type of exception thrown by the visitor.
	 * @param block
	 *            block to start with
	 * @param visitor
	 *            the visitor
	 * @throws X
	 *             exception thrown by the visitor.
	 */
	public static <X extends Exception> void visitDepthFirst(
			SimulinkBlock block, IVisitor<SimulinkBlock, X> visitor) throws X {
		visitor.visit(block);
		if (!block.hasSubBlocks()) {
			return;
		}
		for (SimulinkBlock child : block.getSubBlocks()) {
			visitDepthFirst(child, visitor);
		}
	}

	/** Replace double forward slashes by single forward slashes */
	private static String removeEscapes(String name) {
		return name.replace("//", "/");
	}

	/** Returns all recursively reachable subblocks of the given block. */
	public static List<SimulinkBlock> listBlocksDepthFirst(SimulinkBlock block) {
		final List<SimulinkBlock> result = new ArrayList<SimulinkBlock>();
		SimulinkUtils.visitDepthFirst(block,
				new IVisitor<SimulinkBlock, NeverThrownRuntimeException>() {
					@Override
					public void visit(SimulinkBlock block) {
						result.add(block);
					}
				});
		return result;
	}

	/**
	 * Calculate the set of all parent blocks up to the model for the given
	 * blocks.
	 */
	public static Set<SimulinkBlock> calculateParentSet(
			Collection<SimulinkBlock> blocks) {

		Set<SimulinkBlock> parents = new IdentityHashSet<SimulinkBlock>();
		if (blocks.isEmpty()) {
			return parents;
		}

		for (SimulinkBlock block : blocks) {
			SimulinkModel model = block.getModel();
			while (block != model) {
				parents.add(block);
				block = block.getParent();
			}
		}

		return parents;
	}

	/** Recursively count sub blocks. */
	public static int countSubBlocks(SimulinkBlock block) {
		BlockCounter counter = new BlockCounter();
		SimulinkUtils.visitDepthFirst(block, counter);
		// minus the root block
		return counter.blockCount - 1;
	}

	/** Recursively count lines. */
	public static int countLines(SimulinkBlock block) {
		BlockCounter counter = new BlockCounter();
		for (SimulinkBlock child : block.getSubBlocks()) {
			SimulinkUtils.visitDepthFirst(child, counter);
		}
		return counter.lineCount;
	}

	/** Recursively count Stateflow states. */
	public static int countStates(IStateflowNodeContainer<?> node) {
		int count = 0;
		if (node instanceof StateflowState) {
			count = 1;
		} else {
			count = 0;
		}

		for (StateflowNodeBase element : node.getNodes()) {
			if (element instanceof IStateflowNodeContainer<?>) {
				count += countStates((IStateflowNodeContainer<?>) element);
			}
		}
		return count;
	}

	/** Count states of all charts of the machine. */
	public static int countStates(StateflowMachine stateflowMachine) {
		int stateCount = 0;
		for (StateflowChart chart : stateflowMachine.getCharts()) {
			stateCount += countStates(chart);
		}
		return stateCount;
	}

	/**
	 * Get the Stateflow chart a Stateflow element belongs to.
	 * 
	 * @return the Stateflow chart or <code>null</code> if the element is
	 *         unconnected or not associated with a chart, e.g.
	 *         {@link StateflowTarget}.
	 */
	public static StateflowChart getChart(IStateflowElement<?> element) {
		if (element instanceof StateflowChart) {
			return (StateflowChart) element;
		}
		IStateflowElement<?> parent = element.getParent();
		if (parent == null) {
			return null;
		}
		return getChart(parent);
	}

	/**
	 * Get the Stateflow block a Stateflow element belongs to.
	 * 
	 * @return the Stateflow block or <code>null</code> if the element is
	 *         unconnected or not associated with a chart, e.g.
	 *         {@link StateflowTarget}.
	 */
	public static StateflowBlock getBlock(IStateflowElement<?> element) {
		StateflowChart chart = getChart(element);
		if (chart == null) {
			return null;
		}
		return chart.getStateflowBlock();
	}

	/**
	 * Get name of a Stateflow state as defined in the Stateflow manual. As
	 * Stateflow awkwardly stores the names as part of the label, this is put in
	 * a utility methods and not directly at class {@link StateflowState}.
	 */
	public static String getStateName(StateflowState state) {
		String label = state.getLabel();
		if (StringUtils.isEmpty(label)) {
			return null;
		}
		String name = label.split("\\\\n")[0];

		// State names MAY end with a slash
		if (name.length() > 1 && name.endsWith("/")) {
			name = name.substring(0, name.length() - 1);
		}
		return name;
	}

	/**
	 * Get full qualified state name. This is deliberately not part of class
	 * {@link StateflowState} as names of Stateflow derives names from the state
	 * labels.
	 */
	public static String getFQStateName(StateflowState state) {
		String name = getStateName(state);
		IStateflowNodeContainer<?> parent = state.getParent();
		if (parent == null) {
			return name;
		}
		if (parent instanceof StateflowChart) {
			StateflowChart chart = (StateflowChart) parent;
			return chart.getStateflowBlock().getId() + "/" + name;
		}

		// Can be only a state
		return getFQStateName((StateflowState) parent) + "." + name;
	}

	/**
	 * Obtain out port block that is below the a Stateflow block and describes
	 * the output of a Stateflow chart.
	 * 
	 * What Simulink displays like an atomic Stateflow chart is internally
	 * represented as a Simulink sub system that itself contains multiple
	 * blocks. The sub system itself has a normal inport/outport which has only
	 * a number (as (almost) all ports of Simulink sub systems do). However the
	 * sub system contains a block of <b>type</b> Inport/Outport (quite
	 * confusing...) and this is the one the carries the name of the Stateflow
	 * output. Note that this is related to a past CR described at
	 * <https://bugzilla.informatik.tu-muenchen.de/show_bug.cgi?id=1502>.
	 * 
	 * The code is the following:
	 * <ul>
	 * <li>iterate over all child blocks of the sub system that represents the
	 * Stateflow chart
	 * <li>pick the one that is of type Inport/Outport and that has the same
	 * port index as the inport/outport of the sub system (the index defines the
	 * mapping between the Inport/Ouport block and the actual inport/outport)
	 * </ul>
	 */
	public static SimulinkBlock getStateflowOutport(SimulinkOutPort outPort) {
		CCSMPre.isInstanceOf(outPort.getBlock(), StateflowBlock.class);
		SimulinkBlock result = null;
		for (SimulinkBlock block : outPort.getBlock().getSubBlocks()) {
			if (SimulinkConstants.TYPE_Outport.equals(block.getType())
					&& block.getParameter(SimulinkConstants.PARAM_Port).equals(
							outPort.getIndex())) {
				CCSMAssert.isTrue(result == null,
						"We assummed that there is only one matching port.");
				result = block;
			}
		}
		return result;
	}

	/**
	 * Obtain in port. See {@link #getStateflowOutport(SimulinkOutPort)} for
	 * details.
	 */
	public static SimulinkBlock getStateflowInport(SimulinkInPort inPort) {
		CCSMPre.isInstanceOf(inPort.getBlock(), StateflowBlock.class);
		SimulinkBlock result = null;
		for (SimulinkBlock block : inPort.getBlock().getSubBlocks()) {
			if (SimulinkConstants.TYPE_Inport.equals(block.getType())
					&& block.getParameter(SimulinkConstants.PARAM_Port).equals(
							inPort.getIndex())) {
				CCSMAssert.isTrue(result == null,
						"We assummed that there is only one matching port.");
				result = block;
			}
		}
		return result;
	}

	/** Visitor for counting sub blocks. */
	private static class BlockCounter implements
			IVisitor<SimulinkBlock, NeverThrownRuntimeException> {
		/** Counter for blocks. */
		private int blockCount = 0;

		/** Counter for lines. */
		private int lineCount = 0;

		/** Count block. */
		@Override
		public void visit(SimulinkBlock element) {
			blockCount++;
			lineCount += element.getOutLines().size();
		}
	}

	/**
	 * This method checks if two block have the same syntactic interface, i.e.
	 * the same input and output ports.
	 */
	public static boolean checkCompatibility(SimulinkBlock block1,
			SimulinkBlock block2) {
		boolean inPortsEqual = obtainPortIndexes(block1.getInPorts()).equals(
				obtainPortIndexes(block2.getInPorts()));
		boolean outPortsEqual = obtainPortIndexes(block1.getOutPorts()).equals(
				obtainPortIndexes(block2.getOutPorts()));
		return inPortsEqual && outPortsEqual;
	}

	/** Creates a set of the indexes of the provided ports. */
	private static HashSet<String> obtainPortIndexes(
			Collection<? extends SimulinkPortBase> ports) {
		HashSet<String> indexes = new HashSet<String>();
		for (SimulinkPortBase port : ports) {
			indexes.add(port.getIndex());
		}
		return indexes;
	}

	/**
	 * Replaces this line with another line, copying the given parameter values.
	 * This method removes {@code line} and creates a new line with the given
	 * source and target ports.
	 * 
	 * @param line
	 *            The line to replace
	 * @param srcPort
	 *            The source port of the new line
	 * @param dstPort
	 *            The destination port of the new line
	 * @param params
	 *            The parameter that should be copied from {@code line} to the
	 *            new line
	 */
	public static SimulinkLine replaceLine(SimulinkLine line,
			SimulinkOutPort srcPort, SimulinkInPort dstPort, String... params) {

		// Save the parameters, as they are not accessible once the line is
		// removed
		Map<String, String> paramValues = new HashMap<String, String>();
		for (String param : params) {
			String value = line.getParameter(param);
			paramValues.put(param, value);
		}

		// We have first to remove line before creating the replacement, as the
		// lines are not allowed to share an in-port
		line.remove();

		SimulinkLine newLine = new SimulinkLine(srcPort, dstPort);

		// Set the parameter values
		for (String param : params) {
			String value = paramValues.get(param);
			if (value != null) {
				newLine.setParameter(param, value);
			}
		}
		return newLine;
	}
}