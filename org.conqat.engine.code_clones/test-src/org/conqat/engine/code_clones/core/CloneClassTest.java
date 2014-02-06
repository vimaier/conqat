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

import org.conqat.engine.commons.findings.location.TextRegionLocation;
import org.conqat.lib.commons.test.CCSMTestCaseBase;

/**
 * Test cases for {@link CloneClass}.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43764 $
 * @ConQAT.Rating GREEN Hash: A644A726E299D1AE46126B06C8035DE5
 */
public class CloneClassTest extends CCSMTestCaseBase {

	/** {@link IdProvider} used to create test data ids */
	private final IdProvider idProvider = new IdProvider();

	/** Tests whether clone fingerprints are independent of sequence */
	public void testGetFingerprint() {
		TextRegionLocation location1 = new TextRegionLocation("location",
				"path", 10, 20, 30, 40);
		TextRegionLocation location2 = new TextRegionLocation("location",
				"path", 110, 120, 130, 140);

		CloneClass cloneClassAB = new CloneClass(1, idProvider.provideId());
		new Clone(idProvider.provideId(), cloneClassAB, location1, 1, 5,
				"FingerPrint A");
		new Clone(idProvider.provideId(), cloneClassAB, location2, 7, 11,
				"FingerPrint B");

		CloneClass cloneClassBA = new CloneClass(1, idProvider.provideId());
		new Clone(idProvider.provideId(), cloneClassBA, location2, 7, 11,
				"FingerPrint B");
		new Clone(idProvider.provideId(), cloneClassBA, location1, 1, 5,
				"FingerPrint A");

		assertEquals(cloneClassAB.getFingerprint(),
				cloneClassBA.getFingerprint());
	}
}