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
package org.conqat.engine.commons.defs;

import org.conqat.engine.commons.CommonUtils;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.traversal.ETargetNodes;
import org.conqat.engine.commons.traversal.TargetExposedNodeTraversingProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;

/**
 * Stores a constant value under a fixed key in each target node.
 * 
 * @author $Author: poehlmann $
 * @version $Rev: 42198 $
 * @ConQAT.Rating GREEN Hash: C80975BF1D9B42822457FD5ACD17C9EE
 */
@AConQATProcessor(description = "Stores a constant value under a fixed key in each target node")
public class ConstantAssigner extends
		TargetExposedNodeTraversingProcessorBase<IConQATNode> {

	/** Name of target key */
	private String key;

	/** Value of constant */
	private Object value;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "onlyIfNotSet", attribute = "value", optional = true, description = "Specifies"
			+ " whether the constant shall only be assigned in case no value was previously set for the key. Default is false, i.e. always assign.")
	public boolean onlyIfNotSet = false;

	/**
	 * ConQAT Parameter
	 */
	@AConQATParameter(name = "constant", minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "Constant value that gets set")
	public void setConstant(
			@AConQATAttribute(name = ConQATParamDoc.WRITEKEY_KEY_NAME, description = ConQATParamDoc.WRITEKEY_KEY_DESC) String key,
			@AConQATAttribute(name = ConQATParamDoc.VALUE_KEY_NAME, description = ConQATParamDoc.STRING_VALUE_KEY_DESC) String valueString,
			@AConQATAttribute(name = ConQATParamDoc.TYPE_KEY_NAME, description = ConQATParamDoc.TYPE_KEY_DESC) String typeName)
			throws ConQATException {
		value = CommonUtils.convertTo(valueString, typeName);
		this.key = key;
	}

	/** {@inheritDoc} */
	@Override
	protected ETargetNodes getDefaultTargetNodes() {
		return ETargetNodes.LEAVES;
	}

	/** Sets constant in every node */
	@Override
	public void visit(IConQATNode node) {
		if (!onlyIfNotSet || node.getValue(key) == null) {
			node.setValue(key, value);
		}
	}
}