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
package org.conqat.engine.resource.mark;

import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 35821 $
 * @ConQAT.Rating GREEN Hash: 6B99212CBFFFEB4D451EC6631DC28140
 */
@AConQATProcessor(description = "Marks all elements whose content matches one of the given regular expressions. "
		+ "This processor can i.e. be used to mark generated code in scenarios in which "
		+ "it is not desirable to remove the generated code via a filter, since it is "
		+ "still wanted for visualization.")
public class ContentMarker extends ResourceMarkerBase<ITextResource, ITextElement> {

	/** {@inheritDoc} */
	@Override
	protected Class<ITextElement> getElementClass() {
		return ITextElement.class;
	}

	/** {@inheritDoc} */
	@Override
	protected String getElementStringToMatch(ITextElement element)
			throws ConQATException {
		return element.getTextContent();
	}

	/** {@inheritDoc} */
	@Override
	protected String defaultLogCaption() {
		return "Element content";
	}

}