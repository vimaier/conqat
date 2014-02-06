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
import java.util.ArrayList;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.html_presentation.HTMLPresentation;
import org.conqat.engine.html_presentation.IPageDescriptor;
import org.conqat.engine.html_presentation.PageWriter;
import org.conqat.engine.html_presentation.util.LayouterBase;
import org.conqat.lib.commons.html.EHTMLAttribute;
import org.conqat.lib.commons.html.EHTMLElement;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 3185330BAD716A019E23F0C63A532082
 */
@AConQATProcessor(description = "This layout renders a list of links which point to the original pages.")
public class PageLinkLayouter extends LayouterBase {

	/** List of pages. */
	private final ArrayList<IPageDescriptor> pages = new ArrayList<IPageDescriptor>();

	/** The output directory. */
	private File outputDirectory;

	/** Add page */
	@AConQATParameter(name = ConQATParamDoc.INPUT_NAME, minOccurrences = 1, description = ConQATParamDoc.INPUT_DESC)
	public void addPage(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) IPageDescriptor page) {
		pages.add(page);
	}

	/**
	 * Set output directory. Must be the same as specified for
	 * {@link HTMLPresentation}.
	 */
	@AConQATParameter(name = "output", minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "Output directory; must be the same as specified for HtmlPresentation.")
	public void setOutputDirectory(
			@AConQATAttribute(name = "dir", description = "Name of the output directory") String outputDirectoryName) {
		outputDirectory = new File(outputDirectoryName);
	}

	/** Returns {@inheritDoc} */
	@Override
	protected String getIconName() {
		return PageMergeLayouter.MERGED_PAGE_ICON_NAME;
	}

	/** {@inheritDoc} */
	@Override
	protected void layoutPage() throws ConQATException {
		writer.openElement(EHTMLElement.UL);
		for (IPageDescriptor page : pages) {
			new PageWriter(outputDirectory, page).write();

			writer.openElement(EHTMLElement.LI);
			writer.addClosedTextElement(EHTMLElement.A, page.getName(),
					EHTMLAttribute.HREF, page.getFilename());
			writer.closeElement(EHTMLElement.LI);
		}
		writer.closeElement(EHTMLElement.UL);
	}

	/** Returns assessment of first page. */
	@Override
	protected Object getSummary() {
		// must have at least one entry
		return pages.get(0).getSummary();
	}
}