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
package org.conqat.engine.commons.assessment;

import static org.conqat.lib.commons.assessment.ETrafficLightColor.GREEN;
import static org.conqat.lib.commons.assessment.ETrafficLightColor.RED;
import static org.conqat.lib.commons.assessment.ETrafficLightColor.YELLOW;
import junit.framework.TestCase;
import org.conqat.engine.commons.testutils.NodeCreator;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.testutils.ProcessorInfoMock;

/**
 * Test the integration of the assessment processors.
 * 
 * @author Benjamin Hummel
 * @author Tilman Seifert
 * @author $Author: heinemann $
 * @version $Rev: 43096 $
 * @ConQAT.Rating GREEN Hash: 0E062D020E623F6034EB5ECE4C85DC70
 */
public class DoubleAssessorTest extends TestCase {

	/**
	 * Test for the {@link DoubleAssessor}
	 * 
	 * @throws ConQATException
	 *             if configuration fails
	 */
	public void testSampleConfig() throws ConQATException {
		String valueKey = "test";
		String targetKey = "assessment";

		NodeCreator root = new NodeCreator();
		NodeCreator node1 = createNode(valueKey, root, 1);
		NodeCreator node2 = createNode(valueKey, root, 2);
		NodeCreator node3 = createNode(valueKey, root, 3);
		NodeCreator node4 = createNode(valueKey, root, 4);

		DoubleAssessor assessor = new DoubleAssessor();
		assessor.init(new ProcessorInfoMock());
		assessor.setRoot(root);
		assessor.setReadKey(valueKey);
		assessor.setWriteKey(targetKey);
		assessor.addRange(.9, 1, GREEN);
		assessor.addRange(1.9, 2.5, YELLOW);
		assessor.defaultColor = RED;
		assessor.setErrorColor(RED);

		/*
		 * since DoubleAssessor is a pipeline processor, the assessments are
		 * added to our original nodes.
		 */
		assessor.process();

		assertEquals(node1.getValue(targetKey).toString(), GREEN.toString());
		assertEquals(node2.getValue(targetKey).toString(), YELLOW.toString());
		assertEquals(node3.getValue(targetKey).toString(), RED.toString());
		assertEquals(node4.getValue(targetKey).toString(), RED.toString());
	}

	/** Create node and store value */
	private NodeCreator createNode(String key, NodeCreator root, int value) {
		NodeCreator node = new NodeCreator();
		node.addDoubleValue(key, value);
		root.addChild(node);
		return node;
	}
}