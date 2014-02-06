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
package org.conqat.engine.core.driver.declaration;

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.core.driver.specification.BlockSpecificationAttribute;
import org.conqat.engine.core.driver.specification.SpecificationOutput;
import org.conqat.engine.core.driver.util.IInputReferencable;
import org.conqat.lib.commons.reflect.ClassType;
import org.conqat.lib.commons.string.StringUtils;

/**
 * The output of a declaration.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: A8187BBB81054C0BF72A4914287FEA39
 */
public class DeclarationOutput implements IInputReferencable {

	/** The specification output this is connected to. */
	private final SpecificationOutput specificationOutput;

	/** The declaration this belongs to. */
	private final IDeclaration declaration;

	/**
	 * The attributes from which the type is taken (pipeline attribute) or null
	 * if this is a regular output.
	 */
	private List<DeclarationAttribute> pipelineAttributes;

	/**
	 * The type of this output after applying pipelines. This is (potentially)
	 * set from the DeclarationTypeChecker. If it is null, simply the value
	 * known from the specification is returned.
	 */
	private ClassType pipelineResultType = null;

	/**
	 * Create new output.
	 * 
	 * @param specificationOutput
	 *            the specification output this is connected to.
	 * @param declaration
	 *            the declaration this belongs to.
	 */
	/* package */DeclarationOutput(SpecificationOutput specificationOutput,
			IDeclaration declaration) {
		this.specificationOutput = specificationOutput;
		this.declaration = declaration;
	}

	/** Returns the declaration this output belongs to. */
	public IDeclaration getDeclaration() {
		return declaration;
	}

	/** Returns the name of this output. */
	public String getName() {
		return specificationOutput.getName();
	}

	/**
	 * Returns the attributes from which the type is taken (pipeline attribute).
	 * If this is a regular (non-pipeline) output, null is returned.
	 */
	public List<DeclarationAttribute> getPipelineAttributes() {
		return pipelineAttributes;
	}

	/** {@inheritDoc} */
	@Override
	public ClassType getType() {
		if (pipelineResultType == null) {
			return specificationOutput.getType();
		}
		return pipelineResultType;
	}

	/** Returns the specification output this is referencing. */
	public SpecificationOutput getSpecificationOutput() {
		return specificationOutput;
	}

	/**
	 * Set the result type actually returned due to pipelining effects. This is
	 * set from the DeclarationTypeChecker.
	 */
	public void mergeActualResultType(ClassType type) {
		if (pipelineResultType == null) {
			pipelineResultType = type;
		} else {
			pipelineResultType = pipelineResultType.intersect(type);
		}
	}

	/**
	 * Adds an attribute which is used to get the type from (pipeline
	 * attribute). This is called from
	 * {@link DeclarationAttribute#linkTo(org.conqat.engine.core.driver.specification.SpecificationAttribute, java.util.Map)}
	 * .
	 */
	/* package */void addPipelineAttribute(
			DeclarationAttribute pipelineAttribute) {
		if (pipelineAttributes == null) {
			pipelineAttributes = new ArrayList<DeclarationAttribute>();
		}

		pipelineAttributes.add(pipelineAttribute);
	}

	/** {@inheritDoc} */
	@Override
	public BlockSpecificationAttribute asBlockSpecificationAttribute() {
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public DeclarationOutput asDeclarationOutput() {
		return this;
	}

	/**
	 * Returns the name used to reference this output (using '@' notation and
	 * including the name of the owning declaration). This is used to resolve
	 * references in ReferenceResolver.
	 */
	public String getReferenceName() {
		// processor declarations only have one output and thus no output name
		if (declaration instanceof ProcessorDeclaration) {
			return "@" + declaration.getName();
		}
		return "@" + declaration.getName() + "." + getName();
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		if (StringUtils.isEmpty(specificationOutput.getName())) {
			return declaration.toString() + ": default output";
		}
		return declaration.toString() + ": output '"
				+ specificationOutput.getName() + "'";
	}

	/**
	 * Freezes a pipeline output if one of its attributes is fed with a concrete
	 * value. This causes the other attributes to get a fixed value as well.
	 */
	/* package */void freezePipeline(ClassType type) {
		if (pipelineResultType == null) {
			pipelineResultType = type;
		} else {
			pipelineResultType = pipelineResultType.intersect(type);
		}

		// We use a local variable to iterate over pipelineAttribute, so we can
		// set pipelineAttribute to null beforehand (needed because of recursive
		// calls during freezing)
		List<DeclarationAttribute> attributes = pipelineAttributes;
		pipelineAttributes = null;
		if (attributes != null) {
			for (DeclarationAttribute attribute : attributes) {
				attribute.freezePipeline(type);
			}
		}
	}
}
