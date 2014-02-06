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
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.conqat.engine.architecture.assessment.shared.ICodeMapping;
import org.conqat.engine.architecture.assessment.shared.IComponent;
import org.conqat.engine.architecture.format.ECodeMappingType;
import org.conqat.engine.architecture.format.EStereotype;
import org.conqat.engine.architecture.overlap.PatternToCodeMappingAdaptor;
import org.conqat.engine.commons.CommonUtils;
import org.conqat.engine.commons.node.ConQATGeneralNodeBase;
import org.conqat.engine.commons.pattern.PatternList;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.assertion.CCSMPre;
import org.conqat.lib.commons.clone.DeepCloneException;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.IdentityHashSet;
import org.conqat.lib.commons.collections.UnmodifiableList;
import org.conqat.lib.commons.string.StringUtils;

/**
 * This is a node representing a component (which can in turn have sub
 * components). The elements of a system (usually these are classes) are mapped
 * onto these components.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43813 $
 * @ConQAT.Rating GREEN Hash: 24ECC0409BC8B8D69B73C3AA8149EDF7
 */
public class ComponentNode extends ConQATGeneralNodeBase<ComponentNode>
		implements IComponent {

	/** Full unique name of this component. */
	private final String name;

	/** A human-readable description for this component. */
	private String description = StringUtils.EMPTY_STRING;

	/**
	 * Position of this Component. The Position is relative to the parent
	 * Component!
	 */
	private final Point pos;

	/** Dimension of this Component. */
	private final Dimension dim;

	/** Patterns matching names of elements contained in this component. */
	private final PatternList containedElementPatterns = new PatternList();

	/**
	 * Patterns matching names of elements explicitly excluded from this
	 * component. (Overrules containedElementPatterns)
	 */
	private final PatternList excludedElementPatterns = new PatternList();

	/**
	 * All policies for which this component is the source. This is a map from
	 * the target to policy.
	 */
	private final Map<IComponent, DependencyPolicy> sourcePolicies = new IdentityHashMap<IComponent, DependencyPolicy>();

	/** All policies for which this component is the target. */
	private final Collection<DependencyPolicy> targetPolicies = new HashSet<DependencyPolicy>();

	/** The stereotype of this component */
	private final EStereotype stereotype;

	/**
	 * Creates a new {@link ComponentNode}.
	 * 
	 * @param pos
	 *            Position of this component.
	 * @param stereotype
	 *            the stereotype of this component.
	 * 
	 * @throws IllegalArgumentException
	 *             if pos is null.
	 * 
	 */
	public ComponentNode(String name, Point pos, Dimension dim,
			EStereotype stereotype) {
		CCSMPre.isNotNull(pos, "The position must not be null");
		CCSMPre.isNotNull(dim, "The dimension must not be null");
		CCSMPre.isNotNull(stereotype, "The stereotype must not be null");

		this.name = name;
		this.pos = pos;
		this.dim = dim;
		this.stereotype = stereotype;
	}

	/** Copy constructor. */
	protected ComponentNode(ComponentNode other) throws DeepCloneException {
		super(other);
		name = other.name;
		description = other.description;
		containedElementPatterns.addAll(other.containedElementPatterns);
		excludedElementPatterns.addAll(other.excludedElementPatterns);

		pos = other.pos;
		dim = other.dim;
		stereotype = other.stereotype;

		// policies are not copied, as they are processed in the
		// ArchitectureDefinition
	}

	/**
	 * Creates a lookup table from names to components by traversing the nodes
	 * in DFS order and putting results into the provided map.
	 */
	protected void fillNameLookup(Map<String, ComponentNode> nameLookup) {
		nameLookup.put(getName(), this);
		if (hasChildren()) {
			for (ComponentNode child : getChildren()) {
				child.fillNameLookup(nameLookup);
			}
		}
	}

	/**
	 * Collect all policies by traversing the tree in DFS order and using only
	 * fromPolicies, to avoid duplicate entries. Results are put into the
	 * provided collection.
	 */
	public void collectPolicies(Collection<DependencyPolicy> policies) {
		policies.addAll(sourcePolicies.values());
		if (hasChildren()) {
			for (ComponentNode child : getChildren()) {
				child.collectPolicies(policies);
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	protected ComponentNode[] allocateArray(int size) {
		return new ComponentNode[size];
	}

	/** {@inheritDoc} */
	@Override
	public String getId() {
		// names are unique for architectures
		return getName();
	}

	/** {@inheritDoc} */
	@Override
	public String getName() {
		return name;
	}

	/** Returns the name. */
	@Override
	public String toString() {
		return getName();
	}

	/** {@inheritDoc} */
	@Override
	public ComponentNode deepClone() throws DeepCloneException {
		return new ComponentNode(this);
	}

	/** Adds an additional pattern to identify elements of this component. */
	public void addIncludeRegex(String regex) throws ConQATException {
		containedElementPatterns.add(CommonUtils.compilePattern(regex));
	}

	/**
	 * Adds an additional exclude pattern to explicitly exclude elements of this
	 * component. (Overrules patterns added by {@link #addIncludeRegex(String)}
	 * ).
	 */
	/* package */void addExcludeRegex(String regex) throws ConQATException {
		excludedElementPatterns.add(CommonUtils.compilePattern(regex));
	}

	/** Add a dependency policy to this component. */
	/* package */void addPolicy(DependencyPolicy policy) throws ConQATException {
		if (policy.getSource() == this) {
			if (sourcePolicies.containsKey(policy.getTarget())) {
				throw new ConQATException("Duplicate policy from " + getName()
						+ " to " + policy.getTarget());
			}
			sourcePolicies.put(policy.getTarget(), policy);
		}
		if (policy.getTarget() == this) {
			targetPolicies.add(policy);
		}
	}

	/**
	 * Find all components (this plus child components) into which the given
	 * element name fits. They are put into the provided collection. The list of
	 * component nodes is filled by 'preorder' traversal.
	 */
	/* package */void findMatchingComponents(String elementName,
			List<ComponentNode> result) {
		if (containedElementPatterns.matchesAny(elementName)) {

			// explicitly check if elementName is excluded
			if (!excludedElementPatterns.matchesAny(elementName)) {
				result.add(this);
			}
		}

		if (hasChildren()) {
			for (ComponentNode child : getChildren()) {
				child.findMatchingComponents(elementName, result);
			}
		}
	}

	/** Returns whether there is a policy to the other node. */
	@Override
	public boolean hasPolicyTo(IComponent other) {
		return sourcePolicies.containsKey(other);
	}

	/** Returns the dependency to the given component (or null if non exists). */
	@Override
	public DependencyPolicy getPolicyTo(IComponent other) {
		return sourcePolicies.get(other);
	}

	/**
	 * Returns true, if this node (or one of its ancestors) carries the
	 * {@link EStereotype#PUBLIC} stereotype
	 */
	public boolean isPublic() {
		return hasTypeAncestor(EStereotype.PUBLIC);
	}

	/**
	 * Returns true, if this node (or one of its ancestors) carries the
	 * {@link EStereotype#COMPONENT_PUBLIC} stereotype
	 */
	public boolean isComponentPublic() {
		return hasTypeAncestor(EStereotype.COMPONENT_PUBLIC);
	}

	/** Returns true, if node has an ancestor (including this) of given type. */
	private boolean hasTypeAncestor(EStereotype type) {
		ComponentNode ancestor = this;
		while (ancestor != null) {
			if (ancestor.stereotype == type) {
				return true;
			}
			ancestor = ancestor.getParent();
		}

		return false;
	}

	/**
	 * Returns a set containing this node and all predecessors of this node in
	 * the hierarchy! The order of nodes is preserved and starts from the
	 * current node down to the root.
	 */
	@Override
	public LinkedHashSet<ComponentNode> getAncestors() {
		LinkedHashSet<ComponentNode> result = new LinkedHashSet<ComponentNode>();
		ComponentNode node = this;
		while (node != null) {
			result.add(node);
			node = node.getParent();
		}
		return result;
	}

	/** Returns the relative Position of this Component. */
	public Point getPosition() {
		return pos;
	}

	/** Returns the stereotype. */
	@Override
	public EStereotype getStereotype() {
		return stereotype;
	}

	/** Returns the absolute Position of this Component. */
	public Point getAbsolutePosition() {
		Point result = new Point(pos);
		if (getParent() != null) {
			Point parentPos = getParent().getAbsolutePosition();
			result.translate(parentPos.x, parentPos.y);
		}
		return result;
	}

	/** Returns the Dimension of this Component. */
	public Dimension getDimension() {
		return dim;
	}

	/** Returns the absolute bounds of this component. */
	public Rectangle getAbsoluteBounds() {
		return new Rectangle(getAbsolutePosition(), getDimension());
	}

	/** Retrieves all outgoing policies of this component. */
	public Collection<DependencyPolicy> getOutgoingPolicies() {
		return CollectionUtils.asUnmodifiable(sourcePolicies.values());
	}

	/** Retrieves all incoming policies of this component. */
	public Collection<DependencyPolicy> getIncomingPolicies() {
		return CollectionUtils.asUnmodifiable(targetPolicies);
	}

	/**
	 * Creates and returns a list of {@link ICodeMapping}s out of a
	 * {@link PatternList} with the specified {@link ECodeMappingType}.
	 */
	private static List<ICodeMapping> createCodeMappingList(
			PatternList patternList, ECodeMappingType type) {
		List<ICodeMapping> codeMappings = new ArrayList<ICodeMapping>();

		for (Pattern pattern : patternList) {
			codeMappings.add(new PatternToCodeMappingAdaptor(pattern, type));
		}

		return codeMappings;
	}

	/** {@inheritDoc} */
	@Override
	public UnmodifiableList<ICodeMapping> getCodeMappings() {
		List<ICodeMapping> codeMappings = new ArrayList<ICodeMapping>();
		codeMappings.addAll(createCodeMappingList(containedElementPatterns,
				ECodeMappingType.INCLUDE));
		codeMappings.addAll(createCodeMappingList(excludedElementPatterns,
				ECodeMappingType.EXCLUDE));
		return CollectionUtils.asUnmodifiable(codeMappings);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isDescendant(IComponent component) {
		if (component instanceof ComponentNode) {
			LinkedHashSet<ComponentNode> ancestors = getAncestors();
			if (ancestors.contains(component)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Collects all transitive children of this ComponentNode. The result is
	 * returned in the provided collection.
	 */
	private void collectTransitiveChildren(Collection<ComponentNode> children) {
		children.add(this);

		if (hasChildren()) {
			for (ComponentNode component : getChildren()) {
				component.collectTransitiveChildren(children);
			}
		}
	}

	/** Returns true, if a node is a top level component in the architecture */
	@Override
	public boolean isToplevel() {
		return getParent() != null && getParent().getParent() == null;
	}

	/** {@inheritDoc} */
	@Override
	public Set<ComponentNode> getDescendants() {
		Set<ComponentNode> result = new IdentityHashSet<ComponentNode>();
		collectTransitiveChildren(result);
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public Set<? extends IComponent> getSubComponents() {
		if (hasChildren()) {
			IdentityHashSet<IComponent> result = new IdentityHashSet<IComponent>();
			Collections.addAll(result, getChildren());
			return result;
		}
		return CollectionUtils.emptySet();
	}

	/**
	 * Retrieves the human-readable description of this node. This may be an
	 * empty string in case there is no description, but this method will never
	 * return <em>null</em>.
	 */
	public String getDescription() {
		return description;
	}

	/** Sets a human-readable description for this component. */
	public void setDescription(String description) {
		this.description = description;
	}
}