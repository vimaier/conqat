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
package org.conqat.engine.html_presentation.pattern;

import org.conqat.lib.commons.treemap.IDrawingPattern;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;

/**
 * A pattern having diagonal stripes. This is both a pattern and a processor for
 * creating the pattern.
 * 
 * @author Benjamin Hummel
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: A0E579D017E1622ACACCAE07A3A4FF2B
 */
@AConQATProcessor(description = "Defines a pattern of diagonal stripes.")
public class DiagonalStripePattern extends ConQATProcessorBase implements
		IDrawingPattern {

	/** The width of the stripes. */
	private int stripeWidth = 2;

	/** The overall width of the pattern. */
	private int width = 6;

	/** Set the patterns parameters. */
	@AConQATParameter(name = "parameters", maxOccurrences = 1, description = ""
			+ "Set the parameters for this pattern.")
	public void setParameters(
			@AConQATAttribute(name = "width", description = "The width of the stripes in pixels [2].")
			int stripeWidth,
			@AConQATAttribute(name = "spacing", description = "The free space between the stripes in pixels [4].")
			int stripeSpacing) throws ConQATException {
		if (stripeWidth <= 0 || stripeSpacing <= 0) {
			throw new ConQATException("Parameters must be positive!");
		}

		this.stripeWidth = stripeWidth;
		this.width = stripeSpacing + stripeWidth;
	}

	/** {@inheritDoc} */
	@Override
	public IDrawingPattern process() {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isForeground(int x, int y) {
		return (x + y) % width < stripeWidth;
	}
}