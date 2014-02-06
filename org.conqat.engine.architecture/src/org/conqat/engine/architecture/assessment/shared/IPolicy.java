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

import org.conqat.engine.architecture.format.EPolicyType;

/**
 * A policy between two components of an architecture. Apart from the
 * connection itself, a policy has a type.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 41263 $
 * @ConQAT.Rating GREEN Hash: 2D9B7B300CEF86BCCEFB7709754D3EC2
 */
public interface IPolicy extends IConnection {
    
    /** Retrieve the type of policy between the source and target. */
    EPolicyType getType();
    
    /**
     * Retrieves a list of type-level dependencies which are tolerated. This
     * always returns an empty collection unless the policy's type is
     * 'TOLERATE_EXPLICIT'. In that case, the collection contains all tolerated
     * type-level dependencies. Note. that the collection may still be empty in
     * that case.
     */
    Collection<TypeDependency> getToleratedDependencies();
}
