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
package org.conqat.engine.code_clones.core.report;

import org.conqat.engine.code_clones.core.KeyValueStoreBase;

/**
 * Collects information about an element that was analyzed during clone
 * detection. This class is immutable.
 * 
 * @author $Author: juergens $
 * @version $Rev: 37611 $
 * @ConQAT.Rating GREEN Hash: 26FA0F0E40463331DD474C939819A70D
 */
public final class SourceElementDescriptor extends KeyValueStoreBase {

	/** The location. */
	private final String location;

	/** The uniform path. */
	private final String uniformPath;

	/** Length of the element */
	private final int length;

	/** String that identifies content of source element during detection */
	private final String fingerprint;

	/**
	 * Constructor.
	 * 
	 * @param id
	 *            Used to identify the source element.
	 * @param length
	 *            Char length of the element
	 */
	public SourceElementDescriptor(int id, String location, String uniformPath,
			int length, String fingerprint) {
		super(id);
		this.location = location;
		this.uniformPath = uniformPath;
		this.length = length;
		this.fingerprint = fingerprint;
	}

	/** Returns the location. */
	public String getLocation() {
		return location;
	}

	/** Returns the uniform path. */
	public String getUniformPath() {
		return uniformPath;
	}

	/** Returns length in lines. */
	public int getLength() {
		return length;
	}

	/** Returns element fingerprint */
	public String getFingerprint() {
		return fingerprint;
	}
}