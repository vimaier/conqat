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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.conqat.engine.architecture.format.ECodeMappingType;
import org.conqat.engine.core.core.IProgressMonitor;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.ListMap;
import org.conqat.lib.commons.collections.SetMap;
import org.conqat.lib.commons.collections.UnmodifiableList;

/**
 * <p>
 * This class takes care of mapping types to components and vice versa. Types
 * are simple {@link String}s and components are {@link IComponent}s. Types are
 * mapped to components based on the regular expressions that are defined for
 * each component.
 * </p>
 * 
 * <p>
 * After construction, use the method {@link #map(Set)} to create a mapping with
 * the given types. After that you can use the getters to query the result. You
 * can create a new mapping by simply calling {@link #map(Set)} again.
 * </p>
 * 
 * <p>
 * Please note that each mapper is created for one specific architecture. The
 * architecture may change but you cannot replace the architecture itself with a
 * different one. The mapper does not listen to changes of the architecture, so
 * it is up to you to call {@link #map(Set)} when the components of the
 * architecture have changed.
 * </p>
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 44633 $
 * @ConQAT.Rating GREEN Hash: 505C029210C6C610E12688AAB671361E
 */
public class TypeToComponentMapper {

    /** The architecture that contains the relevant components. */
    private IArchitecture architecture;

    /** Types without an associated component. */
    private List<String> orphans;

    /**
     * Maps from a type to the components to which it can be mapped. Assuming an
     * appropriate specification, there should be exactly one component per
     * type. However, this may not always be the case. If the list of components
     * for a type is empty, the type is an orphan. If the list contains more
     * than one component, the specification has overlapping regular
     * expressions. In that case, the type is mapped to the first component of
     * the list which is sorted lexicographically based on the components'
     * names.
     * */
    private ListMap<String, IComponent> typesToComponents =
            new ListMap<String, IComponent>();

    /** Maps from a component to the types mapped to this component. */
    private SetMap<IComponent, String> componentsToTypes =
            new SetMap<IComponent, String>();

    /** Cache for regular expression patterns. */
    private Map<String, Pattern> patterns = new HashMap<String, Pattern>();

    /** If set, progress is reported to this monitor. */
    private IProgressMonitor progressMonitor = null;

    /** Compares two components based on their name. */
    private Comparator<IComponent> componentByNameComparator =
            new Comparator<IComponent>() {
                @Override
                public int compare(IComponent a, IComponent b) {
                    return a.getName().compareTo(b.getName());
                }
            };

    /**
     * Constructs a new mapper for the given architecture. The progress is
     * reported to the given progress monitor, which may be <em>null</em>.
     */
    public TypeToComponentMapper(IArchitecture architecture,
            IProgressMonitor progressMonitor) {
        this.architecture = architecture;
        this.progressMonitor = progressMonitor;
    }

    /** Maps types and components. */
    public void map(Set<String> types) {
        componentsToTypes.clear();
        typesToComponents.clear();

        List<? extends IComponent> components = getAllComponentsSortedByDepth();

        orphans = new ArrayList<String>();
        int typesProcessed = 0;
        for (String type : types) {
            List<IComponent> matchingComponents =
                    getMatchingComponents(type, components);
            if (matchingComponents.isEmpty()) {
                orphans.add(type);
            } else {
                Collections.sort(matchingComponents, componentByNameComparator);
                typesToComponents.addAll(type, matchingComponents);
                componentsToTypes.add(matchingComponents.get(0), type);
            }
            typesProcessed++;
            if (progressMonitor != null) {
                progressMonitor.reportProgress(typesProcessed, types.size());
            }
        }
    }

    /** Returns the names of all types without associated component. */
    public UnmodifiableList<String> getOrphans() {
        return CollectionUtils.asUnmodifiable(orphans);
    }

