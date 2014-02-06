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
package org.conqat.engine.html_presentation.util;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.html_presentation.IPageDescriptor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: deissenb $
 * @version $Rev: 39913 $
 * @ConQAT.Rating GREEN Hash: 85F81FC0EB83D2BAFCA8C0F41D2200FC
 */
@AConQATProcessor(description = "Collections page descriptors and lists of page descriptors into a single descriptor list.")
public class PageDescriptorCollector extends ConQATProcessorBase {

	/** The resulting list. */
	private final PageDescriptorList result = new PageDescriptorList();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "descriptor", description = "Adds a descriptor to this collector.")
	public void addPageDescriptor(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) IPageDescriptor descriptor) {
		result.add(descriptor);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "descriptor-list", description = "Adds a descriptor list to this collector.")
	public void addPageDescriptorList(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) PageDescriptorList list) {
		result.addAll(list);
	}

	/** {@inheritDoc} */
	@Override
	public PageDescriptorList process() {
		return result;
	}
}
