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
import static org.conqat.lib.commons.html.EHTMLElement.A;
import static org.conqat.lib.commons.html.EHTMLElement.BR;
import static org.conqat.lib.commons.html.EHTMLElement.H2;
import static org.conqat.lib.commons.html.EHTMLElement.P;
import static org.conqat.lib.commons.html.EHTMLElement.TABLE;
import static org.conqat.lib.commons.html.EHTMLElement.TD;
import static org.conqat.lib.commons.html.EHTMLElement.TR;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.conqat.engine.core.bundle.BundleDependency;
import org.conqat.engine.core.bundle.BundleInfo;
import org.conqat.engine.core.conqatdoc.JavaDocLinkResolver;
import org.conqat.engine.core.conqatdoc.SpecUtils;
import org.conqat.engine.core.conqatdoc.compare.SpecificationNameComparator;
import org.conqat.engine.core.driver.specification.BlockSpecification;
import org.conqat.engine.core.driver.specification.ISpecification;
import org.conqat.engine.core.driver.specification.ProcessorSpecification;

/**
 * Generator page for the main page of a bundle. Besides details for the bundle
 * (including its bundle.html) all processors and blocks for the bundle are
 * listed with description. This is shown in the main frame.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 0CC0B09F6B491C588BD72C52FA8F0570
 */
public class BundleDetailsPageGenerator extends ContentPageGeneratorBase {

	/** The suffix for the HTML page generated. */
	private static final String PAGE_SUFFIX = "_details.html";

	/** The list of bundles. */
	private final BundleInfo bundle;

	/** The resolver used for handling JavaDoc links. */
	protected final JavaDocLinkResolver javaDocResolver;

	/** The processors contained in this bundle. */
	private final List<ProcessorSpecification> processors;

	/** The blocks contained in this bundle. */
	private final List<BlockSpecification> blocks;

	/** Create a new generator for a bundle details page. */
	public BundleDetailsPageGenerator(File targetDirectory, BundleInfo bundle,
			Collection<ProcessorSpecification> processors,
			Collection<BlockSpecification> blocks,
			JavaDocLinkResolver javaDocResolver) {
		super(targetDirectory);
		this.bundle = bundle;
		this.javaDocResolver = javaDocResolver;

		// copy them as we intend to sort them later
		this.processors = new ArrayList<ProcessorSpecification>(processors);
		this.blocks = new ArrayList<BlockSpecification>(blocks);
	}

	/** {@inheritDoc} */
	@Override
	protected String getPageTitle() {
		return bundle.getName();
	}

	/** {@inheritDoc} */
	@Override
	protected String getPageName() {
		return getPageName(bundle);
	}

	/** {@inheritDoc} */
	@Override
	protected void appendContents() throws IOException {
		generateBundleDetails();

		pageWriter.addClosedTextElement(H2, "Included Processors");
		writeSpecTable(processors);

		pageWriter.addClosedTextElement(H2, "Included blocks");
		writeSpecTable(blocks);
	}

	/** Generate details for the bundle. */
	private void generateBundleDetails() throws IOException {

		if (bundle.getDescription() != null) {
			pageWriter.addClosedTextElement(P, bundle.getDescription());
		}

		pageWriter.openElement(TABLE);
		writeDetailTableRows();
		writeDependencyRow();
		pageWriter.closeElement(TABLE);

		new BundleHTMLProcessor(javaDocResolver).process(bundle, pageWriter);
	}

	/** Write out the rows of the details table. */
	private void writeDetailTableRows() {
		writeTableRow("Name", bundle.getName());
		writeTableRow("Id", bundle.getId());
		writeTableRow("Version", bundle.getVersion().toString());
		writeTableRow("Provider", bundle.getProvider());
		writeTableRow("Location", bundle.getLocation().getAbsolutePath());
		writeTableRow("Required Core Version", bundle.getRequiredCoreVersion()
				.toString());
	}

	/** Write the row containing the dependencies to the details table. */
	private void writeDependencyRow() {
		pageWriter.openElement(TR);
		pageWriter.addClosedTextElement(TD, "Dependencies");
		pageWriter.openElement(TD);
		for (BundleDependency dep : bundle.getDependencies()) {
			pageWriter.addClosedTextElement(A, dep.getId(), HREF,
					getPageName(dep.getId()));
			pageWriter.addText(" (Version " + dep.getVersion() + ")");
			pageWriter.addClosedElement(BR);
		}
		pageWriter.closeElement(TD);
		pageWriter.closeElement(TR);
	}

	/** Writes a single row of a cell. */
	private void writeTableRow(String name, Object value) {
		pageWriter.openElement(TR);
		pageWriter.addClosedTextElement(TD, name);
		pageWriter.addClosedTextElement(TD, value.toString());
		pageWriter.closeElement(TR);
	}

	/** Generate a table from all provided specifications. */
	private void writeSpecTable(List<? extends ISpecification> specifications) {
		Collections.sort(specifications, SpecificationNameComparator.INSTANCE);

		pageWriter.openElement(TABLE);
		for (ISpecification spec : specifications) {
			pageWriter.openElement(TR);
			pageWriter.openElement(TD);
			pageWriter.addClosedTextElement(A, SpecUtils.getShortName(spec),
					HREF, SpecUtils.getLinkName(spec));
			pageWriter.closeElement(TD);
			pageWriter.addClosedTextElement(TD, nullProtect(spec.getDoc()));
			pageWriter.closeElement(TR);
		}
		pageWriter.closeElement(TABLE);
	}

	/** Returns the name of the details page for the given bundle. */
	private static String getPageName(String bundleId) {
		return bundleId + PAGE_SUFFIX;
	}

	/** Returns the name of the details page for the given bundle. */
	public static String getPageName(BundleInfo bundle) {
		return getPageName(bundle.getId());
	}
}