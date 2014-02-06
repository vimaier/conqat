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
import org.conqat.lib.commons.clone.IDeepCloneable;

/**
 * The interface for ConQAT nodes used to exchange data between processors.
 * ConQATNodes are usually organized either as a tree or as a flat list (e.g. a
 * tree of depth one). Additionally each node can carry data which is accessed
 * using string keys.
 * 
 * @author Benjamin Hummel
 * @author $Author: hummelb $
 * 
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: 7E7ECC61ECF21A7F63CFC777AFF2B6FF
 */
public interface IConQATNode extends IDeepCloneable {

	/**
	 * Returns the ID of the node. This should be a string which is globally
	 * unique for the entire tree. Often the ID is composed of the name of the
	 * node and the nodes of all ancestors, although this is not required.
	 */
	public String getId();

	/**
	 * Returns the name of this node. The name must be unique within all nodes
	 * sharing the same parent.
	 */
	public String getName();

	/** Obtains one of the values of the node by using a keyword. */
	public Object getValue(String key);

	/** Setting a value in the node by using a keyword. */
	public void setValue(String key, Object value);

	/** Returns the parent node of this node, or null if this is the root node. */
	public IConQATNode getParent();

	/**
	 * Returns all children of this node. The return value is an array, so
	 * subclasses can use covariant return types. If {@link #hasChildren()} is
	 * false this may return <code>null</code>.
	 */
	public IConQATNode[] getChildren();

	/** Returns whether the node has children. */
	public boolean hasChildren();

	/**
	 * {@inheritDoc}
	 * <p>
	 * Deep clone of this node. This method is redefined because of the
	 * covariant return type.
	 */
	@Override
	public IConQATNode deepClone() throws DeepCloneException;
}