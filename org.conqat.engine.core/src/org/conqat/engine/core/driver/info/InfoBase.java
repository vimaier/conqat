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
package org.conqat.engine.core.driver.info;

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.core.driver.instance.IInstance;
import org.conqat.engine.core.driver.instance.InstanceOutput;
import org.conqat.engine.core.driver.instance.InstanceParameter;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableList;

/**
 * Base class for {@link BlockInfo} and {@link ProcessorInfo} containing common
 * code.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: C4D8D02180917C4296BAA22DBB6BD842
 */
public abstract class InfoBase implements IInfo {

	/** The instance this is based on. */
	private final IInstance instance;

	/** The parent block. */
	private final BlockInfo parent;

	/** The parameters of this object. */
	private final List<InfoParameter> parameters = new ArrayList<InfoParameter>();

	/** The outputs of this object. */
	private final List<InfoOutput> outputs = new ArrayList<InfoOutput>();

	/** Creates new info base instance. */
	protected InfoBase(IInstance instance, BlockInfo parent) {
		this.instance = instance;
		this.parent = parent;
		initParameters();
		initOutputs();
	}

	/** Initialize the list of parameters. */
	private void initParameters() {
		for (InstanceParameter param : instance.getParameters()) {
			parameters.add(new InfoParameter(param, this));
		}
	}

	/** Initialize the list of outputs. */
	private void initOutputs() {
		for (InstanceOutput output : instance.getOutputs()) {
			outputs.add(new InfoOutput(output, this));
		}
	}

	/** {@inheritDoc} */
	@Override
	public String getInstanceName() {
		return instance.getName();
	}

	/** {@inheritDoc} */
	@Override
	public String getDeclarationName() {
		return instance.getDeclaration().getName();
	}

	/** {@inheritDoc} */
	@Override
	public String getSpecificationName() {
		return instance.getDeclaration().getSpecification().getName();
	}

	/** {@inheritDoc} */
	@Override
	public BlockInfo getParent() {
		return parent;
	}

	/** {@inheritDoc} */
	@Override
	public UnmodifiableList<InfoOutput> getOutputs() {
		return CollectionUtils.asUnmodifiable(outputs);
	}

	/** {@inheritDoc} */
	@Override
	public UnmodifiableList<InfoParameter> getParameters() {
		return CollectionUtils.asUnmodifiable(parameters);
	}

	/** {@inheritDoc} */
	@Override
	public List<InfoParameter> getNonsyntheticParameters() {
		List<InfoParameter> result = new ArrayList<InfoParameter>();
		for (InfoParameter parameter : parameters) {
			if (!parameter.isSynthetic()) {
				result.add(parameter);
			}
		}
		return result;
	}
}