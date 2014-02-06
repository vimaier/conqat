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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.collections.UnmodifiableSet;
import org.conqat.lib.commons.string.StringUtils;

/**
 * A finding type spec is basically just a set of key name/type entries and
 * supports additional convenience method for (de)serialization.
 * 
 * @author hummelb
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: A8772D0E20355599D9B24C1E006D6798
 */
public class FindingTypeSpec {

	/** The entries. */
	private final Set<FindingTypeSpecEntry> entries = new HashSet<FindingTypeSpecEntry>();

	/** Constructor. */
	public FindingTypeSpec(FindingTypeSpecEntry... entries) {
		this.entries.addAll(Arrays.asList(entries));
	}

	/** Constructor for deserialization (inverse of {@link #toString()}). */
	public FindingTypeSpec(String serialized) {
		for (String format : serialized.split("[,]")) {
			entries.add(new FindingTypeSpecEntry(format));
		}
	}

	/** Returns the entries. */
	public UnmodifiableSet<FindingTypeSpecEntry> getEntries() {
		return CollectionUtils.asUnmodifiable(entries);
	}

	/** Adds an entry. */
	public void addEntry(FindingTypeSpecEntry entry) {
		entries.add(entry);
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return StringUtils.concat(entries, ",");
	}
}