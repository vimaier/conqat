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
 * Base class for features which have a name.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 44457 $
 * @ConQAT.Rating GREEN Hash: BE01BD034708147759BC894779A6E043
 */
public abstract class NamedFeatureBase<T> implements IFeature<T> {

	/** Name of the feature. */
	private final String name;

	/** Constructor. */
	public NamedFeatureBase(String name) {
		this.name = name;
	}

	/** Returns name. */
	@Override
	public String getName() {
		return name;
	}

}
