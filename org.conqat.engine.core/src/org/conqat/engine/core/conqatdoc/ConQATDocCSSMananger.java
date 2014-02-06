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
package org.conqat.engine.core.conqatdoc;

import static org.conqat.lib.commons.html.ECSSProperty.BACKGROUND_COLOR;
import static org.conqat.lib.commons.html.ECSSProperty.BORDER_COLLAPSE;
import static org.conqat.lib.commons.html.ECSSProperty.COLOR;
import static org.conqat.lib.commons.html.ECSSProperty.FONT_FAMILY;
import static org.conqat.lib.commons.html.ECSSProperty.FONT_SIZE;
import static org.conqat.lib.commons.html.ECSSProperty.FONT_WEIGHT;
import static org.conqat.lib.commons.html.ECSSProperty.LINE_HEIGHT;
import static org.conqat.lib.commons.html.ECSSProperty.MARGIN_TOP;
import static org.conqat.lib.commons.html.ECSSProperty.PADDING_BOTTOM;
import static org.conqat.lib.commons.html.ECSSProperty.PADDING_LEFT;
import static org.conqat.lib.commons.html.ECSSProperty.PADDING_RIGHT;
import static org.conqat.lib.commons.html.ECSSProperty.PADDING_TOP;
import static org.conqat.lib.commons.html.ECSSProperty.TEXT_ALIGN;
import static org.conqat.lib.commons.html.ECSSProperty.TEXT_DECORATION;
import static org.conqat.lib.commons.html.ECSSProperty.VERTICAL_ALIGN;
import static org.conqat.lib.commons.html.EHTMLElement.A;
import static org.conqat.lib.commons.html.EHTMLElement.BODY;
import static org.conqat.lib.commons.html.EHTMLElement.H1;
import static org.conqat.lib.commons.html.EHTMLElement.H2;
import static org.conqat.lib.commons.html.EHTMLElement.PRE;
import static org.conqat.lib.commons.html.EHTMLElement.TABLE;
import static org.conqat.lib.commons.html.EHTMLElement.TD;
import static org.conqat.lib.commons.html.EHTMLElement.TH;
import static org.conqat.lib.commons.html.EHTMLElement.UL;

import java.io.PrintStream;

import org.conqat.lib.commons.html.CSSDeclarationBlock;
import org.conqat.lib.commons.html.CSSManagerBase;
import org.conqat.lib.commons.html.ECSSPseudoClass;

/**
 * The CSS manager used for ConQATDoc. It is mostly used to setup default styles
 * for HTML elements.
 * 
 * @author hummelb
 * @author $Author: juergens $
 * @version $Rev: 35194 $
 * @ConQAT.Rating GREEN Hash: 1E08520523D43AAC4BFBC82F628A87A8
 */
public class ConQATDocCSSMananger extends CSSManagerBase {

	/** Single instance of this singleton. */
	private static final ConQATDocCSSMananger instance = new ConQATDocCSSMananger();

	/** Return the single instance of this manager. */
	public static ConQATDocCSSMananger getInstance() {
		return instance;
	}

	/** Create and register all CSS blocks used. */
	private ConQATDocCSSMananger() {

		CSSDeclarationBlock fontBase = new CSSDeclarationBlock(FONT_FAMILY,
				"verdana, helvetica, sans-serif");

		addDefaultDeclaration(BODY, new CSSDeclarationBlock(fontBase,
				BACKGROUND_COLOR, "white", FONT_SIZE, "13px"));

		addDefaultDeclaration(H1, new CSSDeclarationBlock(fontBase, COLOR,
				"#666666", FONT_SIZE, "26px"));

		addDefaultDeclaration(H2,
				new CSSDeclarationBlock(fontBase, FONT_SIZE, "13px",
						LINE_HEIGHT, "18px", COLOR, "black", BACKGROUND_COLOR,
						"white", PADDING_TOP, "6px", PADDING_BOTTOM, "6px")
						.setMargin("0px"));

		addDefaultDeclaration(PRE, new CSSDeclarationBlock(BACKGROUND_COLOR,
				"#CCCCCC").setPadding("6px"));

		addDefaultDeclaration(TABLE, new CSSDeclarationBlock(BORDER_COLLAPSE,
				"collapse", FONT_SIZE, "13px").setBorder("1px", "solid",
				"#CCCCCC"));

		CSSDeclarationBlock td = new CSSDeclarationBlock(fontBase,
				VERTICAL_ALIGN, "top", PADDING_LEFT, "3px", PADDING_RIGHT,
				"3px", PADDING_TOP, "1px", PADDING_BOTTOM, "1px").setBorder(
				"1px", "dotted", "#CCCCCC");
		addDefaultDeclaration(TD, td);

		addDefaultDeclaration(TH, new CSSDeclarationBlock(td, BACKGROUND_COLOR,
				"#666666", COLOR, "white", TEXT_ALIGN, "left", FONT_SIZE,
				"12px", FONT_WEIGHT, "bold").setPadding("3px"));

		addDefaultDeclaration(A, new CSSDeclarationBlock(fontBase, COLOR,
				"#639CCE", TEXT_DECORATION, "none"));

		addDefaultDeclaration(UL, new CSSDeclarationBlock(MARGIN_TOP, "0px;"));

		addDefaultDeclaration(A, ECSSPseudoClass.HOVER,
				new CSSDeclarationBlock(TEXT_DECORATION, "underline"));
	}

	/** Change visibility. */
	@Override
	public void writeOut(PrintStream ps) {
		super.writeOut(ps);
	}
}