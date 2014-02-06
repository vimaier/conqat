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

import static org.conqat.lib.commons.html.EHTMLAttribute.CONTENT;
import static org.conqat.lib.commons.html.EHTMLAttribute.HREF;
import static org.conqat.lib.commons.html.EHTMLAttribute.HTTP_EQUIV;
import static org.conqat.lib.commons.html.EHTMLAttribute.REL;
import static org.conqat.lib.commons.html.EHTMLAttribute.SRC;
import static org.conqat.lib.commons.html.EHTMLAttribute.TYPE;
import static org.conqat.lib.commons.html.EHTMLElement.HEAD;
import static org.conqat.lib.commons.html.EHTMLElement.HTML;
import static org.conqat.lib.commons.html.EHTMLElement.LINK;
import static org.conqat.lib.commons.html.EHTMLElement.META;
import static org.conqat.lib.commons.html.EHTMLElement.SCRIPT;
import static org.conqat.lib.commons.html.EHTMLElement.TITLE;

import java.io.File;
import java.io.IOException;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.html_presentation.CSSMananger;
import org.conqat.engine.html_presentation.javascript.JavaScriptManager;
import org.conqat.lib.commons.date.DateUtils;
import org.conqat.lib.commons.html.HTMLWriter;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Base class for all writers in the HTML presentation. Subclasses implement the
 * {@link #addBody()} template-method and use the {@link #writer}-object to
 * create the page content.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 68FA35865BC180CEE61C304C7B0DAA19
 */
public abstract class WriterBase {

	/** The file to write to. */
	private final File file;

	/**
	 * The relative path to the root directory of the HTML presentation. This is
	 * the directory typically called "output.dir" in our processors.
	 */
	protected final String relativePathToRoot;

	/** The underlying HTML writer. */
	protected HTMLWriter writer;

	/** Constructor. */
	protected WriterBase(File file) {
		this(file, StringUtils.EMPTY_STRING);
	}

	/** Constructor. */
	protected WriterBase(File file, String relativePathToRoot) {
		this.file = file;

		if (!relativePathToRoot.isEmpty() && !relativePathToRoot.endsWith("/")) {
			relativePathToRoot += "/";
		}

		this.relativePathToRoot = relativePathToRoot;
	}

	/**
	 * Starts writing of the page. Creation of content is delegated to
	 * {@link #addBody()}.
	 * 
	 * @throws ConQATException
	 *             if the writer could no write to the specified file.
	 */
	public final void write() throws ConQATException {
		try {
			writer = new HTMLWriter(file, CSSMananger.getInstance());
		} catch (IOException ex) {
			throw new ConQATException(ex);
		}

		writer.addStdHeader();

		writer.addComment("Generated by ConQAT (" + getClass().getSimpleName()
				+ ") @ " + DateUtils.getNow());

		writer.openElement(HTML);
		writer.openElement(HEAD);

		writer.addClosedElement(META, HTTP_EQUIV, "content-type", CONTENT,
				"application/xhtml+xml; charset=UTF-8");

		writer.addClosedTextElement(TITLE, getTitle());

		writer.addClosedElement(LINK, REL, "SHORTCUT ICON", HREF,
				relativePathToRoot + "images/conqat-icon.ico");
		writer.addClosedElement(LINK, REL, "stylesheet", TYPE, "text/css",
				HREF, relativePathToRoot + "css/style.css");

		writer.openElement(SCRIPT, SRC, relativePathToRoot
				+ JavaScriptManager.SCRIPT_NAME, TYPE, "text/javascript");
		// this is required, as some browsers choke at a directly closed
		// script element
		writer.addText("");
		writer.closeElement(SCRIPT);

		writer.closeElement(HEAD);

		addBody();

		writer.closeElement(HTML);
		writer.close();
	}

	/**
	 * Template method for adding the page body. Overriding methods use the
	 * {@link #writer} to write the body.
	 */
	protected abstract void addBody() throws ConQATException;

	/** Get title of page. */
	protected abstract String getTitle();
}