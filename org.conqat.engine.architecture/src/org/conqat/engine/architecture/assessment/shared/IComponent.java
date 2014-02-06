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

import java.util.Collection;

import org.conqat.engine.architecture.format.EStereotype;
import org.conqat.lib.commons.collections.UnmodifiableList;

/**
 * An interface for a component of an architecture. By implementing this
 * interface, components can be used for the general architecture assessment
 * infrastructure.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 41263 $
 * @ConQAT.Rating GREEN Hash: 678C9E64CB8EB96DB745D22765C1F34C
 */
public interface IComponent {

    /**
     * Retrieves the name of this components. The name has to be unique for each
     * component.
     */
    String getName();
    
    /**
     * Retrieves a collection with all ancestors of this component, including
     * the component itself.
     */
    Collection<? extends IComponent> getAncestors();
    
    /**
     * Retrieves a collection with all descendants of this component, including
     * this component itself.
     */
    Collection<? extends IComponent> getDescendants();

    /** Tests whether this component is a descendant of the given component. */
    boolean isDescendant(IComponent source);

    /** Retrieves the list of code mappings associated with this component. */
    UnmodifiableList<? extends ICodeMapping> getCodeMappings();

    /** Tests if this component has an outgoing policy to the given target. */
    boolean hasPolicyTo(IComponent target);

    /**
     * Retrieves the outgoing policy to the given target, or <code>null</code>
     * if there is no such policy.
     */
    IPolicy getPolicyTo(IComponent target);

    /**
     * Retrieves the parent of this component, or <code>null</code> if the
     * component is top-level and does not have a parent.
     */
    IComponent getParent();

    /** Retrieves the stereotype for this component. */
    EStereotype getStereotype();

    /** Tests whether this component is top-level, i.e., it has no parent. */
    boolean isToplevel();

    /** Retrieves the direct children of this component. */
    Collection<? extends IComponent> getSubComponents();
}
