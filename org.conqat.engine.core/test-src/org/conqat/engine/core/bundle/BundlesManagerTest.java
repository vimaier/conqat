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
package org.conqat.engine.core.bundle;

import org.conqat.lib.commons.test.CCSMTestCaseBase;
import org.conqat.engine.core.driver.error.EDriverExceptionType;

/**
 * Test class for {@link BundlesManager}.
 * 
 * @author Florian Deissenboeck
 * @author Elmar Juergens
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: E8C37893320F8FC7670F9C2781D266E4
 */
public class BundlesManagerTest extends CCSMTestCaseBase {

	/** Tests the case, when no bundles are provided. */
	public void testNoBoundlesConfigured() {
		BundlesManager manager = new BundlesManager();
		try {
			manager.initBundles();
			fail("Expected an exception here.");
		} catch (BundleException e) {
			assertEquals(EDriverExceptionType.NO_BUNDLES_CONFIGURED, e
					.getType());
		}
	}

}