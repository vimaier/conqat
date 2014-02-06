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
package org.conqat.engine.commons.findings.location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.conqat.lib.commons.algo.Diff;
import org.conqat.lib.commons.algo.Diff.Delta;
import org.conqat.lib.commons.region.Region;
import org.conqat.lib.commons.string.LineOffsetConverter;

/**
 * This class is used for adjusting the offsets used in locations (i.e.
 * subclasses of {@link ElementLocation} for text that is slightly modified. The
 * main use-case is the update of locations where the local (adjusted) text has
 * different line ending, different content due to keyword expansion, or minor
 * local modifications compared to the text on which the analysis was executed
 * (original text).
 * 
 * Both the original and adjusted text may have arbitrary line endings.
 * 
 * The implementation is based on a token diff, which can lead to minor
 * deviations for offsets that are not aligned with token boundaries. A
 * character diff would be more precise, but is too performance and memory
 * intensive for large files.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46933 $
 * @ConQAT.Rating GREEN Hash: 94B339C990564B3C666C469E1661FBFF
 */
public class LocationAdjuster {

	/**
	 * If the number of tokens in the adjusted region differs by the tokens in
	 * the original region by more than this factor, the mapping is counted as
	 * wrong.
	 */
	private static final double LOSS_FACTOR = 2;

	/**
	 * Pattern defining tokens for the diff. Matches either alphanumeric strings
	 * (typical identifiers), or single non-whitespace characters.
	 */
	private static final Pattern TOKEN_PATTERN = Pattern
			.compile("[a-zA-Z0-9_]+|\\S");

	/** The tokens of the original string. */
	private final List<AdjusterToken> originalTokens;

	/**
	 * Adjusted tokens corresponding to the {@link #originalTokens}. If there is
	 * no corresponding token, this list contains null at the index.
	 */
	private final List<AdjusterToken> mappedAdjustedTokens;

	/** Line offset converted for the adjusted text. */
	private final LineOffsetConverter adjustedLineOffsetConverter;

	/**
	 * Constructor.
	 * 
	 * @param originalText
	 *            the text for which the input locations have been created, i.e.
	 *            the text from the analysis.
	 * @param adjustedText
	 *            the text for which the locations should be adjusted, i.e. the
	 *            local text.
	 */
	public LocationAdjuster(String originalText, String adjustedText) {

		adjustedLineOffsetConverter = new LineOffsetConverter(adjustedText);
		originalTokens = toTokens(originalText);
		mappedAdjustedTokens = new ArrayList<AdjusterToken>(
				Collections.nCopies(originalTokens.size(), (AdjusterToken) null));

		List<AdjusterToken> adjustedTokens = toTokens(adjustedText);
		Delta<AdjusterToken> delta = Diff.computeDelta(originalTokens,
				adjustedTokens);

		int originalIndex = 0;
		int adjustedIndex = 0;
		for (int i = 0; i < delta.getSize(); ++i) {
			int position = delta.getPosition(i);
			if (position > 0) {
				position -= 1;

				while (adjustedIndex < position) {
					mappedAdjustedTokens.set(originalIndex++,
							adjustedTokens.get(adjustedIndex++));
				}
				adjustedIndex += 1;
			} else {
				position = -position - 1;

				while (originalIndex < position) {
					mappedAdjustedTokens.set(originalIndex++,
							adjustedTokens.get(adjustedIndex++));
				}
				originalIndex += 1;
			}
		}

		while (originalIndex < originalTokens.size()) {
			mappedAdjustedTokens.set(originalIndex++,
					adjustedTokens.get(adjustedIndex++));
		}
	}

	/** Splits a string into tokens. */
	private static List<AdjusterToken> toTokens(String s) {
		List<AdjusterToken> tokens = new ArrayList<AdjusterToken>();
		Matcher matcher = TOKEN_PATTERN.matcher(s);
		while (matcher.find()) {
			tokens.add(new AdjusterToken(matcher.group(), matcher.start()));
		}
		return tokens;
	}

