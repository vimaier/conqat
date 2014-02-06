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
package org.conqat.lib.simulink.model;

import static org.conqat.lib.simulink.model.SimulinkConstants.PARAM_BlockType;
import static org.conqat.lib.simulink.model.SimulinkConstants.PARAM_Points;
import static org.conqat.lib.simulink.model.SimulinkConstants.PARAM_SourceType;
import static org.conqat.lib.simulink.model.SimulinkConstants.TYPE_Reference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.assertion.PreconditionException;
import org.conqat.lib.commons.clone.DeepCloneException;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.IdentityHashSet;
import org.conqat.lib.commons.collections.UnmodifiableCollection;
import org.conqat.lib.commons.collections.UnmodifiableSet;
import org.conqat.lib.simulink.builder.SimulinkModelBuildingException;
import org.conqat.lib.simulink.util.SimulinkUtils;

/**
 * A Simulink block has a type and maintains a parameter map, a list of sub
 * blocks, a list of annotations and in/out-ports.
 * 
 * @author hummelb
 * @author $Author: deissenb $
 * @version $Rev: 35225 $
 * @ConQAT.Rating GREEN Hash: FF48628ADCC4457361F2C68D65907060
 */
public class SimulinkBlock extends SimulinkElementBase {

	/** The subBlocks of this block indexed by name. */
	private final HashMap<String, SimulinkBlock> subBlocks = new HashMap<String, SimulinkBlock>();

	/** Inports of this block indexed by port index. */
	private final HashMap<String, SimulinkInPort> inPorts = new HashMap<String, SimulinkInPort>();

	/** Outports of this block indexed by port index. */
	private final HashMap<String, SimulinkOutPort> outPorts = new HashMap<String, SimulinkOutPort>();

	/** Annotations of this block. */
	private final IdentityHashSet<SimulinkAnnotation> annotations = new IdentityHashSet<SimulinkAnnotation>();

	/** Create new Simulink block. */
	public SimulinkBlock() {
		super();
	}

	/**
	 * Copy constructor. This is used from the {@link SimulinkModel} during
	 * cloning.
	 */
	protected SimulinkBlock(SimulinkBlock origBlock) throws DeepCloneException {
		super(origBlock);

		for (SimulinkInPort inPort : origBlock.inPorts.values()) {
			new SimulinkInPort(this, inPort.getIndex());
		}

		for (SimulinkOutPort outPort : origBlock.outPorts.values()) {
			new SimulinkOutPort(this, outPort.getIndex());
		}

		for (SimulinkAnnotation annotation : origBlock.annotations) {
			addAnnotation(annotation.deepClone());
		}

		// Recursively deep clone sub blocks
		for (SimulinkBlock subBlock : origBlock.subBlocks.values()) {
			addSubBlock(subBlock.deepClone());
		}

		cloneLines(origBlock);
	}

	/** Add an annotation. */
	public void addAnnotation(SimulinkAnnotation annotation) {
		annotations.add(annotation);
		annotation.setParent(this);
	}

	/** Adds a sub block. */
	public void addSubBlock(SimulinkBlock subBlock) {
		CCSMPre.isTrue(subBlock.getParent() == null,
				"May not add block which already has a parent!");
		subBlock.setParent(this);

		CCSMPre.isFalse(subBlocks.containsKey(subBlock.getName()),
				"Block already has a sub block called: " + subBlock.getName());
		subBlocks.put(subBlock.getName(), subBlock);
	}

	/** Get annotations. */
	public UnmodifiableSet<SimulinkAnnotation> getAnnotations() {
		return CollectionUtils.asUnmodifiable(annotations);
	}

	/**
	 * Get all incoming lines of this block.
	 */
	public List<SimulinkLine> getInLines() {
		ArrayList<SimulinkLine> inLines = new ArrayList<SimulinkLine>();

		for (SimulinkInPort inPort : getInPorts()) {
			if (inPort.getLine() != null) {
				inLines.add(inPort.getLine());
			}
		}
		return inLines;
	}

