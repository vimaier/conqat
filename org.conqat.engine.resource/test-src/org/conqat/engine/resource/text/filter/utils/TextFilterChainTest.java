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
package org.conqat.engine.resource.text.filter.utils;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.filter.TextFilterTestBase;
import org.conqat.engine.resource.text.filter.util.TextFilterChain;

/**
 * Tests the {@link TextFilterChain}.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 40865 $
 * @ConQAT.Rating GREEN Hash: 44D5CE770753C54DF641EAF1A363FE36
 */
public class TextFilterChainTest extends TextFilterTestBase {

	/** Tests the case of two chained filters. */
	public void testSimple() throws ConQATException {
		TextFilterChain chain = new TextFilterChain();
		chain.add(regexFilter("abc"));
		chain.add(regexFilter("z+"));

		assertCleanDeletions(
				chain.getDeletions("barabcabctezzzst",
						TextFilterTestBase.class.getCanonicalName()), 3, 9, 11,
				14);
	}
}