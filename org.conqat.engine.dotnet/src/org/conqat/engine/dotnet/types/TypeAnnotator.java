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
package org.conqat.engine.dotnet.types;

import java.util.List;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.engine.sourcecode.resource.TokenElementProcessorBase;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: goede $
 * @version $Rev: 40969 $
 * @ConQAT.Rating GREEN Hash: 0C97C1E39BE57347D590BF174E3C8FA2
 */
@AConQATProcessor(description = "Annotates IShallowParsedElements with the fully qualified names of the types they contain.")
public class TypeAnnotator extends TokenElementProcessorBase {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.WRITEKEY_NAME, attribute = ConQATParamDoc.WRITEKEY_KEY_NAME, description = ConQATParamDoc.WRITEKEY_DESC, optional = true)
	public String writeKey = "types";

	/** {@inheritDoc} */
	@Override
	protected void setUp(ITokenResource root) {
		NodeUtils.addToDisplayList(root, writeKey);
	}

	/** {@inheritDoc} */
	@Override
	protected void processElement(ITokenElement element) {
		try {
			RootCodeEntity root = CodeEntityFactory.codeEntitiesFor(element
					.getTokens(getLogger()));

			List<String> typeFqNames = root.collectTypeNames();
			element.setValue(writeKey, typeFqNames);
		} catch (ConQATException e) {
			// Code could be mal-formed. We want to be robust and continue
			getLogger().warn(
					"Could not parse element: " + element.getUniformPath(), e);
		}
	}

}
