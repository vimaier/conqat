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
package org.conqat.engine.html_presentation.treemap;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeConstants;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.conqat.lib.commons.treemap.IDrawingPattern;
import org.conqat.lib.commons.treemap.ITreeMapNode;

/**
 * A tree map node defining its color and patterns based on the values stored at
 * the tree nodes.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 15CED28067F88EA6F173303A7BB3B742
 */
public class ColorKeyedTreeMapNode implements ITreeMapNode<IConQATNode> {

	/** The area of this node including all child nodes. */
	private double aggregatedArea = 0;

	/** The list of child nodes. */
	private final List<ColorKeyedTreeMapNode> children = new ArrayList<ColorKeyedTreeMapNode>();

	/** The ConQAT node this is based on. */
	protected final IConQATNode baseNode;

	/** The recangle this node is layouted into. */
	private Rectangle2D rect = null;

	/** The key used for reading the color. */
	private final String colorKey;

	/** The key used for reading the size. */
	private final String sizeKey;

	/** The key used for reading the color used in the pattern. */
	private final String patternColorKey;

	/** The key used for reading the drawing pattern. */
	private final String drawingPatternKey;

	/** {@inheritDoc} */
	@Override
	public String getText() {
		return baseNode.getName();
	}

	/**
	 * Create a new instance based on a ConQAT node.
	 * 
	 * @param baseNode
	 *            the node to build this from.
	 * @param sizeKey
	 *            the key used for reading the size. If null, then the value 1
	 *            will be used.
	 * @param colorKey
	 *            the key used to read the color.
	 * @param patternColorKey
	 *            the key used for reading the color used in the pattern.
	 * @param drawingPatternKey
	 *            the key used for reading the drawing pattern.
	 */
	public ColorKeyedTreeMapNode(IConQATNode baseNode, String sizeKey,
			String colorKey, String patternColorKey, String drawingPatternKey) {
		this.baseNode = baseNode;
		this.sizeKey = sizeKey;
		this.colorKey = colorKey;
		this.patternColorKey = patternColorKey;
		this.drawingPatternKey = drawingPatternKey;
		createChildNodes();
	}

	/** This method creates all child nodes of this node. */
	@SuppressWarnings("unchecked")
	private void createChildNodes() {
		if (baseNode.hasChildren()) {
			IConQATNode[] children = baseNode.getChildren();

			// sort ?
			Object comp = baseNode.getValue(NodeConstants.COMPARATOR);
			if (comp != null && (comp instanceof Comparator)) {
				Arrays.sort(children, (Comparator<IConQATNode>) comp);
			}

			for (IConQATNode child : children) {
				ColorKeyedTreeMapNode node = new ColorKeyedTreeMapNode(child,
						sizeKey, colorKey, patternColorKey, drawingPatternKey);
				this.children.add(node);
				aggregatedArea += node.getArea();
			}
		} else {
			aggregatedArea = 1;
			if (sizeKey != null) {
				Object value = baseNode.getValue(sizeKey);
				if (value instanceof Number) {
					aggregatedArea = ((Number) value).doubleValue();
				}
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public Color getColor() {
		if (colorKey == null) {
			return Color.GRAY;
		}
		Object value = baseNode.getValue(colorKey);
		if (value instanceof Color) {
			return (Color) value;
		}
		return Color.GRAY;
	}

	/** {@inheritDoc} */
	@Override
	public Color getPatternColor() {
		if (patternColorKey == null) {
			return Color.GRAY;
		}
		Object value = baseNode.getValue(patternColorKey);
		if (value instanceof Color) {
			return (Color) value;
		}
		return Color.GRAY;
	}

	/** {@inheritDoc} */
	@Override
	public IDrawingPattern getDrawingPattern() {
		if (drawingPatternKey == null) {
			return null;
		}
		Object value = baseNode.getValue(drawingPatternKey);
		if (value instanceof IDrawingPattern) {
			return (IDrawingPattern) value;
		}
		return null;
	}

	/** Returns the list of children of this node. */
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public UnmodifiableList<ITreeMapNode<IConQATNode>> getChildren() {
		return (UnmodifiableList) CollectionUtils.asUnmodifiable(children);
	}

	/** {@inheritDoc} */
	@Override
	public double getArea() {
		return aggregatedArea;
	}

	/** {@inheritDoc} */
	@Override
	public Rectangle2D getLayoutRectangle() {
		return rect;
	}

	/** {@inheritDoc} */
	@Override
	public void setLayoutRectangle(Rectangle2D rect) {
		this.rect = rect;
	}

	/** Returns the underlying ConQATNode. */
	@Override
	public IConQATNode getUserDatum() {
		return baseNode;
	}

	/** {@inheritDoc} */
	@Override
	public List<String> getTooltipKeys() {
		return NodeUtils.getDisplayList(baseNode).getKeyList();
	}

	/** {@inheritDoc} */
	@Override
	public String getTooltipId() {
		return baseNode.getId();
	}

	/** {@inheritDoc} */
	@Override
	public Object getTooltipValue(String key) {
		return baseNode.getValue(key);
	}
}