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

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;

import org.conqat.engine.architecture.scope.ComponentNode;

/**
 * This interface describes a strategy that is used by the
 * {@link ArchitectureImageCreator} to visualize information in architecture
 * components.
 * 
 * @author Florian Deissenboeck
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 2172850177AE35DBD2402346821589FF
 */
public interface IComponentDecorator {

	/**
	 * This method can be applied to draw additional information in a component
	 * node. The graphics object is translated so drawing can begin at
	 * coordinate 0,0. Also, there's a clipping defined so decorations cannot be
	 * drawn outside a designated decoration area. This area is as wide as the
	 * line below the label. The height of the area is defined by the ascent of
	 * the applied font so it can be used to display one line of text or other
	 * drawings with corresponding height.
	 */
	public void decorate(ComponentNode component, Graphics2D graphics);

	/** Obtain the paint used to fill a component. This may not return null. */
	public Paint obtainFillPaint(ComponentNode component);

	/**
	 * Obtain the paint used to draw the outline of a component. This may not
	 * return null.
	 */
	public Paint obtainOutlinePaint(ComponentNode component);

	/**
	 * Obtain the stroke used to draw the outline of a component. This may not
	 * return null.
	 */
	public Stroke obtainStroke(ComponentNode component);

}