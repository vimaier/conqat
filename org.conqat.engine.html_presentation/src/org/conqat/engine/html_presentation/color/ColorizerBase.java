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
package org.conqat.engine.html_presentation.color;

import java.awt.Color;
import java.util.Map;

import org.conqat.lib.commons.error.NeverThrownRuntimeException;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATPipelineProcessorBase;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.commons.traversal.INodeVisitor;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;

/**
 * Base class for processors assigning colors. This class works in two stages.
 * In the first stage all nodes are visited and their values processed ({@link #visitValue(Object)}).
 * This phase ends with {@link #calculateColorTable()}, where the processor
 * might prepare its color table. The second stage consists of actually
 * determining the colors for the nodes based on the value of a key ({@link #determineColor(Object)}).
 * This phase is finished by {@link #getLegend()}, which can be used to return
 * a summary of the colors used for a legend.
 * <p>
 * Most of the methods contain empty default implementations for convenience, so
 * only those phases actually required must be used.
 * 
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: C0CE4BB954FCFD10E3AAB5B4515B699B
 * 
 * @param <E>
 *            the type expected to be read.
 * @param <L>
 *            the type type used for the legend key.
 */
public abstract class ColorizerBase<E, L extends Comparable<? super L>> extends
		ConQATPipelineProcessorBase<IConQATNode> {

	/**
	 * The key used for adding a legend to the root node. The legend is a
	 * mapping from a comparable to color.
	 */
	public static final String LEGEND_KEY = "COLOR_LEGEND";

	/** Default value used for the color key. */
	public static final String COLOR_KEY_DEFAULT = "color";

	/**
	 * The key used for writing the color.
	 */
	private String colorKey = COLOR_KEY_DEFAULT;

	/** The key used for reading. */
	private String readKey;

	/** The notes targeted by this operation. */
	private ETargetNodes targetNodes = ETargetNodes.ALL;

	/** Set the targets to use. */
	@AConQATParameter(name = "target", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "The target nodes to operate on [default: all]")
	public void setTargets(
			@AConQATAttribute(name = "nodes", description = "the nodes this operation targets")
			ETargetNodes targets) {
		this.targetNodes = targets;
	}

	/** Set read key. */
	@AConQATParameter(name = ConQATParamDoc.READKEY_NAME, minOccurrences = 1, maxOccurrences = 1, description = ConQATParamDoc.READKEY_DESC)
	public void setReadKey(
			@AConQATAttribute(name = ConQATParamDoc.READKEY_KEY_NAME, description = ConQATParamDoc.READKEY_KEY_DESC)
			String key) {
		this.readKey = key;
	}

	/** Set color key. */
	@AConQATParameter(name = "color", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "The key to write the color into. The default is to write into '"
			+ COLOR_KEY_DEFAULT + "'.")
	public void setColorKey(
			@AConQATAttribute(name = ConQATParamDoc.WRITEKEY_KEY_NAME, description = ConQATParamDoc.WRITEKEY_KEY_DESC)
			String key) {
		this.colorKey = key;
	}

	/** {@inheritDoc} */
	@Override
	protected void processInput(IConQATNode root) {
		TraversalUtils.visitDepthFirst(new InitialValueVisitor(), root,
				targetNodes);
		calculateColorTable();

		TraversalUtils
				.visitDepthFirst(new ColorDeterminer(), root, targetNodes);

		Map<L, Color> legend = getLegend();
		if (legend != null) {
			root.setValue(LEGEND_KEY, legend);
		}
	}

	/**
	 * Visitor method called for each value, so the implementing processor gets
	 * an idea of all values used before it has to decide on colors later on.
	 * <p>
	 * Provides an empty default implementation for convenience.
	 */
	protected void visitValue(@SuppressWarnings("unused")
	E value) {
		// does nothing
	}

	/**
	 * Signals that the traversal of values is completed and now the processor
	 * will be asked for actual colors. So here any lookup tables should be
	 * prepared.
	 * <p>
	 * Provides an empty default implementation for convenience.
	 */
	protected void calculateColorTable() {
		// does nothing
	}

	/** Returns the color for the given value. */
	protected abstract Color determineColor(E value);

	/**
	 * If this method returns a non-null value, the map is added to the root
	 * node using the {@link #LEGEND_KEY} key. It will be called at the very
	 * end, when all colors have been assigned.
	 */
	protected Map<L, Color> getLegend() {
		return null;
	}

	/**
	 * Base class for both visitors used in this class. Here we handle the
	 * {@link ClassCastException} which could occur.
	 */
	private abstract class ValueVisitorBase implements
			INodeVisitor<IConQATNode, NeverThrownRuntimeException> {

		/** {@inheritDoc} */
		@Override
		@SuppressWarnings("unchecked")
		public final void visit(IConQATNode node) {
			Object value = node.getValue(readKey);
			if (value != null) {
				try {
					visitNodeValue(node, (E) value);
				} catch (ClassCastException e) {
					getLogger().warn(
							"Unexpected type in key " + readKey + " at node "
									+ node.getId());
				}
			}
		}

		/** Visit the node's value. */
		protected abstract void visitNodeValue(IConQATNode node, E value);
	}

	/**
	 * The visitor used for the first traversal where all values are listed
	 * once.
	 */
	private final class InitialValueVisitor extends ValueVisitorBase {

		/** {@inheritDoc} */
		@Override
		public void visitNodeValue(IConQATNode node, E value) {
			ColorizerBase.this.visitValue(value);
		}
	}

	/**
	 * The visitor used for the second traversal where colors are determined and
	 * assigned.
	 */
	private final class ColorDeterminer extends ValueVisitorBase {
		/** {@inheritDoc} */
		@Override
		public void visitNodeValue(IConQATNode node, E value) {
			Color color = ColorizerBase.this.determineColor(value);
			if (color != null) {
				node.setValue(colorKey, color);
			}
		}
	}

}