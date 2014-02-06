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
package org.conqat.engine.resource.analysis;

import java.util.HashSet;
import java.util.Set;

import org.conqat.engine.commons.findings.FindingCategory;
import org.conqat.engine.commons.findings.FindingGroup;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.engine.resource.text.TextElementUtils;
import org.conqat.engine.resource.util.ResourceUtils;
import org.conqat.lib.commons.math.Range;
import org.conqat.lib.commons.string.StringUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating GREEN Hash: 37CFD8C52756D90A5F5A7C60A6FD6721
 */
@AConQATProcessor(description = "Checks if an element contains characters "
		+ "outside a given character code range. To work properly, the encoding of "
		+ "the analyzed element must be specified correctly. However, for "
		+ "the most common case that checks if a source code element contains characters "
		+ "outside the US-ASCII range, the encoding does, in fact, not matter as all "
		+ "encodings behave similar in the US-ASCII range. If no ranges are specified, this "
		+ "processor considers the character codes between 32 and 126 (inclusive), the "
		+ "tab character, the line feed and the carriage return as valid.")
public class InvalidCharacterAnalyzer extends TextElementAnalyzerBase {

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "Invalid Characters", type = "java.util.List<org.conqat.engine.commons.findings.Finding>")
	public static final String KEY = "Invalid Characters";

	/** Valid ranges. */
	private final HashSet<Range> ranges = new HashSet<Range>();

	/** Flag that indicates if the processor works in lenient mode. */
	private boolean lenient = true;

	/** Finding group used for the results. */
	private FindingGroup group;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "range", description = "Adds a range of legal characters;"
			+ " ranges are inclusive. "
			+ "See processor description for the default case.")
	public void addRange(
			@AConQATAttribute(name = "lower", description = "Lower bound") int lower,
			@AConQATAttribute(name = "upper", description = "Upper bound") int upper) {
		ranges.add(new Range(lower, upper));
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "lenient", description = "If set to false, the processor "
			+ "fails if an element could not be read. Otherwise, it only logs a warning."
			+ "This is true by default.", maxOccurrences = 1)
	public void setLenient(
			@AConQATAttribute(name = "value", description = "Value for leniency") boolean lenient) {
		this.lenient = lenient;
	}

	/** {@inheritDoc} */
	@Override
	protected void setUp(ITextResource root) throws ConQATException {
		super.setUp(root);
		if (ranges.isEmpty()) {
			addDefaultValidCharacterRanges(ranges);
		}
		FindingCategory category = NodeUtils.getFindingReport(root)
				.getOrCreateCategory("Illegal Character search");
		group = category.getOrCreateFindingGroup("Character outside ranges "
				+ StringUtils.concat(ranges));
	}

	/** Adds default valid character ranges to a set */
	public static void addDefaultValidCharacterRanges(Set<Range> validRanges) {
		validRanges.add(new Range(32, 126));
		validRanges.add(new Range(9, 10));
		validRanges.add(new Range(13, 13));
	}

	/** {@inheritDoc} */
	@Override
	protected void analyzeElement(ITextElement element) throws ConQATException {
		try {
			analyzeCharacters(element);
		} catch (ConQATException e) {
			if (lenient) {
				getLogger().warn(
						"Could not analyze element " + element.getLocation()
								+ ": " + e.getMessage(), e);
			} else {
				throw e;
			}
		}
	}

	/** Analyze characters of an element. */
	private void analyzeCharacters(ITextElement element) throws ConQATException {
		String[] lines = TextElementUtils.getLines(element);

		for (int currentLine = 0; currentLine < lines.length; ++currentLine) {
			String line = lines[currentLine];
			for (int i = 0; i < line.length(); ++i) {
				int codePoint = Character.codePointAt(line, i);
				if (!isValid(codePoint, ranges)) {
					createFinding(element, currentLine, codePoint);

					// at most one finding per line
					break;
				}
			}
		}
	}

	/** Creates the finding. */
	private void createFinding(ITextElement element, int line, int codePoint)
			throws ConQATException {
		ResourceUtils.createAndAttachFindingForFilteredLine(group,
				"Invalid character with code point " + codePoint + " |"
						+ String.valueOf(Character.toChars(codePoint)) + "|",
				element, line + 1, KEY);
	}

	/** Checks if at least one of the ranges contains the code point. */
	public static boolean isValid(int codePoint, Set<Range> ranges) {
		for (Range range : ranges) {
			if (range.contains(codePoint)) {
				return true;
			}
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	protected String[] getKeys() {
		return new String[] { KEY };
	}

}