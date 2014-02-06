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

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.commons.traversal.TargetExposedNodeTraversingProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;

/**
 * Base class for analyzers performing arithmetic on keys.
 * 
 * @author $Author: steidl $
 * @version $Rev: 47137 $
 * @ConQAT.Rating GREEN Hash: 91631074AAEE3F5E09C0856831AB7F2B
 */
public abstract class CalculatorBase extends
		TargetExposedNodeTraversingProcessorBase<IConQATNode> {

	/** The keys to store the result. */
	private List<String> resultKeys = new ArrayList<String>();

	/** The providers for the first argument. */
	private List<IValueProvider> argument1Providers = new ArrayList<IValueProvider>();

	/** The providers for the second argument. */
	private List<IValueProvider> argument2Providers = new ArrayList<IValueProvider>();

	/** {@inheritDoc} */
	@Override
	protected ETargetNodes getDefaultTargetNodes() {
		return ETargetNodes.ALL;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "keys", description = "The keys to read from and write to.")
	public void addKeys(
			@AConQATAttribute(name = "arg1", description = "Name of the key for the first argument.") String arg1,
			@AConQATAttribute(name = "arg2", description = "Name of the key for the second argument.") String arg2,
			@AConQATAttribute(name = "result", description = "Name of the key for the result.") String result) {

		argument1Providers.add(new KeyBasedProvider(arg1));
		argument2Providers.add(new KeyBasedProvider(arg2));
		resultKeys.add(result);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "const-keys", description = "Performs calculation with a constant as first argument")
	public void addConstKey(
			@AConQATAttribute(name = "arg1", description = "The constant value used for the first argument.") double arg1,
			@AConQATAttribute(name = "arg2", description = "Name of the key for the second argument.") String arg2,
			@AConQATAttribute(name = "result", description = "Name of the key for the result.") String result) {

		argument1Providers.add(new ConstProvider(arg1));
		argument2Providers.add(new KeyBasedProvider(arg2));
		resultKeys.add(result);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "const-keys-inverse", description = "Performs calculation with a constant as second argument")
	public void addConstKeyInverse(
			@AConQATAttribute(name = "arg1", description = "Name of the key for the first argument.") String arg1,
			@AConQATAttribute(name = "arg2", description = "The constant value used for the second argument.") double arg2,
			@AConQATAttribute(name = "result", description = "Name of the key for the result.") String result) {

		argument1Providers.add(new KeyBasedProvider(arg1));
		argument2Providers.add(new ConstProvider(arg2));
		resultKeys.add(result);
	}

	/** {@inheritDoc} */
	@Override
	protected void setUp(IConQATNode root) throws ConQATException {
		super.setUp(root);
		NodeUtils.addToDisplayList(root, resultKeys);
	}

	/** {@inheritDoc} */
	@Override
	public void visit(IConQATNode node) {
		for (int i = 0; i < resultKeys.size(); i++) {
			double arg1 = argument1Providers.get(i).getValue(node);
			double arg2 = argument2Providers.get(i).getValue(node);
			String resultKey = resultKeys.get(i);
			// propagate non-standard doubles as NaN
			if (Double.isNaN(arg1) || Double.isNaN(arg2)
					|| Double.isInfinite(arg1) || Double.isInfinite(arg2)) {
				node.setValue(resultKey, Double.NaN);
			} else {
				node.setValue(resultKey, calculate(arg1, arg2));
			}
		}
	}

	/**
	 * Performs the actual calculation on the numbers provided. The method may
	 * also return {@link Double#NaN} or infinity in case of errors, however the
	 * arguments are guaranteed to be "normal" Doubles.
	 */
	protected abstract double calculate(double args1, double arg2);

	/** Provides a value for calculation. */
	private static interface IValueProvider {
		/** Returns the value that may depend on the node. */
		double getValue(IConQATNode node);
	}

	/** Key based value provider. */
	private class KeyBasedProvider implements IValueProvider {

		/** The key to read from. */
		private final String key;

		/** The key. */
		public KeyBasedProvider(String key) {
			this.key = key;
		}

		/** {@inheritDoc} */
		@Override
		public double getValue(IConQATNode node) {
			Object o = node.getValue(key);
			if (!(o instanceof Number)) {
				getLogger().warn(
						"Non-numerical value at node " + node.getId()
								+ " for key " + key + ": " + o);
				return Double.NaN;
			}
			return ((Number) o).doubleValue();
		}
	}

	/** Const value provider. */
	private static class ConstProvider implements IValueProvider {

		/** The constant value. */
		private final double value;

		/** The key. */
		public ConstProvider(double value) {
			this.value = value;
		}

		/** {@inheritDoc} */
		@Override
		public double getValue(IConQATNode node) {
			return value;
		}
	}
}
