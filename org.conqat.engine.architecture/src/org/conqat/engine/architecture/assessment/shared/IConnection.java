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
package org.conqat.engine.architecture.assessment.shared;

/**
 * A directed connection between two components of an architecture represented
 * as source and target.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 41263 $
 * @ConQAT.Rating GREEN Hash: B011567ACAB009D298331BA10EFCEB66
 */
public interface IConnection {

	/**
	 * Returns the source {@link IComponent}. The source is never
	 * <code>null</code>.
	 */
	IComponent getSource();

	/**
	 * Returns the target {@link IComponent}. The target is never
	 * <code>null</code>.
	 */
	IComponent getTarget();
	
}