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

import static org.conqat.lib.commons.html.EHTMLElement.BODY;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.conqat.engine.core.bundle.BundleInfo;
import org.conqat.engine.core.conqatdoc.PageGeneratorBase;
import org.conqat.engine.core.conqatdoc.compare.BundleInfoIdComparator;
import org.conqat.lib.commons.html.EHTMLAttribute;
import org.conqat.lib.commons.html.EHTMLElement;

/**
 * Generator class for the small list of all bundles installed, which is
 * displayed in the top left corner of the documentation (the bundles frame).
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 6BD1F2C8D13D5AC141E7883AA9816F95
 */
public class BundlesListGenerator extends PageGeneratorBase {

	/** The name of the HTML page generated. */
	public static final String PAGE_NAME = "_bundles.html";

	/** The list of bundles. */
	private final List<BundleInfo> bundles;

	/** Create a new generator for the main page. */
	public BundlesListGenerator(File targetDirectory,
			Collection<BundleInfo> bundles) {
		super(targetDirectory);

		this.bundles = new ArrayList<BundleInfo>(bundles);
		Collections.sort(this.bundles, new BundleInfoIdComparator());
	}

	/** {@inheritDoc} */
	@Override
	protected String getPageName() {
		return PAGE_NAME;
	}

	/** {@inheritDoc} */
	@Override
	protected String getPageTitle() {
		return "Installed bundles";
	}

	/** {@inheritDoc} */
	@Override
	protected void appendBody() {
		pageWriter.openElement(BODY);

		pageWriter.addClosedTextElement(EHTMLElement.A, "ALL",
				EHTMLAttribute.TARGET, IndexPageGenerator.LIST_FRAME,
				EHTMLAttribute.HREF, SpecificationListGenerator.PAGE_NAME);

		pageWriter.addClosedElement(EHTMLElement.BR);
		pageWriter.addClosedElement(EHTMLElement.BR);

		for (BundleInfo bundle : bundles) {
			pageWriter.addClosedTextElement(EHTMLElement.A, bundle.getName(),
					EHTMLAttribute.TARGET, IndexPageGenerator.LIST_FRAME,
					EHTMLAttribute.HREF,
					BundleSpecificationListGenerator.getPageName(bundle));
			pageWriter.addClosedElement(EHTMLElement.BR);
		}
		pageWriter.closeElement(BODY);
	}
}