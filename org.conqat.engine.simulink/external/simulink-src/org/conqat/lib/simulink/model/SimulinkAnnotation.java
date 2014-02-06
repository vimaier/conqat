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
package org.conqat.lib.simulink.model;

import java.util.Set;

/**
 * Class for Simulink annotations, which are basically comments in the Simulink
 * model.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: D389A647AB397A36E23C3CF786FD9C7D
 */
public class SimulinkAnnotation extends SimulinkElementBase {
	/** Create annotation. */
	public SimulinkAnnotation() {
		super();
	}

	/** Create annotation from other annotation (for deep cloning). */
	private SimulinkAnnotation(SimulinkAnnotation other) {
		super(other);
	}

	/**
	 * Get annotation default parameter.
	 */
	@Override
	/* package */String getDefaultParameter(String name) {
		return getModel().getAnnotationDefaultParameter(name);
	}

	/**
	 * Get annotation default parameter names.
	 */
	@Override
	/* package */Set<String> getDefaultParameterNames() {
		return getModel().getAnnotationDefaultParameterNames();
	}

	/** Deep clone annotation. */
	@Override
	public SimulinkAnnotation deepClone() {
		return new SimulinkAnnotation(this);
	}

}