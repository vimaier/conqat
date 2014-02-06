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
package org.conqat.engine.architecture.assessment;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.conqat.engine.architecture.scope.ArchitectureDefinition;
import org.conqat.engine.architecture.scope.ArchitectureDefinitionReader;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.logging.testutils.LoggerMock;
import org.conqat.engine.core.logging.testutils.ProcessorInfoMock;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.lib.commons.test.CCSMTestCaseBase;

/**
 * Tests for the {@link AssessmentFileWriter}.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: C19847E15DF982330B59027274152C80
 */
public class AssessmentFileWriterTest extends CCSMTestCaseBase {

	/** Test fixture */
	private ByteArrayOutputStream assessment;
	/** Architecture definition used for test */
	private ArchitectureDefinition architecture;

	/** {@inheritDoc} */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		assessment = new ByteArrayOutputStream();
		ArchitectureDefinitionReader reader = new ArchitectureDefinitionReader();
		File testArchitecture = useTestFile("p.architecture");
		reader.init(new ProcessorInfoMock());
		reader.setInputFile(testArchitecture);
		architecture = reader.process();
	}

	/** Simple test for writing an assessment. */
	public void testSimple() throws Exception {
		assertAssessment("p_assessment.xml");
	}

	/** Test for orphans */
	public void testOrphans() throws Exception {
		NodeUtils.getOrCreateStringList(architecture,
				ArchitectureAnalyzer.ORPHANS_KEY).add("i.am.an.Orphan");
		assertAssessment("orphans_assessment.xml");
	}

	/** Asserts that the assessment corresponds to expected assessment */
	private void assertAssessment(String expectedAssessmentFileName)
			throws Exception {
		writeAssessment();
		String expectedAssessment = FileSystemUtils
				.readFile(useTestFile(expectedAssessmentFileName));
		String actualAssessment = new String(assessment.toByteArray());
		assertEquals(StringUtils.replaceLineBreaks(expectedAssessment),
				StringUtils.replaceLineBreaks(actualAssessment));
	}

	/** Writes the assessment */
	private void writeAssessment() {
		new AssessmentFileWriter(assessment, architecture, new LoggerMock())
				.writeArchitecture();
	}

}