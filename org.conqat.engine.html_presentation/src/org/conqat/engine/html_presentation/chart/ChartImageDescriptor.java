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
package org.conqat.engine.html_presentation.chart;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.conqat.engine.html_presentation.image.ITooltipDescriptor;
import org.conqat.engine.html_presentation.image.ImageDescriptorBase;
import org.jfree.chart.JFreeChart;

/**
 * Descriptor for JFreeChart charts.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: A62D3739BBC6E8584DB87624F2CED934
 */
public class ChartImageDescriptor extends ImageDescriptorBase {

	/** Name of the chart icon. */
	/* package */static final String CHART_ICON_NAME = "chart.gif";

	/** The chart. */
	private final JFreeChart chart;

	/** The preferred size. */
	private final Dimension preferredSize;

	/** Constructor. */
	public ChartImageDescriptor(JFreeChart chart, Dimension preferredSize) {
		super(CHART_ICON_NAME);
		this.chart = chart;
		this.preferredSize = preferredSize;

	}

	/** {@inheritDoc} */
	@Override
	public Dimension getPreferredSize() {
		return preferredSize;
	}

	/** {@inheritDoc} */
	@Override
	public void draw(Graphics2D graphics, int width, int height) {
		chart.draw(graphics, new Rectangle2D.Double(0, 0, width, height));
	}

	/** {@inheritDoc} */
	@Override
	public boolean isVectorFormatSupported() {
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public ITooltipDescriptor<Object> getTooltipDescriptor(int width, int height) {
		return null;
	}
}
