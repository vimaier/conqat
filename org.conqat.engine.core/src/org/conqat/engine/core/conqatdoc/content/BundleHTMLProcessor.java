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
package org.conqat.engine.core.conqatdoc.content;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.conqat.engine.core.bundle.BundleInfo;
import org.conqat.engine.core.conqatdoc.JavaDocLinkResolver;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.html.EHTMLElement;
import org.conqat.lib.commons.html.HTMLWriter;

/**
 * This class is responsible for lading and processing the bundle HTML.
 * Additionally it supports some JavaDoc style tags using the '@' inside of
 * curly braces followed by a fully qualified name. These tags are
 * <ul>
 * <li>link: link to JavaDoc</li>
 * <li>processor: link to a processor description</li>
 * <li>block: link to a block description</li>
 * </ul>
 * 
 * @author hummelb
 * @author $Author: juergens $
 * @version $Rev: 35194 $
 * @ConQAT.Rating GREEN Hash: 214B9A3457D87E0DB1F604DF62D031C0
 */
/* package */class BundleHTMLProcessor {

	/** The name of the bundle HTML file: {@value #BUNDLE_HTML}. */
	public static final String BUNDLE_HTML = "bundle.html";

	/**
	 * The pattern used for extracting the body portion of the HTML file. It
	 * accepts everything between an opening anc a closing body tag, where the
	 * opening tag we have additional "clutter" (i.e. attributes).
	 */
	private static final Pattern BODY_PATTERN = Pattern.compile(
			"<body(?:\\s+[^>]*)?>(.+)</body>", Pattern.CASE_INSENSITIVE
					| Pattern.DOTALL);

	/**
	 * The pattern used for finding replaceable tags. This matches patterns of
	 * an '@'-sign followed by a keyword some spaces and a class/package name,
	 * all in curly braces. These are expressions similar to those used in the
	 * package.html (e.g. link).
	 */
	private static final Pattern REPLACEMENT_PATTERN = Pattern
			.compile("\\{@(\\w+)\\s+([a-zA-Z0-9._]+)\\}");

	/** Resolver used in this class for JavaDoC links. */
	private final JavaDocLinkResolver javaDocResolver;

	/** Constructor. */
	public BundleHTMLProcessor(JavaDocLinkResolver javaDocResolver) {
		this.javaDocResolver = javaDocResolver;
	}

	/**
	 * Read the bundle HTML from the given bundle and add the contents to the
	 * given HTML writer. If there is no bundle HTML, nothing will be written.
	 */
	public void process(BundleInfo bundle, HTMLWriter pageWriter)
			throws IOException {
		File bundleHTML = new File(bundle.getLocation(), BUNDLE_HTML);
		if (!bundleHTML.isFile()) {
			System.err.println("Warning: No " + BUNDLE_HTML
					+ " found for bundle " + bundle.getId());
			return;
		}

		String text = FileSystemUtils.readFile(bundleHTML);
		Matcher bodyMatcher = BODY_PATTERN.matcher(text);
		if (!bodyMatcher.find()) {
			System.err.println("Warning: " + BUNDLE_HTML
					+ " contains no body for bundle " + bundle.getId());
			return;
		}

		pageWriter.addClosedElement(EHTMLElement.BR);
		pageWriter.addRawString(replaceTags(bodyMatcher.group(1)));
	}

	/**
	 * Replaces all detected and resolved tags ('@' in braces) in the given text
	 * and returns the result.
	 */
	private String replaceTags(String text) {
		StringBuffer result = new StringBuffer();
		Matcher m = REPLACEMENT_PATTERN.matcher(text);
		while (m.find()) {
			String replacement = handleTag(m.group(1), m.group(2));
			if (replacement == null) {
				replacement = m.group(0);
			}
			m.appendReplacement(result, replacement);
		}
		m.appendTail(result);
		return result.toString();
	}

	/**
	 * Returns the result for handling the given tag (and parameter). If it
	 * could not be handled, <code>null</code> is returned.
	 */
	private String handleTag(String tagName, String parameter) {
		if ("link".equals(tagName)) {
			String link = javaDocResolver.resolveLink(parameter);
			if (link == null) {
				return parameter;
			}
			return "<a href=\"" + link + "\">"
					+ parameter.replaceFirst("^[^<]+\\.", "") + "</a>";
		} else if ("processor".equals(tagName)) {
			return "<a href=\"" + parameter
					+ ProcessorSpecificationPageGenerator.PAGE_SUFFIX + "\">"
					+ parameter + "</a>";
		} else if ("block".equals(tagName)) {
			return "<a href=\"" + parameter
					+ BlockSpecificationPageGenerator.PAGE_SUFFIX + "\">"
					+ parameter + "</a>";
		}
		return null;
	}

}