	/**
	 * Maps a zero-based offset range (both inclusive) to the adjusted string.
	 * Returns null if the region could not be approximately mapped.
	 */
	/* package */Region getAdjustedRegion(int originalStartOffset,
			int originalEndOffset) {

		Region originalIndexRegion = findOriginalIndexRegion(
				originalStartOffset, originalEndOffset);
		if (originalIndexRegion.isEmpty()) {
			return null;
		}

		int numOriginalTokens = originalIndexRegion.getLength();
		int numAdjustedTokens = 0;

		AdjusterToken firstAdjustedToken = null;
		AdjusterToken lastAdjustedToken = null;
		for (int i = originalIndexRegion.getStart(); i <= originalIndexRegion
				.getEnd(); ++i) {
			AdjusterToken adjustedToken = mappedAdjustedTokens.get(i);
			if (adjustedToken != null) {
				numAdjustedTokens += 1;
				if (firstAdjustedToken == null) {
					firstAdjustedToken = adjustedToken;
				}
				lastAdjustedToken = adjustedToken;
			}
		}

		if (firstAdjustedToken == null || lastAdjustedToken == null
				|| LOSS_FACTOR * numAdjustedTokens < numOriginalTokens) {
			return null;
		}

		return new Region(firstAdjustedToken.startOffset,
				lastAdjustedToken.endOffset);
	}

	/**
	 * Returns the region of indexes in the {@link #originalTokens} contained in
	 * the given offsets.
	 */
	private Region findOriginalIndexRegion(int originalStartOffset,
			int originalEndOffset) {
		AdjusterToken searchToken = new AdjusterToken(null,
				originalStartOffset, originalEndOffset);

		int originalStartTokenIndex = Collections.binarySearch(originalTokens,
				searchToken, AdjusterToken.COMPARE_BY_START_OFFSET);
		if (originalStartTokenIndex < 0) {
			originalStartTokenIndex = -originalStartTokenIndex - 1;
		}

		int originalEndTokenIndex = Collections.binarySearch(originalTokens,
				searchToken, AdjusterToken.COMPARE_BY_END_OFFSET);
		if (originalEndTokenIndex < 0) {
			// we want insertion point -1
			originalEndTokenIndex = -originalEndTokenIndex - 2;
		}

		return new Region(originalStartTokenIndex, originalEndTokenIndex);
	}

	/**
	 * Returns a new location with adjusted offsets (if necessary). Returns null
	 * if the location does not exist anymore.
	 */
	public ElementLocation adjustLocation(ElementLocation location) {
		if (location instanceof TextRegionLocation) {
			return adjustLocation((TextRegionLocation) location);
		}
		// other locations do not have offsets
		return location;
	}

	/**
	 * Returns a new location with adjusted offsets (if necessary). Returns null
	 * if the location does not exist anymore.
	 */
	public TextRegionLocation adjustLocation(TextRegionLocation location) {
		Region adjustedOffsets = getAdjustedRegion(
				location.getRawStartOffset(), location.getRawEndOffset());

		if (adjustedOffsets == null || adjustedOffsets.isEmpty()) {
			return null;
		}

		int newStartOffset = adjustedOffsets.getStart();
		int newEndOffset = adjustedOffsets.getEnd();
		return new TextRegionLocation(location.getLocation(),
				location.getUniformPath(), newStartOffset, newEndOffset,
				adjustedLineOffsetConverter.getLine(newStartOffset),
				adjustedLineOffsetConverter.getLine(newEndOffset));
	}

	/** Simple token representation used in location adjustment. */
	private static class AdjusterToken {

		/** Compares by start offset. */
		private static final Comparator<AdjusterToken> COMPARE_BY_START_OFFSET = new Comparator<AdjusterToken>() {
			@Override
			public int compare(AdjusterToken token1, AdjusterToken token2) {
				return token1.startOffset - token2.startOffset;
			}
		};

		/** Compares by end offset. */
		private static final Comparator<AdjusterToken> COMPARE_BY_END_OFFSET = new Comparator<AdjusterToken>() {
			@Override
			public int compare(AdjusterToken token1, AdjusterToken token2) {
				return token1.endOffset - token2.endOffset;
			}
		};

		/** The text content. */
		private final String text;

		/** The start offset in the text. */
		private final int startOffset;

		/** The inclusive end offset in the text. */
		private final int endOffset;

		/** Constructor. */
		public AdjusterToken(String text, int startOffset) {
			this(text, startOffset, startOffset + text.length() - 1);
		}

		/** Constructor. */
		public AdjusterToken(String text, int startOffset, int endOffset) {
			this.text = text;
			this.startOffset = startOffset;
			this.endOffset = endOffset;
		}

		/** {@inheritDoc} */
		@Override
		public boolean equals(Object obj) {
			return (obj instanceof AdjusterToken)
					&& ((AdjusterToken) obj).text.equals(text);
		}

		/** {@inheritDoc} */
		@Override
		public int hashCode() {
			return text.hashCode();
		}
	}
}
