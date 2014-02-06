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
package org.conqat.engine.commons.statistics;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.INodeVisitor;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.commons.util.ConQATInputProcessorBase;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.lib.commons.collections.CounterSet;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: pfaller $
 * @version $Rev: 37529 $
 * @ConQAT.Rating GREEN Hash: B4489E41E73E33F234898F51A1FBE8F3
 */
@AConQATProcessor(description = "This processor creates a KeyedData object by "
		+ "counting the frequency of objects specified by a key at the leaves of the "
		+ "ConQATNode hierarchy. The values must implement interface "
		+ "java.util.Comparable. If the number-key is specified, the processor does "
		+ "not only add 1 for each leave but the number stored at the specified key. ")
public class ValueFrequencyProcessor extends
		ConQATInputProcessorBase<IConQATNode> implements
		INodeVisitor<IConQATNode, ConQATException> {

	/** Counter array for counting the frequency. */
	private final CounterSet<Comparable<?>> counter = new CounterSet<Comparable<?>>();

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.READKEY_KEY_NAME, attribute = ConQATParamDoc.READKEY_KEY_NAME, description = ConQATParamDoc.READKEY_KEY_DESC)
	public String key;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "number-key", attribute = ConQATParamDoc.READKEY_KEY_NAME, description = "Optional key for stored numbers.", optional = true)
	public String numberKey;

	/** {@inheritDoc} */
	@Override
	public KeyedData<?> process() throws ConQATException {
		TraversalUtils.visitLeavesDepthFirst(this, input);
		return new KeyedData<Comparable<?>>(counter);
	}

	/**
	 * This method obtains the values from the node and increases counter
	 * accordingly. If the value is <code>null</code> it will be ignored but
	 * logged.
	 * 
	 * @throws ConQATException
	 *             if the value is neither <code>null</code> nor instance of
	 *             {@link Comparable}.
	 */
	@Override
	public void visit(IConQATNode node) throws ConQATException {
		Object valueToStore = node.getValue(key);

		if (valueToStore == null) {
			getLogger().info("Null value for key " + key + " ignored.");
			return;
		}

		int number = 1;

		if (numberKey != null) {
			number = (int) NodeUtils.getDoubleValue(node, numberKey);
		}

		counter.inc(convert(valueToStore), number);
	}

	/**
	 * Template method to convert an object found at node to something
	 * comparable. The default implementation only checks if the value is
	 * comparable and raises an exception otherwise.
	 */
	protected Comparable<?> convert(Object value) throws ConQATException {
		if (!(value instanceof Comparable<?>)) {
			throw new ConQATException("Can't store value " + value
					+ " as it is not comparabale.");
		}
		return (Comparable<?>) value;
	}
}