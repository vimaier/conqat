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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.conqat.engine.code_clones.normalization.provider.ProviderBase;
import org.conqat.engine.core.logging.IConQATLogger;
import org.conqat.engine.resource.regions.RegionMarkerStrategyBase;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.lib.commons.error.NeverThrownRuntimeException;

/**
 * Returns the elements from a scope one by one in a lazy fashion. (Also works,
 * if the root element of the scope is a leaf. It can thus either be used on
 * whole scopes or on single elements.)
 * 
 * (Difference between this class and <code>ProviderBase</code>: The data
 * objects provided by classes deriving from {@link ElementProviderBase} are
 * required to be elements, whereas the data objects returned by classes
 * deriving from {@link ProviderBase} can be of arbitrary type.)
 * <p>
 * This class has package visibility in order to enforce the use of its
 * subclasses from outside this package.
 * <p>
 * Since this class is generic, it should not be used as a return type in a
 * ConQAT processor, since the ConQAT load time type checking mechanism cannot
 * handle generic types.
 * 
 * @author $Author: kinnen $
 * @version $Revision: 41751 $
 * @ConQAT.Rating GREEN Hash: 3847C0EA6CD6F1AC520B3C4228EF9530
 */
public abstract class ElementProviderBase<Resource extends ITextResource, Element extends ITextElement>
		extends ProviderBase<Resource, Element, NeverThrownRuntimeException>
		implements IElementProvider<Resource, Element>, Serializable {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** List of strategies that get evaluated */
	private final List<RegionMarkerStrategyBase<Element>> strategies = new ArrayList<RegionMarkerStrategyBase<Element>>();

	/**
	 * Iterator that keeps track of the {@link ITextElement} that gets returned
	 * for the next call to {@link #getNext()}
	 */
	private transient Iterator<Element> elementsIterator;

	/** Constructor. */
	protected ElementProviderBase() {
		// nothing to do
	}

	/** Constructor. */
	protected ElementProviderBase(
			List<RegionMarkerStrategyBase<Element>> strategies) {
		this.strategies.addAll(strategies);
	}

	/** {@inheritDoc} */
	@Override
	public void init(Resource root, IConQATLogger logger) {
		for (RegionMarkerStrategyBase<Element> strategy : strategies) {
			strategy.setLogger(logger);
		}
		super.init(root, logger);
	}

	/** {@inheritDoc} */
	@Override
	protected void init(Resource root) {
		List<Element> elements = ResourceTraversalUtils.listElements(root,
				getElementClass());

		// evaluate strategies
		for (Element fileElement : elements) {
			for (RegionMarkerStrategyBase<Element> strategy : strategies) {
				strategy.annotate(fileElement);
			}
		}

		elementsIterator = elements.iterator();
	}

	/** Template method. Return class of Element */
	protected abstract Class<Element> getElementClass();

	/** {@inheritDoc} */
	@Override
	protected Element provideNext() {
		// no more elements to return
		if (elementsIterator == null) {
			return null;
		}

		// if more elements present, return next element
		if (elementsIterator.hasNext()) {
			return elementsIterator.next();
		}

		// no more elements: delete iterator and return null
		elementsIterator = null;
		return null;
	}

}