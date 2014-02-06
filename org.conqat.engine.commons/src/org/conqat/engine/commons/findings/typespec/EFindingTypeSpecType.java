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
package org.conqat.engine.commons.findings.typespec;

/**
 * Enumeration of types for finding type spec keys.
 * 
 * @author hummelb
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: BE5B3318BA8EADD307755BDA2444F1BA
 */
public enum EFindingTypeSpecType {

	/** Textual type. */
	STRING("s"),

	/** Integral number. */
	INT("i"),

	/** Real number. */
	DOUBLE("d");

	/** The type name, which is used for serialization. */
	private final String typeName;

	/** Constructor. */
	private EFindingTypeSpecType(String typeName) {
		this.typeName = typeName;
	}

	/** Returns the type name for this type. */
	public String getTypeName() {
		return typeName;
	}

	/**
	 * Returns the format type for a given type name. If nothing matches,
	 * {@link #STRING} is returned.
	 */
	public static EFindingTypeSpecType parseTypeName(String typeName) {
		for (EFindingTypeSpecType type : values()) {
			if (type.getTypeName().equals(typeName)) {
				return type;
			}
		}
		return STRING;
	}
}