	/**
	 * Get inport by index or <code>null</code> if no inport with this index was
	 * found.
	 */
	public SimulinkInPort getInPort(String portIndex) {
		return inPorts.get(portIndex);
	}

	/** Returns the inports this block. */
	public UnmodifiableCollection<SimulinkInPort> getInPorts() {
		return CollectionUtils.asUnmodifiable(inPorts.values());
	}

	/**
	 * Get all outgoing lines of this block.
	 */
	public List<SimulinkLine> getOutLines() {
		ArrayList<SimulinkLine> outLines = new ArrayList<SimulinkLine>();

		for (SimulinkOutPort outPort : outPorts.values()) {
			outLines.addAll(outPort.getLines());
		}
		return outLines;
	}

	/**
	 * Get outport by index or <code>null</code> if no outport with this index
	 * was found.
	 */
	public SimulinkOutPort getOutPort(String portIndex) {
		return outPorts.get(portIndex);
	}

	/** Returns the outport of this block. */
	public UnmodifiableCollection<SimulinkOutPort> getOutPorts() {
		return CollectionUtils.asUnmodifiable(outPorts.values());
	}

	/**
	 * If this block is of type 'Reference' this returns
	 * <code>Reference.&lt;source type of the reference&gt;</code>. Otherwise
	 * this just returns the type of the block.
	 */
	public String getResolvedType() {
		String type = getType();
		if (TYPE_Reference.equals(type)) {
			String sourceBlock = getParameter(PARAM_SourceType);
			if (sourceBlock == null) {
				return type;
			}
			return TYPE_Reference + "." + sourceBlock;
		}
		return type;
	}

	/**
	 * Get named sub block or <code>null</code> if no sub block with the given
	 * name is present.
	 */
	public SimulinkBlock getSubBlock(String name) {
		return subBlocks.get(name);
	}

	/** Returns the sub blocks of this block. */
	public UnmodifiableCollection<SimulinkBlock> getSubBlocks() {
		return CollectionUtils.asUnmodifiable(subBlocks.values());
	}

	/** Returns the type. */
	public String getType() {
		// We have to access the super class here as we do not want any defaults
		// (infinite recursion!)
		return getDeclaredParameter(PARAM_BlockType);
	}

	/** Returns whether this block has subBlocks. */
	public boolean hasSubBlocks() {
		return !subBlocks.isEmpty();
	}

	/**
	 * Unlinks this object from the simulink tree. Also removes all sub-blocks,
	 * ports and annotations
	 */
	@Override
	public void remove() {

		for (SimulinkBlock subBlock : new ArrayList<SimulinkBlock>(
				subBlocks.values())) {
			subBlock.remove();
		}

		for (SimulinkOutPort outPort : new ArrayList<SimulinkOutPort>(
				getOutPorts())) {
			outPort.remove();
		}

		for (SimulinkInPort inPort : new ArrayList<SimulinkInPort>(getInPorts())) {
			inPort.remove();
		}

		for (SimulinkAnnotation annotation : new ArrayList<SimulinkAnnotation>(
				annotations)) {
			annotation.remove();
		}

		super.remove();
	}

	/**
	 * Unlinks this object from the simulink tree. Unlike {@link #remove()} does
	 * not unlink sub-blocks, ports and annotations.
	 * 
	 * 
	 * @see SimulinkElementBase#remove
	 */
	public void detach() {
		super.remove();
	}

	/** Get string representation of this block. */
	@Override
	public String toString() {
		return getId() + " [" + getType() + ", " + inPorts.size() + ":"
				+ outPorts.size() + "]";
	}

	/**
	 * Creates a deep clone of this block. Please note that is possible to clone
	 * a single block but the resulting block will behave not properly as it
	 * does not belong to {@link SimulinkModel}. Therefore it is strongly
	 * recommended to deep clone only whole models.
	 */
	@Override
	public SimulinkBlock deepClone() throws DeepCloneException {
		return new SimulinkBlock(this);
	}

