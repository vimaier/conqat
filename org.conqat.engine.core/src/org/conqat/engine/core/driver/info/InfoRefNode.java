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
import java.util.Collection;

import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableCollection;

/**
 * Base class for nodes in the reference graph. There are exactly two sub
 * classes:
 * <ul>
 * <li>{@link InfoAttribute}, which can reference outputs on the same hierarchy
 * level and inputs one level above</li>
 * <li>{@link InfoOutput}, which can reference attributes on the same hierarchy
 * level and output one level below</li>
 * </ul>
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: EF22106ED4879FC7FF50A417EC17B7A8
 */
public abstract class InfoRefNode {

	/**
	 * The node which is referenced by this one (i.e. the one it gets the result
	 * from).
	 */
	private InfoRefNode referenced = null;

	/**
	 * The nodes having references to this one (i.e. those depending on the
	 * results of this one).
	 */
	private final Collection<InfoRefNode> referencedBy = new ArrayList<InfoRefNode>();

	/**
	 * Returns the node which is referenced by this one (i.e. the one it gets
	 * the result from). If no node is referenced, null is returned.
	 */
	public InfoRefNode getReferenced() {
		return referenced;
	}

	/**
	 * Returns the nodes having references to this one (i.e. those depending on
	 * the results of this one). The collection provided is unmodifiable.
	 */
	public UnmodifiableCollection<InfoRefNode> getReferencedBy() {
		return CollectionUtils.asUnmodifiable(referencedBy);
	}

	/**
	 * Set the node referenced by this one. This is called by {@link BlockInfo}.
	 */
	/* package */void setReferenced(InfoRefNode referenced) {
		if (this.referenced != null) {
			throw new IllegalStateException("This should only be set once!");
		}
		this.referenced = referenced;
		referenced.referencedBy.add(this);
	}

}