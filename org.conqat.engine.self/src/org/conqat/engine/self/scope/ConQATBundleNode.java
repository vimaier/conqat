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

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.commons.node.ConQATNodeBase;
import org.conqat.engine.commons.node.IRemovableConQATNode;
import org.conqat.engine.core.bundle.BundleInfo;
import org.conqat.lib.commons.clone.DeepCloneException;

/**
 * A node representing a single ConQAT bundle. We use specific nodes for this,
 * as the {@link BundleInfo} is a "real" attribute to improve type safety.
 * 
 * @author Benjamin Hummel
 * @author $Author: juergens $
 * @version $Rev: 35201 $
 * @ConQAT.Rating GREEN Hash: 1F4C36F6029BFF31FB5CDDE81510F9C5
 */
public class ConQATBundleNode extends ConQATNodeBase implements
		IRemovableConQATNode {

	/** The parent node. */
	private ConQATInstallationRoot parent;

	/** Info on the underlying bundle. */
	private final BundleInfo bundleInfo;

	/** The list of child nodes. */
	private final List<ConQATUnitNode> children = new ArrayList<ConQATUnitNode>();

	/** Create new bundle node. */
	/* package */ConQATBundleNode(BundleInfo bundleInfo) {
		this.bundleInfo = bundleInfo;
	}

	/** Copy constructor. */
	protected ConQATBundleNode(ConQATBundleNode node) throws DeepCloneException {
		super(node);
		bundleInfo = node.bundleInfo;
	}

	/** Name string is revision. */
	@Override
	public String getName() {
		return getId();
	}

	/** Returns the revision number. */
	@Override
	public String getId() {
		return bundleInfo.getId();
	}

	/** {@inheritDoc} */
	@Override
	public ConQATBundleNode deepClone() throws DeepCloneException {
		return new ConQATBundleNode(this);
	}

	/** Returns <code>null</code>. */
	@Override
	public ConQATUnitNode[] getChildren() {
		return children.toArray(new ConQATUnitNode[children.size()]);
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
	public ConQATInstallationRoot getParent() {
		return parent;
	}

	/** Returns <code>false</code>. */
	@Override
	public boolean hasChildren() {
		return !children.isEmpty();
	}

	/** Add a child. */
	/* package */void addChild(ConQATUnitNode child) {
		children.add(child);
		child.setParent(this);
	}

	/** Remove a child node. */
	/* package */void removeNode(ConQATUnitNode node) {
		children.remove(node);
	}

	/** Set the parent node. */
	/* package */void setParent(ConQATInstallationRoot parent) {
		this.parent = parent;
	}

	/** Returns the contained bundle info. */
	public BundleInfo getBundleInfo() {
		return bundleInfo;
	}
}