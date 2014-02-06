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
package org.conqat.engine.self.scope;

import org.conqat.engine.commons.node.ConQATNodeBase;
import org.conqat.engine.commons.node.IRemovableConQATNode;
import org.conqat.lib.commons.clone.DeepCloneException;

/**
 * A node that represents a unit (block or processor) in the ConQAT hierarchy.
 * 
 * @author hummelb
 * @author $Author: juergens $
 * @version $Rev: 35201 $
 * @ConQAT.Rating GREEN Hash: 4BBB0A49F79E565326E4268EA08D53B4
 */
public class ConQATUnitNode extends ConQATNodeBase implements
		IRemovableConQATNode {

	/** The parent node. */
	private ConQATBundleNode parent;

	/** The name of the unit. */
	private final String unitName;

	/** Create new bundle node. */
	protected ConQATUnitNode(String unitName, String typeValue,
			ConQATBundleNode parent) {
		this.unitName = unitName;
		setValue(ConQATInstallationScope.TYPE_KEY, typeValue);
		parent.addChild(this);
	}

	/** Copy constructor. */
	protected ConQATUnitNode(ConQATUnitNode node) throws DeepCloneException {
		super(node);
		unitName = node.unitName;
	}

	/** {@inheritDoc} */
	@Override
	public ConQATUnitNode deepClone() throws DeepCloneException {
		return new ConQATUnitNode(this);
	}

	/** Returns the ID. */
	@Override
	public String getName() {
		return getId();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * As unit names are fully qualified and globally unique in ConQAT, we use
	 * the name as ID.
	 */
	@Override
	public String getId() {
		return unitName;
	}

	/** Returns <code>null</code>. */
	@Override
	public IRemovableConQATNode[] getChildren() {
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public void remove() {
		if (parent != null) {
			parent.removeNode(this);
		}
	}

	/** {@inheritDoc} */
	@Override
	public ConQATBundleNode getParent() {
		return parent;
	}

	/** Returns <code>false</code>. */
	@Override
	public boolean hasChildren() {
		return false;
	}

	/** Set the parent node. */
	/* package */void setParent(ConQATBundleNode parent) {
		this.parent = parent;
	}
}