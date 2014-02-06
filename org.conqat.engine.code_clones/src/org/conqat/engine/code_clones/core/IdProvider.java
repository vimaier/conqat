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
package org.conqat.engine.code_clones.core;

/**
 * Creates fresh Ids.
 * 
 * @author juergens
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: D61C9F5994D236E069C93A1FE0CC8DCB
 */
public class IdProvider {

	/** Counter used to keep track of used ids */
	private long idCounter;

	/** Constructor */
	protected IdProvider(long lowestFreeId) {
		idCounter = lowestFreeId;
	}

	/** Create an IdProvider that returns 0 as its first id. */
	public IdProvider() {
		this(0);
	}

	/** Returns next fresh id. */
	public long provideId() {
		return idCounter++;
	}

}