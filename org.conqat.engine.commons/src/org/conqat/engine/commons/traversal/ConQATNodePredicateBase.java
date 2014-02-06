/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
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
package org.conqat.engine.commons.traversal;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.lib.commons.predicate.IPredicate;

/**
 * Base class for predicate processors that operate on {@link IConQATNode}s.
 * <p>
 * This is not generic, as ConQAT does not allow to check for generic type
 * parameters at the interface.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 45908 $
 * @ConQAT.Rating YELLOW Hash: 2ADB1A2F3941B4EA455526607F61D041
 */
public abstract class ConQATNodePredicateBase extends ConQATProcessorBase
		implements IPredicate<IConQATNode> {

	/** {@inheritDoc} */
	@Override
	public abstract boolean isContained(IConQATNode node);

	/** {@inheritDoc} */
	@Override
	public ConQATNodePredicateBase process() {
		return this;
	}
}
