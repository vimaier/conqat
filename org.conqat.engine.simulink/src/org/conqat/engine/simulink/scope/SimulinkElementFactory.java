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
package org.conqat.engine.simulink.scope;

import java.io.StringReader;

import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IContentAccessor;
import org.conqat.engine.resource.text.TextElementFactory;
import org.conqat.lib.simulink.builder.SimulinkModelBuilder;
import org.conqat.lib.simulink.builder.SimulinkModelBuildingException;
import org.conqat.lib.simulink.model.SimulinkModel;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: D7D5623F9FFD0FA6E44D36B36A6F420F
 */
@AConQATProcessor(description = "Factory for token elements.")
public class SimulinkElementFactory extends TextElementFactory {

	/**
	 * {@inheritDoc}
	 * 
	 * @throws ConQATException
	 *             if the model could not be built
	 */
	@Override
	public ISimulinkElement create(IContentAccessor accessor)
			throws ConQATException {
		try {
			SimulinkModel model = buildModel(accessor);
			return new SimulinkElement(accessor, encoding, model);
		} catch (SimulinkModelBuildingException ex) {
			throw new ConQATException("Could not build model for "
					+ accessor.getLocation() + ": " + ex.getMessage(), ex);
		}
	}

	/** Build Simulink model. */
	private SimulinkModel buildModel(IContentAccessor accessor)
			throws ConQATException, SimulinkModelBuildingException {
		// we do not reuse the code from the TextElement here, as the
		// TextElement also performs normalization of new lines, which can
		// affect the scanner
		String content = new String(accessor.getContent(), encoding);
		SimulinkModelBuilder modelBuilder = new SimulinkModelBuilder(
				new StringReader(content), getLogger(), accessor
						.getUniformPath());
		return modelBuilder.buildModel();
	}
}