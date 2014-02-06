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
package org.conqat.engine.sourcecode.analysis;


import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.region.Region;
import org.conqat.lib.commons.region.RegionSet;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.regions.RegionSetDictionary;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.TokenTestCaseBase;
import org.conqat.lib.scanner.ELanguage;

/**
 * Test case for {@link BlockMarker}
 * 
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: 1D4726BB4022853E672D447515305E67
 */
public class BlockMarkerTest extends TokenTestCaseBase {

	/** Name of region set used in test */
	private static final String REGION_SET_NAME = "Blocks";

	/** Regular expression used in test */
	public static final String PATTERN = "void\\s+InitializeComponent\\(\\)\\s+\\{";

	/** Test functionality of processor */
	public void testMatchInitializeComponent() throws ConQATException {
		ITokenElement element = createTokenElement(
				useCanonicalTestFile("InitializeComponent.cs"), ELanguage.CS);

		executeProcessor(BlockMarker.class, "(input=(ref=", element,
				"), regions=(name='", REGION_SET_NAME,
				"'), patterns=(ref=patList('", PATTERN, "')))");

		// check regions
		RegionSet regions = RegionSetDictionary.retrieve(element,
				REGION_SET_NAME);
		assertEquals(1, regions.size());
		Region region = CollectionUtils.getAny(regions);
		assertEquals(5222, region.getStart()); // 5222 manually validated
		assertEquals(11848, region.getEnd()); // 11848 manually validated
	}
}