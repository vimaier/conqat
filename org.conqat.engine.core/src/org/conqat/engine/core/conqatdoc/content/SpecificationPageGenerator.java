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

import static org.conqat.lib.commons.html.EHTMLAttribute.HREF;
import static org.conqat.lib.commons.html.EHTMLAttribute.NAME;
import static org.conqat.lib.commons.html.EHTMLElement.A;
import static org.conqat.lib.commons.html.EHTMLElement.BR;
import static org.conqat.lib.commons.html.EHTMLElement.H2;
import static org.conqat.lib.commons.html.EHTMLElement.H3;
import static org.conqat.lib.commons.html.EHTMLElement.I;
import static org.conqat.lib.commons.html.EHTMLElement.P;
import static org.conqat.lib.commons.html.EHTMLElement.TABLE;
import static org.conqat.lib.commons.html.EHTMLElement.TD;
import static org.conqat.lib.commons.html.EHTMLElement.TH;
import static org.conqat.lib.commons.html.EHTMLElement.TR;

import java.io.File;
import java.io.IOException;

import org.conqat.engine.core.bundle.BundleInfo;
import org.conqat.engine.core.conqatdoc.JavaDocLinkResolver;
import org.conqat.engine.core.conqatdoc.SpecUtils;
import org.conqat.engine.core.conqatdoc.types.TypeListGenerator;
import org.conqat.engine.core.driver.error.DriverException;
import org.conqat.engine.core.driver.specification.ISpecification;
import org.conqat.engine.core.driver.specification.ISpecificationParameter;
import org.conqat.engine.core.driver.specification.ProcessorSpecification;
import org.conqat.engine.core.driver.specification.SpecificationAttribute;
import org.conqat.engine.core.driver.specification.SpecificationOutput;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Generator page for general specifications. This is the base class for
 * {@link BlockSpecificationPageGenerator} and
 * {@link ProcessorSpecificationPageGenerator} containing shared code.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 1FF38E8C9AF0BD57D372C00D2648702A
 */
