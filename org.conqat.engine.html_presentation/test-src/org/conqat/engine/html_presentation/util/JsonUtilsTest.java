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
package org.conqat.engine.html_presentation.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.commons.test.CCSMTestCaseBase;
import org.conqat.lib.scanner.ELanguage;

/**
 * Test for {@link JsonUtils}. Ensures that it correctly serializes complex data
 * structures consisting of nested maps and lists.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 45587 $
 * @ConQAT.Rating GREEN Hash: 7A65D0D28B2DD1AB4373CDA5F9B237FD
 */
public class JsonUtilsTest extends CCSMTestCaseBase {

	/** Tests serialization of primitives. */
	public void testPrimitiveSerialization() {
		assertJSON(null, "null");
		assertJSON(42, "42");
		assertJSON("test", "\"test\"");
	}

	/** Tests if list serialization actually produces a JSON array. */
	public void testListSerialization() {
		assertJSON(new int[] { 3, 4, 5 }, "[3,4,5]");
		assertJSON(new ArrayList<Integer>(Arrays.asList(3, 4, 5)), "[3,4,5]");
		assertJSON(Arrays.asList(3, 4, 5), "[3,4,5]");
		assertJSON(new Object[] { 3, "test", null }, "[3,\"test\",null]");
	}

	/** Tests if serialization of EnumSet works. */
	public void testEnumSetSerialization() {
		assertJSON(EnumSet.of(ELanguage.JAVA, ELanguage.CPP),
				"[\"JAVA\",\"CPP\"]");
	}

	/** Tests serialization of custom objects. */
	public void testObjectSerialization() {
		assertJSON(new TestClass(), "{\"foo\":42,\"bar\":\"test\"}");
	}

	/** Just test data. */
	@SuppressWarnings("unused")
	private static class TestClass {
		/** Test value. */
		private int foo = 42;
		/** Test value. */
		private String bar = "test";
	}

	/** Tests if map serialization actually produces a JSON map. */
	public void testMapSerialization() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("foo", 1);
		map.put("bar", "value");
		map.put("baz", null);
		assertJSON(map, "{\"foo\":1,\"bar\":\"value\",\"baz\":null}");
	}

	/**
	 * Tests if list serialization actually produces a JSON array, if the list
	 * is the value of a map.
	 */
	public void testListInMapSerialization() {
		List<Object> list = new ArrayList<Object>(3);
		list.add(3);
		list.add(4);
		Map<String, Object> map = new TreeMap<String, Object>();
		map.put("foo", list);

		assertJSON(map, "{\"foo\":[3,4]}");
	}

	/** Tests the behavior in case of problems. */
	public void testErrorCases() {

		try {
			JsonUtils.deserializeFromJSON("this is not json", ArrayList.class);
			fail("Expected exception");
		} catch (ConQATException e) {
			// expected
		}

		try {
			JsonUtils.deserializeFromJSON("{}", ConQATProcessorBase.class);
			fail("Expected exception");
		} catch (ConQATException e) {
			// expected
		}
	}

	/** Asserts that the given object is serialized to the given JSON string. */
	private static void assertJSON(Object value, String expectedJson) {
		String json = JsonUtils.serializeToJSON(value);
		assertEquals(expectedJson, StringUtils.removeWhitespace(json));
	}
}