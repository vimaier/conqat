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
package org.conqat.engine.core.driver.instance;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.conqat.engine.core.driver.declaration.DeclarationOutput;
import org.conqat.engine.core.driver.specification.BlockSpecificationAttribute;
import org.conqat.engine.core.driver.specification.BlockSpecificationOutput;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.clone.DeepCloneException;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * The output of an instance.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 8582DCEC2300B5C7C885F22F5A30E7B9
 */
public class InstanceOutput extends ValueProviderBase {

	/** The instance this belongs to. */
	private final IInstance instance;

	/** The declaration output this is based upon. */
	private final DeclarationOutput declarationOutput;

	/** The value provider to get the value from. */
	private final IValueProvider referencedValueProvider;

	/**
	 * Create a new output for a processor instance.
	 * 
	 * @param declarationOutput
	 *            the declaration output this is based on.
	 * @param instance
	 *            the instance this belongs to.
	 */
	/* package */InstanceOutput(DeclarationOutput declarationOutput,
			ProcessorInstance instance) {
		this.declarationOutput = declarationOutput;
		this.instance = instance;

		// processor instances will set the value of the output "manually"
		referencedValueProvider = null;
	}

	/**
	 * Create a new instance output for a block instance.
	 * 
	 * @param declarationOutput
	 *            the declaration output this is based on.
	 * @param instance
	 *            the instance this belongs to.
	 */
	/* package */InstanceOutput(DeclarationOutput declarationOutput,
			BlockInstance instance) {
		this.declarationOutput = declarationOutput;
		this.instance = instance;

		// This is a BlockSpecificationOutput as we are working with blocks here
		BlockSpecificationOutput bspecOutput = (BlockSpecificationOutput) declarationOutput
				.getSpecificationOutput();

		referencedValueProvider = determineReferencedValueProvider(instance,
				bspecOutput);
		referencedValueProvider.addConsumer();
	}

	/**
	 * Determines the references value provider (i.e. the object to get the
	 * value from) for the newly created block instance output.
	 */
	private IValueProvider determineReferencedValueProvider(
			BlockInstance instance, BlockSpecificationOutput bspecOutput) {

		DeclarationOutput referencedOutput = bspecOutput.getReference()
				.asDeclarationOutput();
		if (referencedOutput != null) {
			return instance.getChildOutputInstance(referencedOutput);
		}

		// otherwise this must be a BlockSpecificationAttribute
		// (contract of IInputReferencable)
		BlockSpecificationAttribute bspecAttr = bspecOutput.getReference()
				.asBlockSpecificationAttribute();

		List<Map<String, InstanceParameter>> instParams = instance
				.getSpecificationParameterInstances(Collections.singletonMap(
						StringUtils.EMPTY_STRING, bspecAttr.getParameter()));

		CCSMAssert
				.isTrue(instParams.size() == 1,
						"As the referenced attribute is a pipeline attribute "
								+ "the corresponding parameter must have exactly multiplicity one.");

		InstanceParameter specificationParameter = CollectionUtils.getAny(
				instParams.get(0).entrySet()).getValue();
		return specificationParameter.getAttributeByName(bspecAttr.getName());
	}

	/** Returns the declaration this is based upon. */
	public DeclarationOutput getDeclaration() {
		return declarationOutput;
	}

	/** Signals this output to get its result from the referenced object. */
	/* package */void copyReferencedResult() throws DeepCloneException {
		if (referencedValueProvider == null) {
			throw new IllegalStateException(
					"May only use this for 'linked' outputs! In " + this);
		}
		if (referencedValueProvider.hasValue()) {
			setValue(referencedValueProvider.consumeValue());
		}
	}

	/** Returns the object providing the value for this one. */
	public IValueProvider getValueProvider() {
		return referencedValueProvider;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return instance.toString() + ": output '" + declarationOutput.getName()
				+ "'";
	}
}