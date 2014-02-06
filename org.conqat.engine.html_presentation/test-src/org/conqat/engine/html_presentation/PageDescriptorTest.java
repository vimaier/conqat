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
package org.conqat.engine.html_presentation;

import java.text.NumberFormat;
import java.util.Locale;

import org.conqat.lib.commons.clone.DeepCloneException;
import org.conqat.lib.commons.html.EHTMLElement;
import org.conqat.lib.commons.test.CCSMTestCaseBase;

/**
 * Test for {@link PageDescriptor}.
 * 
 * @author deissenb
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @levd.rating GREEN Hash: C8A4651005594D804D506E02511FA63A
 */
public class PageDescriptorTest extends CCSMTestCaseBase {

	/**
	 * Test deep cloning.
	 * 
	 * @throws DeepCloneException
	 */
	public void testDeepClone() throws DeepCloneException {
		PageDescriptor descriptor = new PageDescriptor("description", "name",
				"groupId", "iconName", "filename");

		descriptor.getWriter().openElement(EHTMLElement.BODY);
		descriptor.getWriter().addText("test");
		descriptor.getWriter().closeElement(EHTMLElement.BODY);

		PageDescriptor clone = descriptor.deepClone();

		assertEquals(descriptor.getContent(), clone.getContent());

		clone.getWriter().addText("test");
		assertFalse(descriptor.getContent().equals(clone.getContent()));

		assertEquals(descriptor.getContent() + "test", clone.getContent());
	}

	/** This test is for bug #2858 */
	public void testEncoding() {
		// file is never actually written in this test.
		PageDescriptor descriptor = new PageDescriptor("test", "test", "test",
				"test", "dummy.html");
		NumberFormat format = NumberFormat.getInstance(new Locale("pl"));
		descriptor.getWriter().addRawString(format.format(10000.55));
		descriptor.getWriter().close();
		assertEquals("10\u00a0000,55", descriptor.getContent());
	}
}