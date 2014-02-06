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
package org.conqat.engine.simulink.analyzers;

import static org.conqat.engine.simulink.analyzers.PatternBasedBlockTraversingProcessorBase.ALLOW_EVERYTHING_PATTERN;
import static org.conqat.engine.simulink.analyzers.PatternBasedBlockTraversingProcessorBase.ALL_BLOCKS_TYPE;
import static org.conqat.engine.simulink.analyzers.PatternBasedBlockTraversingProcessorBase.DENY_EVERYTHING_PATTERN;
import static org.conqat.engine.simulink.analyzers.SimulinkBlockParameterAssessor.NONE;

import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.findings.FindingsList;
import org.conqat.engine.commons.findings.location.ElementLocation;
import org.conqat.engine.commons.findings.location.QualifiedNameLocation;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.testutils.ProcessorInfoMock;
import org.conqat.engine.simulink.scope.ISimulinkElement;
import org.conqat.engine.simulink.scope.ISimulinkResource;
import org.conqat.engine.simulink.util.SimulinkTestCaseBase;
import org.conqat.lib.commons.collections.ListMap;

/**
 * Test for {@link SimulinkBlockParameterAssessor}.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating YELLOW Hash: EA7AE49E41BB4216655CA42F7E18E747
 */
public class SimulinkBlockParameterAssessorTest extends SimulinkTestCaseBase {

	/** The assessor under test. */
	private SimulinkBlockParameterAssessor assessor;

	/** Create assessor. */
	@Override
	public void setUp() throws Exception {
		ISimulinkResource simulinkElement = readSimulinkElement();
		assessor = new SimulinkBlockParameterAssessor();
		assessor.init(new ProcessorInfoMock());
		assessor.setRoot(simulinkElement);
		assessor.setFindingGroupName("Test");
	}

	/** Test if rule for defined type with deny-everything-pattern works. */
	public void testDefinedTypeDenyEverything() throws ConQATException {
		assessor.addRule(NONE, ALLOW_EVERYTHING_PATTERN, false, "Sum",
				"IconShape", "round", DENY_EVERYTHING_PATTERN);
		checkMessages("SimulinkBlockParameterAssessorTest/Sum");
	}

	/** Test if rule for defined type works. */
	public void testDefinedType() throws ConQATException {
		assessor.addRule(NONE, ALLOW_EVERYTHING_PATTERN, false, "Sum",
				"ForegroundColor", "bl.*", "blue");
		checkMessages("SimulinkBlockParameterAssessorTest/Controller/PID/Sum1",
				"SimulinkBlockParameterAssessorTest/Controller/PID/Sum");
	}

	/** Test if rule for defined type with allow-everything-pattern works. */
	public void testDefinedTypeAllowEverything() throws ConQATException {
		assessor.addRule(NONE, ALLOW_EVERYTHING_PATTERN, false, "Sum",
				"ForegroundColor", ALLOW_EVERYTHING_PATTERN, "yellow");

		checkMessages("SimulinkBlockParameterAssessorTest/Controller/PID/Sum1");
	}

	/** Test if rule for all types with deny-everything-pattern works. */
	public void testAllBlocksDenyEverything() throws ConQATException {
		assessor.addRule(NONE, ALLOW_EVERYTHING_PATTERN, false,
				ALL_BLOCKS_TYPE, "DropShadow", "off", DENY_EVERYTHING_PATTERN);
		checkMessages("SimulinkBlockParameterAssessorTest/Controller/PID/I-Delay");
	}

	/** Test if rule for all types works. */
	public void testAllBlocks() throws ConQATException {
		assessor.addRule(NONE, ALLOW_EVERYTHING_PATTERN, false,
				ALL_BLOCKS_TYPE, "ForegroundColor", "bl.*", "blue");
		checkMessages("SimulinkBlockParameterAssessorTest/Controller/PID/Sum1",
				"SimulinkBlockParameterAssessorTest/Controller/PID/Sum");
	}

	/** Test if rule for all types with allow-everything-pattern works. */
	public void testAllBlocksAllowEverything() throws ConQATException {
		assessor.addRule(NONE, ALLOW_EVERYTHING_PATTERN, false,
				ALL_BLOCKS_TYPE, "ForegroundColor", ALLOW_EVERYTHING_PATTERN,
				"yellow");

		checkMessages("SimulinkBlockParameterAssessorTest/Controller/PID/Sum1");
	}

	/**
	 * Run processor and checks if the Simulink elements with the specified ids
	 * have warnings.
	 */
	private void checkMessages(String... ids) throws ConQATException {
		ISimulinkResource resource = assessor.process();
		ISimulinkElement modelElement = getModelElement(resource);
		FindingsList findings = NodeUtils.getFindingsList(modelElement,
				SimulinkBlockParameterAssessor.KEY);

		assertNotNull(findings);

		ListMap<String, Finding> nameMap = obtainFindingMap(findings);

		for (String id : ids) {
			assertEquals(1, nameMap.getCollection(id).size());
		}

		modelElement.setValue(SimulinkBlockParameterAssessor.KEY, null);
	}

	/** Returns a mapping from qualified names to lists of findings. */
	private ListMap<String, Finding> obtainFindingMap(FindingsList findings) {
		ListMap<String, Finding> result = new ListMap<String, Finding>();
		for (Finding finding : findings) {
			ElementLocation location = finding.getLocation();
			if (location instanceof QualifiedNameLocation) {
				String name = ((QualifiedNameLocation) location)
						.getQualifiedName();
				result.add(name, finding);
			}
		}
		return result;
	}
}