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
package org.conqat.engine.html_presentation.pattern;

import java.awt.Color;
import java.util.Map;

import org.conqat.lib.commons.treemap.IDrawingPattern;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.commons.traversal.NodeTraversingProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;

/**
 * Base class for processors assigning drawing patterns.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: F3FEC4400DA3BD12413C56CBC367F48F
 * 
 * @param <E>
 *            the type expected to be read.
 * @param <L>
 *            the type used for the legend key.
 */
public abstract class PatternAssignerBase<E, L extends Comparable<? super L>>
		extends NodeTraversingProcessorBase<IConQATNode> {

	/**
	 * The key used for adding a legend to the root node. The legend is a
	 * mapping from a comparable to {@link IDrawingPattern}.
	 */
	public static final String LEGEND_KEY = "PATTERN_LEGEND";

	/** The key used to write the pattern color into. */
	@AConQATKey(description = "The key used to write the pattern color into.", type = "java.awt.Color")
	public static final String PATTERN_COLOR_KEY = "pattern-color";

	/** The key used to write the drawing pattern into. */
	@AConQATKey(description = "The key used to write the drawing pattern into.", type = "org.conqat.engine.html_presentation.pattern.IDrawingPattern")
	public static final String DRAWING_PATTERN_KEY = "pattern";

	/** The key used for reading. */
	private String readKey;

	/** Set read key. */
	@AConQATParameter(name = ConQATParamDoc.READKEY_NAME, minOccurrences = 1, maxOccurrences = 1, description = ConQATParamDoc.READKEY_DESC)
	public void setReadKey(
			@AConQATAttribute(name = ConQATParamDoc.READKEY_KEY_NAME, description = ConQATParamDoc.READKEY_KEY_DESC)
			String key) {

		this.readKey = key;
	}

	/** {@inheritDoc} */
	@Override
	protected ETargetNodes getTargetNodes() {
		return ETargetNodes.ALL;
	}

	/** Assigns a pattern to a node. */
	protected void assignPattern(IConQATNode node,
			IDrawingPattern drawingPattern, Color patternColor) {
		node.setValue(PATTERN_COLOR_KEY, patternColor);
		node.setValue(DRAWING_PATTERN_KEY, drawingPattern);
	}

	/** {@inheritDoc} */
	@Override
	@SuppressWarnings("unchecked")
	public void visit(IConQATNode node) {
		Object value = node.getValue(readKey);
		if (value != null) {
			try {
				handleNode(node, (E) value);
			} catch (ClassCastException e) {
				getLogger().warn(
						"Unexpected type in key " + readKey + " at node "
								+ node.getId());
			}
		}
	}

	/**
	 * Handle the assignment for a single node. The Method should used
	 * {@link #assignPattern(IConQATNode, IDrawingPattern, Color)} for assigning
	 * the pattern.
	 */
	protected abstract void handleNode(IConQATNode node, E value);

	/** {@inheritDoc} */
	@Override
	protected void finish(IConQATNode root) throws ConQATException {
		Map<L, IDrawingPattern> legend = getLegend();
		if (legend != null) {
			root.setValue(LEGEND_KEY, legend);
		}
		super.finish(root);
	}

	/**
	 * If this method returns a non-null value, the map is added to the root
	 * node using the {@link #LEGEND_KEY} key. It will be called at the very
	 * end, when all patterns have been assigned.
	 */
	protected Map<L, IDrawingPattern> getLegend() {
		return null;
	}
}