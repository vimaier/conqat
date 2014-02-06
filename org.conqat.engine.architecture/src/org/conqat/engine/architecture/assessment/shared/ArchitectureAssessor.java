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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.conqat.engine.architecture.format.EPolicyType;
import org.conqat.engine.core.core.IProgressMonitor;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.ListMap;
import org.conqat.lib.commons.collections.SetMap;
import org.conqat.lib.commons.collections.UnmodifiableList;

/**
 * This class performs the assessment of an architecture.
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 44634 $
 * @ConQAT.Rating GREEN Hash: 35585B9F89FAB5121305BFFC74770123
 */
public class ArchitectureAssessor implements IProgressMonitor {

    /** Identifies the name of the findings category for dependencies. */
    public static final String DEPENDENCY_CATEGORY = "Dependencies";

    /** Identifies the name of the findings group for dependencies. */
    public static final String DEPENDENY_GROUP = "Generic Dependencies";

    /** Identifies the name of the findings category for types. */
    public static final String TYPES_CATEGORY = "Types";

    /** Identifies the name of the findings group for types. */
    public static final String TYPES_GROUP = "All Types";

    /** The architecture which is to be assessed. */
    private IArchitecture architecture;

    /** All dependencies that exist between components. */
    private List<Dependency<? extends IComponent>> dependencies;

    /**
     * All types that exist in the corresponding system. Each type is mapped to
     * the set of types to which it has a dependency.
     */
    private SetMap<String, String> types;

    /** Maps types to the components it matches. */
    private TypeToComponentMapper typeToComponentMapper;

    /** Maps from source components to all their target components. */
    private SetMap<IComponent, IComponent> componentDependencies;

    /** If set, progress is reported to this monitor. */
    private IProgressMonitor progressMonitor = null;

    /** The overall amount of works that needs to be done. */
    private int overallWork = 0;

    /** The amount of works that has already been done. */
    private int workDone = 0;

    /**
     * Creates a new assessor for the given architecture and the given
     * type-level dependencies. The architecture, as well as the type level
     * dependencies may change. In that case, call {@link #assess()} again to
     * create an up-to-date assessment.
     */
    public ArchitectureAssessor(IArchitecture architecture,
            SetMap<String, String> types) {
        this.architecture = architecture;
        this.types = types;
        typeToComponentMapper = new TypeToComponentMapper(architecture, this);
        assess();
    }

    /**
     * Assesses the architecture. To ensure an up-to-date assessment, this
     * method has to be called when the architecture or type-level dependencies,
     * which have been passed to the constructor, changed.
     */
    public final void assess() {
        workDone = 0;
        overallWork = 2 * getTypeCount() + architecture.getAllPolicies().size();
        componentDependencies = new SetMap<IComponent, IComponent>();
        typeToComponentMapper.map(types.getKeys());
        calculateDependencies();
        this.progressMonitor = null;
    }

    /**
     * Assesses the architecture and reports the progress to the given monitor.
     */
    public void assess(IProgressMonitor progressMonitor) {
        this.progressMonitor = progressMonitor;
        assess();
    }

    /** Returns all dependencies that exist between components. */
    public UnmodifiableList<Dependency<? extends IComponent>> getAllDependencies() {
        return CollectionUtils.asUnmodifiable(dependencies);
    }

    /** Returns the names of all types without associated component. */
    public UnmodifiableList<String> getOrphans() {
        return typeToComponentMapper.getOrphans();
    }

    /** Retrieves the total number of types found in the system. */
    public int getTypeCount() {
        return types.getKeys().size();
    }

    /** Retrieves types that match more than one component. */
    public ListMap<String, IComponent> getTypesMatchingMultipleComponents() {
        return typeToComponentMapper.getTypesMatchingMultipleComponents();
    }

    /**
     * Retrieves the types mapped to the given component. If the component does
     * not exist, an empty set is returned.
     */
    public Set<String> getMappedTypes(IComponent component) {
        return typeToComponentMapper.getMappedTypes(component);
    }

    /**
     * Calculates all dependencies between components. Dependencies may
     * originate from the architecture specification, the system's
     * implementation, or both.
     */
    private void calculateDependencies() {
        dependencies = new ArrayList<Dependency<?>>();

        // Add dependencies from the architecture specification.
        loadDependenciesFromSpecifiedArchitecture();

        // Add dependencies that have been extracted from the system.
        loadDependenciesFromImplementation();

        for (Dependency<?> dependency : dependencies) {
            addTypeDependencies(dependency, dependency.getSource(),
                    dependency.getTarget());
        }
    }

