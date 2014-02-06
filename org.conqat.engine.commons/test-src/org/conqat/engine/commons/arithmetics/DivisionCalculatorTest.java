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
package org.conqat.engine.commons.arithmetics;

import junit.framework.TestCase;

import org.conqat.engine.commons.testutils.NodeCreator;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.testutils.ProcessorInfoMock;

/**
 * Test the {@link DivisionCalculator}.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating YELLOW Hash: 59547B4298552D1519AAD29F90565C01
 */
public class DivisionCalculatorTest extends TestCase {

	/** Key used for the divisor. */
	private static final String KEY_DIVISOR = "test2";

	/** Key used for the dividend. */
	private static final String KEY_DIVIDEND = "test1";

	/** Key used for the result. */
	private static final String KEY_RESULT = KEY_DIVIDEND + "/" + KEY_DIVISOR;

	/** The analyzer used. */
	private DivisionCalculator analyzer;

	/** {@inheritDoc} */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		analyzer = new DivisionCalculator();
		analyzer.init(new ProcessorInfoMock());
	}

	/** Test the case of a legal division. */
	public void testLegal() throws ConQATException {
		NodeCreator root = new NodeCreator();
		root.setValue(KEY_DIVIDEND, 6);
		root.setValue(KEY_DIVISOR, 3);

		process(root);

		assertEquals(2.0, root.getValue(KEY_RESULT));
	}

	/** Test the case of division by zero. */
	public void testDivsionByZero() throws ConQATException {
		NodeCreator root = new NodeCreator();
		root.setValue(KEY_DIVIDEND, 6);
		root.setValue(KEY_DIVISOR, 0);

		process(root);

		assertTrue(((Double) root.getValue(KEY_RESULT)).isInfinite());
	}

	/** Test the case of division by zero with custom return value. */
	public void testDivisionByZeroWithCustomReturnValue() throws Exception {
		NodeCreator root = new NodeCreator();
		root.setValue(KEY_DIVIDEND, 6);
		root.setValue(KEY_DIVISOR, 0);

		process(root, 0);

		assertEquals(0.0, root.getValue(KEY_RESULT));
	}

	/** Test handling of non-numeric values. */
	public void testDividendNoNumber() throws ConQATException {
		NodeCreator root = new NodeCreator();
		root.setValue(KEY_DIVIDEND, "test");
		root.setValue(KEY_DIVISOR, 3);

		process(root);

		assertTrue(((Double) root.getValue(KEY_RESULT)).isNaN());
	}

	/** Test complete tree handling. */
	public void testMultiNode() throws ConQATException {
		NodeCreator root = new NodeCreator();
		root.setValue(KEY_DIVIDEND, 6);
		root.setValue(KEY_DIVISOR, 3);

		NodeCreator firstChild = new NodeCreator();
		firstChild.setValue(KEY_DIVIDEND, 9);
		firstChild.setValue(KEY_DIVISOR, 3);

		NodeCreator secondChild = new NodeCreator();
		secondChild.setValue(KEY_DIVIDEND, 5);
		secondChild.setValue(KEY_DIVISOR, 10);

		NodeCreator firstGrandChild = new NodeCreator();
		firstGrandChild.setValue(KEY_DIVIDEND, 5);
		firstGrandChild.setValue(KEY_DIVISOR, "test");

		root.addChild(firstChild);
		root.addChild(secondChild);
		firstChild.addChild(firstGrandChild);

		process(root);

		assertEquals(2.0, root.getValue(KEY_RESULT));

		assertEquals(3.0, firstChild.getValue(KEY_RESULT));

		assertEquals(0.5, secondChild.getValue(KEY_RESULT));

		assertEquals(Double.NaN, firstGrandChild.getValue(KEY_RESULT));
	}

	/** Poor man's driver ;-) */
	private void process(NodeCreator node) throws ConQATException {
		analyzer.setRoot(node);
		analyzer.addKeys(KEY_DIVIDEND, KEY_DIVISOR, KEY_RESULT);
		analyzer.process();
	}

	/** Poor man's driver ;-) */
	private void process(NodeCreator node, double divByZeroREturnValue)
			throws ConQATException {
		analyzer.setRoot(node);
		analyzer.addKeys(KEY_DIVIDEND, KEY_DIVISOR, KEY_RESULT);
		analyzer.setDivisionByZeroReturnValue(divByZeroREturnValue);
		analyzer.process();
	}
}