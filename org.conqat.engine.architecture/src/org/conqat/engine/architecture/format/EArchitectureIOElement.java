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
package org.conqat.engine.architecture.format;

/**
 * Enum of elements used in the architecture definition file and architecture
 * assessment file.
 * 
 * @author hummelb
 * @author $Author: juergens $
 * @version $Rev: 35037 $
 * @ConQAT.Rating GREEN Hash: 270B289A76D8F8B0125AA8E38170AE3D
 */
public enum EArchitectureIOElement {

	/** Element 'conqat-architecture'. */
	CONQAT_ARCHITECTURE,

	/** Assessment element */
	ASSESSMENT,

	/** Element 'component'. */
	COMPONENT,

	/** Element 'allow'. */
	ALLOW,

	/** Element 'tolerate'. */
	TOLERATE,

	/** Element 'deny'. */
	DENY,

	/** Element 'code-mapping'. */
	CODE_MAPPING,

	/** Element 'comment'. */
	COMMENT,

	/** Element 'dependency-policy'. */
	DEPENDENCY_POLICY,

	/** Dependency element */
	DEPENDENCY,

	/** Matched type element */
	TYPE,

	/** Orphan. */
	ORPHAN;

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return name().toLowerCase().replace('_', '-');
	}
}