    /**
     * Adds all type-level dependencies between the given source and target
     * component to the given dependency. The method proceeds recursively with
     * the source's and target's children until another more specific dependency
     * is found.
     */
    private void addTypeDependencies(Dependency<?> dependency,
            IComponent source, IComponent target) {
        for (String from : getMappedTypes(source)) {
            for (String to : types.getCollection(from)) {
                if (typeToComponentMapper.getMappedComponentForType(to) == target) {
                    dependency.addTypeDependency(new TypeDependency(from, to));
                }
            }
        }

        // Proceed with the source's children.
        for (IComponent sourceChild : source.getSubComponents()) {
            if (!existsDependency(sourceChild, target)) {
                addTypeDependencies(dependency, sourceChild, target);
            }
        }

        // Proceed with the target's children.
        for (IComponent targetChild : target.getSubComponents()) {
            if (!existsDependency(source, targetChild)) {
                addTypeDependencies(dependency, source, targetChild);
            }
        }
    }

    /** Tests whether a dependency between the given components exists. */
    private boolean existsDependency(IComponent source, IComponent target) {
        return componentDependencies.contains(source, target);
    }

    /**
     * Loads all dependencies which are specified in the architecture, namely
     * the 'policies'.
     */
    private void loadDependenciesFromSpecifiedArchitecture() {
        for (IPolicy policy : architecture.getAllPolicies()) {
            Dependency<?> dependency =
                    new Dependency<IComponent>(policy.getSource(),
                            policy.getTarget());

            dependencies.add(dependency);
            componentDependencies.add(policy.getSource(), policy.getTarget());

            // In case of a 'tolerate' policy, load the type-level dependencies
            // which are tolerated and add them to the dependency.
            if (policy.getType() == EPolicyType.TOLERATE_EXPLICIT) {
                for (TypeDependency typeDependency : policy
                        .getToleratedDependencies()) {
                    dependency.addToleratedTypeDependency(new TypeDependency(
                            typeDependency.getSource(), typeDependency
                                    .getTarget()));
                }
            }

            finishStepAndReportProgress();
        }
    }

    /**
     * Loads all dependencies that truly exist in the system. These may overlap
     * with those specified in the architecture but may also include
     * dependencies which have not been specified in the architecture.
     */
    private void loadDependenciesFromImplementation() {
        // The first part looks at each type and maps its associated component
        // (if it has one) to the components of the type's target types. The
        // purpose of this procedure is to exclude redundant dependencies
        // between components as different type-level dependencies may result in
        // a single component-level dependency.
        SetMap<IComponent, IComponent> rawComponentDependencies =
                new SetMap<IComponent, IComponent>();

        for (String sourceType : types.getKeys()) {
            IComponent source =
                    typeToComponentMapper.getMappedComponentForType(sourceType);
            if (source != null) {
                for (String targetType : types.getCollection(sourceType)) {
                    IComponent target =
                            typeToComponentMapper
                                    .getMappedComponentForType(targetType);
                    if (target != null) {
                        rawComponentDependencies.add(source, target);
                    }
                }
            }
            finishStepAndReportProgress();
        }

        // The second part translates the previously created map into actual
        // dependencies excluding those dependencies where one component is an
        // ancestor of the other component.
        for (IComponent source : rawComponentDependencies.getKeys()) {
            for (IComponent target : rawComponentDependencies
                    .getCollection(source)) {
                if (!target.isDescendant(source)
                        && !source.isDescendant(target)
                        && !isSubsumedByOtherDependency(source, target)) {
                    dependencies
                            .add(new Dependency<IComponent>(source, target));
                    componentDependencies.add(source, target);
                }
            }
        }
    }

    /**
     * Tests if the dependency between the two given components is subsumed by
     * another dependency, i.e., there is an explicit dependency between the
     * ancestors of the two components.
     */
    private boolean isSubsumedByOtherDependency(IComponent source,
            IComponent target) {
        for (IDependency dependency : dependencies) {
            // A dependency can be subsumed by explicit dependencies only.
            if (dependency.getType() == EPolicyType.ALLOW_IMPLICIT
                    || dependency.getType() == EPolicyType.DENY_IMPLICIT) {
                continue;
            }

            // Try all combinations of the components' ancestors and check
            // whether they match the dependency's source and target.
            for (IComponent sourceAncestor : source.getAncestors()) {
                for (IComponent targetAncestor : target.getAncestors()) {
                    if (dependency.getSource() == sourceAncestor
                            && dependency.getTarget() == targetAncestor) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /** Finishes one step in the analysis and reports the progress. */
    private void finishStepAndReportProgress() {
        workDone++;
        CCSMAssert.isTrue(workDone <= overallWork,
                "The work that has been done (" + workDone + ") exceeds the " +
                "maximum amount (" + overallWork + ") that has been " +
                "specified before.");
        if (progressMonitor != null) {
            progressMonitor.reportProgress(workDone, overallWork);
        }
    }

    /**
     * This method should be called only by the TypeToComponentMapper that is a
     * member of this class. Otherwise, the progress will be reported
     * erroneously. Unfortunately, we cannot restrict the visibility
     * appropriately. It is important that the TypeToComponentMapper calls this
     * method <em>once</em> after <em>each</em> single type. The workDone and
     * overallWork arguments are ignored, because this class takes care of
     * counting the steps itself and knows how many types need to be processed.
     */
    @Override
    public void reportProgress(int workDone, int overallWork) {
        finishStepAndReportProgress();
    }
}
