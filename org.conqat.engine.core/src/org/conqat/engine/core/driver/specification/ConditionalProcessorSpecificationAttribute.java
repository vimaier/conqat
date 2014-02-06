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
package org.conqat.engine.core.driver.specification;

import java.util.List;

import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.reflect.ClassType;
import org.conqat.lib.commons.string.StringUtils;

/**
 * The {@link ProcessorSpecificationAttribute} for the synthetic conditional
 * parameter.
 * 
 * @author $Author: juergens $
 * @version $Rev: 35194 $
 * @ConQAT.Rating GREEN Hash: 38F464FF36DE91FFAB55441CE6B5DFFB
 */
public class ConditionalProcessorSpecificationAttribute extends
		ProcessorSpecificationAttribute {

	/** Constructor. */
	protected ConditionalProcessorSpecificationAttribute(String name,
			ProcessorSpecificationParameter parameter) {
		super(name, new ClassType(Boolean.class), parameter);
	}

	/** {@inheritDoc} */
	@Override
	public Object getDefaultValue() {
		// no default value
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public List<SpecificationOutput> getPipelineOutputs() {
		return CollectionUtils.emptyList();
	}

	/** {@inheritDoc} */
	@Override
	public boolean hasPipelineOutputs() {
		return false;
	}

	/** {@inheritDoc}. */
	@Override
	public String getDoc() {
		return StringUtils.EMPTY_STRING;
	}
}