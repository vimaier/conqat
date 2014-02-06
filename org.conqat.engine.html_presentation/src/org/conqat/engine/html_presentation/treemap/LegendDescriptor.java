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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.html_presentation.EHtmlPresentationFont;
import org.conqat.engine.html_presentation.color.ColorizerBase;
import org.conqat.engine.html_presentation.pattern.PatternAssignerBase;
import org.conqat.engine.html_presentation.pattern.PatternUtils;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.commons.treemap.IDrawingPattern;

/**
 * This class implements the drawing and layout of the legends.
 * <p>
 * Note: This may appear a bit over-engineered at first sight. However, I found
 * that implementing this without proper abstraction with the
 * {@link TreeMapImageDescriptor} created hard to read and to maintain code.
 * 
 * @author $Author: steidl $
 * @version $Rev: 43636 $
 * @ConQAT.Rating GREEN Hash: 25BC98F2E13190B2E96E5F15E64ED5AA
 */
public class LegendDescriptor {

	/**
	 * Font used by the legend drawer. If required this can later on be
	 * parameterized.
	 */
	private final static EHtmlPresentationFont font = EHtmlPresentationFont.SANS_CONDENSED;

	/** The font metrics the layout is based on. */
	private final FontMetrics metrics = font.getFontMetrics();

	/** The total available width. */
	private final int width;

	/**
	 * The size of the blobs that visualize color or drawing patterns. They are
	 * quadratic.
	 */
	private final int blobSize;

	/** The entries of the legend. */
	private final List<LegendEntryBase> entries;

	/**
	 * Constructor.
	 * 
	 * @param width
	 *            the total available width.
	 * @param root
	 *            the ConQAT node that stores the legends at
	 *            {@link ColorizerBase#LEGEND_KEY} and
	 *            {@link PatternAssignerBase#LEGEND_KEY}.
	 */
	public LegendDescriptor(int width, IConQATNode root) {
		this.width = width;
		blobSize = (int) (metrics.getAscent() * 1.2);
		entries = createEntries(root);
	}

	/** Create the legend entries. */
	private List<LegendEntryBase> createEntries(IConQATNode root) {
		List<LegendEntryBase> entries = new ArrayList<LegendEntryBase>();

		int maxWidth = 0;

		maxWidth = Math.max(maxWidth,
				addLegend(root, ColorizerBase.LEGEND_KEY, entries));
		maxWidth = Math.max(maxWidth,
				addLegend(root, PatternAssignerBase.LEGEND_KEY, entries));

		// add some space between
		maxWidth += 20;

		if (entries.isEmpty()) {
			return entries;
		}

		int columnCount = Math.max(1, width / maxWidth);

		int currentColumn = 0;
		int currentRow = 0;

		int rowHeight = obtainRowHeight();

		// this performs the actual layout
		for (LegendEntryBase entry : entries) {
			entry.pos = new Point(currentColumn * maxWidth, currentRow
					* rowHeight);
			currentColumn++;
			if (currentColumn >= columnCount) {
				currentColumn = 0;
				currentRow++;
			}
		}

		return entries;
	}

	/** Determine row height. */
	private int obtainRowHeight() {
		return blobSize + metrics.getDescent();
	}

	/**
	 * Add the legend specified via the key at the provided node to entry list.
	 * 
	 * @return this returns the width of the longest legend entry.
	 */
	private int addLegend(IConQATNode root, String legendKey,
			List<LegendEntryBase> legend) {
		int maxWidth = 0;

		Map<Comparable<Comparable<?>>, ?> colorLegend = obtainLegend(root,
				legendKey);
		for (Comparable<?> key : CollectionUtils.sort(colorLegend.keySet())) {
			LegendEntryBase entry = newLegendEntry(key, colorLegend.get(key));
			legend.add(entry);
			maxWidth = Math.max(maxWidth, entry.getWidth());
		}

		return maxWidth;
	}

