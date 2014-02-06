package org.conqat.engine.html_presentation.listing;

import static org.conqat.lib.commons.html.ECSSProperty.BACKGROUND_COLOR;
import static org.conqat.lib.commons.html.ECSSProperty.COLOR;
import static org.conqat.lib.commons.html.EHTMLAttribute.CLASS;
import static org.conqat.lib.commons.html.EHTMLAttribute.ID;
import static org.conqat.lib.commons.html.EHTMLElement.SPAN;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.conqat.engine.html_presentation.CSSMananger;
import org.conqat.lib.commons.collections.Pair;
import org.conqat.lib.commons.html.CSSDeclarationBlock;
import org.conqat.lib.commons.html.ECSSProperty;
import org.conqat.lib.commons.html.HTMLWriter;
import org.conqat.lib.commons.region.Region;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.scanner.ELanguage;
import org.conqat.lib.scanner.highlighting.SourceCodeHtmlFormatter;

/**
 * {@link SourceCodeHtmlFormatter} for code listings
 * 
 * @author $Author: hummelb $
 * @version $Rev: 44691 $
 * @ConQAT.Rating GREEN Hash: EC34DB601C519EA339F712FAF604C9EC
 */
public class ListingHtmlFormatter extends SourceCodeHtmlFormatter {

	/** Set of lines that should be marked as inserted */
	private final Set<Integer> insertedPositions = new HashSet<Integer>();

	/** Set of lines that should be marked as deletion points */
	private final Set<Integer> deletionPositions = new HashSet<Integer>();

	/** Caches CSS blocks for line number colors and inherited styles. */
	private final Map<Pair<String, CSSDeclarationBlock>, CSSDeclarationBlock> lineNumberCssStyles = new HashMap<Pair<String, CSSDeclarationBlock>, CSSDeclarationBlock>();

	/** CSS declaration used for dashed red lines that mark deletion points */
	private final CSSDeclarationBlock dashedLineStyle = new CSSDeclarationBlock(
			ECSSProperty.BORDER_BOTTOM_COLOR, "red",
			ECSSProperty.BORDER_BOTTOM_STYLE, "dashed",
			ECSSProperty.BORDER_BOTTOM_WIDTH, "1px");

	/** {@inheritDoc} */
	@Override
	protected CSSDeclarationBlock createOddLineStyle(
			CSSDeclarationBlock baseStyle) {
		return new CSSDeclarationBlock(super.createOddLineStyle(baseStyle),
				BACKGROUND_COLOR, "#eeeeee");
	}

	/** {@inheritDoc} */
	@Override
	protected CSSDeclarationBlock createBaseStyle() {
		return new CSSDeclarationBlock(CSSMananger.DEFAULT_FONT,
				ECSSProperty.WHITE_SPACE, "pre", ECSSProperty.FONT_FAMILY,
				"Monospace");
	}

	/** {@inheritDoc} */
	@Override
	protected void insertLineNumberSpacer(int lineNumber) {
		writer.addClosedTextElement(SPAN, StringUtils.SPACE, ID,
				ListingWriter.getLineId(lineNumber), CLASS,
				ListingWriter.SPACER_CLASS);
	}

	/** {@inheritDoc} */
	@Override
	protected CSSDeclarationBlock lineNumberStyleFor(int lineNumber) {
		String htmlColor = "#aaaaaa";
		CSSDeclarationBlock inheritedStyle = null;

		if (insertedPositions.contains(lineNumber - 1)) {
			htmlColor = "green";
		}

		if (deletionPositions.contains(lineNumber)) {
			inheritedStyle = dashedLineStyle;
		}

		return getOrCreateLineNumberColorBlock(htmlColor, inheritedStyle);
	}

	/**
	 * Get CSS block for a html color and an optional inherited style. Caches
	 * blocks for colors to keep CSS style sheet size small.
	 */
	private CSSDeclarationBlock getOrCreateLineNumberColorBlock(
			String htmlColor, CSSDeclarationBlock inheritedStyle) {
		Pair<String, CSSDeclarationBlock> key = new Pair<String, CSSDeclarationBlock>(
				htmlColor, inheritedStyle);
		if (!lineNumberCssStyles.containsKey(key)) {
			CSSDeclarationBlock cssDeclarationBlock = new CSSDeclarationBlock(
					createBaseStyle(), COLOR, htmlColor);
			if (inheritedStyle != null) {
				cssDeclarationBlock.inheritFrom(inheritedStyle);
			}
			lineNumberCssStyles.put(key, cssDeclarationBlock);
		}

		return lineNumberCssStyles.get(key);
	}

	/** {@inheritDoc} */
	@Override
	public void formatSourceCode(String content, ELanguage language,
			HTMLWriter writer) {
		clearLineDiffInformation();
		super.formatSourceCode(content, language, writer);
	}

	/** {@inheritDoc} */
	@Override
	public void formatSourceCode(String content, ELanguage language,
			List<Region> disabledRegions, HTMLWriter writer) {
		clearLineDiffInformation();
		super.formatSourceCode(content, language, disabledRegions, writer);
	}

	/**
	 * @see SourceCodeHtmlFormatter#formatSourceCode(String, ELanguage, List,
	 *      HTMLWriter)
	 * 
	 * @param lineDiffProvider
	 *            May be null; then no diff highlighting gets performed.
	 */
	public void formatSourceCode(String content, ELanguage language,
			List<Region> filteredRegions,
			LineDiffColorProvider lineDiffProvider, HTMLWriter writer) {

		clearLineDiffInformation();

		if (lineDiffProvider != null) {
			insertedPositions.addAll(lineDiffProvider.getInsertions());
			deletionPositions.addAll(lineDiffProvider.getDeletions());
		}

		super.formatSourceCode(content, language, filteredRegions, writer);
	}

	/**
	 * Clears line differencing information.
	 */
	private void clearLineDiffInformation() {
		insertedPositions.clear();
		deletionPositions.clear();
	}
}