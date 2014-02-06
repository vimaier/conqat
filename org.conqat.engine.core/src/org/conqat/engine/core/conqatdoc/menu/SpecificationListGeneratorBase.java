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

import org.conqat.engine.core.conqatdoc.PageGeneratorBase;
import org.conqat.engine.core.conqatdoc.SpecUtils;
import org.conqat.engine.core.conqatdoc.compare.SpecificationShortNameComparator;
import org.conqat.engine.core.driver.specification.BlockSpecification;
import org.conqat.engine.core.driver.specification.ISpecification;
import org.conqat.engine.core.driver.specification.ProcessorSpecification;
import org.conqat.lib.commons.html.EHTMLAttribute;
import org.conqat.lib.commons.html.EHTMLElement;

/**
 * Base class for {@link SpecificationListGenerator} and
 * {@link BundleSpecificationListGenerator} containing shared code.
 * 
 * @author Benjamin Hummel
 * @author $Author: juergens $
 * @version $Rev: 35194 $
 * @ConQAT.Rating GREEN Hash: D5160299A71F73F959DA324E3927DBB8
 */
/* package */abstract class SpecificationListGeneratorBase extends
		PageGeneratorBase {

	/** The processors contained in this bundle. */
	private final List<ISpecification> specifications;

	/** Create a new generator for a bundle details page. */
	public SpecificationListGeneratorBase(File targetDirectory,
			Collection<ProcessorSpecification> processors,
			Collection<BlockSpecification> blocks) {
		super(targetDirectory);
		specifications = new ArrayList<ISpecification>(processors);
		specifications.addAll(blocks);
		Collections
				.sort(specifications, new SpecificationShortNameComparator());
	}

	/** {@inheritDoc} */
	@Override
	public void appendBody() {
		pageWriter.openElement(BODY);
		appendToplevelLink();
		pageWriter.addClosedElement(EHTMLElement.BR);
		pageWriter.addClosedElement(EHTMLElement.BR);

		for (ISpecification spec : specifications) {
			appendSpecification(spec);
		}
		pageWriter.closeElement(BODY);
	}

	/**
	 * This is a hook to add some HTML to the top of the list. Usually this is a
	 * link pointing to the "source" of the list.
	 */
	protected abstract void appendToplevelLink();

	/** Append a single specification to the list. */
	private void appendSpecification(ISpecification spec) {
		pageWriter.openElement(EHTMLElement.A, EHTMLAttribute.TARGET,
				IndexPageGenerator.MAIN_FRAME, EHTMLAttribute.HREF,
				SpecUtils.getLinkName(spec));

		if (spec instanceof BlockSpecification) {
			pageWriter.addClosedTextElement(EHTMLElement.I,
					SpecUtils.getShortName(spec));
		} else {
			pageWriter.addText(SpecUtils.getShortName(spec));
		}

		pageWriter.closeElement(EHTMLElement.A);
		pageWriter.addClosedElement(EHTMLElement.BR);
	}

}