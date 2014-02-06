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
package org.conqat.engine.architecture.output;

import org.conqat.engine.architecture.scope.ArchitectureDefinition;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.html_presentation.image.IImageDescriptor;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 4AED447136C366CBB1B1D2A5A1C98DEE
 */
@AConQATProcessor(description = "A processor to output bitmap and vector graphics of an architecture analysis.")
public class ArchitectureImageCreator extends ConQATProcessorBase {

	/** The architecture we will render. */
	private ArchitectureDefinition arch;

	/** The render mode used. */
	private ERenderMode renderMode;

	/** Decorator used for components. */
	private IComponentDecorator decorator = new DefaultComponentDecorator();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.INPUT_NAME, minOccurrences = 1, maxOccurrences = 1, description = "Architecture to visualize.")
	public void setInput(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) ArchitectureDefinition arch) {
		this.arch = arch;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "render", minOccurrences = 1, maxOccurrences = 1, description = "Sets the render mode used.")
	public void setRenderMode(
			@AConQATAttribute(name = "mode", description = "The render mode (POLICIES, ASSESSMENT, VIOLATIONS, VIOLATIONS_AND_TOLERATIONS)") ERenderMode renderMode) {
		this.renderMode = renderMode;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "component-decorator", minOccurrences = 0, maxOccurrences = 1, description = "Sets the component decorator.")
	public void setComponentDecorator(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) IComponentDecorator decorator) {
		this.decorator = decorator;
	}

	/** {@inheritDoc} */
	@Override
	public IImageDescriptor process() {
		return new ArchitectureImageDescriptor(arch, decorator, renderMode);
	}
}