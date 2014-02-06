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

import java.util.Set;

/**
 * This interface defines the methods which an architecture has to provide in
 * order to be used with the {@link ArchitectureAssessor}.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 42066 $
 * @ConQAT.Rating GREEN Hash: 6204CC833D8E236DBD42ABD3897FC96B
 */
public interface IArchitecture {
    
    /** Retrieves the list of all policies that exist in this architecture. */
    Set<? extends IPolicy> getAllPolicies();
    
    /** Retrieves the list of all components that exist in this architecture. */
    Set<? extends IComponent> getAllComponents();

    /**
     * Retrieves the component with the given name or <code>null</code> if there
     * is no component with the given name.
     */
    IComponent getComponentByName(String name);
}
