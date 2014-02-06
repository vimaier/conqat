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
package org.conqat.engine.text.msword;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.poi.hwpf.extractor.WordExtractor;
import org.conqat.engine.resource.IContentAccessor;
import org.conqat.engine.resource.text.TextElement;
import org.conqat.engine.resource.text.filter.base.ITextFilter;

import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.engine.core.core.ConQATException;

/**
 * A {@link TextElement} that transparently handles the text access to MS Word
 * documents.
 * 
 * @author $Author: juergens $
 * @version $Rev: 35207 $
 * @ConQAT.Rating GREEN Hash: 410228EFEEFDEB6AE818159D5B41D9D1
 */
public class MSWordTextElement extends TextElement {

	/** If set to true, each a newline is inserted after each dot. */
	private final boolean wrapAtDot;

	/** If set to true, each a newline is inserted after each whitespace block. */
	private final boolean wrapAtWhitespace;

	/** Constructor. */
	public MSWordTextElement(IContentAccessor accessor, ITextFilter filter,
			boolean wrapAtDot, boolean wrapAtWhitespace) {
		// encoding is not relevant, but expected for TextElement
		super(accessor, Charset.defaultCharset(), filter);

		this.wrapAtDot = wrapAtDot;
		this.wrapAtWhitespace = wrapAtWhitespace;
	}

	/** {@inheritDoc} */
	@Override
	public String getUnfilteredTextContent() throws ConQATException {
		InputStream in = new ByteArrayInputStream(getContent());
		String[] paragraphs;
		try {
			WordExtractor extractor = new WordExtractor(in);
			paragraphs = extractor.getParagraphText();
		} catch (IOException e) {
			throw new ConQATException(
					"Had an error while reading word document: "
							+ e.getMessage(), e);
		} finally {
			FileSystemUtils.close(in);
		}

		String text = StringUtils.concat(paragraphs, StringUtils.CR);

		if (wrapAtDot) {
			text = text.replaceAll("[.]", "." + StringUtils.CR);
		}

		if (wrapAtWhitespace) {
			text = text.replaceAll("\\s+", StringUtils.CR);
		}

		// normalize linebreaks according to method's contract
		return StringUtils.replaceLineBreaks(text, "\n");
	}
}