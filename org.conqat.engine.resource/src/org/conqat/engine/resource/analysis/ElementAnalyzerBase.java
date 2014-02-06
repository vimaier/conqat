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
package org.conqat.engine.resource.analysis;

import java.util.HashSet;
import java.util.Set;

import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.commons.traversal.NodeTraversingProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Base class for processors that analyze elements.
 * 
 * @param <R>
 *            type of the resource
 * 
 * @param <E>
 *            type of the element (in the resource tree)
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating YELLOW Hash: DB6742976BD78E919F979396197FBF9D
 */
public abstract class ElementAnalyzerBase<R extends IResource, E extends IElement>
		extends NodeTraversingProcessorBase<R> {

	/** Key in which ignore flags are stored */
	protected final Set<String> ignoreKeys = new HashSet<String>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "filter", description = "Determines whether elements that are marked as ignored, "
			+ "e.g. because they are in genereated code, are ignored.", minOccurrences = 0, maxOccurrences = -1)
	public void setFilterIgnored(
			@AConQATAttribute(name = "key", description = "Key that contains ignore flags.", defaultValue = "ignore") String ignoreKey)
			throws ConQATException {

		if (StringUtils.isEmpty(ignoreKey)) {
			throw new ConQATException("Ignore key must not be empty");
		}

		ignoreKeys.add(ignoreKey);
	}

	/** {@inheritDoc} */
	@Override
	protected ETargetNodes getTargetNodes() {
		return ETargetNodes.ALL;
	}

	/** Template method. Returns class for parameter E */
	protected Class<?> getElementClass() {
		return IElement.class;
	}

	/** Adds all keys provided by {@link #getKeys()} to the the display list. */
	@SuppressWarnings("unused")
	@Override
	protected void setUp(R root) throws ConQATException {
		NodeUtils.addToDisplayList(root, getKeys());
	}

	/**
	 * Calls {@link #analyzeElement(IElement)} for every element.
	 * 
	 * @throws ConQATException
	 *             if analysis fails.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void visit(R element) throws ConQATException {
		if (getElementClass().isAssignableFrom(element.getClass())
				&& !ResourceTraversalUtils.isIgnored(element, ignoreKeys)) {
			analyzeElement((E) element);
		}
	}

	/** Template method for the keys to add to the display list. */
	protected abstract String[] getKeys();

	/**
	 * This is called for every element.
	 * 
	 * @throws ConQATException
	 *             if analysis fails.
	 */
	protected abstract void analyzeElement(E element) throws ConQATException;
}