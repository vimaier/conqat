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
package org.conqat.engine.dotnet.ila.scenario;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.conqat.engine.commons.keys.IDependencyListKey;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.core.bundle.BundleException;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.testutils.ProcessorInfoMock;
import org.conqat.engine.dotnet.ila.ILAnalyzerRunnerProcessor;
import org.conqat.engine.dotnet.ila.ILAnalyzerRunnerProcessorTest;
import org.conqat.engine.dotnet.ila.ILDependenciesImporterProcessor;
import org.conqat.engine.graph.builder.ScopeGraphCreator;
import org.conqat.engine.graph.nodes.ConQATGraph;
import org.conqat.engine.graph.nodes.ConQATVertex;
import org.conqat.engine.resource.scope.filesystem.FileContentAccessor;
import org.conqat.engine.resource.text.TextElement;
import org.conqat.lib.commons.filesystem.CanonicalFile;
import org.conqat.lib.commons.test.TestletBase;
import org.junit.Ignore;

import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;

/**
 * Test for the Intermediate Language Analyzer (ILA) that extracts dependency
 * information from .NET assemblies.
 * <p>
 * This integration test performs the following steps:
 * <ol>
 * <li>Run ILA on test file (.dll) to extract dependencies into XML file</li>
 * <li>Create ConQATGraph from XML dependency file</li>
 * <li>Assert that dependency from SOURCE to TARGET node is present. In order to
 * provide for different target node types (such as outer and inner classes,
 * TARGET check tests for a prefix and a tail, instead of a fixed target node
 * string). For .dlls whose names start with
 * <code>{@value #NO_DEPENDENCY_PREFIX}</code>, it is asserted that no
 * dependency between SOURCE and TARGET is found.</li>
 * </ol>
 * This way, this testlet tests the functionality of ILA and the integration of
 * ILA into ConQAT.
 * 
 * @author $Author: juergens $
 * @version $Rev: 36632 $
 * @ConQAT.Rating GREEN Hash: 2B75DE743B3E59B8EEBF5DD4A7D2B383
 */
// Ignore tells JUnit runner not to execute testlet
@Ignore
public class IlaTestlet extends TestletBase {

	/** Id of dependency source vertex */
	private static final String SOURCE_VERTEX_PREFIX = "edu.tum.cs.conqat.dotnet.Source";

	/** Tag of target vertex is */
	private static final String TARGET_VERTEX_TAG = "Target";

	/** Prefix of target vertex id */
	private static final String TARGET_VERTEX_ID_PREFIX = "edu.tum.cs.conqat.dotnet";

	/** Tail of target vertex is */
	private static final String CECIL_PREFIX = "Mono.Cecil";

	/**
	 * Test file name prefix that indicates that no dependency between SOURCE
	 * and TARGET is expected
	 */
	private static final String NO_DEPENDENCY_PREFIX = "nodependency_";

	/** Name of the test file */
	private final String testFileName;

	/** Name of the folder that contains the test data */
	private final String testFolderName;

	/** Fully qualified name of the assembly that gets tested */
	private final String assemblyPath;

	/**
	 * Creates an IlaTestlet for a test file
	 * 
	 * @param testFile
	 *            IL file that gets analysed
	 */
	public IlaTestlet(File testFile) {
		testFolderName = testFile.getParent() + File.separator;
		testFileName = testFile.getName();

		assemblyPath = testFolderName
				+ testFileName.substring(0, testFileName.lastIndexOf('.'))
				+ ".dll";
	}

	/** Return name of test input file as name of JUnit test */
	@Override
	public String getName() {
		return testFileName;
	}

	/** Main test name that performs the entire test case. */
	@Override
	public void test() throws Exception {
		deleteExistingXmlFile();

		runIlaToGenerateXml();

		ConQATGraph dependencyGraph = loadXmlFile();

		performDependencySanityCheck(dependencyGraph);
		assertDependency(dependencyGraph);
	}

	/**
	 * Tries to delete an XML file that is left over from a previous test run.
	 * Else, the test could accidentally succeed, although the ILA did not even
	 * run.
	 */
	private void deleteExistingXmlFile() {
		File xmlFile = new File(assemblyPath + ".xml");
		if (xmlFile.exists()) {
			xmlFile.delete();
		}
	}

	/** Performs sanity checks on the dependencies. */
	private void performDependencySanityCheck(ConQATGraph dependencyGraph) {
		for (DirectedSparseEdge edge : dependencyGraph.getEdges()) {
			ConQATVertex target = (ConQATVertex) edge.getDest();

			assertNoDependencyToCecil(target.getId());
			assertNoIntegerLiteral(target.getId());
			assertNoIllegalCharacter(target.getId());
		}
	}

