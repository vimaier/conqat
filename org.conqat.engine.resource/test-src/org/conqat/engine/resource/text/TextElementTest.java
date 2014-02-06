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
import java.util.List;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.scope.memory.InMemoryContentAccessor;
import org.conqat.engine.resource.test.ResourceProcessorTestCaseBase;
import org.conqat.engine.resource.text.filter.util.TextFilterChain;
import org.conqat.lib.commons.region.Region;

/**
 * Tests the {@link TextElement} and especially the filtering subsystem.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46849 $
 * @ConQAT.Rating GREEN Hash: 663E90354C93F7D0016DD4690740437F
 */
public class TextElementTest extends ResourceProcessorTestCaseBase {

	/** Tests basic filtering. */
	public void testFiltering() throws ConQATException {
		TextElement element = new TextElement(new InMemoryContentAccessor(
				"TEST", "a1b2c34567d89e0".getBytes()),
				Charset.defaultCharset(), regexFilter("[0-9]+"));

		assertEquals("abcde", element.getTextContent());
		assertEquals(10, element.getUnfilteredOffset(3));
		assertTrue(element.isFilteredOffset(1));
		assertTrue(element.isFilteredOffset(5));
		assertTrue(element.isFilteredOffset(9));
		assertFalse(element.isFilteredOffset(4));
		assertFalse(element.isFilteredOffset(10));
		assertTrue(element.isFilteredOffset(14));
	}

	/** Tests filtering when the filter matches at the beginning. */
	public void testFilteringFromMatch() throws ConQATException {
		TextElement element = new TextElement(new InMemoryContentAccessor(
				"TEST", "000a1b2c34567d89e".getBytes()),
				Charset.defaultCharset(), regexFilter("[0-9]+"));

		assertEquals("abcde", element.getTextContent());
		assertEquals(13, element.getUnfilteredOffset(3));

		assertEquals(0, element.getFilteredOffset(0));
		assertEquals(0, element.getFilteredOffset(1));
		assertEquals(0, element.getFilteredOffset(1));
		assertEquals(3, element.getFilteredOffset(11));
		assertEquals(3, element.getFilteredOffset(12));
		assertEquals(3, element.getFilteredOffset(13));
		assertEquals(4, element.getFilteredOffset(14));
		assertEquals(4, element.getFilteredOffset(15));

		assertTrue(element.isFilteredOffset(0));
		assertTrue(element.isFilteredOffset(2));
		assertFalse(element.isFilteredOffset(3));
		assertTrue(element.isFilteredOffset(4));
		assertTrue(element.isFilteredOffset(8));
		assertTrue(element.isFilteredOffset(12));
		assertFalse(element.isFilteredOffset(7));
		assertFalse(element.isFilteredOffset(13));
		assertFalse(element.isFilteredOffset(16));
	}

	/** Tests filtering when the filter matches at the end. */
	public void testFilteringAtEnd() throws ConQATException {
		TextElement element = new TextElement(new InMemoryContentAccessor(
				"TEST", "abc123".getBytes()), Charset.defaultCharset(),
				regexFilter("[0-9]+"));

		assertEquals("abc", element.getTextContent());
		assertEquals(2, element.getUnfilteredOffset(2));

		assertEquals(0, element.getFilteredOffset(0));
		assertEquals(1, element.getFilteredOffset(1));
		assertEquals(2, element.getFilteredOffset(2));
		assertEquals(3, element.getFilteredOffset(3));
		assertEquals(3, element.getFilteredOffset(4));
		assertEquals(3, element.getFilteredOffset(5));

		assertFalse(element.isFilteredOffset(2));
		assertTrue(element.isFilteredOffset(3));
		assertTrue(element.isFilteredOffset(5));
	}

	/** Test extacting filtered regions from the element. */
	public void testRegionExtraction() throws ConQATException {
		TextElement element = new TextElement(new InMemoryContentAccessor(
				"TEST", "000abc34567de89".getBytes()),
				Charset.defaultCharset(), regexFilter("[0-9]+"));

		List<Region> filtered = element.getFilteredRegions();
		assertNotNull(filtered);
		assertEquals(3, filtered.size());
		assertEquals(new Region(0, 2).toString(), filtered.get(0).toString());
		assertEquals(new Region(6, 10).toString(), filtered.get(1).toString());
		assertEquals(new Region(13, 14).toString(), filtered.get(2).toString());
	}

	/** Tests whether filter gaps are detected correctly. */
	public void testFilteringGaps() throws ConQATException {
		TextFilterChain filterChain = new TextFilterChain();
		filterChain.add(regexFilter("[0-4]+", false));
		filterChain.add(regexFilter("[5-9]+", true));
		TextElement element = new TextElement(new InMemoryContentAccessor(
				"TEST", "000a1b2c34567d89e".getBytes()),
				Charset.defaultCharset(), filterChain);

		assertEquals("abcde", element.getTextContent());

		// no gap between 'a' and 'c'
		assertFalse(element.isFilterGapBetween(0, 2));

		// gap between 'c' and 'd'
		assertTrue(element.isFilterGapBetween(2, 3));

		// thus also gap for nearly entire string
		assertTrue(element.isFilterGapBetween(0, 4));

		// no gap at the end
		assertFalse(element.isFilterGapBetween(4, 5));
	}
}