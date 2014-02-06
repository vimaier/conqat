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
package org.conqat.engine.resource.binary;

import org.conqat.engine.resource.IContentAccessor;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.build.IElementFactory;
import org.conqat.engine.resource.test.ResourceProcessorTestCaseBase;

/**
 * Tests the {@link BinaryElementFactory}.
 * 
 * @author hummelb
 * @author $Author: deissenb $
 * @version $Rev: 34252 $
 * @levd.rating GREEN Hash: F0552B03DB3030210436E98151E1E429
 */
public class BinaryElementFactoryTest extends ResourceProcessorTestCaseBase {

	/** Performs some basic tests. */
	public void testBasic() throws Exception {
		IElementFactory factory = (IElementFactory) executeProcessor(
				BinaryElementFactory.class, "()");
		IContentAccessor[] accessors = (IContentAccessor[]) parseCQDDL("memScope(foo=bar)");
		IElement binaryElement = factory.create(accessors[0]);

		assertEquals("foo", binaryElement.getUniformPath());
		assertEquals("bar", new String(binaryElement.getContent()));
	}
}