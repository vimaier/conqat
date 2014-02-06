/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
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
package org.conqat.engine.commons.format;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.test.CCSMTestCaseBase;

/**
 * Tests the {@link EValueFormatter}.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 37456 $
 * @ConQAT.Rating YELLOW Hash: 5A0AA6D9081E637F5B925E678CAB3979
 */
public class EValueFormatterTest extends CCSMTestCaseBase {

	/** Tests result of basic formatting. */
	public void testBasicFormatting() throws ConQATException {

		assertEquals("17", EValueFormatter.INTEGER.format(17.235));
		assertEquals("17.235", EValueFormatter.DOUBLE.format(17.235));

		assertEquals("17.2", EValueFormatter.FIXED_1.format(17.235));
		assertEquals("17.24", EValueFormatter.FIXED_2.format(17.235));
		assertEquals("17.235", EValueFormatter.FIXED_3.format(17.235));

		assertEquals("17%", EValueFormatter.PERCENT.format(.17));
		assertEquals("17.2%", EValueFormatter.PERCENT.format(.17235));

		assertEquals("17%", EValueFormatter.PERCENT_0.format(.17235));
		assertEquals("17.2%", EValueFormatter.PERCENT_1.format(.17235));
		assertEquals("17.24%", EValueFormatter.PERCENT_2.format(.17235));
	}
}
