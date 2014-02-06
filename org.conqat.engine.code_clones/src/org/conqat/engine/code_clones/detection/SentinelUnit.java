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
package org.conqat.engine.code_clones.detection;

import org.conqat.engine.code_clones.core.TokenUnit;
import org.conqat.lib.scanner.ETokenType;

/**
 * Sentinel units mark boundaries between files. In a unit stream that contains
 * units from many different files, they are inserted between units from
 * different files.
 * <p>
 * The equals method may never return true on two different instances of
 * {@link SentinelUnit} in order to avoid clones that cross file boundaries.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43764 $
 * @ConQAT.Rating GREEN Hash: 5D09396B5A5323C1C165A1E21620D882
 */
public class SentinelUnit extends TokenUnit {

	/** String used to depict sentinels */
	private static final String SENTINEL_STRING = "$s";

	/** Used to create unique ids for sentinels */
	private static int idcounter = 0;

	/** Constructor */
	public SentinelUnit(String elementUniformPath) {
		super(SENTINEL_STRING + idcounter++, 0, 0, elementUniformPath,
				ETokenType.SENTINEL, 0);
	}

	/** Sentinel units are not equal to any other unit except themselves */
	@Override
	public boolean equals(Object other) {
		return this == other;
	}

	/** Sentinel units are synthetic */
	@Override
	public boolean isSynthetic() {
		return true;
	}

}