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
package org.conqat.engine.architecture.scope;

import org.conqat.engine.architecture.assessment.shared.Dependency;
import org.conqat.engine.architecture.assessment.shared.TypeDependency;
import org.conqat.engine.architecture.format.EPolicyType;
import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.findings.FindingReport;
import org.conqat.engine.commons.findings.FindingsList;
import org.conqat.engine.commons.findings.util.FindingUtils;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableList;

/**
 * This class extends the shared type {@link Dependency} adding some engine
 * specific features.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 23830E2D0F3FEFF141528DFBD2A17994
 */
public class DependencyPolicy extends Dependency<ComponentNode> {

	/** The dependencies findings. */
	private FindingsList dependencies;

	/** Create new policy. */
	public DependencyPolicy(ComponentNode from, ComponentNode to,
			EPolicyType policyType) {
	    super(from, to, policyType);
		if (from == null || to == null) {
			throw new IllegalArgumentException("Nodes must not be null");
		}
		if (from == to) {
			throw new IllegalArgumentException("No self loops allowed!");
		}

		dependencies = new FindingsList(from);
	}

	/**
	 * Copy constructor using another DependencyPolicy as reference but new from
	 * and to components. Used for cloning component node hierarchies.
	 */
	/* package */DependencyPolicy(DependencyPolicy depPolicy,
			ComponentNode newFrom, ComponentNode newTo) {
		this(newFrom, newTo, depPolicy.getType());

		dependencies = new FindingsList(depPolicy.dependencies, newFrom);
        
		for (TypeDependency dependency : depPolicy.getToleratedDependencies()) {
		    addToleratedTypeDependency(dependency);
		}
		
		for (TypeDependency dependency : depPolicy.getTypeDependencies()) {
            addTypeDependency(dependency);
        }
	}

	/** Insert this policy into the referenced components. */
	public void registerWithComponents() throws ConQATException {
	    getSource().addPolicy(this);
		getTarget().addPolicy(this);
	}

	/** Adds a dependency for this policy. */
	public void addDependency(Finding finding) throws ConQATException {
		FindingReport findingReport = NodeUtils.getFindingReport(NodeUtils
				.getRootNode(getSource()));
		dependencies.add(FindingUtils.adoptFinding(findingReport, finding));	
	}

	/** Returns the dependencies. */
	public UnmodifiableList<Finding> getDependencies() {
		return CollectionUtils.asUnmodifiable(dependencies);
	}
}