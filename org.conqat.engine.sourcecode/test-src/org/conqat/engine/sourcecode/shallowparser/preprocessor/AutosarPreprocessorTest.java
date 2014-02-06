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
package org.conqat.engine.sourcecode.shallowparser.preprocessor;

import java.util.List;

import org.conqat.engine.core.logging.testutils.LoggerMock;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.TokenTestCaseBase;
import org.conqat.engine.sourcecode.shallowparser.ShallowParserFactory;
import org.conqat.engine.sourcecode.shallowparser.framework.EShallowEntityType;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;
import org.conqat.lib.scanner.ELanguage;

/**
 * Tests the {@link AutosarCPreprocessor}.
 * 
 * @author $Author: goede $
 * @version $Rev: 43365 $
 * @ConQAT.Rating GREEN Hash: 58A905E515472B7D0FC0A5BC06D62FBA
 */
public class AutosarPreprocessorTest extends TokenTestCaseBase {

	/** Code example. */
	private static final String code = "extern FUNC(void, RTE_APPL_CODE) RTERunnable_foo("
			+ "IN P2CONST(UInt8,AUTOMATIC,RTE_APPL_DATA) data1, "
			+ "IN VAR(UInt16,AUTOMATIC) data2, "
			+ "OUT P2VAR(UInt8,AUTOMATIC,RTE_APPL_DATA) data3, "
			+ "OUT P2VAR(UInt16,AUTOMATIC,RTE_APPL_DATA) data4, "
			+ "OUT P2VAR(DiagRetCode,AUTOMATIC,RTE_APPL_DATA) result);";

	/** Code example. */
	private static final String code2 = "STATIC FUNC(Bar_ChannelType, Bar_CODE) "
			+ "Bar_foo(CONST(Bar_GroupType, Bar_CONST) Group) { "
			+ "VAR(Bar_ChannelType, Bar_VAR) ch=(Bar_ChannelType)0U; "
			+ "VAR(Bar_ChannelType, Bar_VAR) ChIndex; }";

	/** Tests whether preprocessing of {@link #code} yield a parsable result. */
	public void testPreprocessing() throws Exception {
		List<ShallowEntity> entities = parseContent(code);

		assertEquals(1, entities.size());
		ShallowEntity entity = entities.get(0);
		assertTrue(entity.isCompleted());
		assertEquals(EShallowEntityType.METHOD, entity.getType());
		assertEquals("function declaration", entity.getSubtype());
		assertEquals("RTERunnable_foo", entity.getName());
	}

	/** Parses the given code snippet with the {@link AutosarCPreprocessor} */
	private List<ShallowEntity> parseContent(String content) throws Exception {
		ITokenElement element = createTokenElement(content, ELanguage.CPP);
		executeProcessor(AutosarCPreprocessor.class, "(input=(ref=", element,
				"))");
		return ShallowParserFactory.parse(element, new LoggerMock());
	}

	/** Tests whether preprocessing of {@link #code2} yield a parsable result. */
	public void testPreprocessing2() throws Exception {
		List<ShallowEntity> entities = parseContent(code2);

		assertEquals(1, entities.size());
		ShallowEntity entity = entities.get(0);
		assertTrue(entity.isCompleted());
		assertEquals(EShallowEntityType.METHOD, entity.getType());
		assertEquals("function", entity.getSubtype());
		assertEquals("Bar_foo", entity.getName());
	}
}
