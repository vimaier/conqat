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
package org.conqat.engine.resource.text;

import java.nio.charset.Charset;

import org.conqat.engine.resource.IContentAccessor;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.build.IElementFactory;
import org.conqat.engine.resource.test.ResourceProcessorTestCaseBase;

/**
 * Tests the {@link TextElementFactory}.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: FA69D8D542D1C93EAC52A830187F37AA
 */
public class TextElementFactoryTest extends ResourceProcessorTestCaseBase {

	/** Performs some basic tests. */
	public void testBasic() throws Exception {
		IElementFactory factory = (IElementFactory) parseCQDDL("textFactory()");
		IContentAccessor[] accessors = (IContentAccessor[]) parseCQDDL("memScope(foo=bar)");
		IElement element = factory.create(accessors[0]);

		assertTrue(element instanceof ITextElement);
		ITextElement textElement = (ITextElement) element;
		assertEquals("foo", textElement.getUniformPath());
		assertEquals("bar", textElement.getUnfilteredTextContent());
	}

	/** Tests with explicit encoding. */
	public void testEncoding() throws Exception {
		Charset encoding = Charset.forName("UTF-16");
		IElementFactory factory = (IElementFactory) executeProcessor(
				TextElementFactory.class, "(encoding=(name='", encoding.name(),
				"'))");
		byte[] content = "bar".getBytes(encoding);
		IContentAccessor[] accessors = (IContentAccessor[]) parseCQDDL(
				"memScope(foo=", content, ")");
		IElement element = factory.create(accessors[0]);

		assertTrue(element instanceof ITextElement);
		ITextElement textElement = (ITextElement) element;

		// should decode correctly
		assertEquals("bar", textElement.getUnfilteredTextContent());

		// expecting 8 bytes for UTF-16 encoding (2 bytes for each char + BOM)
		assertEquals(8, textElement.getContent().length);
	}

	/** Performs some basic tests. */
	public void testFilters() throws Exception {
		IElementFactory factory = (IElementFactory) executeProcessor(
				TextElementFactory.class, "('text-filter'=(ref=",
				regexFilter("abc"), "), 'text-filter'=(ref=",
				regexFilter("z+"), "))");
		IContentAccessor[] accessors = (IContentAccessor[]) parseCQDDL("memScope(foo=barabcabctezzzst)");
		IElement element = factory.create(accessors[0]);

		assertTrue(element instanceof ITextElement);
		ITextElement textElement = (ITextElement) element;
		assertEquals("foo", textElement.getUniformPath());
		assertEquals("bartest", textElement.getTextContent());
	}
}