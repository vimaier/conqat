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
package org.conqat.engine.core.conqatdoc.menu;

import static org.conqat.lib.commons.html.EHTMLAttribute.COLS;
import static org.conqat.lib.commons.html.EHTMLAttribute.NAME;
import static org.conqat.lib.commons.html.EHTMLAttribute.ROWS;
import static org.conqat.lib.commons.html.EHTMLAttribute.SCROLLING;
import static org.conqat.lib.commons.html.EHTMLAttribute.SRC;
import static org.conqat.lib.commons.html.EHTMLElement.FRAME;
import static org.conqat.lib.commons.html.EHTMLElement.FRAMESET;

import java.io.File;

import org.conqat.engine.core.conqatdoc.PageGeneratorBase;
import org.conqat.engine.core.conqatdoc.content.MainPageGenerator;

/**
 * Generator class for the index page. This is the one defining the frames.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: EE86106634EEEB29424F5206394AA69C
 */
public class IndexPageGenerator extends PageGeneratorBase {

	/** The name of the HTML page generated. */
	private static final String PAGE_NAME = "index.html";

	/** The name of the main frame. */
	/* package */static final String MAIN_FRAME = "mainFrame";

	/** The name of the frame listing all specifications (processors, blocks). */
	/* package */static final String LIST_FRAME = "listFrame";

	/** The name of the frame listing all bundles. */
	private static final String BUNDLE_FRAME = "bundleFrame";

	/** Create a new generator for the index page. */
	public IndexPageGenerator(File targetDirectory) {
		super(targetDirectory);
	}

	/** {@inheritDoc} */
	@Override
	protected String getPageName() {
		return PAGE_NAME;
	}

	/** {@inheritDoc} */
	@Override
	protected String getPageTitle() {
		return "ConQAT Installation Overview";
	}

	/** {@inheritDoc} */
	@Override
	protected void appendBody() {
		pageWriter.openElement(FRAMESET, COLS, "20%,80%");
		pageWriter.openElement(FRAMESET, ROWS, "30%,70%");
		pageWriter.addClosedElement(FRAME, SRC, BundlesListGenerator.PAGE_NAME,
				NAME, BUNDLE_FRAME);
		pageWriter.addClosedElement(FRAME, SRC,
				SpecificationListGenerator.PAGE_NAME, NAME, LIST_FRAME);
		pageWriter.closeElement(FRAMESET);
		pageWriter.addClosedElement(FRAME, SRC, MainPageGenerator.PAGE_NAME,
				NAME, MAIN_FRAME, SCROLLING, "yes");
		pageWriter.closeElement(FRAMESET);
	}
}