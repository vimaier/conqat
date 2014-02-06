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
package org.conqat.engine.html_presentation.base;

import static org.conqat.lib.commons.html.EHTMLAttribute.COLS;
import static org.conqat.lib.commons.html.EHTMLAttribute.MARGINHEIGHT;
import static org.conqat.lib.commons.html.EHTMLAttribute.MARGINWIDTH;
import static org.conqat.lib.commons.html.EHTMLAttribute.NAME;
import static org.conqat.lib.commons.html.EHTMLAttribute.NORESIZE;
import static org.conqat.lib.commons.html.EHTMLAttribute.ROWS;
import static org.conqat.lib.commons.html.EHTMLAttribute.SCROLLING;
import static org.conqat.lib.commons.html.EHTMLAttribute.SRC;
import static org.conqat.lib.commons.html.EHTMLElement.FRAME;
import static org.conqat.lib.commons.html.EHTMLElement.FRAMESET;

import java.io.File;

import org.conqat.engine.html_presentation.util.WriterBase;

/**
 * This class generates the index page of the output ({@value #PAGE_NAME}).
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 59D72EF7A0E07FEFE007976B53E7F5C4
 */
public class IndexPageWriter extends WriterBase {

	/** Name of the page. */
	public static final String PAGE_NAME = "index.html";

	/** Name of the analyzed project. */
	private final String projectName;

	/**
	 * Create new index page writer.
	 * 
	 * @param outputDirectory
	 *            output directory.
	 * @param projectName
	 *            name of the analyzed project.
	 */
	public IndexPageWriter(File outputDirectory, String projectName) {
		super(new File(outputDirectory, PAGE_NAME));
		this.projectName = projectName;
	}

	/** {@inheritDoc} */
	@Override
	protected void addBody() {

		// escape if we are embedded in frames (see CR#3962)
		writer.insertJavaScript("if (window != top) top.location.href = location.href;");
		
		writer.openElement(FRAMESET, ROWS, "41,*");
		writer.addClosedElement(FRAME, SRC, "header.html", SCROLLING, "no",
				MARGINWIDTH, "0", MARGINHEIGHT, "0", NORESIZE, "noresize");

		writer.openElement(FRAMESET, COLS, "200,*");
		writer.addClosedElement(FRAME, SRC, "navigation.html", MARGINWIDTH,
				"0", MARGINHEIGHT, "0");
		writer.addClosedElement(FRAME, SRC, "overview.html", NAME, "content",
				MARGINWIDTH, "0", MARGINHEIGHT, "0");
		writer.closeElement(FRAMESET);

		writer.closeElement(FRAMESET);
	}

	/** {@inheritDoc} */
	@Override
	protected String getTitle() {
		return "ConQAT: " + projectName;
	}
}