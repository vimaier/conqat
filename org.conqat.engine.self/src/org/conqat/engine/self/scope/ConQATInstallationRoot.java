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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.commons.node.ConQATNodeBase;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.IRemovableConQATNode;
import org.conqat.engine.commons.node.NodeConstants;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.clone.DeepCloneException;

/**
 * The root node for a ConQAT installation.
 * 
 * @author Benjamin Hummel
 * @author $Author: juergens $
 * @version $Rev: 35201 $
 * @ConQAT.Rating GREEN Hash: E772C8B1D440C87D47A18CCC1999207E
 */
public class ConQATInstallationRoot extends ConQATNodeBase implements
		IRemovableConQATNode {

	/** The directory ConQAT is installed in. */
	private final File conqatDirectory;

	/** The list of child nodes. */
	private final List<ConQATBundleNode> children = new ArrayList<ConQATBundleNode>();

	/** Create a new log entry root. */
	/* package */ConQATInstallationRoot(File conqatDirectory)
			throws ConQATException {
		setValue(NodeConstants.HIDE_ROOT, true);
		this.conqatDirectory = conqatDirectory;

		// Check for the existence of a 'lib' directory as "heuristic" for
		// identifying a ConQAT directory.
		if (!conqatDirectory.isDirectory()
				|| !new File(conqatDirectory, "lib").isDirectory()) {
			throw new ConQATException("Not a ConQAT installation directory: "
					+ conqatDirectory);
		}
	}

	/** Copy constructor. */
	protected ConQATInstallationRoot(ConQATInstallationRoot root)
			throws DeepCloneException {
		super(root);
		conqatDirectory = root.conqatDirectory;
		for (ConQATBundleNode c : root.children) {
			addChild(c.deepClone());
		}
	}

	/** {@inheritDoc} */
	@Override
	public String getName() {
		return conqatDirectory.toString();
	}

	/** {@inheritDoc} */
	@Override
	public String getId() {
		return "ConQAT installation at " + conqatDirectory.toString();
	}

	/** {@inheritDoc} */
	@Override
	public ConQATInstallationRoot deepClone() throws DeepCloneException {
		return new ConQATInstallationRoot(this);
	}

	/** {@inheritDoc} */
	@Override
	public ConQATBundleNode[] getChildren() {
		return children.toArray(new ConQATBundleNode[children.size()]);
	}

	/** {@inheritDoc} */
	@Override
	public void remove() {
		// nothing to do.
	}

	/** {@inheritDoc} */
	@Override
	public IConQATNode getParent() {
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public boolean hasChildren() {
		return !children.isEmpty();
	}

	/** Add a child. */
	/* package */void addChild(ConQATBundleNode child) {
		children.add(child);
		child.setParent(this);
	}

	/** Remove a child node. */
	/* package */void removeNode(ConQATBundleNode node) {
		children.remove(node);
	}

	/** Returns the directory ConQAT is installed into. */
	public File getConQATDirectory() {
		return conqatDirectory;
	}
}