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
package org.conqat.engine.html_presentation.chart.annotation;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleInsets;

import org.conqat.engine.commons.CommonUtils;

/**
 * Base class event annotations. Event annotations are used to display date
 * specific texts in a chart.
 * 
 * @author ladmin
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: D7ECFFEDC603026287EB842269FF0BFD
 */
public abstract class EventAnnotationBase {

	/** Padding around annotation marker labels */
	private static final int PAD = 7;

	/** Date format used in {@link #toString()} */
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			CommonUtils.DEFAULT_DATE_FORMAT_PATTERN);

	/** Annotation message */
	private final String message;

	/** String that identifies the message. */
	private String mnemonic;

	/** Constructor */
	protected EventAnnotationBase(String message) {
		this.message = message;
	}

	/** Returns description. */
	public String getMessage() {
		return message;
	}

	/** Returns date. */
	public abstract Date getDate();

	/** Returns mnemonic. */
	public String getMnemonic() {
		return mnemonic;
	}

	/** Sets mnemonic. */
	/* package */void setMnemonic(String mnemonic) {
		this.mnemonic = mnemonic;
	}

	/** Factory method that creates the marker for the annotation */
	public void createMarker(XYPlot plot) {
		Marker marker = doCreateMarker();
		marker.setLabel(getMnemonic());
		marker.setLabelOffset(new RectangleInsets(PAD, PAD, PAD, PAD));
		plot.addDomainMarker(marker, Layer.BACKGROUND);
	}

	/**
	 * Template method that deriving classes override to create the actual
	 * marker
	 */
	protected abstract Marker doCreateMarker();

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return getMnemonic() + ": " + getMessage() + " ("
				+ dateFormat.format(getDate()) + ")";
	}
}