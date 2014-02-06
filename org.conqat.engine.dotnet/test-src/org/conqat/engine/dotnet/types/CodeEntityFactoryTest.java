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
package org.conqat.engine.dotnet.types;

import static org.conqat.lib.scanner.ETokenType.NAMESPACE;

import java.util.List;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.testutils.ProcessorInfoMock;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.TokenTestCaseBase;
import org.conqat.lib.scanner.ELanguage;
import org.conqat.lib.scanner.ETokenType;
import org.conqat.lib.scanner.IToken;

/**
 * Test cases for {@link CodeEntityFactory}.
 * 
 * @author $Author: juergens $
 * @version $Rev: 41606 $
 * @ConQAT.Rating GREEN Hash: 6F4D6EC96A3DBF3EFC0F13A7C1781165
 */
public class CodeEntityFactoryTest extends TokenTestCaseBase {

	/**
	 * Make sure that shallow parser survives class keywords in parent
	 * declaration
	 */
	public void testClassKeywordBug() throws ConQATException {
		CodeEntityBase root = parseTestFile("classkeyword.cs");
		assertEquals(1, root.getChildren().size());

		CodeEntityBase clazz = root.getChildren().get(0).getChildren().get(0);
		assertTypeAndFqName(clazz, ETokenType.CLASS, "A.Controller<T0>");
	}

	/**
	 * Make sure that shallow parser works for interface with generic methods
	 */
	public void testClassKeywordBug2() throws ConQATException {
		CodeEntityBase root = parseTestFile("interface.cs");
		assertEquals(1, root.getChildren().size());

		CodeEntityBase clazz = root.getChildren().get(0);
		assertTypeAndFqName(clazz, ETokenType.INTERFACE, "IInterface");
	}

	/** Test parsing of hand-constructed file with simple examples */
	public void testParseSimpleCodeEntities() throws ConQATException {
		CodeEntityBase root = parseTestFile("codeEntities.cs");

		assertEquals(2, root.getChildren().size());

		CodeEntityBase namespace1 = root.getChildren().get(0);
		assertTypeAndFqName(namespace1, NAMESPACE, "NUnit.UiKit");

		assertEquals(2, namespace1.getChildren().size());
		CodeEntityBase clazz1 = namespace1.getChildren().get(0);
		assertTypeAndFqName(clazz1, ETokenType.CLASS, "NUnit.UiKit.MyClass");
		CodeEntityBase interfaz1 = namespace1.getChildren().get(1);
		assertTypeAndFqName(interfaz1, ETokenType.INTERFACE,
				"NUnit.UiKit.IInterface");

		assertTypeAndFqName(root.getChildren().get(1), NAMESPACE,
				"NUnit.UiKit2");
	}

	/** Run shallow parser on file from test-data folder */
	private CodeEntityBase parseTestFile(String filename)
			throws ConQATException {
		ITokenElement element = createTokenElement(
				useCanonicalTestFile(filename), ELanguage.CS);

		List<IToken> tokens = element.getTokens(new ProcessorInfoMock()
				.getLogger());

		return CodeEntityFactory.codeEntitiesFor(tokens);
	}

	/** Asserts name and type of a {@link NamedCodeEntity} */
	private void assertTypeAndFqName(CodeEntityBase entity,
			ETokenType expectedType, String expectedName) {
		assertEquals(expectedType, ((NamedCodeEntity) entity).getType());
		assertEquals(expectedName, entity.getFqName());
	}

	/** Asserts that generic class names are parsed correctly */
	public void testParseGenericClassNames() throws Exception {
		CodeEntityBase root = parseTestFile("generictypes.cs");

		CodeEntityBase namespace = root.getChildren().get(0);
		assertTypeAndFqName(namespace, NAMESPACE, "GenericsTestLibrary");
		assertEquals(7, namespace.getChildren().size());

		assertTypeAndFqName(namespace.getChildren().get(0), ETokenType.STRUCT,
				"GenericsTestLibrary.Test<T0>");
		assertTypeAndFqName(namespace.getChildren().get(1),
				ETokenType.DELEGATE, "GenericsTestLibrary.SampleDelegate");
		assertTypeAndFqName(namespace.getChildren().get(2), ETokenType.CLASS,
				"GenericsTestLibrary.Class1");
		assertTypeAndFqName(namespace.getChildren().get(3), ETokenType.CLASS,
				"GenericsTestLibrary.Class1<T0,T1>");
		assertTypeAndFqName(namespace.getChildren().get(4), ETokenType.CLASS,
				"GenericsTestLibrary.Class1<T0,T1>");
		assertTypeAndFqName(namespace.getChildren().get(5), ETokenType.CLASS,
				"GenericsTestLibrary.Class1<T0>");
		assertTypeAndFqName(namespace.getChildren().get(6),
				ETokenType.INTERFACE,
				"GenericsTestLibrary.IInterface<T0,T1,T2>");
	}

	/** Asserts that parsing an empty file will not rise an error. */
	public void testParsingEmptyFile() throws Exception {
		CodeEntityBase root = parseTestFile("empty.cs");

		assertSame(root.getChildren().size(), 0);
		assertSame(root.collectTypeNames().size(), 0);
		assertSame(root.getFqName(), null);
	}

}
