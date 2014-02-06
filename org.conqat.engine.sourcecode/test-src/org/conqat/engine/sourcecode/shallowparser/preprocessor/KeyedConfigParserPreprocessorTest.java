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

import org.conqat.engine.commons.config.KeyedConfig;
import org.conqat.engine.core.logging.testutils.LoggerMock;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.TokenTestCaseBase;
import org.conqat.engine.sourcecode.shallowparser.ShallowParserFactory;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntity;
import org.conqat.engine.sourcecode.shallowparser.framework.ShallowEntityTraversalUtils;
import org.conqat.lib.scanner.ELanguage;

/**
 * Tests the {@link KeyedConfigParserPreprocessor}.
 * 
 * @author $Author: goede $
 * @version $Rev: 43365 $
 * @ConQAT.Rating GREEN Hash: 37C5598F1724DFCBB0DAA7EE9E9B1445
 */
public class KeyedConfigParserPreprocessorTest extends TokenTestCaseBase {

	/** Code example. */
	private static final String code = "void STUPID_MACRO test() {\n"
			+ "#if THIS_IS_TRUE\n" + "keep_this_code ();\n" + "#else\n"
			+ "discard this as it is incomplete\n" + "#endif\n" + "}";

	/** Tests whether preprocessing the {@link #code} yields a parsable result. */
	public void testPreprocessing() throws Exception {
		List<ShallowEntity> entities = parseContent(code, true);
		assertEquals(1, entities.size());
		assertNull(ShallowEntityTraversalUtils.findIncompleteEntity(entities));
	}

	/** Tests whether preprocessing works with ifndef as well. */
	public void testPreprocessingIfndef() throws Exception {
		List<ShallowEntity> entities = parseContent(
				code.replaceAll("#if", "#ifndef"), false);
		assertEquals(1, entities.size());
		assertNull(ShallowEntityTraversalUtils.findIncompleteEntity(entities));
	}

	/**
	 * Parses the given code snippet with the
	 * {@link KeyedConfigParserPreprocessor}.
	 */
	private List<ShallowEntity> parseContent(String content,
			boolean conditionIsTrue) throws Exception {
		ITokenElement element = createTokenElement(content, ELanguage.CPP);

		KeyedConfig config = new KeyedConfig();
		config.set("prefix.discard-identifier.1", "STUPID_MACRO");
		config.set("prefix." + conditionIsTrue + "-condition.2", "THIS_IS_TRUE");

		executeProcessor(KeyedConfigParserPreprocessor.class, "(input=(ref=",
				element, "), key=(prefix='prefix.'), config=(ref=", config,
				"))");
		return ShallowParserFactory.parse(element, new LoggerMock());
	}
}
