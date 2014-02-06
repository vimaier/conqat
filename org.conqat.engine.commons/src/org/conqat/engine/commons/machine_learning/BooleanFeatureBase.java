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
 * Base class for a feature of type boolean.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 44457 $
 * @ConQAT.Rating GREEN Hash: 7A225D5F102181688C4365BC09743515
 */
public abstract class BooleanFeatureBase<T> extends NamedFeatureBase<T> {

	/** Constructor. */
	public BooleanFeatureBase(String name) {
		super(name);
	}

	/** {@inheritDoc} */
	@Override
	public double getValue(T classificationObject) {
		if (getBooleanValue(classificationObject)) {
			return 1;
		}
		return 0;
	}

	/** Returns a boolean feature value. */
	public abstract boolean getBooleanValue(T classificationObject);
}
