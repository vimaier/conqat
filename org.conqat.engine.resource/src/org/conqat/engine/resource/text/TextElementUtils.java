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
package org.conqat.engine.resource.text;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IContentAccessor;
import org.conqat.engine.resource.scope.memory.InMemoryContentAccessor;
import org.conqat.engine.resource.util.ResourceUtils;
import org.conqat.lib.commons.cache4j.CacheFactory;
import org.conqat.lib.commons.cache4j.ICache;
import org.conqat.lib.commons.error.NeverThrownRuntimeException;
import org.conqat.lib.commons.factory.IParameterizedFactory;
import org.conqat.lib.commons.filesystem.CanonicalFile;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.region.Region;
import org.conqat.lib.commons.string.LineOffsetConverter;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.commons.xml.XMLUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Utility methods working on {@link ITextElement}s.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46847 $
 * @ConQAT.Rating GREEN Hash: B1DA72C84EC2CDFDE4FF98AB4C8BCACD
 */
public class TextElementUtils {

	/** Cache for the {@link LineOffsetConverter}. */
	private static final ICache<String, LineOffsetConverter, NeverThrownRuntimeException> lineOffsetConverterCache = CacheFactory
			.obtainCache(
					TextElementUtils.class,
					new IParameterizedFactory<LineOffsetConverter, String, NeverThrownRuntimeException>() {
						@Override
						public LineOffsetConverter create(String s) {
							return new LineOffsetConverter(s);
						}
					});

	/**
	 * Count lines of code of the element's <em>filtered</em> content. If
	 * filters are used, the filtered content may be smaller than the original
	 * content of the element and, hence, this method may return a smaller value
	 * than its counterpart
	 * {@link TextElementUtils#countUnfilteredLOC(ITextElement)}.
	 */
	public static int countLOC(ITextElement element) throws ConQATException {
		return StringUtils.splitLines(element.getTextContent()).length;
	}

	/**
	 * Count lines of code of the element's <em>unfiltered</em> content, i.e.,
	 * the lines as they occur in the original text of the element. If filters
	 * are used and you want to count the lines only of the filtered content,
	 * use this method's counterpart
	 * {@link TextElementUtils#countLOC(ITextElement)}.
	 */
	public static int countUnfilteredLOC(ITextElement element)
			throws ConQATException {
		return StringUtils.splitLines(element.getUnfilteredTextContent()).length;
	}

	/**
	 * This method returns a list with all non-whitespace lines. Each line
	 * itself is also trimmed.
	 */
	public static List<String> getNormalizedContent(ITextElement textElement)
			throws ConQATException {
		return getNormalizedContent(textElement.getTextContent());
	}

	/**
	 * This method returns a list with all non-whitespace lines. Each line
	 * itself is also trimmed.
	 */
	public static List<String> getNormalizedContent(String content) {
		List<String> result = new ArrayList<String>();
		for (String line : StringUtils.splitLines(content)) {
			String trimmed = line.trim();
			if (!trimmed.isEmpty()) {
				result.add(trimmed);
			}
		}
		return result;
	}

	/**
	 * Returns the content of a text element as array of strings representing
	 * its lines.
	 * 
	 * @param element
	 *            The element to split content of.
	 * 
	 * @return The lines of the file
	 */
	public static String[] getLines(ITextElement element)
			throws ConQATException {
		String content = element.getTextContent();

		if (StringUtils.isEmpty(content)) {
			return new String[0];
		}

		return StringUtils.splitLines(content);
	}

	/**
	 * Returns a {@link LineOffsetConverter} for a specific string. The main
	 * difference to directly calling the constructor is that the result is
	 * potentially cached. The line breaks in the string must be normalized to
	 * '\n', as is the case for all strings obtained from {@link ITextElement}.
	 */
	public static LineOffsetConverter getLineOffsetConverter(String s) {
		return lineOffsetConverterCache.obtain(s);
	}

	/**
	 * Returns the start offset for a given line in the string. The line breaks
	 * in the string must be normalized to '\n', as is the case for all strings
	 * obtained from {@link ITextElement}.
	 */
	public static int lineToOffset(String s, int line) {
		return getLineOffsetConverter(s).getOffset(line);
	}

	/**
	 * Returns the line for a given offset in the string. The line breaks in the
	 * string must be normalized to '\n', as is the case for all strings
	 * obtained from {@link ITextElement}.
	 */
	public static int offsetToLine(String s, int offset) {
		return getLineOffsetConverter(s).getLine(offset);
	}

	/**
	 * Converts a {@link Region} based on filtered offsets w.r.t a
	 * {@link ITextElement} to a {@link Region} based on unfiltered lines.
	 */
	public static Region convertFilteredOffsetRegionToRawLineRegion(
			ITextElement element, Region region) throws ConQATException {
		int unfiltertedStart = element.getUnfilteredOffset(region.getStart());
		int lineStart = offsetToLine(element.getUnfilteredTextContent(),
				unfiltertedStart);
		int unfiltertedEnd = element.getUnfilteredOffset(region.getEnd());
		int lineEnd = offsetToLine(element.getUnfilteredTextContent(),
				unfiltertedEnd);
		return new Region(lineStart, lineEnd);
	}

	/**
	 * Converts from 0-based offsets in the filtered text to 1-based line
	 * numbers in the unfiltered text.
	 */
	public static int convertFilteredOffsetToUnfilteredLine(
			ITextElement textElement, int filteredOffset)
			throws ConQATException {
		return textElement.convertUnfilteredOffsetToLine(textElement
				.getUnfilteredOffset(filteredOffset));
	}

	/**
	 * Converts from 0-based offsets in the unfiltered text to 1-based line
	 * numbers in the filtered text.
	 */
	public static int convertRawOffsetToFilteredLine(ITextElement textElement,
			int rawOffset) throws ConQATException {
		int filteredOffset = textElement.getFilteredOffset(rawOffset);
		return textElement.convertFilteredOffsetToLine(filteredOffset);
	}

	/**
	 * Constructs a {@link TextElement} using an {@link InMemoryContentAccessor}
	 * and a UTF-8 encoding.
	 */
	public static TextElement createInMemoryTextElement(String uniformPath,
			String content) {
		IContentAccessor accessor = new InMemoryContentAccessor(uniformPath,
				StringUtils.stringToBytes(content));
		return new TextElement(accessor, FileSystemUtils.UTF8_CHARSET);
	}

	/**
	 * Parses the content of a text element using an XML SAX parser. The method
	 * automatically uses direct disk access for files that are on the
	 * hard-disk, thus preventing memory issues when loading large files into
	 * memory. Note that this bypassing of the element also means that no
	 * filters are applied.
	 */
	public static void parseSAX(ITextElement inputElement,
			DefaultHandler saxHandler) throws ConQATException {
		CanonicalFile underlyingFile = ResourceUtils.getFile(inputElement);
		try {
			if (underlyingFile != null) {
				// if the inputElement is a plain file, we read directly from
				// the file to avoid any memory overhead
				XMLUtils.parseSAX(underlyingFile, saxHandler);
			} else {
				// otherwise we use the "normal" access using the element's
				// getTextContent() and accept the penalty of keeping the entire
				// file in memory
				StringReader reader = new StringReader(
						inputElement.getTextContent());
				XMLUtils.parseSAX(new InputSource(reader), saxHandler);
			}
		} catch (SAXException e) {
			throw new ConQATException(e);
		} catch (IOException e) {
			throw new ConQATException(e);
		}
	}
}
