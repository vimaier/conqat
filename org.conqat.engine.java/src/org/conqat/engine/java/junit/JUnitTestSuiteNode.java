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
package org.conqat.engine.java.junit;

import org.conqat.engine.commons.node.ConQATNodeBase;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.IRemovableConQATNode;
import org.conqat.lib.commons.clone.DeepCloneException;

/**
 * Objects of this class represent the results for a JUnit test suite but don't
 * provide detailed information about the test cases. They merely represent the
 * number of test cases, error and failures.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: A4028909FE0E23123EAAA65E4A0C5509
 */
public class JUnitTestSuiteNode extends ConQATNodeBase implements
		IRemovableConQATNode {

	/** The parent node. */
	private JUnitResultNode parent = null;

	/** Fully qualified classname of the test suite. */
	private final String name;

	/** Number of test cases in this suite. */
	private final int testCount;

	/** Number of test cases with error. */
	private final int errorCount;

	/** Number of test cases with failures. */
	private final int failureCount;

	/**
	 * Create a new test suite node.
	 * 
	 * @param name
	 *            full qualified class name of the test suite
	 * @param testCount
	 *            number of test cases
	 * @param errorCount
	 *            number of test cases with error
	 * @param failureCount
	 *            nubmer of test cases with failures.
	 */
	public JUnitTestSuiteNode(String name, int testCount, int errorCount,
			int failureCount) {
		this.name = name;
		this.testCount = testCount;
		this.errorCount = errorCount;
		this.failureCount = failureCount;

		setValue(JUnitResultScope.KEY_TEST_COUNT, testCount);
		setValue(JUnitResultScope.KEY_ERROR_COUNT, errorCount);
		setValue(JUnitResultScope.KEY_FAILURE_COUNT, failureCount);
	}

	/** Copy constructor. */
	protected JUnitTestSuiteNode(JUnitTestSuiteNode testSuiteNode)
			throws DeepCloneException {
		super(testSuiteNode);
		name = testSuiteNode.name;
		testCount = testSuiteNode.testCount;
		errorCount = testSuiteNode.errorCount;
		failureCount = testSuiteNode.failureCount;

		setValue(JUnitResultScope.KEY_TEST_COUNT, testCount);
		setValue(JUnitResultScope.KEY_ERROR_COUNT, errorCount);
		setValue(JUnitResultScope.KEY_FAILURE_COUNT, failureCount);
	}

	/** Name of the test suite. */
	@Override
	public String getName() {
		return name;
	}

	/** Returns the name of the test suite. */
	@Override
	public String getId() {
		return name;
	}

	/** {@inheritDoc} */
	@Override
	public JUnitTestSuiteNode deepClone() throws DeepCloneException {
		return new JUnitTestSuiteNode(this);
	}

	/** Get number of test cases with errors. */
	public int getErrorCount() {
		return errorCount;
	}

	/** Get number of test cases with failures. */
	public int getFailureCount() {
		return failureCount;
	}

	/** Get number of test cases. */
	public int getTestCount() {
		return testCount;
	}

	/** {@inheritDoc} */
	@Override
	public IRemovableConQATNode[] getChildren() {
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public void remove() {
		if (parent != null) {
			parent.removeNode(this);
		}
	}

	/** {@inheritDoc} */
	@Override
	public IConQATNode getParent() {
		return parent;
	}

	/** {@inheritDoc} */
	@Override
	public boolean hasChildren() {
		return false;
	}

	/** Set the parent node. */
	/* package */void setParent(JUnitResultNode parent) {
		this.parent = parent;
	}
}