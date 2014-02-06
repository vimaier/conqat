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
package org.conqat.engine.core.driver.specification;

import java.util.List;

import org.conqat.engine.core.driver.declaration.DeclarationParameter;
import org.conqat.engine.core.driver.declaration.IDeclaration;
import org.conqat.engine.core.driver.error.DriverException;
import org.conqat.engine.core.driver.error.EDriverExceptionType;

/**
 * Tests for {@link StarReferenceResolver}.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @levd.rating GREEN Hash: B98F37439AA07766AEC78D83BF6410E3
 */
public class StarReferenceResolverTest extends SpecificationTestBase {

	/** Test the simplest case. */
	public void testBasicResolution() throws Exception {
		BlockSpecification bSpec = loadBlock("star-01");
		bSpec.initialize();

		assertEquals(2, bSpec.getDeclarationList().size());

		IDeclaration bDecl = bSpec.getDeclarationList().get(0);
		assertEquals("b", bDecl.getName());

		IDeclaration cDecl = bSpec.getDeclarationList().get(1);
		assertEquals("c", cDecl.getName());
		assertEquals(4, cDecl.getParameters().size());

		List<DeclarationParameter> nonSyntheticParameters = cDecl
				.getNonSyntheticParameters();
		assertEquals(3, nonSyntheticParameters.size());
		for (int i = 0; i < 3; ++i) {
			assertEquals("Param " + i, bDecl.getOutputs().get(i),
					nonSyntheticParameters.get(i).getAttributes().get(0)
							.getReference());
			assertEquals("Param " + i, bDecl.getOutputs().get(i),
					nonSyntheticParameters.get(i).getAttributes().get(1)
							.getReference());
		}
	}

	/** Test reference to undefined element. */
	public void testUndefinedReference() throws DriverException {
		BlockSpecification spec = loadBlock("star-02");
		try {
			spec.initialize();
			fail("Expected exception!");
		} catch (DriverException e) {
			assertEquals(EDriverExceptionType.UNDEFINED_REFERENCE, e.getType());
		}
	}

	/** Test multiple star references in a single parameter. */
	public void testMultipleStarsInParameter() throws DriverException {
		BlockSpecification spec = loadBlock("star-03");
		try {
			spec.initialize();
			fail("Expected exception!");
		} catch (DriverException e) {
			assertEquals(EDriverExceptionType.MULTIPLE_STARS_IN_PARAMETER, e
					.getType());
		}
	}

}