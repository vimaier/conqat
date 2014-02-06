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
package org.conqat.engine.commons.assessment;

/**
 * Enumeration for specifying how the delta of a value is interpreted.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 40515 $
 * @ConQAT.Rating GREEN Hash: 1BD5D59E92E92E93DF5B537A9C70840B
 */
public enum EDeltaRating {
    
    /** Indicates that a higher value is an improvement. */
    HIGHER_IS_BETTER,
    
    /** Indicates that a lower value is an improvement. */
    LOWER_IS_BETTER,
    
    /** Indicates that 'better' is not defined for a value. */
    NONE;

}
