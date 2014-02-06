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
package org.conqat.engine.core.driver.declaration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.core.bundle.BundleInfo;
import org.conqat.engine.core.core.IConQATProcessor;
import org.conqat.engine.core.driver.error.BlockFileException;
import org.conqat.engine.core.driver.error.DriverException;
import org.conqat.engine.core.driver.error.EDriverExceptionType;
import org.conqat.engine.core.driver.specification.BlockSpecification;
import org.conqat.engine.core.driver.specification.SpecificationLoader;
import org.conqat.engine.core.logging.testutils.DriverTestBase;

/**
 * Tests for {@link DeclarationBase} and its support classes (
 * {@link DeclarationParameter}, {@link DeclarationAttribute},
 * {@link DeclarationOutput}). For simplicity reasons we are using the
 * {@link ProcessorDeclaration} for this purpose.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @levd.rating GREEN Hash: A2FE3D0B5413C9F69555BF17CFF06C7B
 */
public class DeclarationTest extends DriverTestBase {

	/** Test whether the declaration we build can reference its type info. */
	public void testDeclarationCanReference() throws Exception {
		buildDeclaration(ProcessorToTestDecl.class, "b", "b")
				.referenceSpecification();
	}

	/** Test whether the declaration can cope with optional attributes. */
	public void testDeclarationCompleteness() throws Exception {
		IDeclaration decl = buildDeclaration(ProcessorToTestDecl.class, "b",
				"b", "a:a:4", "a:b:5:a:7");
		decl.referenceSpecification();

		// the default attribute should have been created
		assertEquals(2, decl.getParameters().get(2).getAttributes().size());
		assertEquals("a", decl.getParameters().get(2).getAttributes().get(0)
				.getName());
		assertEquals("b", decl.getParameters().get(2).getAttributes().get(1)
				.getName());

		// this should have been reordered
		assertEquals(2, decl.getParameters().get(3).getAttributes().size());
		assertEquals("a", decl.getParameters().get(3).getAttributes().get(0)
				.getName());
		assertEquals("b", decl.getParameters().get(3).getAttributes().get(1)
				.getName());
	}

	/** Test providing unsupported parameters. */
	public void testUnsupportedParameter() throws BlockFileException {
		checkException(buildDeclaration(ProcessorToTestDecl.class, "b", "b",
				"x"), EDriverExceptionType.UNSUPPORTED_PARAMETER);
	}

	/** Test providing an unsupported attribute. */
	public void testUnsupportedAttribute() throws BlockFileException {
		checkException(
				buildDeclaration(ProcessorToTestDecl.class, "b", "b:a:4"),
				EDriverExceptionType.UNSUPPORTED_ATTRIBUTE);
	}

	/** Test with missing attribute. */
	public void testMissingAttribute() throws BlockFileException {
		checkException(buildDeclaration(ProcessorToTestDecl.class, "b", "b",
				"a:b:5"), EDriverExceptionType.MISSING_ATTRIBUTE);
	}

	/**
	 * Creates a declaration.
	 * 
	 * @param processorClass
	 *            the processor class
	 * @param parameters
	 *            the parameters to add to the declaration. Each string
	 *            corresponds to one parameter. The individual parts of the
	 *            string are separated by colons, where the first part is the
	 *            name of the parameter, and the remaining pairs are names and
	 *            values for attributes.
	 */
	private ProcessorDeclaration buildDeclaration(
			Class<? extends IConQATProcessor> processorClass,
			String... parameters) throws BlockFileException {
		BlockSpecification spec = new BlockSpecification("test", new File(
				"/DUMMY-FILE"));
		ProcessorDeclaration decl = new ProcessorDeclaration("decl",
				processorClass.getName(), spec, new SpecificationLoader(null,
						new ArrayList<BundleInfo>()));
		List<DeclarationParameter> params = new ArrayList<DeclarationParameter>();
		for (String s : parameters) {
			String[] parts = s.split(":");
			DeclarationParameter param = new DeclarationParameter(parts[0],
					decl);
			for (int i = 1; i < parts.length; i += 2) {
				param.addAttribute(new DeclarationAttribute(parts[i],
						parts[i + 1], param));
			}
			params.add(param);
		}
		decl.setParameters(params);
		return decl;
	}

	/**
	 * Performs the referenceTypeInfo operation on the given declaration and
	 * checks that an exception of the given type is thrown.
	 * 
	 * @param decl
	 *            the declaration to work on.
	 * @param exceptionType
	 *            the type of exception expected.
	 */
	private void checkException(IDeclaration decl,
			EDriverExceptionType exceptionType) {
		try {
			decl.referenceSpecification();
			fail("Exception should have been thrown");
		} catch (DriverException e) {
			assertEquals(exceptionType, e.getType());
		}
	}
}