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
 * An enumeration for code mapping types.
 * 
 * @author poehlmann
 * @author $Author: juergens $
 * @version $Rev: 35037 $
 * @ConQAT.Rating GREEN Hash: 63A272CEAF304400F3662EB9826E1F3D
 */
public enum ECodeMappingType {

	/** Value 'include'. */
	INCLUDE,

	/** Value 'exclude'. */
	EXCLUDE;

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return name().toLowerCase();
	}
}