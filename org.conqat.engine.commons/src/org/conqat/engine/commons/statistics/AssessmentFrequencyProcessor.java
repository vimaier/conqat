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

import java.util.HashSet;

import org.conqat.lib.commons.assessment.Assessment;
import org.conqat.lib.commons.assessment.ETrafficLightColor;
import org.conqat.lib.commons.collections.CounterSet;
import org.conqat.lib.commons.error.NeverThrownRuntimeException;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.traversal.INodeVisitor;
import org.conqat.engine.commons.traversal.TraversalUtils;
import org.conqat.engine.commons.util.ConQATInputProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * This processor counts the number of RED warnings stored at different keys and
 * creats a KeyedData object.
 * 
 * @author Florian Deissenboeck
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: FE9BC593E84ADD76AC8CFDEA3E747CBE
 */
@AConQATProcessor(description = "This processor counts the number of RED "
		+ "assessments stored at different keys and creates a KeyedData object, "
		+ "i.e. a map that maps from each key to the number of RED leaves found for the key.")
public class AssessmentFrequencyProcessor extends ConQATInputProcessorBase<IConQATNode>
		implements INodeVisitor<IConQATNode, NeverThrownRuntimeException> {

	/** Set of keys to check for collections. */
	private final HashSet<String> keys = new HashSet<String>();

	/** The counter. */
	private final CounterSet<String> counter = new CounterSet<String>();

	/** Flag signaling relative results. */
	private boolean relative = false;

	/** Number of leaves counter for relative results. */
	private int leaveCount = 0;

	/** Set relative flag. */
	@AConQATParameter(name = "relative", maxOccurrences = 1, description = "Flag for relative results.")
	public void setRelative(
			@AConQATAttribute(name = "value", description = "If set to true, the number of RED assessed leaves is put into "
					+ "relation with the total number of leaves [false].") boolean relative) {
		this.relative = relative;
	}

	/** Set the key to use. */
	@AConQATParameter(name = ConQATParamDoc.READKEY_NAME, minOccurrences = 1, description = ConQATParamDoc.READKEY_DESC)
	public void addKey(
			@AConQATAttribute(name = ConQATParamDoc.READKEY_KEY_NAME, description = ConQATParamDoc.READKEY_KEY_DESC) String key) {

		keys.add(key);
	}

	/** Does the actual processing. */
	@Override
	public KeyedData<String> process() {
		TraversalUtils.visitLeavesDepthFirst(this, input);
		KeyedData<String> data = new KeyedData<String>();
		for (String key : keys) {
			data.add(key, getValue(key));
		}
		return data;
	}

	/** Get value for key. */
	private double getValue(String key) {
		double value = counter.getValue(key);
		if (!relative) {
			return value;
		}
		return value / leaveCount;
	}

	/**
	 * Count number of red assessed leaves.
	 */
	@Override
	public void visit(IConQATNode node) {
		leaveCount++;
		for (String key : keys) {
			Object valueObject = node.getValue(key);
			if (valueObject instanceof Assessment) {
				Assessment assessment = (Assessment) valueObject;
				if (assessment.getDominantColor() == ETrafficLightColor.RED) {
					counter.inc(key);
				}
			}
		}
	}
}