/* package */abstract class SpecificationPageGenerator extends
		ContentPageGeneratorBase {

	/** The bundle we are talking about. May be null. */
	protected final BundleInfo bundle;

	/** The specification handled here. */
	private final ISpecification specification;

	/** The resolver used for handling JavaDoc links. */
	protected final JavaDocLinkResolver javaDocResolver;

	/**
	 * Create a new generator for a bundle details page.
	 * 
	 * @param targetDirectory
	 *            the directory to generate into.
	 * @param specification
	 *            the specification being displayed by this page.
	 * @param bundle
	 *            the bundle the specification was taken from.
	 * @param javaDocResolver
	 *            the resolver used for handling JavaDoc links.
	 */
	public SpecificationPageGenerator(File targetDirectory,
			ISpecification specification, BundleInfo bundle,
			JavaDocLinkResolver javaDocResolver) {
		super(targetDirectory);
		this.bundle = bundle;
		this.specification = specification;
		this.javaDocResolver = javaDocResolver;
	}

	/** {@inheritDoc} */
	@Override
	protected String getPageName() {
		return specification.getName() + getPageSuffix();
	}

	/** {@inheritDoc} */
	@Override
	protected String getPageTitle() {
		return getSpecificationTypeName() + " "
				+ SpecUtils.getShortName(specification);
	}

	/** {@inheritDoc} */
	@Override
	protected void appendContents() throws IOException, DriverException {
		if (specification.getDoc() != null) {
			pageWriter.addClosedTextElement(P, specification.getDoc());
		}

		writeBasicInfo();
		writeKeys();
		writeOutputs();
		writeParameters();
		writeAdditionalSections();
	}

	/** Writes the most basic info as a table to the page. */
	private void writeBasicInfo() {
		pageWriter.openElement(TABLE);
		pageWriter.openElement(TR);
		pageWriter.addClosedTextElement(TD, "Full name");

		pageWriter.openElement(TD);
		if (specification instanceof ProcessorSpecification) {
			// for processors there has to be a class of the same name
			javaDocResolver.resolveLink(specification.getName(), pageWriter);
		} else {
			pageWriter.addText(specification.getName());
		}
		pageWriter.closeElement(TD);
		pageWriter.closeElement(TR);

		if (bundle != null) {
			pageWriter.openElement(TR);
			pageWriter.addClosedTextElement(TD, "Contained in");
			pageWriter.openElement(TD);
			pageWriter.addClosedTextElement(A, bundle.getName(), HREF,
					BundleDetailsPageGenerator.getPageName(bundle));
			pageWriter.closeElement(TD);
			pageWriter.closeElement(TR);
		}
		pageWriter.closeElement(TABLE);
	}

	/** This method is a hook for writing information on keys (if available). */
	protected abstract void writeKeys();

	/** Hook for writing information on the provided outputs. */
	protected abstract void writeOutputs();

	/** Writes the parameters to the page. */
	private void writeParameters() throws DriverException {
		pageWriter.addClosedTextElement(H2, "Parameters");

		writeParameterOverview();

		for (ISpecificationParameter param : specification
				.getNonSyntheticParameters()) {
			writeParameterDetails(param);
		}
	}

	/** Writes the overview table of all supported parameters. */
	private void writeParameterOverview() {
		pageWriter.openElement(TABLE);
		writeTableHeader("Name", "Multipl.", "Description");
		for (ISpecificationParameter param : specification
				.getNonSyntheticParameters()) {
			pageWriter.openElement(TR);

			pageWriter.openElement(TD);
			pageWriter.addClosedTextElement(A, param.getName(), HREF, "#"
					+ param.getName());
			pageWriter.closeElement(TD);

			pageWriter.addClosedTextElement(TD, param.getMultiplicity()
					.toString());
			pageWriter.addClosedTextElement(TD, nullProtect(param.getDoc()));
			pageWriter.closeElement(TR);
		}
		pageWriter.closeElement(TABLE);
	}

	/** Writes the details (especially the attributes) for a given parameter. */
	private void writeParameterDetails(ISpecificationParameter param)
			throws DriverException {
		pageWriter.addClosedTextElement(H3,
				param.getName() + " " + param.getMultiplicity());

		// We use an empty string inside, as otherwise Firefox has problems
		pageWriter.addClosedTextElement(A, "", NAME, param.getName());

		if (param.getDoc() != null) {
			pageWriter.addClosedTextElement(P, param.getDoc());
		}

		pageWriter.openElement(TABLE);
		writeTableHeader("Name", "Type", "Default", "Pipeline", "Description",
				"Producers");
		for (SpecificationAttribute attr : param.getAttributes()) {
			writeAttributeRow(attr);
		}
		pageWriter.closeElement(TABLE);
	}

	/**
	 * Writes the attributes information into a row of the parameter details
	 * table.
	 */
	private void writeAttributeRow(SpecificationAttribute attr)
			throws DriverException {

		pageWriter.openElement(TR);
		pageWriter.addClosedTextElement(TD, attr.getName());

		pageWriter.openElement(TD);
		javaDocResolver.resolveLink(attr.getType().toString(), pageWriter);
		pageWriter.closeElement(TD);

		writeDefaultValue(attr);
		writePipelineCell(attr);
		pageWriter.addClosedTextElement(TD, nullProtect(attr.getDoc()));
		pageWriter.openElement(TD);
		pageWriter.addClosedTextElement(A, "Producers", HREF,
				TypeListGenerator.PAGE_NAME + "#" + attr.getType().toString());
		pageWriter.closeElement(TD);
		pageWriter.closeElement(TR);
	}

	/**
	 * Writes the default value cell in
	 * {@link #writeAttributeRow(SpecificationAttribute)}.
	 */
	private void writeDefaultValue(SpecificationAttribute attr)
			throws DriverException {
		pageWriter.openElement(TD);
		if (attr.getDefaultValue() == null) {
			pageWriter.addClosedTextElement(I, "none");
		} else {
			pageWriter.addText(attr.getDefaultValue().toString());
		}
		pageWriter.closeElement(TD);
	}

	/**
	 * Writes the cell containing the pipeline outputs in
	 * {@link #writeAttributeRow(SpecificationAttribute)}.
	 */
	private void writePipelineCell(SpecificationAttribute attr) {
		pageWriter.openElement(TD);
		for (SpecificationOutput output : attr.getPipelineOutputs()) {
			if (StringUtils.isEmpty(output.getName())) {
				pageWriter.addClosedTextElement(I, "output");
			} else {
				pageWriter.addText(output.getName());
			}
			pageWriter.addClosedElement(BR);
		}
		if (attr.getPipelineOutputs().isEmpty()) {
			pageWriter.addClosedTextElement(I, "none");
		}
		pageWriter.closeElement(TD);
	}

	/** Writes a table head. */
	protected void writeTableHeader(String... values) {
		pageWriter.openElement(TR);
		for (String value : values) {
			pageWriter.addClosedTextElement(TH, value == null ? "" : value);
		}
		pageWriter.closeElement(TR);
	}

	/**
	 * This method is called after most of the page has been written but before
	 * closing the {@link #pageWriter}. So here any subclass may add specific
	 * section.
	 */
	protected abstract void writeAdditionalSections() throws IOException;

	/** Returns the type name of the specification (e.g. Processor or Block). */
	protected abstract String getSpecificationTypeName();

	/**
	 * Returns the suffix used to make the page name (after adding it to the
	 * name of the specification).
	 */
	protected abstract String getPageSuffix();
}