	/**
	 * Add a inport to this block.
	 * 
	 * @throws PreconditionException
	 *             if the port does not belong to this block or a port with the
	 *             same index was defined before.
	 */
	/* package */void addInPort(SimulinkInPort inPort)
			throws IllegalArgumentException {
		CCSMPre.isTrue(inPort.getBlock() == this,
				"Port does not belong to block.");
		CCSMPre.isFalse(inPorts.containsKey(inPort.getIndex()),
				"Port with index " + inPort.getIndex() + " already defined.");

		inPorts.put(inPort.getIndex(), inPort);
	}

	/**
	 * Add a outport to this block.
	 * 
	 * @throws PreconditionException
	 *             if the port does not belong to this block or a port with the
	 *             same index was defined before.
	 */
	/* package */void addOutPort(SimulinkOutPort outPort)
			throws IllegalArgumentException {
		CCSMPre.isTrue(outPort.getBlock() == this,
				"Port does not belong to block.");
		CCSMPre.isFalse(outPorts.containsKey(outPort.getIndex()),
				"Port with index " + outPort.getIndex() + " already defined.");

		outPorts.put(outPort.getIndex(), outPort);
	}

	/**
	 * Clone all lines contained in the given block or one of its descendant
	 * blocks. This is usually called by {@link #deepClone()} after copying the
	 * block using the copy constructor. The lines cloned is the maximal set of
	 * lines which could be cloned without connecting to some parent.
	 * 
	 * @param origBlock
	 *            the original block.
	 */
	private void cloneLines(SimulinkBlock origBlock) {

		List<SimulinkLine> lines = new ArrayList<SimulinkLine>();
		origBlock.collectLines(lines);

		for (SimulinkLine line : lines) {
			SimulinkBlock srcSub = origBlock.getAncestralChild(line
					.getSrcPort().getBlock());
			SimulinkBlock dstSub = origBlock.getAncestralChild(line
					.getDstPort().getBlock());

			if (srcSub == null || dstSub == null) {
				// not a line between children
				continue;
			}

			if (srcSub != origBlock && srcSub == dstSub) {
				// has already been cloned when cloning srcSub
				continue;
			}

			cloneLine(line, origBlock);
		}
	}

	/**
	 * Get block default parameter.
	 */
	@Override
	/* package */String getDefaultParameter(String name) {
		return getModel().getTypeBlockDefaultParameter(getType(), name);
	}

	/**
	 * Get block default parameter names.
	 */
	@Override
	/* package */Set<String> getDefaultParameterNames() {
		return getModel().getBlockDefaultParameterNames(getType());
	}

	/** Removes the given element. */
	/* package */void removeElement(SimulinkElementBase element) {
		if (element instanceof SimulinkAnnotation) {
			annotations.remove(element);
		} else if (element instanceof SimulinkBlock) {
			subBlocks.remove(element.getName());
		} else {
			CCSMAssert.fail(element.getClass().getName()
					+ " is a unknown sub class of "
					+ SimulinkElementBase.class.getName());
		}
	}

	/** Remove in port. */
	/* package */void removeInPort(SimulinkInPort inPort) {
		CCSMPre.isTrue(inPorts.containsValue(inPort),
				"Port does not belong to this block!");
		inPorts.remove(inPort.getIndex());
	}

	/** Remove out port. */
	/* package */void removeOutPort(SimulinkOutPort outPort) {
		CCSMPre.isTrue(outPorts.containsValue(outPort),
				"Port does not belong to this block!");
		outPorts.remove(outPort.getIndex());
	}

