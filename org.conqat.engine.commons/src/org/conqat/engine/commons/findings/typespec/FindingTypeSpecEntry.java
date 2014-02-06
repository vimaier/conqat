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
 * A single entry in a finding type spec.
 * 
 * @author hummelb
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: 81B61B86147DC097B69E3FE9C475B57B
 */
public final class FindingTypeSpecEntry {

	/** The key this refers to. */
	private final String key;

	/** Determines as which type the key should be interpreted. */
	private final EFindingTypeSpecType type;

	/** Constructor. */
	public FindingTypeSpecEntry(String key, EFindingTypeSpecType type) {
		this.key = key;
		this.type = type;
	}

	/**
	 * Constructs a new instance from the serialized string (inverse to
	 * {@link #toString()}).
	 */
	public FindingTypeSpecEntry(String serialized) {
		String[] parts = serialized.split(":", 2);
		this.key = parts[0];
		if (parts.length == 1) {
			this.type = EFindingTypeSpecType.STRING;
		} else {
			this.type = EFindingTypeSpecType.parseTypeName(parts[1]);
		}
	}

	/** Returns the key this refers to. */
	public String getKey() {
		return key;
	}

	/** Returns as which type the key should be interpreted. */
	public EFindingTypeSpecType getType() {
		return type;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof FindingTypeSpecEntry)) {
			return false;
		}

		FindingTypeSpecEntry other = (FindingTypeSpecEntry) obj;
		return key.equals(other.key) && type == other.type;
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return key.hashCode() + 13 * type.hashCode();
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return key + ":" + type.getTypeName();
	}
}