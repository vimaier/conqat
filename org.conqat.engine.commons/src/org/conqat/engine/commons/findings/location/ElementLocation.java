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
package org.conqat.engine.commons.findings.location;

import java.io.Serializable;

import org.conqat.lib.commons.clone.IDeepCloneable;

/**
 * Base class for locations. Locations are immutable and thus return this at
 * deep cloning.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating GREEN Hash: 82BEB1645BABD9B7DBA7D15166C05209
 */
public class ElementLocation implements IDeepCloneable, Serializable {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/** The location (see {@link #getLocation()}). */
	private final String location;

	/** The uniform path (see {@link #getUniformPath()}). */
	private final String uniformPath;

	/** Constructor. */
	public ElementLocation(String location, String uniformPath) {
		this.location = location;
		this.uniformPath = uniformPath;
	}

	/**
	 * Get a string that identifies the location of the element, e.g. a file
	 * system path. This location is specific to the running analysis, i.e.
	 * depends on time and the concrete machine ConQAT is running on.
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * Returns the uniform path. This is an artificial path that uniquely
	 * defines a resource across machine boundaries. This should be used for
	 * persisted information.
	 */
	public String getUniformPath() {
		return uniformPath;
	}

	/** {@inheritDoc} */
	@Override
	public IDeepCloneable deepClone() {
		return this;
	}

	/**
	 * Returns a single line description of the location that is meaningful to
	 * the user.
	 */
	public String toLocationString() {
		return getUniformPath();
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return toLocationString();
	}

}