	/**
	 * Clone a single line.
	 * 
	 * @param origLine
	 *            the line to clone.
	 */
	private void cloneLine(SimulinkLine origLine, SimulinkBlock origBlock) {
		SimulinkOutPort origSrcPort = origLine.getSrcPort();
		SimulinkInPort origDstPort = origLine.getDstPort();

		SimulinkBlock cloneSrcBlock = resolveRelativeBlock(
				origSrcPort.getBlock(), origBlock);

		CCSMAssert.isFalse(cloneSrcBlock == null, "Cloning Problem: Src block "
				+ origSrcPort.getBlock().getName() + " not found.");

		SimulinkBlock cloneDstBlock = resolveRelativeBlock(
				origDstPort.getBlock(), origBlock);
		CCSMAssert.isFalse(cloneDstBlock == null, "Cloning Problem: Dst block "
				+ origDstPort.getBlock().getName() + " not found.");

		@SuppressWarnings("null")
		SimulinkOutPort cloneSrcPort = cloneSrcBlock.getOutPort(origSrcPort
				.getIndex());
		CCSMAssert.isFalse(
				cloneSrcPort == null,
				"Cloning Problem: Src port with index "
						+ origSrcPort.getIndex() + " not found.");

		@SuppressWarnings("null")
		SimulinkInPort cloneDstPort = cloneDstBlock.getInPort(origDstPort
				.getIndex());
		CCSMAssert.isFalse(
				cloneDstPort == null,
				"Cloning Problem: Dst port with index "
						+ origDstPort.getIndex() + " not found.");

		// clone line
		SimulinkLine line = new SimulinkLine(cloneSrcPort, cloneDstPort);
		SimulinkUtils.copyParameters(origLine, line);
	}

	/**
	 * Fills the given list with all lines contained in this block or one of its
	 * descendant blocks.
	 */
	private void collectLines(List<SimulinkLine> lines) {
		lines.addAll(getOutLines());
		for (SimulinkBlock sub : getSubBlocks()) {
			sub.collectLines(lines);
		}
	}

	/**
	 * Removes lines from this block. All of the lines have to be connected to
	 * this block.
	 * 
	 * @param lines
	 *            The lines that should be removed
	 */
	private void removeLines(Collection<SimulinkLine> lines) {
		for (SimulinkLine line : lines) {
			CCSMPre.isTrue(getInLines().contains(line)
					|| getOutLines().contains(line),
					"Only lines that are connected to this block can be removed by this block");
			line.remove();
		}
	}

	/**
	 * Removes all incoming lines of this block
	 */
	public void removeInLines() {
		removeLines(getInLines());
	}

	/**
	 * Removes all outgoing lines of this block
	 */
	private void removeOutLines() {
		removeLines(getOutLines());
	}

	/**
	 * Returns the sub block, which has the given block as a descendant (i.e.
	 * direct or indirect sub block). If the block is a sub block of
	 * <code>this</code>, the sub block is returned. If the block is
	 * <code>this</code>, then <code>this</code> is returned. If the given block
	 * is not a descendant, <code>null</code> is returned.
	 */
	private SimulinkBlock getAncestralChild(SimulinkBlock block) {
		CCSMPre.isFalse(block == null, "Block may not be null");
		if (block == this) {
			return this;
		}
		while (block != null) {
			if (block.getParent() == this) {
				return block;
			}
			block = block.getParent();
		}

		// not a descendant
		return null;
	}

	/**
	 * Returns the block that is in the same relation to this block as is
	 * <code>block</code> to <code>root</code>. This may only be called if
	 * <code>block</code> is a descendant of <code>root</code> and such a
	 * relative block actually exists. Otherwise assertion exceptions are
	 * thrown.
	 */
	@SuppressWarnings("null")
	private SimulinkBlock resolveRelativeBlock(SimulinkBlock block,
			SimulinkBlock root) {
		CCSMAssert.isFalse(block == null,
				"Block must be a descendant of root block!");
		if (block == root) {
			return this;
		}

		SimulinkBlock resultParent = resolveRelativeBlock(block.getParent(),
				root);
		CCSMAssert
				.isFalse(resultParent == null, "Parent block does not exist.");

		return resultParent.getSubBlock(block.getName());
	}

