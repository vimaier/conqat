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
package org.conqat.engine.commons.node;

import org.conqat.lib.commons.clone.DeepCloneException;

/**
 * The interface for ConQAT nodes supporting the remove operation.
 * 
 * @author Benjamin Hummel
 * @author $Author: hummelb $
 * 
 * @version $Rev: 36404 $
 * @ConQAT.Rating GREEN Hash: 8C488C94629946D083E4359561400A59
 */
public interface IRemovableConQATNode extends IConQATNode {

	/**
	 * Removes this node from the node hierarchy by unlinking from the parent
	 * node. If this is the root node (i.e. {@link IConQATNode#getParent()}
	 * returns <code>null</code>), nothing will happen.
	 */
	public void remove();

	/**
	 * {@inheritDoc}
	 * <p>
	 * Redeclared for covariant return type.
	 */
	@Override
	public IRemovableConQATNode[] getChildren();

	/**
	 * {@inheritDoc}
	 * <p>
	 * Redeclared for covariant return type.
	 */
	@Override
	public IRemovableConQATNode deepClone() throws DeepCloneException;
}