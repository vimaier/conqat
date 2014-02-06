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
package org.conqat.engine.core.conqatdoc.layout;

import java.awt.Color;

/**
 * Reusable resources and constants like colors, font sizes, alpha-values and
 * pixel-sizes defining the visual design of the layouted blocks. We define some
 * more constants here than are actually used by the rendering code in
 * ConQATDoc, as we reuse this class in cq.edit where the additional constants
 * are needed. The goal was to keep the constants in one place (maybe we even
 * extend the rendering here, so we might need them later).
 * 
 * @author $Author: juergens $
 * @version $Rev: 35194 $
 * @ConQAT.Rating GREEN Hash: 1F1D5AA9F74545640F21E307F7CAF098
 */
public class DesignConstants {

	/**
	 * The translucency of the top-down gradient from white to background color.
	 */
	public static final int GRADIENT_ALPHA = 200;

	/**
	 * One of the colors to use for the gradient (the other one is the
	 * respective background color of the shape)
	 */
	public static final Color GRADIENT_COLOR = Color.WHITE;

	/**
	 * Insets to use at edges of parent shape to place ports (i.e. ins/outs)
	 * "overlapping" there.
	 */
	public static final int PORT_INSET = 7;

	/** Line width for connections. */
	public static final int LINE_WIDTH = 1;

	/** Line width for connections to star ports. */
	public static final int STAR_LINE_WIDTH = 2;

	/** The default size for ports (e.g. parameters/outputs). */
	public static final int DEFAULT_PORT_SIZE = 10;

	/** Margin for the diagram. */
	public static final int MARGIN = 5;

	/** The top/bottom margin for ports (e.g. parameters/outputs). */
	public static final int PORT_MARGIN = 2;

	/** The width of space between ports(e.g. parameters/outputs). */
	public static final int PORT_SPACING = 2;

	/** The corner radius ("roundness") for ports (e.g. parameters/outputs). */
	public static final int PORT_CORNER = 1;

	/** The left/right margin of labels. */
	public static final int HORIZONTAL_LABEL_MARGIN = 14;

	/** Pixels to place a label below the a border. */
	public static final int VERTICAL_LABEL_MARGIN = 5;

	/** The font size for titles. */
	public static final int TITLE_FONT_SIZE = 11;

	/** The font size that is used for the labels of connections. */
	public static final int CONNECTION_LABEL_FONT_SIZE = 11;

	/** The width of the complete unit. */
	public static final int UNIT_WIDTH = 180;

	/** The height of the complete unit. */
	public static final int UNIT_HEIGHT = 60;

	/** Corner radius for units. */
	public static final int UNIT_CORNER = 10;

	/** The additional left indent of the name label or a unit. */
	public static final int NAME_LABEL_LEFT_INDENT = 20;

	/** The width of an specification input or output. */
	public static final int SPEC_IO_WIDTH = 100;

	/** The height of an specification input or output. */
	public static final int SPEC_IO_HEIGHT = 40;

	/** Creates a new color with given rgb-values in the default display. */
	public static Color color(int r, int g, int b) {
		return new Color(r, g, b);
	}

	/** The foreground color of outputs. */
	public static final Color COLOR_OUTPUT_FOREGROUND = color(39, 69, 37);

	/** The background color of outputs. */
	public static final Color COLOR_OUTPUT_BACKGROUND = color(89, 156, 86);

	/** The foreground color of parameters. */
	public static final Color COLOR_PARAMETER_FOREGROUND = color(177, 108, 5);

	/** The background color of parameters. */
	public static final Color COLOR_PARAMETER_BACKGROUND = color(250, 154, 10);

	/** The foreground color of conditional port. */
	public static final Color COLOR_CONDITIONAL_FOREGROUND = color(0, 90, 160);

	/** The background color of conditional port. */
	public static final Color COLOR_CONDITIONAL_BACKGROUND = color(0, 140, 250);

	/** The foreground color of negated conditional port. */
	public static final Color COLOR_CONDITIONAL_NEGATED_FOREGROUND = color(170,
			30, 0);

	/** The background color of negated conditional port. */
	public static final Color COLOR_CONDITIONAL_NEGATED_BACKGROUND = color(250,
			40, 30);

	/** The foreground color of processors. */
	public static final Color COLOR_PROCESSOR_FOREGROUND = color(14, 46, 88);

	/** The background color of processors. */
	public static final Color COLOR_PROCESSOR_BACKGROUND = color(159, 186, 221);

	/** The foreground color of blocks. */
	public static final Color COLOR_BLOCK_FOREGROUND = color(34, 45, 45);

	/** The background color of blocks. */
	public static final Color COLOR_BLOCK_BACKGROUND = color(156, 180, 179);

	/** The foreground color of spec attributes. */
	public static final Color COLOR_SPEC_ATTR_FOREGROUND = color(9, 52, 104);

	/** The background color of spec attributes. */
	public static final Color COLOR_SPEC_ATTR_BACKGROUND = color(15, 88, 179);

	/** The foreground color of spec outputs. */
	public static final Color COLOR_SPEC_OUT_FOREGROUND = color(9, 52, 104);

	/** The background color of spec outputs. */
	public static final Color COLOR_SPEC_OUT_BACKGROUND = color(153, 196, 247);

	/** The foreground color of spec parameters. */
	public static final Color COLOR_SPEC_PARAM_FOREGROUND = color(9, 52, 104);

	/** The background color of spec parameters. */
	public static final Color COLOR_SPEC_PARAM_BACKGROUND = color(153, 196, 247);

	/** The foreground color of connections. */
	public static final Color COLOR_CONNECTION_FOREGROUND = color(0, 0, 0);

	/** The foreground color of connections for star ports. */
	public static final Color COLOR_STAR_CONNECTION_FOREGROUND = color(110,
			169, 108);
}