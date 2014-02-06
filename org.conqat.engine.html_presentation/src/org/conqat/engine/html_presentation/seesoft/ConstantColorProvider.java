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
package org.conqat.engine.html_presentation.seesoft;

import java.awt.Color;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.lib.commons.color.IColorProvider;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: juergens $
 * @version $Rev: 43653 $
 * @ConQAT.Rating GREEN Hash: 4EF5446500C068A0B8CB414DD65BA79E
 */
@AConQATProcessor(description = "Color provider always returning the same (configurable) color.")
public class ConstantColorProvider extends ConQATProcessorBase implements
		IColorProvider<Object> {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "color", attribute = "value", description = "The color to provide.")
	public Color color;

	/** {@inheritDoc} */
	@Override
	public Color getColor(Object object) {
		return color;
	}

	/** {@inheritDoc} */
	@Override
	public ConstantColorProvider process() {
		return this;
	}
}