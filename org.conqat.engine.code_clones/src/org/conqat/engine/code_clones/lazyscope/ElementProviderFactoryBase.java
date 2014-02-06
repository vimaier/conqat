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
package org.conqat.engine.code_clones.lazyscope;

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.resource.regions.RegionMarkerStrategyBase;
import org.conqat.engine.resource.text.ITextElement;

/**
 * Base class for element provider factories.
 * 
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: 068073AD03F67A740AD79AC4B4A8BF6B
 */
public abstract class ElementProviderFactoryBase<Element extends ITextElement>
		extends ConQATProcessorBase {

	/** List of strategies that get evaluated */
	protected final List<RegionMarkerStrategyBase<Element>> strategies = new ArrayList<RegionMarkerStrategyBase<Element>>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "region-marker", description = "Add region-marker strategy", minOccurrences = 0)
	public void addAnnotationStrategy(
			@AConQATAttribute(name = "strategy", description = ConQATParamDoc.INPUT_REF_NAME) RegionMarkerStrategyBase<Element> strategy) {
		this.strategies.add(strategy);
	}

}