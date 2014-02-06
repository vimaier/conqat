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
package org.conqat.engine.dotnet.ila;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.conqat.engine.core.bundle.BundleException;
import org.conqat.engine.core.bundle.BundleInfo;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.dotnet.BundleContext;
import org.conqat.engine.resource.IResource;
import org.conqat.lib.commons.filesystem.CanonicalFile;

/**
 * Test case for {@link ILAnalyzerRunnerProcessor}.
 * 
 * @author $Author: streitel $
 * @version $Rev: 46015 $
 * @ConQAT.Rating GREEN Hash: A168D0526967CA659919963FCFCB02F1
 */
public class ILAnalyzerRunnerProcessorTest extends AssemblyExecutorTestBase {

	/** Root of the resource tree that contains the assemblies on which ILA runs */
	private IResource input;

	/** {@inheritDoc} */
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		// create input resource tree with all assemblies in test directory
		input = createBinaryScope(useTestFile("assemblies"),
				new String[] { "*" }, null);
	}

	/** Runs ILA without any existing XML files. All XML must be created. */
	public void testRunIlaWithoutExistingXml() throws Exception {
		runIlaAndAssertXmlFilesUptodate(input);
	}

	/** Runs ILA twice. Make sure that second run creates no new XML files */
	public void testRunIlaTwice() throws Exception {
		runIlaAndAssertXmlFilesUptodate(input);

		// store last modification date of XML files for later comparison
		Map<CanonicalFile, Long> modificationDates = new HashMap<CanonicalFile, Long>();
		for (CanonicalFile xmlFile : sortedTmpFiles()) {
			modificationDates.put(xmlFile, xmlFile.lastModified());
		}

		runIlaAndAssertXmlFilesUptodate(input);

		// assert that modification dates did not change
		for (CanonicalFile xmlFile : sortedTmpFiles()) {
			long expectedLastModified = modificationDates.get(xmlFile);
			assertEquals("Modification date changed! File: " + xmlFile,
					expectedLastModified, xmlFile.lastModified());

			modificationDates.remove(xmlFile);
		}
		assertTrue("Test cannot make sure that all XML files were unchanged",
				modificationDates.isEmpty());
	}

	/** Make sure that ILA recomputes the XML for assemblies that changed */
	public void testIlaRecomputesUpdatedFile() throws Exception {
		runIlaAndAssertXmlFilesUptodate(input);

		// set modification date of assembly to later than its xml file
		long xmlLastModified = sortedTmpFiles().get(2).lastModified();

		// On some platforms, modification time cannot be set more precisely
		// than to a single second. We set it to second later than it is and
		// wait to make sure that the next time we query a modification time, we
		// really get a later value.
		int delay = 1001;
		assertTrue("Could not set time", sortedFiles(input).get(2)
				.setLastModified(xmlLastModified + delay));
		// make sure that xml is not finished before
		Thread.sleep(delay);

		runIlaAndAssertXmlFilesUptodate(input);
	}

	/**
	 * Make sure that XML files, for which no corresponding assemblies are in
	 * the scope, are removed from the XML dir.
	 */
	public void testSuperfluousXmlFileRemoved() throws Exception {
		runIlaAndAssertXmlFilesUptodate(input);

		// create input that contains less assemblies
		IResource smallerInput = createBinaryScope(useTestFile("assemblies"),
				new String[] { "*" }, new String[] { "*attribute*" });
		assertTrue(
				"Expecting larger number of xml files than assemblies in scope",
				sortedTmpFiles().size() > sortedFiles(smallerInput).size());

		// assert that next ila run reduces number of xml files
		runIlaAndAssertXmlFilesUptodate(smallerInput);
		assertEquals(
				"Expecting same number of XML files as assemblies in scope",
				sortedTmpFiles().size(), sortedFiles(smallerInput).size());
	}

	/** Run ILA and assert that all XML files are up to date */
	private void runIlaAndAssertXmlFilesUptodate(IResource input)
			throws ConQATException {
		executeProcessor(ILAnalyzerRunnerProcessor.class, "(input=(ref=",
				input, "), xml=(folder='" + getTmpDirectory().getAbsolutePath()
						+ "'))");

		assertXmlFilesUptodate(input);
	}

	/**
	 * Asserts that, for each assembly, there exists an XML file with a
	 * modification date later than that of the assembly
	 */
	private void assertXmlFilesUptodate(IResource input) throws ConQATException {
		List<File> assemblies = sortedFiles(input);
		List<CanonicalFile> xmlFiles = sortedTmpFiles();

		assertEquals("Expecting same number of assemblies and XML files",
				assemblies.size(), xmlFiles.size());

		for (int i = 0; i < assemblies.size(); i++) {
			File assembly = assemblies.get(i);
			File xmlFile = xmlFiles.get(i);

			String expected = ILAnalyzerRunnerProcessor.xmlNameFor(assembly
					.getName());
			String actual = xmlFile.getName();
			assertEquals("Expecting different name of XML file", expected,
					actual);

			long assemblyLastModified = assembly.lastModified();
			long xmlLastModified = xmlFile.lastModified();
			assertTrue(
					"Expecting later modification date of XML file than of assembly, but was: XML:"
							+ xmlLastModified + ", assembly: "
							+ assemblyLastModified + " (" + assembly.getName()
							+ ")", xmlLastModified >= assemblyLastModified);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void initBundleContext() throws BundleException {
		new BundleContext(new BundleInfo(
				getBundleDir(org.conqat.engine.dotnet.BundleContext.class)));
	}

}