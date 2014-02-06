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
package org.conqat.engine.commons.findings;

import junit.framework.TestCase;

import org.conqat.engine.commons.findings.location.ElementLocation;
import org.conqat.engine.commons.findings.location.QualifiedNameLocation;
import org.conqat.engine.commons.findings.location.TextRegionLocation;
import org.conqat.lib.commons.io.SerializationUtils;

/**
 * Test the serialization of ElementLocation and its subtypes.
 * 
 * @author $Author: lochmann$
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 6C21C1247DE25EEF6B34D977DF51E1FC
 */
public class ElementLocationSerializationTest extends TestCase {

	/** Test the serialization of {@link ElementLocation}. */
	public void testSerializationOfElementLocation() {
		cloneBySerializationAndTest(new ElementLocation("a", "b"));
	}

	/** Test the serialization of {@link TextRegionLocation}. */
	public void testSerializationOfTextRegionLocation() {
		TextRegionLocation el1 = new TextRegionLocation("a", "b", 1, 2, 3, 4);
		TextRegionLocation el2 = cloneBySerializationAndTest(el1);
		assertEquals(el1.getRawStartLine(), el2.getRawStartLine());
		assertEquals(el1.getRawEndLine(), el2.getRawEndLine());
		assertEquals(el1.getRawStartOffset(), el2.getRawStartOffset());
		assertEquals(el1.getRawEndOffset(), el2.getRawEndOffset());
	}

	/** Test the serialization of {@link QualifiedNameLocation}. */
	public void testSerializationOfQualifiedNameLocation() {
		QualifiedNameLocation el1 = new QualifiedNameLocation("a", "b", "c");
		QualifiedNameLocation el2 = cloneBySerializationAndTest(el1);
		assertEquals(el1.getQualifiedName(), el2.getQualifiedName());
	}

	/**
	 * Clone by serialization and test equality of the {@link ElementLocation}
	 * fields.
	 */
	private <T extends ElementLocation> T cloneBySerializationAndTest(T input) {
		T result = SerializationUtils.cloneBySerialization(input, null);

		assertNotSame(input, result);
		assertEquals(input.getLocation(), result.getLocation());
		assertEquals(input.getUniformPath(), result.getUniformPath());

		return result;
	}
}