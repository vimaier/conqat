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
package org.conqat.engine.html_presentation.chart;

import org.jfree.chart.plot.PlotOrientation;

/**
 * A plot orientation. This wraps {@link PlotOrientation} into an enum so we can
 * expose it in ConQAT.
 * 
 * @author $Author: pfaller $
 * @version $Rev: 37970 $
 * @ConQAT.Rating GREEN Hash: E97A6AF4512BA10B572E9CF921BF5BA4
 */
public enum EPlotOrientation {

	/** Vertical orientation. */
	VERTICAL(PlotOrientation.VERTICAL),

	/** Horizontal orientation. */
	HORIZONTAL(PlotOrientation.HORIZONTAL);

	/** The JFreeChart orientation. */
	private final PlotOrientation orientation;

	/** Constructor. */
	private EPlotOrientation(PlotOrientation orientation) {
		this.orientation = orientation;
	}

	/** Returns the JFreeChart orientation. */
	public PlotOrientation getOrientation() {
		return orientation;
	}
}