    /**
     * Retrieves the component to which the given type is mapped. This method
     * return <em>null</em> if the type is not mapped to any component.
     */
    public IComponent getMappedComponentForType(String type) {
        List<IComponent> mappedComponents =
                typesToComponents.getCollection(type);
        if (CollectionUtils.isNullOrEmpty(mappedComponents)) {
            return null;
        }
        return mappedComponents.get(0);
    }

    /** Retrieves types that match more than one component. */
    public ListMap<String, IComponent> getTypesMatchingMultipleComponents() {
        ListMap<String, IComponent> typesMatchingMultipleComponents =
                new ListMap<String, IComponent>();
        for (String type : typesToComponents.getKeys()) {
            List<IComponent> matchingComponents =
                    typesToComponents.getCollection(type);
            if (matchingComponents.size() > 1) {
                typesMatchingMultipleComponents
                        .addAll(type, matchingComponents);
            }
        }
        return typesMatchingMultipleComponents;
    }

    /**
     * Retrieves the types mapped to the given component. If the component does
     * not exist, an empty set is returned.
     */
    public Set<String> getMappedTypes(IComponent component) {
        Set<String> mappedTypes = componentsToTypes.getCollection(component);
        if (mappedTypes == null) {
            return CollectionUtils.emptySet();
        }
        return mappedTypes;
    }

    /** Returns a list of components to which the given type can be mapped. */
    private List<IComponent> getMatchingComponents(String type,
            List<? extends IComponent> components) {
        List<IComponent> matchingComponents = new ArrayList<IComponent>();
        
        Set<IComponent> componentsAlreadyChecked = new HashSet<IComponent>();
        for (IComponent component : components) {
            if (!componentsAlreadyChecked.contains(component)) {
                if (matches(type, component)) {
                    matchingComponents.add(component);
                    componentsAlreadyChecked.addAll(component.getAncestors());
                } else {
                    componentsAlreadyChecked.add(component);
                }
            }
        }
        return matchingComponents;
    }

    /**
     * Retrieves all components of this architecture sorted by their depth. The
     * deepest component will be the first in the list. This has two benefits:
     * 1. When a matching component is searched for a given type, the first
     * match is guaranteed to be the deepest possible. 2. In common
     * architectures, types are more likely to be matched to leaf-components.
     * Hence, the sorting also has a performance benefit.
     */
    private List<? extends IComponent> getAllComponentsSortedByDepth() {
        List<? extends IComponent> components =
                CollectionUtils.sort(architecture.getAllComponents(),
                        new Comparator<IComponent>() {
                            @Override
                            public int compare(IComponent a, IComponent b) {
                                return b.getAncestors().size()
                                        - a.getAncestors().size();
                            }
                        });
        return components;
    }

    /**
     * Tests whether the given type can be mapped to the given components based
     * on the component's include and exclude patterns.
     */
    private boolean matches(String type, IComponent component) {
        boolean result = false;

        // Look for include
        for (ICodeMapping mapping : component.getCodeMappings()) {
            if (mapping.getType() == ECodeMappingType.INCLUDE
                    && getPattern(mapping).matcher(type).matches()) {
                result = true;
                break;
            }
        }

        if (result) {
            // Look for exclude
            for (ICodeMapping mapping : component.getCodeMappings()) {
                if (mapping.getType() == ECodeMappingType.EXCLUDE
                        && getPattern(mapping).matcher(type).matches()) {
                    result = false;
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Retrieves the compiled regular expression for the given code mapping. The
     * regular expression stored in the mapping is assumed to be valid.
     */
    private Pattern getPattern(ICodeMapping mapping) {
        Pattern pattern = patterns.get(mapping.getRegex());
        if (pattern == null) {
            try {
                pattern = Pattern.compile(mapping.getRegex());
            } catch (PatternSyntaxException e) {
                CCSMAssert.fail("Invalid regular expression '"
                        + mapping.getRegex() + "': " + e.getMessage());
            }
            patterns.put(mapping.getRegex(), pattern);
        }
        return pattern;
    }
}
