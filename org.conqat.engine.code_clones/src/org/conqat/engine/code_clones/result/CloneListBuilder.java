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
package org.conqat.engine.code_clones.result;

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.code_clones.core.CloneClass;
import org.conqat.engine.code_clones.core.CloneClassList;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author juergens
 * @author $Author: juergens $
 * @version $Rev: 34670 $
 * @ConQAT.Rating GREEN Hash: 981F4E0FF4B26D8844E380030042DF8C
 */
@AConQATProcessor(description = "Creates a list of the clone classes from a simple list")
public class CloneListBuilder extends CloneListBuilderBase {

	/** List of clone classes. */
	private List<CloneClass> cloneClasses;

	/** ConQAT Parameter */
	@AConQATParameter(name = ConQATParamDoc.INPUT_NAME, description = ConQATParamDoc.INPUT_DESC, minOccurrences = 1, maxOccurrences = 1)
	public void setResult(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) CloneClassList cloneClasses) {
		this.cloneClasses = new ArrayList<CloneClass>(cloneClasses);
	}

	/** {@inheritDoc} */
	@Override
	protected List<CloneClass> getCloneClasses() {
		return cloneClasses;
	}

}