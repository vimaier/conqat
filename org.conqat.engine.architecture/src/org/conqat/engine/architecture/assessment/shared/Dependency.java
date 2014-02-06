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
package org.conqat.engine.architecture.assessment.shared;

import java.util.HashSet;
import java.util.Set;

import org.conqat.engine.architecture.format.EAssessmentType;
import org.conqat.engine.architecture.format.EPolicyType;
import org.conqat.engine.architecture.format.EStereotype;
import org.conqat.lib.commons.assertion.CCSMAssert;

/**
 * A dependency between two components of the architecture.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 42066 $
 * @ConQAT.Rating GREEN Hash: A38A9BC63D4521FD1B97AF1C0C45716B
 */
public class Dependency<C extends IComponent> implements IDependency {

	/** The dependency's source. */
	private C source;

	/** The dependency's target. */
	private C target;

	/** The dependency's type of policy. */
	private EPolicyType policyType;

	/** The assessment of this dependency. */
	private EAssessmentType assessmentType;

	/**
	 * The list of dependencies between types mapped to the source and target
	 * component.
	 */
	private Set<TypeDependency> dependencies = new HashSet<TypeDependency>();

	/** The list of tolerated dependencies between types. */
	private Set<TypeDependency> toleratedDependencies = new HashSet<TypeDependency>();

	/**
	 * Constructs a new dependency between the source and the target. Use this
	 * constructor if you want to have the policy type automatically inferred.
	 */
	public Dependency(C source, C target) {
		this.source = source;
		this.target = target;
		this.policyType = calculatePolicyType();
	}

	/**
	 * Constructs a new dependency between the source and the target. Use this
	 * constructor if you know the policy type already.
	 */
	protected Dependency(C source, C target, EPolicyType policyType) {
		this.source = source;
		this.target = target;
		this.policyType = policyType;
	}

	/**
	 * Adds the given dependency to the list of tolerated dependencies. This
	 * method can be used only if the dependency is a tolerated policy.
	 */
	public final void addToleratedTypeDependency(TypeDependency dependency) {
		CCSMAssert.isTrue(policyType == EPolicyType.TOLERATE_EXPLICIT,
				"Type dependencies can be tolerated only if the dependency's "
						+ "type is " + EPolicyType.TOLERATE_EXPLICIT.toString()
						+ ".");
		toleratedDependencies.add(dependency);
	}

	/** Adds the given dependency to the list of dependencies. */
	public final void addTypeDependency(TypeDependency dependency) {
		dependencies.add(dependency);
	}

	/** {@inheritDoc} */
	@Override
	public C getSource() {
		return source;
	}

	/** {@inheritDoc} */
	@Override
	public C getTarget() {
		return target;
	}

	/** {@inheritDoc} */
	@Override
	public EPolicyType getType() {
		return policyType;
	}

	/** {@inheritDoc} */
	@Override
	public EAssessmentType getAssessment() {
		if (assessmentType == null) {
			assessmentType = assess();
		}
		return assessmentType;
	}

	/**
	 * Creates the assessment for this dependency based on the type of policy
	 * and the dependencies between the types mapped to the dependency's source
	 * and target components.
	 */
	private EAssessmentType assess() {
		boolean empty = !hasTypeLevelDependency();

		switch (policyType) {
		case ALLOW_EXPLICIT:
		case ALLOW_IMPLICIT:
			if (empty) {
				return EAssessmentType.UNNECESSARY;
			}
			return EAssessmentType.VALID;
		case DENY_IMPLICIT:
		case DENY_EXPLICIT:
			if (empty) {
				return EAssessmentType.VALID;
			}
			return EAssessmentType.INVALID;
		case TOLERATE_EXPLICIT:
			if (empty) {
				return EAssessmentType.UNNECESSARY;
			}

			for (TypeDependency dependency : getTypeDependencies()) {
				if (!toleratedDependencies.contains(dependency)) {
					return EAssessmentType.INVALID;
				}
			}

			return EAssessmentType.VALID;
		default:
			CCSMAssert.fail("Unknown value: " + policyType);
			return null;
		}
	}

	/**
	 * Determines whether there is a type-level dependency between the source
	 * and target component. A type-level dependency is a dependency extracted
	 * from the system's implementation is independent from the architecture's
	 * specification.
	 */
	private boolean hasTypeLevelDependency() {
		return !getTypeDependencies().isEmpty();
	}

	/**
	 * Calculates the type of policy between the source and target of this
	 * dependency. The result is an explicit policy if it was specified in the
	 * architecture and implicit if results from the type-level dependencies
	 * extracted from the system.
	 */
	private EPolicyType calculatePolicyType() {
		if (source.hasPolicyTo(target)) {
			// The dependency is specified in the architecture, therefore the
			// policy is explicit and the type is as specified.
			return source.getPolicyTo(target).getType();
		}

		// The dependency is not specified in the architecture and is therefore
		// implicit. Since there cannot be any implicit toleration, we have to
		// determine only whether this is an allow or deny policy. This is done
		// by looking at the ancestors of the source and target component.
		for (IComponent sourceAncestor : source.getAncestors()) {
			for (IComponent targetAncestor : target.getAncestors()) {
				if (sourceAncestor.hasPolicyTo(targetAncestor)) {
					// If there is a policy specified in the architecture use
					// its type. Note, that the policy is still implicit as we
					// are looking at ancestors of the real source and target.
					if (sourceAncestor.getPolicyTo(targetAncestor).getType() == EPolicyType.DENY_EXPLICIT) {
						return EPolicyType.DENY_IMPLICIT;
					}
					return EPolicyType.ALLOW_IMPLICIT;
				} else if (targetAncestor.getStereotype() == EStereotype.PUBLIC) {
					// Otherwise, check whether the target is public...
					return EPolicyType.ALLOW_IMPLICIT;
				} else if (targetAncestor.getStereotype() == EStereotype.COMPONENT_PUBLIC
						&& !targetAncestor.isToplevel()
						&& sourceAncestor.getAncestors().contains(
								targetAncestor.getParent())) {
					// ... or component public, not a top-level component, and
					// the source is within the same component.
					return EPolicyType.ALLOW_IMPLICIT;
				}
			}
		}

		return EPolicyType.DENY_IMPLICIT;
	}

	/**
	 * Retrieves all dependencies between types mapped to the source and target
	 * of this dependency.
	 */
	@Override
	public Set<TypeDependency> getTypeDependencies() {
		return dependencies;
	}

	/** Retrieves the set of type-level dependencies which are tolerated. */
	@Override
	public Set<TypeDependency> getToleratedDependencies() {
		return toleratedDependencies;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return getSource().getName() + " -> " + getTarget().getName();
	}
}
