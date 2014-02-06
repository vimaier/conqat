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
package org.conqat.engine.html_presentation.treemap;

import static org.conqat.engine.html_presentation.CSSMananger.DEFAULT_FONT;
import static org.conqat.lib.commons.html.ECSSProperty.HEIGHT;
import static org.conqat.lib.commons.html.ECSSProperty.WIDTH;

import java.awt.Color;
import java.util.regex.Pattern;

import org.conqat.engine.commons.CommonUtils;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.commons.node.IConQATNode;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.html_presentation.image.IImageDescriptor;
import org.conqat.lib.commons.html.CSSDeclarationBlock;
import org.conqat.lib.commons.string.StringUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 35946 $
 * @ConQAT.Rating GREEN Hash: 774F059DC2B5C7722E0F599FFA90A795
 */
@AConQATProcessor(description = "Creates tree map representation of IConQATNode hierarchies.")
public class TreeMapCreator extends ConQATProcessorBase {

	/** CSS class used for the single colored blocks in the color legend. */
	public static CSSDeclarationBlock MAP_LEGEND = new CSSDeclarationBlock(
			DEFAULT_FONT).setBorderStyle("none").setMargin("8px");

	/** Style used for color squares for legends. */
	public static final CSSDeclarationBlock COLOR_SQUARE_STYLE = new CSSDeclarationBlock(
			WIDTH, "30px", HEIGHT, "20px").setBorder("1px", "solid", "black");

	/** The nodes to display. */
	private IConQATNode root;

	/** The key used for reading the color. */
	private String colorKey = "color";

	/** The key used for reading the size. */
	private String sizeKey = null;

	/** The key used for reading the pattern's color. */
	private String patternColorKey = "pattern-color";

	/** The key used for reading the drawing pattern. */
	private String drawingPatternKey = "pattern";

	/** Height for the cushions. */
	private double cushionHeight = -1;

	/** Height for the cushions. */
	private double cushionScale = -1;

	/** Color for frames around top level elements */
	private Color topLevelFrameColor = null;

	/** Color for node text */
	private Color textColor = null;

	/**
	 * Pattern used to split tree map node names that are too long to be
	 * displayed into parts from which the last part is chosen for display (if
	 * this is null, name is not split)
	 */
	private Pattern separationPattern = null;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.INPUT_NAME, minOccurrences = 1, maxOccurrences = 1, description = "Node hierarchy to visualize.")
	public void setInput(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) IConQATNode root) {

		this.root = root;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "size", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Set the key used to retrieve the size of a node. "
			+ "If no key is given, each node will be weighted with 1, i.e. just the number of leaves is counted.")
	public void setSizeKey(
			@AConQATAttribute(name = ConQATParamDoc.READKEY_KEY_NAME, description = ConQATParamDoc.READKEY_KEY_DESC) String key) {

		sizeKey = key;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "color", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Set the key used to retrieve the color of a node. "
			+ "If no key is given, the value 'color' is used as key. ")
	public void setColorKey(
			@AConQATAttribute(name = ConQATParamDoc.READKEY_KEY_NAME, description = ConQATParamDoc.READKEY_KEY_DESC) String key) {

		colorKey = key;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "pattern-color", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Set the key used to retrieve the color for the pattern of a node. "
			+ "If no key is given, the value 'pattern-color' is used as key. ")
	public void setPatternColorKey(
			@AConQATAttribute(name = ConQATParamDoc.READKEY_KEY_NAME, description = ConQATParamDoc.READKEY_KEY_DESC) String key) {

		patternColorKey = key;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "drawing-pattern", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Set the key used to retrieve the drawing pattern of a node. "
			+ "If no key is given, the value 'pattern' is used as key. ")
	public void setPatternKey(
			@AConQATAttribute(name = ConQATParamDoc.READKEY_KEY_NAME, description = ConQATParamDoc.READKEY_KEY_DESC) String key) {

		drawingPatternKey = key;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "frames", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Determines whether top level elements (i.e. packages, directories) should be made more visible using colored frames. Default is false.")
	public void setDrawFrames(
			@AConQATAttribute(name = "color", description = "Color of the frame") Color frameColor) {
		topLevelFrameColor = frameColor;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "cushion", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Setup the parameters for cushion layout. Cushion layout adds spot lights to make the "
			+ "hierarchy of the tree more visible. If this parameter is omitted a flat layout will be used.")
	public void setCushions(
			@AConQATAttribute(name = "height", defaultValue = "0.5", description = ""
					+ "The relative height of the cushions. Valid values are between 0 and 1.") double h,
			@AConQATAttribute(name = "scale", defaultValue = "0.85", description = ""
					+ "Scale value for the cushion height. Using a smaller value makes the cushion effect less visible for elements deeper in the tree. Valid values are between 0 and 1.") double f)
			throws ConQATException {

		if (h < 0 || h > 1) {
			throw new ConQATException(
					"Height for cushion must be between 0 and 1!");
		}
		if (f < 0 || f > 1) {
			throw new ConQATException(
					"Scale factor for cushion must be between 0 and 1!");
		}

		cushionHeight = h;
		cushionScale = f;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "text", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Determines whether names of the tree map nodes are displayed. Don't specify a color to switch this off")
	public void setDrawText(
			@AConQATAttribute(name = "color", description = "Color of the text") Color textColor,
			@AConQATAttribute(name = "separation-regexp", description = "Regexp used to split tree map node names that are too long "
					+ "to be displayed into parts from which the last part is chosen for display"
					+ "(if this is null, name is not split). Example: Use '[./]' to split at points and forward slashes. ") String separationRegex)
			throws ConQATException {
		this.textColor = textColor;

		if (StringUtils.isEmpty(separationRegex)) {
			separationPattern = null;
			return;
		}

		separationPattern = CommonUtils.compilePattern(separationRegex);
	}

	/** {@inheritDoc} */
	@Override
	public IImageDescriptor process() {
		return new TreeMapImageDescriptor(cushionHeight, cushionScale,
				topLevelFrameColor, textColor, separationPattern,
				new ColorKeyedTreeMapNode(root, sizeKey, colorKey,
						patternColorKey, drawingPatternKey),
				NodeUtils.getSummary(root));
	}

}