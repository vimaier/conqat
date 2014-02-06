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
 * Enum of attributes used in the architecture definition file and architecture
 * assessment file.
 * 
 * @author hummelb
 * @author $Author: juergens $
 * @version $Rev: 35037 $
 * @ConQAT.Rating GREEN Hash: A0B44CAB13344E0CF71EEAB15A72761D
 */
public enum EArchitectureIOAttribute {

	/** Attribute 'policy'. */
	POLICY,

	/** Attribute 'name'. */
	NAME,

	/** Attribute 'regex'. */
	REGEX,

	/** Attribute 'pos'. Used for x/y Position of Components */
	POS,

	/** Attribute 'dim'. Used for h/w Size of Components */
	DIM,

	/** Attribute 'type'. */
	TYPE,

	/** Source attribute */
	SOURCE,

	/** Stereotype attribute */
	STEREOTYPE,

	/** Target attribute */
	TARGET,

	/** Type attribute */
	POLICY_TYPE,

	/** AssessmentType attribute */
	ASSESSMENT_TYPE,

	/** Element used for adding XML namespace. */
	XMLNS;

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return name().toLowerCase().replace('_', '-');
	}
}