	/** Asserts that dependency does not point to Cecil */
	private void assertNoDependencyToCecil(String target) {
		if (target.contains(CECIL_PREFIX)) {
			fail("Dependency to Cecil detected: " + target);
		}
	}

	/** Asserts that dependency target is no integer literal */
	private void assertNoIntegerLiteral(String target) {
		try {
			Integer.parseInt(target);
			fail("Dependency target is an integer literal: " + target);
		} catch (NumberFormatException e) {
			// OK
		}
	}

	/**
	 * Asserts that the dependency target contains no character that must not
	 * appear in fully qualified element names.
	 */
	private void assertNoIllegalCharacter(String id) {
		String[] illegalCharacters = new String[] { "!", ":", "+", "=", "-",
				"*", "#", "'", "<", ">" };
		for (String illegalCharacter : illegalCharacters) {
			assertFalse("Dependency target id must not contain '"
					+ illegalCharacter + "'",
					normalize(id).contains(illegalCharacter));
		}
	}

	/**
	 * Asserts that an edge from the source vertex that starts with the
	 * {@value #SOURCE_VERTEX_PREFIX} to target vertex that contains the
	 * {@value #TARGET_VERTEX_TAG} is present in the dependency graph.
	 */
	private void assertDependency(ConQATGraph dependencyGraph) {
		boolean sourceTargetEdgeFound = false;
		String targetEdgeFullName = "";

		for (DirectedSparseEdge edge : dependencyGraph.getEdges()) {
			ConQATVertex source = (ConQATVertex) edge.getSource();
			ConQATVertex target = (ConQATVertex) edge.getDest();

			String sourceId = normalize(source.getId());
			String targetId = normalize(target.getId());

			// Note: the sourceId could also contain generics, e.g. Source<T>.
			// We check only if the target contains the tag because in case of
			// noDependency-tests this is already a fault (e.g. an identified
			// dependency to an identifier). The correctness of the namespace is
			// checked later.
			if (sourceId.startsWith(SOURCE_VERTEX_PREFIX)
					&& targetId.contains(TARGET_VERTEX_TAG)) {
				sourceTargetEdgeFound = true;

				// store the full name of the target to check the namespace
				// later
				targetEdgeFullName = targetId;
				break;
			}
		}

		if (testFileName.toLowerCase().startsWith(NO_DEPENDENCY_PREFIX)) {
			assertFalse(
					"Dependency to Target found, although no dependency was expected",
					sourceTargetEdgeFound);
		} else {
			assertTrue("Dependency to target not found", sourceTargetEdgeFound);

			// check the correctness of the target's namespace
			assertTrue(
					"The target type is not located in the expected namespace",
					targetEdgeFullName.startsWith(TARGET_VERTEX_ID_PREFIX));
		}
	}

	/**
	 * Remove generic parameters from class names, in order to simplify
	 * comparison with {@value #SOURCE_VERTEX_PREFIX} and
	 * {@value #TARGET_VERTEX_TAG}
	 */
	private String normalize(String id) {
		if (!id.contains("<") && !id.contains(">")) {
			return id;
		}
		return id.substring(0, id.indexOf("<"));
	}

	/** Executes ILA to generate XML containing the dependencies from IL files */
	private void runIlaToGenerateXml() throws BundleException, ConQATException,
			IOException {
		new ILAnalyzerRunnerProcessorTest().initBundleContext();

		ILAnalyzerRunnerProcessor ilaRunner = new ILAnalyzerRunnerProcessor();
		ilaRunner.setXmlTargetFolder(testFolderName);
		ilaRunner.init(new ProcessorInfoMock());
		ilaRunner.addAssembly(assemblyPath);
		ilaRunner.process();
	}

	/** Load dependency graph from XML file */
	private ConQATGraph loadXmlFile() throws ConQATException, IOException {
		// read XML file into dependency list
		ILDependenciesImporterProcessor importer = new ILDependenciesImporterProcessor();
		CanonicalFile testFile = new CanonicalFile(assemblyPath + ".xml");
		TextElement testElement = new TextElement(new FileContentAccessor(
				testFile, testFile.getParentFile(), "TEST"),
				Charset.defaultCharset());

		importer.setRoot(testElement);
		importer.init(new ProcessorInfoMock());
		IConQATNode dependencyList = importer.process();

		// create ConQATGraph from dependency list
		ScopeGraphCreator graphCreator = new ScopeGraphCreator();
		graphCreator.setRoot(dependencyList);
		graphCreator.setCreateMissingNodes(true);
		graphCreator.addListKey(IDependencyListKey.DEPENDENCY_LIST_KEY);
		graphCreator.init(new ProcessorInfoMock());
		return graphCreator.process();
	}
}