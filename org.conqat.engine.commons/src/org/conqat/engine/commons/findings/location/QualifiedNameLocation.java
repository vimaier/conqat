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

import org.conqat.lib.commons.assertion.CCSMPre;

/**
 * Location identified by a qualified name, which usually is some kind of path
 * expression.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 36296 $
 * @ConQAT.Rating GREEN Hash: 848FFE64268220CD10CBE5EC83BDEF2C
 */
public class QualifiedNameLocation extends ElementLocation {

	/** Version used for serialization. */
	private static final long serialVersionUID = 1;

	/**
	 * The characters which are possibly used for separation of the parts of a
	 * qualified name.
	 */
	public final static String SEPARATOR_CHARS = ":/";

	/** The qualified name. */
	private final String qualifiedName;

	/** Constructor. */
	public QualifiedNameLocation(String qualifiedName, String location,
			String uniformPath) {
		super(location, uniformPath);
		CCSMPre.isNotNull(qualifiedName);
		this.qualifiedName = qualifiedName;
	}

	/** Returns the qualified name. */
	public String getQualifiedName() {
		return qualifiedName;
	}

	/** {@inheritDoc} */
	@Override
	public String toLocationString() {
		return super.toLocationString() + ":" + qualifiedName;
	}
}