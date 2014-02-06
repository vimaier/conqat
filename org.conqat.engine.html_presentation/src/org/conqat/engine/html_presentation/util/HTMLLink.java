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
package org.conqat.engine.html_presentation.util;

import static org.junit.Assert.assertTrue;

import org.conqat.engine.html_presentation.layouters.TableLayouter;
import org.conqat.lib.commons.html.EHTMLAttribute;
import org.conqat.lib.commons.html.EHTMLElement;
import org.conqat.lib.commons.html.HTMLWriter;

/**
 * A class representing a link as used in HTML.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 3C804E1229E44B55B2026D232C863125
 */
public class HTMLLink {

	/** The text part. */
	private final String text;

	/** The href part. */
	private final String href;

	/** Optional relative path to root that gets prepended to the href */
	private String relativePathToRoot = "";

	/** Constructor. */
	public HTMLLink(String text, String href) {
		this.text = text;
		this.href = href;
	}

	/** Returns the text of the link. */
	public String getText() {
		return text;
	}

	/** Returns the href part of the link. */
	public String getHref() {
		return href;
	}

	/** Writes this link into the given HTML writer. */
	public void writeTo(HTMLWriter writer) {
		writer.addClosedTextElement(EHTMLElement.A, text, EHTMLAttribute.HREF,
				relativePathToRoot + href, EHTMLAttribute.CLASS,
				TableLayouter.LINK_CLASS);
	}

	/** Sets hrefPrefix. */
	public void setRelativePathToRoot(String relativePathToRoot) {
		// The final field relativePathToRoot gets set in the constructor of
		// WriterBase. The constructor already appends the "/" if its missing.
		// However, I put this assertion here in case the constructor should
		// ever stop to do so.
		assertTrue("Relative path to root must end with '/'",
				relativePathToRoot.endsWith("/"));
		this.relativePathToRoot = relativePathToRoot;
	}
}