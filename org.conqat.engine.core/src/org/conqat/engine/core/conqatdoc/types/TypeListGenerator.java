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
package org.conqat.engine.core.conqatdoc.types;

import static org.conqat.lib.commons.html.EHTMLAttribute.HREF;
import static org.conqat.lib.commons.html.EHTMLElement.A;
import static org.conqat.lib.commons.html.EHTMLElement.BODY;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.conqat.engine.core.conqatdoc.PageGeneratorBase;
import org.conqat.engine.core.conqatdoc.SpecUtils;
import org.conqat.engine.core.conqatdoc.compare.SpecificationNameComparator;
import org.conqat.engine.core.driver.specification.BlockSpecification;
import org.conqat.engine.core.driver.specification.ISpecification;
import org.conqat.engine.core.driver.specification.ISpecificationParameter;
import org.conqat.engine.core.driver.specification.ProcessorSpecification;
import org.conqat.engine.core.driver.specification.SpecificationAttribute;
import org.conqat.engine.core.driver.specification.SpecificationOutput;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.ToStringComparator;
import org.conqat.lib.commons.html.EHTMLAttribute;
import org.conqat.lib.commons.html.EHTMLElement;
import org.conqat.lib.commons.reflect.ClassType;

/**
 * This class generates a page with all types returned by processors and blocks.
 * For each type it lists the processors and blocks that produce this type.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: AC77A270F366ED6C4CF28B8BD8194CDB
 */
public class TypeListGenerator extends PageGeneratorBase {

	/** The name of the HTML page generated. */
	public static final String PAGE_NAME = "_types.html";

	/** Maps from type to set of specifications that produce the type. */
	private final HashMap<ClassType, HashSet<ISpecification>> types2Producers = new HashMap<ClassType, HashSet<ISpecification>>();

	/** Create a new generator for a bundle details page. */
	public TypeListGenerator(File targetDirectory,
			Collection<ProcessorSpecification> processors,
			Collection<BlockSpecification> blocks) {
		super(targetDirectory);
		HashSet<ISpecification> specifications = new HashSet<ISpecification>();
		specifications.addAll(processors);
		specifications.addAll(blocks);

		for (ClassType type : determineInputTypes(specifications)) {
			types2Producers.put(type, getTypeProducers(specifications, type));
		}
	}

	/** Find all types that are used as inputs to a block or processor. */
	private HashSet<ClassType> determineInputTypes(
			HashSet<ISpecification> specifications) {
		HashSet<ClassType> inputTypes = new HashSet<ClassType>();
		for (ISpecification spec : specifications) {
			for (ISpecificationParameter param : spec.getParameters()) {
				for (SpecificationAttribute attr : param.getAttributes()) {
					inputTypes.add(attr.getType());
				}
			}
		}
		return inputTypes;
	}

	/**
	 * Get all generators that produce a specific type.
	 */
	private HashSet<ISpecification> getTypeProducers(
			HashSet<ISpecification> specifications, ClassType type) {
		HashSet<ISpecification> producers = new HashSet<ISpecification>();
		for (ISpecification spec : specifications) {
			for (SpecificationOutput output : spec.getOutputs()) {
				if (type.isAssignableFrom(output.getType())) {
					producers.add(spec);
				}
			}
		}
		return producers;
	}

	/** {@inheritDoc } */
	@Override
	protected void appendBody() {
		pageWriter.openElement(BODY);

		for (ClassType type : CollectionUtils.sort(types2Producers.keySet(),
				ToStringComparator.INSTANCE)) {
			appendType(type);
		}

		pageWriter.closeElement(BODY);
	}

	/** Append a type and its generators. */
	private void appendType(ClassType type) {

		// use type as anchor so we can directly link to the type
		pageWriter.openElement(A, EHTMLAttribute.NAME, type.toString());
		pageWriter.addClosedTextElement(EHTMLElement.H2, type.toString());
		pageWriter.closeElement(A);

		HashSet<ISpecification> generators = types2Producers.get(type);

		if (generators.isEmpty()) {
			pageWriter.addText("No generators found.");
		} else {
			pageWriter.openElement(EHTMLElement.UL);
			for (ISpecification spec : CollectionUtils.sort(generators,
					SpecificationNameComparator.INSTANCE)) {
				pageWriter.openElement(EHTMLElement.LI);
				pageWriter.addClosedTextElement(A, spec.getName(), HREF,
						SpecUtils.getLinkName(spec));
				pageWriter.closeElement(EHTMLElement.LI);
			}
			pageWriter.closeElement(EHTMLElement.UL);
		}

	}

	/** {@inheritDoc } */
	@Override
	protected String getPageName() {
		return PAGE_NAME;
	}

	/** {@inheritDoc } */
	@Override
	protected String getPageTitle() {
		return "Types";
	}

}