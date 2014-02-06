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
package org.conqat.engine.resource.regions;

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.base.ElementTraversingProcessorBase;
import org.conqat.engine.resource.text.ITextElement;

/**
 * Base class for processors that mark regions in elements.
 * 
 * @param <R>
 *            the type of resource that the marker can handle.
 * @param <ELEMENT>
 *            the type of element that the marker can handle (should match R).
 * @param <STRATEGY>
 *            the strategy used for region marking.
 * 
 * @author $Author: hummelb $
 * @version $Revision: 35330 $
 * @ConQAT.Rating GREEN Hash: 4D92533C32B976755B6ED2E631B0EAD6
 */
public abstract class RegionMarkerBase<R extends IResource, ELEMENT extends ITextElement, STRATEGY extends RegionMarkerStrategyBase<ELEMENT>>
		extends ElementTraversingProcessorBase<R, ELEMENT> {

	/** Strategy that is used for region marking */
	protected STRATEGY strategy;

	/** Name of the resulting region set */
	protected String regionSetName;

	/** Sets the name of the resulting region set */
	@AConQATParameter(name = "regions", description = "Name of the resulting region set", minOccurrences = 1, maxOccurrences = 1)
	public void setRegionSetName(
			@AConQATAttribute(name = "name", description = "Name of the resulting region set") String regionSetName) {
		this.regionSetName = regionSetName;
	}

	/**
	 * Template method that deriving classes override to create the strategy.
	 * Implementors create strategy, but do not initialize it.
	 */
	protected abstract STRATEGY createStrategy();

	/**
	 * Template method that deriving classes override to set strategy
	 * parameters.
	 */
	protected abstract void setStrategyParameters(STRATEGY strategy)
			throws ConQATException;

	/** Determine regions that match the patterns for the element */
	@Override
	public void processElement(ELEMENT element) throws ConQATException {
		assertStrategyCreated();
		strategy.annotate(element);
	}

	/** Creates the strategy, if it has not been created before */
	private void assertStrategyCreated() throws ConQATException {
		if (strategy == null) {
			strategy = createStrategy();
			strategy.init(getProcessorInfo());
			strategy.setLogger(getLogger());
			strategy.setRegionSetName(regionSetName);
			setStrategyParameters(strategy);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void finish(R root) throws ConQATException {
		assertStrategyCreated();
		strategy.logRegionInformation();
	}

}