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
package org.conqat.engine.commons.testutils;

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.commons.node.ConQATNodeBase;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.IConQATProcessor;
import org.conqat.engine.core.core.IConQATProcessorInfo;

/**
 * A simple node construction kit.
 * 
 * @author Benjamin Hummel
 * @author $Author: hummelb $
 * @version $Rev: 43290 $
 * @ConQAT.Rating GREEN Hash: 7175DB0D0CC6EFED59FBBE81DD081C10
 */
@AConQATProcessor(description = "A simple node construction kit.")
public class NodeCreator extends ConQATNodeBase implements IConQATProcessor {

	/** Counter for the number of nodes created. */
	private static int counter = 0;

	/** Unique id for the node. */
	private final int id = ++counter;

	/** The children of this node. */
	public List<IConQATNode> children = new ArrayList<IConQATNode>();

	/** Adds a child to this node */
	@AConQATParameter(name = "child", description = "")
	public void addChild(
			@AConQATAttribute(name = "ref", description = "") IConQATNode c) {
		children.add(c);
	}

	/** Adds a string value to this node. */
	@AConQATParameter(name = "string", description = "")
	public void addStringValue(
			@AConQATAttribute(name = "name", description = "") String name,
			@AConQATAttribute(name = "value", description = "") String value) {

		setValue(name, value);
	}

	/** Adds a double value to this node. */
	@AConQATParameter(name = "double", description = "")
	public void addDoubleValue(
			@AConQATAttribute(name = "name", description = "") String name,
			@AConQATAttribute(name = "value", description = "") double value) {

		setValue(name, value);
	}

	/** Adds an object value to this node. */
	@AConQATParameter(name = "object", description = "")
	public void addObjectValue(
			@AConQATAttribute(name = "name", description = "") String name,
			@AConQATAttribute(name = "value", description = "") Object value) {

		setValue(name, value);
	}

	/** {@inheritDoc} */
	@Override
	public void init(IConQATProcessorInfo processorInfo) {
		// nothing to do here
	}

	/** {@inheritDoc} */
	@Override
	public NodeCreator process() {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public String getName() {
		return "node_" + id;
	}

	/** {@inheritDoc} */
	@Override
	public String getId() {
		return getName();
	}

	/** {@inheritDoc} */
	@Override
	public IConQATNode[] getChildren() {
		IConQATNode[] result = new IConQATNode[children.size()];
		return children.toArray(result);
	}

	/** {@inheritDoc} */
	@Override
	public boolean hasChildren() {
		return !children.isEmpty();
	}

	/** {@inheritDoc} */
	@Override
	public IConQATNode deepClone() {
		// we don't support proper cloning
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public IConQATNode getParent() {
		// we will not support this
		return null;
	}
}