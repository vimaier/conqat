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
package org.conqat.engine.resource.scope.zip;

import java.util.HashSet;
import java.util.Set;

import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.commons.traversal.NodeTraversingProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IElement;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 41867 $
 * @ConQAT.Rating GREEN Hash: D6DB54CE4D81368C66ABB0472BDA0884
 */
@AConQATProcessor(description = "Marks the files in the input scope which appear also in the output scope. "
		+ "This is based purely on the name of each element in used-files, and does not compare the scope of used-files).")
public class UsedFilesInZipAnnotationProcessor extends
		NodeTraversingProcessorBase<IConQATNode> {

	/** Set unused files. */
	@AConQATParameter(name = "used-files", minOccurrences = 1, maxOccurrences = 1, description = "Used files parameter.")
	public void setUsedFilename(
			@AConQATAttribute(name = "scope", description = "The used files in the ZIP file.") IConQATNode usedFiles) {
		this.usedFiles = usedFiles;
	}

	/** {@ConQAT.Doc} */
	@AConQATKey(description = "The key where the used property is stored.", type = "java.lang.Boolean")
	public static final String USED_KEY = "used";

	/** The unused files hierarchy. */
	private IConQATNode usedFiles;

	/** The set of used files in the zip. */
	private Set<String> usedFileSet = new HashSet<String>();

	/** {@inheritDoc} */
	@Override
	protected ETargetNodes getTargetNodes() {
		return ETargetNodes.LEAVES;
	}

	/** {@inheritDoc} */
	@Override
	protected void setUp(IConQATNode root) throws ConQATException {
		super.setUp(root);
		if (!usedFiles.hasChildren()) {
			return;
		}
		for (IConQATNode node : usedFiles.getChildren()) {
			usedFileSet.add(node.getName());
		}
	}

	/** {@inheritDoc} */
	@Override
	public void visit(IConQATNode node) {
		// The node is actually an element
		IElement nodeResource = (IElement) node;
		if (usedFileSet.contains(nodeResource.getLocation())) {
			node.setValue(USED_KEY, true);
		} else {
			node.setValue(USED_KEY, false);
		}
	}

}