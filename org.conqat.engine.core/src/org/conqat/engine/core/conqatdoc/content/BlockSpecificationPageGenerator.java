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

import static org.conqat.lib.commons.html.EHTMLAttribute.BORDER;
import static org.conqat.lib.commons.html.EHTMLAttribute.SRC;
import static org.conqat.lib.commons.html.EHTMLAttribute.USEMAP;
import static org.conqat.lib.commons.html.EHTMLElement.H2;
import static org.conqat.lib.commons.html.EHTMLElement.IMG;
import static org.conqat.lib.commons.html.EHTMLElement.TABLE;
import static org.conqat.lib.commons.html.EHTMLElement.TD;
import static org.conqat.lib.commons.html.EHTMLElement.TR;

import java.io.File;
import java.io.IOException;

import org.conqat.engine.core.bundle.BundleInfo;
import org.conqat.engine.core.conqatdoc.JavaDocLinkResolver;
import org.conqat.engine.core.conqatdoc.layout.BlockSpecificationRenderer;
import org.conqat.engine.core.driver.specification.BlockSpecification;
import org.conqat.engine.core.driver.specification.SpecificationOutput;
import org.conqat.lib.commons.html.EHTMLAttribute;
import org.conqat.lib.commons.html.EHTMLElement;

/**
 * Page generator for block specifications. Much of the code deals with
 * generating the graphical view of the block.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 8D0356CE7D628C79ACCF661A6D38AB2C
 */
public class BlockSpecificationPageGenerator extends SpecificationPageGenerator {

	/** The suffix of the HTML page generated. */
	public static final String PAGE_SUFFIX = "_block.html";

	/** The block handled here. */
	private final BlockSpecification blockSpecification;

	/** Flag indicating whether a graph should be generated or not. */
	private final boolean generateGraph;

	/**
	 * Create a new generator for a bundle details page.
	 * 
	 * @param targetDirectory
	 *            the directory to generate into.
	 * @param blockSpecification
	 *            the specification being displayed by this page.
	 * @param bundle
	 *            the bundle the specification was taken from.
	 * @param javaDocResolver
	 *            the resolver used for handling JavaDoc links.
	 */
	public BlockSpecificationPageGenerator(File targetDirectory,
			BlockSpecification blockSpecification, BundleInfo bundle,
			JavaDocLinkResolver javaDocResolver) {
		this(targetDirectory, blockSpecification, bundle, javaDocResolver, true);
	}

	/**
	 * Create a new generator for a bundle details page.
	 * 
	 * @param targetDirectory
	 *            the directory to generate into.
	 * @param blockSpecification
	 *            the specification being displayed by this page.
	 * @param bundle
	 *            the bundle the specification was taken from.
	 * @param javaDocResolver
	 *            the resolver used for handling JavaDoc links.
	 * @param generateGraph
	 *            indicates whether a graph should be generated or not.
	 */
	public BlockSpecificationPageGenerator(File targetDirectory,
			BlockSpecification blockSpecification, BundleInfo bundle,
			JavaDocLinkResolver javaDocResolver, boolean generateGraph) {
		super(targetDirectory, blockSpecification, bundle, javaDocResolver);
		this.blockSpecification = blockSpecification;
		this.generateGraph = generateGraph;
	}

	/** {@inheritDoc} */
	@Override
	protected String getPageSuffix() {
		return PAGE_SUFFIX;
	}

	/** {@inheritDoc} */
	@Override
	protected String getSpecificationTypeName() {
		return "Block";
	}

	/** {@inheritDoc} */
	@Override
	protected void writeKeys() {
		// blocks do not have key documentation (yet?)
	}

	/** {@inheritDoc} */
	@Override
	protected void writeOutputs() {
		pageWriter.addClosedTextElement(H2, "Outputs");
		pageWriter.openElement(TABLE);
		writeTableHeader("Name", "Type", "Description");
		for (SpecificationOutput output : blockSpecification.getOutputs()) {
			pageWriter.openElement(TR);
			pageWriter.addClosedTextElement(TD, output.getName());

			pageWriter.openElement(TD);
			javaDocResolver
					.resolveLink(output.getType().toString(), pageWriter);
			pageWriter.closeElement(TD);

			pageWriter.addClosedTextElement(TD, nullProtect(output.getDoc()));
			pageWriter.closeElement(TR);
		}
		pageWriter.closeElement(TABLE);
	}

	/** {@inheritDoc} */
	@Override
	protected void writeAdditionalSections() throws IOException {
		if (!generateGraph) {
			return;
		}

		pageWriter.addClosedTextElement(H2, "Graphical representation");
		String fileExtension = "png";
		String filename = blockSpecification.getName() + "." + fileExtension;

		final String IMAGEMAP_NAME = "IMAGEMAP";
		String imageMap = new BlockSpecificationRenderer(blockSpecification)
				.renderGraph(getFile(filename), fileExtension);

		pageWriter.addClosedElement(IMG, SRC, filename, BORDER, 0, USEMAP, "#"
				+ IMAGEMAP_NAME);
		pageWriter.openElement(EHTMLElement.MAP, EHTMLAttribute.ID,
				IMAGEMAP_NAME, EHTMLAttribute.NAME, IMAGEMAP_NAME);
		pageWriter.addRawString(imageMap);
		pageWriter.closeElement(EHTMLElement.MAP);
	}
}