/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
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
package org.conqat.engine.html_presentation.seesoft;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.findings.FindingsList;
import org.conqat.engine.commons.findings.location.ElementLocation;
import org.conqat.engine.commons.findings.location.TextRegionLocation;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.IConQATLogger;
import org.conqat.engine.html_presentation.color.ColorizerBase;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.TextElementUtils;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.TokenElement;
import org.conqat.lib.commons.color.IColorProvider;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.scanner.IToken;
import org.conqat.lib.scanner.highlighting.SourceCodeStyle;

/**
 * SeeSoft renderer for {@link ITokenElement}s.
 * 
 * @author $Author: kanis $
 * @version $Rev: 44147 $
 * @ConQAT.Rating GREEN Hash: DA92E5491426F3DDF7E955E0ED2F15CB
 */
public class SeeSoftElementRenderer {

	/**
	 * <code>LINE_SPACING - 1</code> pixels will be left out as vertical spacing
	 * between lines. A value of 1 means no line spacing.
	 */
	private static final int LINE_SPACING = 2;

	/** Parameters that determine appearance */
	private final SeeSoftParameterInfo paramInfo;

	/** {@link IColorProvider} for findings. */
	private final IColorProvider<Finding> findingsColorProvider;

	/** The logger (used for tokenization). */
	private final IConQATLogger logger;

	/** Index of the last token that was rendered. */
	private int lastTokenIndex = 0;

	/** The list of tokens for the currently rendered {@link TokenElement}. */
	private List<IToken> tokens;

	/** Constructor. */
	public SeeSoftElementRenderer(
			IColorProvider<Finding> findingsColorProvider,
			IConQATLogger logger, SeeSoftParameterInfo paramInfo) {
		this.findingsColorProvider = findingsColorProvider;
		this.logger = logger;
		this.paramInfo = paramInfo;
	}

	/** Render an {@link ITokenElement} to the given graphics object. */
	public void draw(Graphics2D graphics, ITokenElement tokenElement)
			throws ConQATException {
		lastTokenIndex = 0;
		tokens = tokenElement.getTokens(logger);

		Dimension size = getPreferredSize(tokenElement);
		drawBackground(graphics, tokenElement, size);
		drawBorder(graphics, size);

		boolean drawForegroundBeforeFindings = paramInfo.isFindingsOpaque();
		if (drawForegroundBeforeFindings) {
			drawForeground(graphics, tokenElement.getTextContent());
		}

		drawFindings(graphics, tokenElement, size);

		if (!drawForegroundBeforeFindings) {
			drawForeground(graphics, tokenElement.getTextContent());
		}
	}

	/**
	 * Calculate the preferred size for a given node.
	 * 
	 * <p>
	 * Takes into account the compression factor.
	 * </p>
	 */
	public Dimension getPreferredSize(ITextElement textElement)
			throws ConQATException {
		return new Dimension(paramInfo.getColumnWidth()
				/ paramInfo.getCompressionFactor(),
				TextElementUtils.countLOC(textElement)
						/ paramInfo.getCompressionFactor()
						* SeeSoftElementRenderer.LINE_SPACING);
	}

	/** Draw the background of the node. */
	protected void drawBackground(Graphics2D graphics, IConQATNode node,
			Dimension size) {
		graphics.setColor(getColor(node));
		graphics.fillRect(0, 0, size.width - 1, size.height - 1);
	}

	/**
	 * Retrieve color value from an {@link IConQATNode}.
	 * 
	 * @return The color of the node or {@link Color#WHITE} if none is attached
	 *         to it.
	 */
	private Color getColor(IConQATNode node) {
		Object color = node.getValue(ColorizerBase.COLOR_KEY_DEFAULT);
		if (color instanceof Color) {
			return (Color) color;
		}

		return Color.WHITE;
	}

	/** Draws a border around the node. */
	protected void drawBorder(Graphics2D graphics, Dimension size) {
		graphics.setColor(Color.DARK_GRAY);
		graphics.drawRect(0, 0, size.width - 1, size.height - 1);
	}

