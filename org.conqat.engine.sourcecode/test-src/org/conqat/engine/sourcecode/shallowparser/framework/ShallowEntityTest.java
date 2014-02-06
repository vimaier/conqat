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
package org.conqat.engine.sourcecode.shallowparser.framework;

import java.util.List;

import org.conqat.engine.sourcecode.shallowparser.ShallowParserTestBase;
import org.conqat.lib.scanner.ELanguage;
import org.conqat.lib.scanner.IToken;

/**
 * Tests the {@link ShallowEntity}.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46253 $
 * @ConQAT.Rating GREEN Hash: 71906EE362CEBA6B8F839CDA05BAEC09
 */
public class ShallowEntityTest extends ShallowParserTestBase {

	/** The entities under test. */
	private List<ShallowEntity> entities;

	/** {@inheritDoc} */
	@Override
	protected void setUp() throws Exception {
		entities = parse("if (x) /* comment */ { int a = 15; } while (true)",
				getLanguage(), true);
		assertEquals(2, entities.size());
	}

	/** Tests token retrieval. */
	public void testTokens() {
		assertEquals(11, entities.get(0).includedTokens().size());
		assertEquals(4, entities.get(1).includedTokens().size());

		StringBuilder builder = new StringBuilder();
		for (IToken token : entities.get(0).ownStartTokens()) {
			builder.append(token.getText());
		}
		assertEquals("if(x){", builder.toString());
	}

	/** Tests the counting methods. */
	public void testCount() {
		assertEquals(2, entities.get(0).getEntityCount());
		assertEquals(2, entities.get(0).getCompleteEntityCount());

		assertEquals(1, entities.get(1).getEntityCount());
		assertEquals(0, entities.get(1).getCompleteEntityCount());
	}

	/** {@inheritDoc} */
	@Override
	protected ELanguage getLanguage() {
		return ELanguage.JAVA;
	}
}
