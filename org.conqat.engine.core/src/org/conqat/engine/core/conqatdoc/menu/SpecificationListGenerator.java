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

import org.conqat.engine.core.conqatdoc.content.MainPageGenerator;
import org.conqat.engine.core.driver.specification.BlockSpecification;
import org.conqat.engine.core.driver.specification.ProcessorSpecification;
import org.conqat.lib.commons.html.EHTMLAttribute;
import org.conqat.lib.commons.html.EHTMLElement;

/**
 * Generator page for the list of all specifications (processors and blocks) of
 * the installation. This is shown in the list frame on the lower left side.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 56F96ADFFE061BEFB34BCDDC7A39B41A
 */
public class SpecificationListGenerator extends SpecificationListGeneratorBase {

	/** The name of the HTML page generated. */
	public static final String PAGE_NAME = "_allspecs.html";

	/** Create a new generator for a bundle details page. */
	public SpecificationListGenerator(File targetDirectory,
			Collection<ProcessorSpecification> processors,
			Collection<BlockSpecification> blocks) {
		super(targetDirectory, processors, blocks);
	}

	/** {@inheritDoc} */
	@Override
	protected String getPageName() {
		return PAGE_NAME;
	}

	/** {@inheritDoc} */
	@Override
	protected String getPageTitle() {
		return "Installed processors and blocks";
	}

	/** {@inheritDoc} */
	@Override
	protected void appendToplevelLink() {
		pageWriter.addClosedTextElement(EHTMLElement.A, "All bundles",
				EHTMLAttribute.TARGET, IndexPageGenerator.MAIN_FRAME,
				EHTMLAttribute.HREF, MainPageGenerator.PAGE_NAME);
	}
}