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
package org.conqat.engine.commons.machine_learning;

/**
 * Interface for a feature used during machine learning.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 44457 $
 * @ConQAT.Rating GREEN Hash: 7067AB5DD21218B328D19175846260F0
 */
public interface IFeature<T> {

	/** Returns the name of the feature */
	public String getName();

	/** Returns the value of the feature for the given classification object */
	public double getValue(T classificationObject);

}
