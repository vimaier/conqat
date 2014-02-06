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

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.commons.node.ConQATNodeBase;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.IRemovableConQATNode;
import org.conqat.engine.commons.node.NodeConstants;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.lib.commons.clone.DeepCloneException;

/**
 * Objects of this class represent a JUnit report for multiple test suites.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: DC18ADB02F849F127EBB292576269F9C
 */
public class JUnitResultNode extends ConQATNodeBase implements
		IRemovableConQATNode {

	/** The list of child nodes. */
	private final List<JUnitTestSuiteNode> children = new ArrayList<JUnitTestSuiteNode>();

	/** Create a new result */
	public JUnitResultNode() {
		setValue(NodeConstants.HIDE_ROOT, true);

		NodeUtils.addToDisplayList(this, JUnitResultScope.KEY_TEST_COUNT,
				JUnitResultScope.KEY_ERROR_COUNT,
				JUnitResultScope.KEY_FAILURE_COUNT);
	}

	/** Copy constructor. */
	protected JUnitResultNode(JUnitResultNode orig) throws DeepCloneException {
		super(orig);

		for (JUnitTestSuiteNode testSuite : orig.children) {
			addChild(testSuite.deepClone());
		}
	}

	/** Description contains number of test suites. */
	@Override
	public String getName() {
		return "JUnit Result [# suites: " + children.size() + "]";
	}

	/** Returns constant value since this is root node. */
	@Override
	public String getId() {
		return "JUnit Node";
	}

	/** {@inheritDoc} */
	@Override
	public JUnitResultNode deepClone() throws DeepCloneException {
		return new JUnitResultNode(this);
	}

	/** {@inheritDoc} */
	@Override
	public JUnitTestSuiteNode[] getChildren() {
		return children.toArray(new JUnitTestSuiteNode[children.size()]);
	}

	/** {@inheritDoc} */
	@Override
	public void remove() {
		// nothing to do, as this is the root node
	}

	/** {@inheritDoc} */
	@Override
	public IConQATNode getParent() {
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public boolean hasChildren() {
		return !children.isEmpty();
	}

	/** Add a child. */
	/* package */void addChild(JUnitTestSuiteNode child) {
		children.add(child);
		child.setParent(this);
	}

	/** Remove a child node. */
	/* package */void removeNode(JUnitTestSuiteNode node) {
		children.remove(node);
	}
}