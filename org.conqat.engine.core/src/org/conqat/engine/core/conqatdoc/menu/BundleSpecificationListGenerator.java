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

import java.io.File;
import java.util.Collection;

import org.conqat.engine.core.bundle.BundleInfo;
import org.conqat.engine.core.conqatdoc.content.BundleDetailsPageGenerator;
import org.conqat.engine.core.driver.specification.BlockSpecification;
import org.conqat.engine.core.driver.specification.ProcessorSpecification;
import org.conqat.lib.commons.html.EHTMLAttribute;
import org.conqat.lib.commons.html.EHTMLElement;

/**
 * Generator page for the list of all specifications (processors and blocks) of
 * a single bundle. This is shown in the list frame at the lower left.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 801BFF119E4D5AC92D733E82F79A53BC
 */
public class BundleSpecificationListGenerator extends
		SpecificationListGeneratorBase {

	/** The suffix of the HTML page generated. */
	private static final String PAGE_SUFFIX = "_list.html";

	/** The list of bundles. */
	private final BundleInfo bundle;

	/** Create a new generator for a bundle details page. */
	public BundleSpecificationListGenerator(File targetDirectory,
			BundleInfo bundle, Collection<ProcessorSpecification> processors,
			Collection<BlockSpecification> blocks) {
		super(targetDirectory, processors, blocks);
		this.bundle = bundle;
	}

	/** {@inheritDoc} */
	@Override
	protected String getPageName() {
		return getPageName(bundle);
	}

	/** {@inheritDoc} */
	@Override
	protected String getPageTitle() {
		return "Processors and blocks for " + bundle.getId();
	}

	/** {@inheritDoc} */
	@Override
	protected void appendToplevelLink() {
		pageWriter.addClosedTextElement(EHTMLElement.A, bundle.getName(),
				EHTMLAttribute.TARGET, IndexPageGenerator.MAIN_FRAME,
				EHTMLAttribute.HREF,
				BundleDetailsPageGenerator.getPageName(bundle));
	}

	/** Returns the name of the details page for the given bundle. */
	public static String getPageName(BundleInfo bundle) {
		return bundle.getId() + PAGE_SUFFIX;
	}
}