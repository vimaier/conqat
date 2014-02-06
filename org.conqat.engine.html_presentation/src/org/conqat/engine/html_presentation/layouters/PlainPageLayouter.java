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
package org.conqat.engine.html_presentation.layouters;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.html_presentation.util.LayouterBase;

/**
 * A simple layouter that includes HTML pages in the output.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: ED0D33B1B2AADF2BCE8FE51D2A966131
 */
@AConQATProcessor(description = "A simple layouter to includes HTML pages "
		+ "in the output. Pages to include must specify the body-part of an "
		+ "HTML page only.")
public class PlainPageLayouter extends LayouterBase {

	/** Name of the icon file for pages. */
	private static final String PAGE_ICON_NAME = "page.gif";

	/**
	 * The pattern used for extracting the body portion of the HTML file. It
	 * accepts everything between an opening and a closing body tag, where the
	 * opening tag we have additional "clutter" (i.e. attributes).
	 */
	private static final Pattern BODY_PATTERN = Pattern.compile(
			"<body(?:\\s+[^>]*)?>(.+)</body>", Pattern.CASE_INSENSITIVE
					| Pattern.DOTALL);

	/** The file to process */
	private File file;

	/**
	 * Set file name.
	 */
	@AConQATParameter(name = "file", minOccurrences = 1, maxOccurrences = 1, description = "The file to include.")
	public void setFilename(
			@AConQATAttribute(name = "name", description = "Name of the file.")
			String filename) {

		file = new File(filename);
	}

	/** {@inheritDoc} */
	@Override
	protected void layoutPage() throws ConQATException {
		String content;
		try {
			content = FileSystemUtils.readFile(file);
		} catch (IOException e) {
			throw new ConQATException(e);
		}

		Matcher bodyMatcher = BODY_PATTERN.matcher(content);
		if (!bodyMatcher.find()) {
			throw new ConQATException("File contains no body!");
		}
		writer.addRawString(bodyMatcher.group(1));
	}

	/** {@inheritDoc} */
	@Override
	public String getIconName() {
		return PAGE_ICON_NAME;
	}
}