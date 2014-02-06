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

import static org.conqat.lib.commons.html.EHTMLElement.H2;
import static org.conqat.lib.commons.html.EHTMLElement.P;
import static org.conqat.lib.commons.html.EHTMLElement.TABLE;
import static org.conqat.lib.commons.html.EHTMLElement.TD;
import static org.conqat.lib.commons.html.EHTMLElement.TR;

import java.io.File;

import org.conqat.engine.core.bundle.BundleInfo;
import org.conqat.engine.core.conqatdoc.JavaDocLinkResolver;
import org.conqat.engine.core.driver.specification.KeySpecification;
import org.conqat.engine.core.driver.specification.ProcessorSpecification;

/**
 * Page generator for processor specifications.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: CED5D71B334CA8A06D70454C064EB933
 */
public class ProcessorSpecificationPageGenerator extends
		SpecificationPageGenerator {

	/** The suffix of the HTML page generated. */
	public static final String PAGE_SUFFIX = "_proc.html";

	/** The processor handled here. */
	private final ProcessorSpecification processorSpecification;

	/**
	 * Create a new generator for a bundle details page.
	 * 
	 * @param targetDirectory
	 *            the directory to generate into.
	 * @param processorSpecification
	 *            the specification being displayed by this page.
	 * @param bundle
	 *            the bundle the specification was taken from.
	 * @param javaDocResolver
	 *            the resolver used for handling JavaDoc links.
	 */
	public ProcessorSpecificationPageGenerator(File targetDirectory,
			ProcessorSpecification processorSpecification, BundleInfo bundle,
			JavaDocLinkResolver javaDocResolver) {
		super(targetDirectory, processorSpecification, bundle, javaDocResolver);
		this.processorSpecification = processorSpecification;
	}

	/** {@inheritDoc} */
	@Override
	protected String getPageSuffix() {
		return PAGE_SUFFIX;
	}

	/** {@inheritDoc} */
	@Override
	protected String getSpecificationTypeName() {
		return "Processor";
	}

	/** {@inheritDoc} */
	@Override
	protected void writeAdditionalSections() {
		// There are no additional sections for processors.
	}

	/** {@inheritDoc} */
	@Override
	protected void writeKeys() {

		if (processorSpecification.getKeys().isEmpty()) {
			return;
		}

		pageWriter.addClosedTextElement(H2, "Keys");
		pageWriter.openElement(TABLE);
		writeTableHeader("Name", "Type", "Description");
		for (KeySpecification key : processorSpecification.getKeys()) {
			pageWriter.openElement(TR);
			pageWriter.addClosedTextElement(TD, key.getName());

			pageWriter.openElement(TD);
			javaDocResolver.resolveLink(key.getType(), pageWriter);
			pageWriter.closeElement(TD);

			pageWriter.addClosedTextElement(TD, nullProtect(key.getDoc()));
			pageWriter.closeElement(TR);
		}
		pageWriter.closeElement(TABLE);
	}

	/** {@inheritDoc} */
	@Override
	protected void writeOutputs() {
		pageWriter.addClosedTextElement(H2, "Output Type");
		pageWriter.openElement(P);
		javaDocResolver.resolveLink(processorSpecification.getOutputs()[0]
				.getType().toString(), pageWriter);
		pageWriter.closeElement(P);
	}
}