	/** Draw the findings of the node. */
	protected void drawFindings(Graphics2D graphics, ITextElement element,
			Dimension size) throws ConQATException {
		if (suppressFindings(element)) {
			return;
		}

		for (Finding finding : findings(element, paramInfo.getFindingsKey())) {
			ElementLocation location = finding.getLocation();
			int from = -1;
			int height = -1;

			if (location instanceof TextRegionLocation) {
				TextRegionLocation regionLocation = (TextRegionLocation) location;

				int startLine = TextElementUtils
						.convertRawOffsetToFilteredLine(element,
								regionLocation.getRawStartOffset());
				int endLine = TextElementUtils.convertRawOffsetToFilteredLine(
						element, regionLocation.getRawEndOffset());

				from = startLine / paramInfo.getCompressionFactor()
						* LINE_SPACING;
				height = endLine / paramInfo.getCompressionFactor()
						* LINE_SPACING - from + 1;
			} else {
				// whole file for findings, that don't have line numbers
				from = 0;
				height = size.height;
			}

			if (height < paramInfo.getFindingsMinHeight()) {
				height = paramInfo.getFindingsMinHeight();
				from -= height / 2;
			}

			graphics.setColor(findingsColorProvider.getColor(finding));
			graphics.fillRect(1, from, size.width - 2, height);
		}
	}

	/**
	 * @return <code>true</code> if findings should be omitted from rendering,
	 *         <code>false</code> otherwise.
	 */
	private boolean suppressFindings(IConQATNode node) throws ConQATException {
		return NodeUtils.getBooleanValue(node,
				paramInfo.getSuppressFindingsKey());
	}

	/**
	 * Extract findings from element. Returns an empty list if no findings are
	 * attached to <code>node</code>
	 */
	private Collection<Finding> findings(IConQATNode node, String findingKey) {
		List<Finding> findings = new ArrayList<Finding>();
		FindingsList findingsList = NodeUtils.getFindingsList(node, findingKey);
		if (findingsList != null) {
			findings.addAll(findingsList);
		}
		return findings;
	}

	/**
	 * Draws the "foreground" (the text pixels). This method expects the content
	 * to have line breaks normalized to a single <code>\n</code>, as is the
	 * case for content that comes from {@link ITextElement#getTextContent()}.
	 */
	protected void drawForeground(Graphics2D graphics, String content) {
		// The current offset in the original content
		int offset = 0;

		String[] lines = StringUtils.splitLines(content);

		int y = 0;
		for (int lineNumber = 0; lineNumber < lines.length; lineNumber++) {
			String line = lines[lineNumber];

			if (!ignoreLine(lineNumber)) {
				y += LINE_SPACING;
				for (int columnNumber = 0, x = 0; columnNumber < line.length(); columnNumber += paramInfo
						.getCompressionFactor(), x++) {
					char c = line.charAt(columnNumber);
					if (!ignore(c)) {
						graphics.setColor(getColor(offset + columnNumber));
						graphics.drawLine(x, y, x, y);
					}
				}
			}

			// This is only OK for single char line breaks!
			offset += line.length() + 1;
		}
	}

	/**
	 * @return <code>true</code> if <code>lineNumber</code> is a line that
	 *         should not be drawn because of compression.
	 */
	private boolean ignoreLine(int lineNumber) {
		int compressionFactor = paramInfo.getCompressionFactor();
		return compressionFactor > 1 && lineNumber % compressionFactor == 0;
	}

	/**
	 * @return <code>true</code> if <code>c</code> should be ignored when
	 *         rendering.
	 */
	private boolean ignore(char c) {
		return Character.isWhitespace(c);
	}

	/** @return the color to use to layout the character at this offset */
	protected Color getColor(int offset) {
		if (paramInfo.isSyntaxHighlightingEnabled()) {
			return getColor(pickToken(offset));
		}

		return Color.BLACK;
	}

	/** Determine the color for a token. */
	private Color getColor(IToken token) {
		if (token == null) {
			return Color.RED;
		}

		return SourceCodeStyle.get(token.getLanguage()).getStyle(token)
				.getFirst();
	}

	/** Determine token at an offset. */
	private IToken pickToken(int offset) {
		for (int i = lastTokenIndex; i < tokens.size(); i++) {
			IToken token = tokens.get(i);

			if (token.getOffset() <= offset && token.getEndOffset() >= offset) {
				lastTokenIndex = i;
				return token;
			}

			if (token.getOffset() > offset) {
				return null;
			}
		}

		// This can happen if there were non-scannable characters in the input
		return null;
	}
}