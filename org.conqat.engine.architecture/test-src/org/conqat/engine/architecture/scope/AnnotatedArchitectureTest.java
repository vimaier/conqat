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
package org.conqat.engine.architecture.scope;

import org.conqat.engine.architecture.analysis.DependenciesExtractor;
import org.conqat.engine.architecture.assessment.ArchitectureAnalyzer;
import org.conqat.engine.architecture.harness.ArchitectureTestHarness;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.node.StringSetNode;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.testutils.ProcessorInfoMock;
import org.conqat.lib.commons.clone.DeepCloneException;
import org.conqat.lib.commons.collections.IIdProvider;
import org.conqat.lib.commons.test.CCSMTestCaseBase;
import org.conqat.lib.commons.test.DeepCloneTestUtils;

/**
 * This is the test class for {@link AnnotatedArchitecture}. To a certain extent
 * this also tests the {@link ArchitectureAnalyzer}.
 * 
 * @author $Author: deissenb $
 * @version $Rev: 41988 $
 * @ConQAT.Rating GREEN Hash: 3C3132F7AFEA3E74AE8232AE3D19FF36
 */
public class AnnotatedArchitectureTest extends CCSMTestCaseBase {

	/** Key where dependency lists are stored. */
	private static final String LIST_KEY = "Dependency List";

	/** The object under test. */
	private AnnotatedArchitecture annotatedArchitecture;

	/** Test node. */
	private StringSetNode nodeA;

	/** Test node. */
	private StringSetNode nodeB;

	/** Test node. */
	private StringSetNode nodeC;

	/**
	 * This method sets up a primitive scope consisting of a root and three
	 * nodes. Dependencies between the nodes are defined via dependency lists.
	 * Then an architecture definition is read from test data and the
	 * {@link ArchitectureAnalyzer} is run on the architecture together with the
	 * scope. As a result {@link #annotatedArchitecture} is initialized.
	 */
	@Override
	public void setUp() throws ConQATException {
		StringSetNode root = new StringSetNode();
		nodeA = new StringSetNode("a");
		nodeB = new StringSetNode("b");
		nodeC = new StringSetNode("c");
		root.addChild(nodeA);
		root.addChild(nodeB);
		root.addChild(nodeC);

		NodeUtils.getOrCreateStringList(nodeA, LIST_KEY).add("b");
		NodeUtils.getOrCreateStringList(nodeA, LIST_KEY).add("c");
		NodeUtils.getOrCreateStringList(nodeB, LIST_KEY).add("a");
		NodeUtils.getOrCreateStringList(nodeC, LIST_KEY).add("a");

		ArchitectureDefinition arch = ArchitectureTestHarness
				.readArchitecture(useTestFile("annoated-architecture-test.architecture"));

		DependenciesExtractor extractor = new DependenciesExtractor();
        extractor.setInput(root);
        extractor.addListKey(LIST_KEY);
        
		ArchitectureAnalyzer analyzer = new ArchitectureAnalyzer();
		analyzer.init(new ProcessorInfoMock());
		analyzer.root = root;
		analyzer.architecture = arch;
		analyzer.report = extractor.process();

		annotatedArchitecture = analyzer.process();
	}

	/**
	 * Test for {@link AnnotatedArchitecture#getMatchedTypes(ComponentNode)}.
	 */
	public void testMatchedTypes() {
		ComponentNode componentA = annotatedArchitecture.getNamedChild("A");
		assertSame(nodeA, annotatedArchitecture.getMatchedTypes(componentA)
				.iterator().next());

		ComponentNode componentB = annotatedArchitecture.getNamedChild("B");
		assertSame(nodeB, annotatedArchitecture.getMatchedTypes(componentB)
				.iterator().next());

		ComponentNode componentC = annotatedArchitecture.getNamedChild("C");
		assertSame(nodeC, annotatedArchitecture.getMatchedTypes(componentC)
				.iterator().next());
	}

	/** This tests if deep cloning is properly implemented. */
	public void testDeepCloning() throws DeepCloneException {
		DeepCloneTestUtils.testDeepCloning(annotatedArchitecture,
				annotatedArchitecture.deepClone(), new IdProvider(),
				"edu.tum.cs");
	}

	/** Id provider to be used for the deep cloning test. */
	private static class IdProvider implements IIdProvider<String, Object> {

		/** {@inheritDoc} */
		@Override
		public String obtainId(Object object) {
			if (object instanceof IConQATNode) {
				return ((IConQATNode) object).getId();
			}
			// we need this for EStereotype
			if (object.getClass().isEnum()) {
				return ((Enum<?>) object).name();
			}
			throw new AssertionError("Unknown type: "
					+ object.getClass().getName());
		}

	}
}