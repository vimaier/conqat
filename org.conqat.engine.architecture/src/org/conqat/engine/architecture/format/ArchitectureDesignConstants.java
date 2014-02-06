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
package org.conqat.engine.architecture.format;

import java.awt.Color;

/**
 * Reusable resources and constants like colors, fonts, alpha-values and
 * pixel-sizes defining the visual design of the edit-parts and shapes. This is
 * reused in arch.edit.
 * 
 * @author schwitze
 * @author $Author: goede $
 * @version $Rev: 38038 $
 * @ConQAT.Rating GREEN Hash: 423F54942CE5117FDA74C77309CB32CA
 */
public class ArchitectureDesignConstants {

	/** Corner radius for the rounded rectangles. */
	public static final int CORNER_RADIUS = 10;

	/** Margin for the editor. */
	public static final int EDITOR_MARGIN = 5;

	/** The left/right margin of labels. */
	public static final int HORIZONTAL_LABEL_MARGIN = 8;

	/** Pixels to place a label below a border. */
	public static final int VERTICAL_LABEL_MARGIN = 5;

	/** the height of the captions label */
	public static final int LABEL_HEIGHT = 15;

	/** the height of the stereotype label */
	public static final int STEREOTYPE_LABEL_HEIGHT = 15;

	/** The foreground color of Components. */
	public static final Color COLOR_COMPONENT_FOREGROUND = color(0, 0, 0);

	/** The background color of Components. */
	public static final Color COLOR_COMPONENT_BACKGROUND_LIGHT = color(255,
			255, 255);

	/** The background color of Components. */
	public static final Color COLOR_COMPONENT_BACKGROUND_DARK = color(230, 230,
			255);

	/** The label used for components with stereotype public */
	public static final String PUBLIC_STEREOTYPE_LABEL = "\u00ABSYS PUBLIC\u00BB";
	
	/** The label used for components with stereotype component public */
	public static final String COMPONENT_PUBLIC_STEREOTYPE_LABEL = "\u00ABCOMP PUBLIC\u00BB";

	/** Creates a new color. */
	public static Color color(int r, int g, int b) {
		return new Color(r, g, b);
	}
}