/*-------------------------------------------------------------------------+
|                                                                          |
| Copyright 2005-2011 the ConQAT Project                                   |
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
package org.conqat.engine.resource.diff;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.commons.pattern.PatternTransformationList;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.ITextResource;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 2B8E29063B7035B95A63B025759489EA
 */
@AConQATProcessor(description = "Creates information about the diff of two scopes. "
		+ "Use e.g. ScopeDiffInfoConverter to visualize the result of this processor.")
public class ScopeDiffSummary extends ConQATProcessorBase {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "main", attribute = ConQATParamDoc.INPUT_REF_NAME, description = "Main scope")
	public ITextResource mainRoot;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "comparee", attribute = ConQATParamDoc.INPUT_REF_NAME, description = "Scope to compare to (the 'older' one)")
	public ITextResource compareeRoot;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.PATH_TRANSFORMATION_PARAM, attribute = ConQATParamDoc.PATH_TRANSFORMATION_ATTRIBUTE, optional = true, description = ConQATParamDoc.PATH_TRANSFORMATION_DESCRIPTION)
	public PatternTransformationList transformations = null;

	/** {@inheritDoc} */
	@Override
	public ScopeDiffInfo process() throws ConQATException {
		return new ScopeDiffInfo(mainRoot, compareeRoot, transformations,
				getLogger());
	}
}
