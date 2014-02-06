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
package org.conqat.engine.resource.filters;

import org.conqat.engine.commons.filter.FilterBase;
import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.IResource;

/**
 * Base classes for element filters that use a pattern list.
 * <p>
 * This base class does not determine which part of an {@link IResource} the
 * patterns are matched against.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 35153FD4420CBB80181222BD72938CC4
 */
public abstract class PatternElementFilterBase<E extends IResource> extends
		FilterBase<E> {

	/** The list of patterns to match against. */
	protected PatternList patternList;

	/** Sets the pattern list used. */
	@AConQATParameter(name = "pattern", minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "An element is discarded if any of the regular expressions in the list matches.")
	public void setPatternList(
			@AConQATAttribute(name = "list", description = "The pattern list used for matching.") PatternList patternList) {
		this.patternList = patternList;
	}

	/** {@inheritDoc} */
	@Override
	protected void preProcessInput(E input) throws ConQATException {
		if (isInverted() && patternList.isEmpty()) {
			throw new ConQATException(
					"Performing filtering with empty pattern list and inversion. "
							+ "This would remove all elements, which is probably not intended!");
		}
	}

	/** {@inheritDoc} */
	@Override
	protected boolean isTarget(E node, ETargetNodes targetNodes) {
		if (!(node instanceof IElement)) {
			return false;
		}
		return super.isTarget(node, targetNodes);
	}
}