	/** Obtain the legend stored at key at the node. */
	@SuppressWarnings("unchecked")
	private Map<Comparable<Comparable<?>>, ?> obtainLegend(IConQATNode root,
			String key) {
		Object o = root.getValue(key);
		if (!(o instanceof Map)) {
			return CollectionUtils.emptyMap();
		}
		return (Map<Comparable<Comparable<?>>, ?>) o;
	}

	/** Get total height of the legend. */
	public int getHeight() {
		if (entries.isEmpty()) {
			// we always reserve at least one row
			return obtainRowHeight();
		}
		// blob size is defined larger than the font height. We add 1 to allow
		// some room below the legend.
		return CollectionUtils.getLast(entries).pos.y + blobSize + 1;
	}

	/**
	 * Create a new legend entry.
	 * 
	 * @param key
	 *            toString() of the key is used to create entry description
	 * @param paint
	 *            this can either be a {@link Color} or an
	 *            {@link IDrawingPattern}. This throws an {@link AssertionError}
	 *            if any other object is provided here.
	 */
	private LegendEntryBase newLegendEntry(Object key, Object paint) {
		if (paint instanceof Color) {
			return new ColorLegendEntry(keyToString(key), (Color) paint);
		} else if (paint instanceof IDrawingPattern) {
			return new PatternLegendEntry(keyToString(key),
					(IDrawingPattern) paint);
		} else {
			throw new AssertionError("Unknown paint type");
		}
	}

	/** Converts a legend key to a string. */
	private String keyToString(Object key) {
		if (key instanceof Number) {
			return String.format("%.2f", key);
		}
		return key.toString();
	}

	/** Draw the legend. */
	public void draw(Graphics2D graphics) {
		font.setFont(graphics);
		for (LegendEntryBase entry : entries) {
			entry.draw(graphics);
		}
	}

	/** Base class for legend entries. */
	private abstract class LegendEntryBase {

		/** The text of the entry. */
		private final String text;

		/** Separator between blob and text. */
		private final int separator = metrics.getHeight() / 2;

		/** The entries position. */
		protected Point pos;

		/** Constructor. */
		protected LegendEntryBase(String text) {
			if (StringUtils.isEmpty(text)) {
				this.text = "<empty>";
			} else {
				this.text = text;
			}
		}

		/** Draw the entry. */
		public void draw(Graphics2D graphics) {
			drawBlob(graphics);

			graphics.setPaint(Color.BLACK);
			graphics.setStroke(new BasicStroke(1));
			graphics.drawRect(pos.x, pos.y, blobSize, blobSize);

			graphics.drawString(text, pos.x + blobSize + separator, pos.y
					+ blobSize);
		}

		/** Get the entry's width. */
		public int getWidth() {
			return blobSize + separator + metrics.stringWidth(text);
		}

		/** Template method to draw the blob. */
		protected abstract void drawBlob(Graphics2D graphics);
	}

	/** Entry with colors. */
	private class ColorLegendEntry extends LegendEntryBase {

		/** The color. */
		private final Color color;

		/** Constructor. */
		public ColorLegendEntry(String text, Color color) {
			super(text);
			this.color = color;
		}

		/** {@inheritDoc} */
		@Override
		protected void drawBlob(Graphics2D graphics) {
			graphics.setPaint(color);
			graphics.fillRect(pos.x, pos.y, blobSize, blobSize);
		}
	}

	/** Entry with pattern. */
	private class PatternLegendEntry extends LegendEntryBase {

		/** The pattern. */
		private IDrawingPattern pattern;

		/** Constructor. */
		public PatternLegendEntry(String text, IDrawingPattern pattern) {
			super(text);
			this.pattern = pattern;
		}

		/** {@inheritDoc} */
		@Override
		protected void drawBlob(Graphics2D graphics) {
			graphics.drawImage(PatternUtils.createPatternImage(pattern,
					blobSize, blobSize), pos.x, pos.y, null);
		}
	}
}