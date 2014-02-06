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

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.conqat.engine.architecture.assessment.shared.IArchitecture;
import org.conqat.engine.architecture.assessment.shared.IComponent;
import org.conqat.engine.architecture.assessment.shared.IPolicy;
import org.conqat.engine.architecture.format.EStereotype;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.clone.DeepCloneException;
import org.conqat.lib.commons.collections.IdentityHashSet;
import org.conqat.lib.commons.collections.ToStringComparator;

/**
 * Architecture definition is the root element in an architecture.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 42073 $
 * @ConQAT.Rating GREEN Hash: 21B773448796D304F5A7000D3B83CF45
 */
public class ArchitectureDefinition extends ComponentNode implements
		IArchitecture {

	/** Maps from components' names to the respective component. */
	private Map<String, ComponentNode> nameLookup;

	/** Creates a new architecture definition. */
	/* package */ArchitectureDefinition() {
		super("<architecture>", new Point(0, 0), new Dimension(0, 0),
				EStereotype.NONE);
	}

	/** Copy constructor. */
	protected ArchitectureDefinition(ArchitectureDefinition other)
			throws DeepCloneException {
		// components are cloned in super
		super(other);

		// cloning of dependency rules is initiated from the root node
		Collection<DependencyPolicy> policies = new ArrayList<DependencyPolicy>();
		other.collectPolicies(policies);
		Map<String, ComponentNode> nameLookup = new HashMap<String, ComponentNode>();
		fillNameLookup(nameLookup);

		for (DependencyPolicy policy : policies) {
			ComponentNode newFrom = nameLookup
					.get(policy.getSource().getName());
			ComponentNode newTo = nameLookup.get(policy.getTarget().getName());
			DependencyPolicy cloned = new DependencyPolicy(policy, newFrom,
					newTo);
			try {
				cloned.registerWithComponents();
			} catch (ConQATException e) {
				// use deep clone exception, as these are handled specifically
				// in the driver
				throw new DeepCloneException(
						"This should not be possible, as the cloned architecture should be same!",
						e);
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public ArchitectureDefinition deepClone() throws DeepCloneException {
		return new ArchitectureDefinition(this);
	}

	/** Returns a sorted list of {@link DependencyPolicy}s */
	public List<DependencyPolicy> getSortedPolicies() {
		List<DependencyPolicy> edges = new ArrayList<DependencyPolicy>();
		collectPolicies(edges);
		Collections.sort(edges, ToStringComparator.INSTANCE);
		return edges;
	}

	/** {@inheritDoc} */
	@Override
	public Set<ComponentNode> getAllComponents() {
		return getDescendants();
	}

	/** {@inheritDoc} */
	@Override
	public IComponent getComponentByName(String name) {
		if (nameLookup == null) {
			nameLookup = new HashMap<String, ComponentNode>();
			fillNameLookup(nameLookup);
		}
		return nameLookup.get(name);
	}

	/** {@inheritDoc} */
	@Override
	public Set<? extends IPolicy> getAllPolicies() {
		Set<DependencyPolicy> edges = new IdentityHashSet<DependencyPolicy>();
		collectPolicies(edges);
		return edges;
	}
}