	/**
	 * Replaces this block by another if the compatibility check succeeds.
	 * 
	 * @param replacement
	 *            The block to be replaced
	 * 
	 * @see SimulinkUtils#checkCompatibility(SimulinkBlock, SimulinkBlock)
	 * 
	 */
	public void replace(SimulinkBlock replacement, String... parameters)
			throws SimulinkModelBuildingException {
		if (!SimulinkUtils.checkCompatibility(this, replacement)) {
			throw new SimulinkModelBuildingException(
					"Blocks are not compatible!");
		}

		replacement.removeInLines();
		redirectInPorts(replacement);
		replacement.removeOutLines();
		redirectOutPorts(replacement);

		CCSMAssert.isTrue(getInLines().isEmpty(),
				"In-lines have not been properly replaced");
		CCSMAssert.isTrue(getOutLines().isEmpty(),
				"Out-lines have not been properly replaced");

		// Save the parent block now, as it is not available after detaching
		SimulinkBlock parent = getParent();

		// Detach this block from its parent block. We have to do this so we can
		// savely add the replacement
		detach();

		replacement.detach();

		parent.addSubBlock(replacement);

		// Copy all parameters from this block to the replacement block
		for (String name : parameters) {
			String value = getParameter(name);
			if (value != null) {
				replacement.setParameter(name, value);
			}
		}

		// Now we can completely remove this block
		remove();
	}

	/**
	 * Redirect the in-ports that of this block to {@code toBlock}
	 * 
	 * @see #redirectInPort
	 * @param toBlock
	 *            The block where the lines are redirected to
	 * @throws SimulinkModelBuildingException
	 */
	private void redirectInPorts(SimulinkBlock toBlock)
			throws SimulinkModelBuildingException {
		// Redirect the incoming lines of this block to the replacement block
		for (SimulinkInPort port : getInPorts()) {
			redirectInPort(port, toBlock);
		}
	}

	/**
	 * Redirect the out-ports of this block to {@code toBlock}
	 * 
	 * @see #redirectOutPort
	 * @param toBlock
	 *            The block where the lines are redirected to
	 * @throws SimulinkModelBuildingException
	 */
	private void redirectOutPorts(SimulinkBlock toBlock)
			throws SimulinkModelBuildingException {
		// Redirect the incoming lines of this block to the replacement block
		for (SimulinkOutPort port : getOutPorts()) {
			redirectOutPort(port, toBlock);
		}
	}

	/**
	 * Connect the out-port that is connected with {@code oldInPort} with the
	 * in-port of {@code target} that has the same index as {@code oldInPort}
	 * and remove the old lines.
	 * 
	 * @param oldInPort
	 *            The in-port that is to be redirected
	 * @param target
	 *            The target block that will be connected
	 * @throws SimulinkModelBuildingException
	 */
	private void redirectInPort(SimulinkInPort oldInPort, SimulinkBlock target)
			throws SimulinkModelBuildingException {

		SimulinkLine line = oldInPort.getLine();
		if (line == null) {
			return;
		}

		// The new in-port has the same index as old one
		SimulinkInPort newInPort = target.getInPort(oldInPort.getIndex());

		if (newInPort == null) {
			throw new SimulinkModelBuildingException(
					"Port could not be redirected.");
		}

		// Copy points parameter so that line will be displayed correctly by an
		// editor
		SimulinkUtils.replaceLine(line, line.getSrcPort(), newInPort,
				PARAM_Points);
	}

	/**
	 * Connect the in-ports that are connected with {@code oldOutPort} with the
	 * out-port of {@code target} that has the same index as {@code oldOutPort}.
	 * 
	 * @param oldOutPort
	 *            The out-port that is to be redirected
	 * @param target
	 *            The target block that will be connected
	 * @throws SimulinkModelBuildingException
	 */
	private void redirectOutPort(SimulinkOutPort oldOutPort,
			SimulinkBlock target) throws SimulinkModelBuildingException {

		// The new out-port has the same index as the old one
		SimulinkOutPort newOutPort = target.getOutPort(oldOutPort.getIndex());
		if (newOutPort == null) {
			throw new SimulinkModelBuildingException(
					"Port could not be redirected.");
		}
		// Avoid concurrent modification exception by copying
		Set<SimulinkLine> lines = new HashSet<SimulinkLine>(
				oldOutPort.getLines());
		for (SimulinkLine line : lines) {
			// Copy points parameter so that line will be displayed correctly by
			// an editor
			SimulinkUtils.replaceLine(line, newOutPort, line.getDstPort(),
					PARAM_Points);
		}
	}
}