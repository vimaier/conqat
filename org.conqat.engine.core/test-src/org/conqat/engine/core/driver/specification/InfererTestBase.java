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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.conqat.lib.commons.reflect.ClassType;
import org.conqat.engine.core.driver.error.DriverException;
import org.conqat.engine.core.driver.error.EDriverExceptionType;
import org.conqat.engine.core.driver.util.Multiplicity;

/**
 * Base class for type inference tests.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @levd.rating GREEN Hash: 0BE32794736D9681B2700888F2893124
 */
public abstract class InfererTestBase extends SpecificationTestBase {

	/** The block specification currently under test. */
	protected BlockSpecification spec;

	/**
	 * Ensures that the multiplicity of the named parameter of the specification
	 * under test ({@link #spec}) is as given.
	 */
	protected void assertParamMult(String paramName, Multiplicity mult) {
		BlockSpecificationParameter param = spec.getParameter(paramName);
		assertNotNull(param);
		assertEquals(mult, param.getMultiplicity());
	}

	/**
	 * Ensures that the type of the named output of the specification under test ({@link #spec})
	 * is as given.
	 */
	@SuppressWarnings("null")
	protected void assertOutputType(String outputName, ClassType type) {
		BlockSpecificationOutput output = null;
		for (BlockSpecificationOutput o : spec.getOutputs()) {
			if (o.getName().equals(outputName)) {
				output = o;
				break;
			}
		}

		assertNotNull(output);
		assertEquals(type, output.getType());
	}

	/**
	 * Ensures that the attributes of the named parameter of the specification
	 * under test ({@link #spec}) are of the given types. The types must be of
	 * the same number as the attributes and should occur in the same order as
	 * the attributes (i.e. as they are written in the XML file).
	 */
	protected void assertAttrTypes(String paramName, ClassType... types) {
		BlockSpecificationParameter param = spec.getParameter(paramName);
		assertNotNull(param);
		BlockSpecificationAttribute[] attrs = param.getAttributes();
		assertEquals(attrs.length, types.length);
		for (int i = 0; i < attrs.length; ++i) {
			assertEquals("Types should match for " + attrs[i].getName(),
					types[i], attrs[i].getType());
		}
	}

	/**
	 * Ensures that the attributes of the named parameter of the specification
	 * under test ({@link #spec}) link to the given outputs as pipeline. The
	 * outputs must be of the same number as the attributes and should occur in
	 * the same order as the attributes (i.e. as they are written in the XML
	 * file). Outputs are given as String arrays of output names.
	 */
	protected void assertAttrPipelines(String paramName,
			String[]... pipelineOutputNames) {
		BlockSpecificationParameter param = spec.getParameter(paramName);
		assertNotNull(param);
		BlockSpecificationAttribute[] attrs = param.getAttributes();
		assertEquals(attrs.length, pipelineOutputNames.length);
		for (int i = 0; i < attrs.length; ++i) {
			List<SpecificationOutput> pipelines = attrs[i].getPipelineOutputs();
			assertEquals("Pipelines should match for " + attrs[i].getName(),
					pipelineOutputNames[i].length, pipelines.size());

			Set<String> pipelineNames = new HashSet<String>();
			for (SpecificationOutput output : pipelines) {
				pipelineNames.add(output.getName());
			}

			for (String name : pipelineOutputNames[i]) {
				assertTrue("Pipelines for " + attrs[i].getName()
						+ " should contain " + name, pipelineNames
						.contains(name));
			}
		}
	}

	/**
	 * Loads the named block and checks if the expected exception is actually
	 * thrown.
	 */
	protected void expectException(String blockName,
			EDriverExceptionType expectedException) {
		try {
			spec = loadBlock(blockName);
			spec.initialize();
			fail("Expected an exception!");
		} catch (DriverException e) {
			assertEquals(expectedException, e.getType());
		}
	}

}