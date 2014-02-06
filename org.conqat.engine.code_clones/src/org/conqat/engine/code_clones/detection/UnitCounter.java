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
package org.conqat.engine.code_clones.detection;

import java.util.LinkedList;
import java.util.List;

import org.conqat.engine.code_clones.core.Unit;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.APipelineSource;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.ITextResource;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: 9E0B4DE46648FEE3105E0D38B815556F
 */
@AConQATProcessor(description = "Counts units and stores their count in each element. Units are discarded.")
public class UnitCounter extends UnitProcessorBase {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "pool", attribute = "clear", optional = true, description = ""
			+ "Flag that determines whether StringPool gets drained after each element")
	public boolean clearStringPoolAfterElement = false;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "collect", attribute = "units", description = "If set to true, all units are collected. Useful to measure impact of unit storage. Default is false.", optional = true)
	public boolean collectUnits = false;

	/** {@ConQAT.Doc} */
	@Override
	@AConQATParameter(name = ConQATParamDoc.INPUT_NAME, description = ConQATParamDoc.INPUT_DESC, minOccurrences = 1, maxOccurrences = 1)
	public void setInput(
			@APipelineSource @AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) ITextResource input) {
		this.input = input;
	}

	/** {@inheritDoc} */
	@Override
	public ITextResource process() throws ConQATException {
		List<Unit> list = null;
		if (collectUnits) {
			list = new LinkedList<Unit>();
			getLogger().info("Counting Units");
		}
		drainUnits(list, clearStringPoolAfterElement);
		return input;
	}

}