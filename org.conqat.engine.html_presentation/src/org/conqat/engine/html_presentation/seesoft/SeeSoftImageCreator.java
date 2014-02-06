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
import java.awt.Dimension;
import java.util.Collections;
import java.util.List;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.findings.Finding;
import org.conqat.engine.commons.node.DisplayList;
import org.conqat.engine.commons.node.NodeNameComparator;
import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.commons.util.ConQATInputProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.html_presentation.image.EImageFormat;
import org.conqat.engine.html_presentation.image.IImageDescriptor;
import org.conqat.engine.resource.findings.FindingsAnnotatorBase;
import org.conqat.engine.resource.util.ElementUniformPathComparator;
import org.conqat.engine.sourcecode.resource.ITokenElement;
import org.conqat.engine.sourcecode.resource.ITokenResource;
import org.conqat.engine.sourcecode.resource.TokenElementUtils;
import org.conqat.lib.commons.color.IColorProvider;
import org.conqat.lib.commons.color.RandomColorProvider;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: juergens $
 * @version $Rev: 44143 $
 * @ConQAT.Rating GREEN Hash: CF56F6006126F999EC4408F0C150BFE8
 */
@AConQATProcessor(description = "Create SeeSoft View for code. Consult processor parameters to tweak appearance.")
public class SeeSoftImageCreator extends
		ConQATInputProcessorBase<ITokenResource> {

	/** Default key under which for suppress findings flag is stored. */
	public static final String SUPPRESS_FINDINGS_KEY = "SupressFindings";

	/** Parameters that influence appearance */
	private final SeeSoftParameterInfo paramInfo = new SeeSoftParameterInfo();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = ConQATParamDoc.FINDING_PARAM_NAME, description = "Keys for findings")
	public void setFinding(
			@AConQATAttribute(name = ConQATParamDoc.FINDING_KEY_NAME, description = ""
					+ "The key under which findings are stored.", defaultValue = FindingsAnnotatorBase.KEY) String findingsKey,
			@AConQATAttribute(name = "suppressKey", defaultValue = SUPPRESS_FINDINGS_KEY, description = ""
					+ "Key under which suppress findings flag is stored.") String suppressFindingsKey) {
		paramInfo.setFindingsKey(findingsKey);
		paramInfo.setSuppressFindingsKey(suppressFindingsKey);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "compress", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Factor by which image is compressed.")
	public void setCompressionFactor(
			@AConQATAttribute(name = "factor", description = "If greater than 1, SeeSoft is compressed by only displaying every line and character for which offset MOD n == 0.") int compressionFactor) {
		paramInfo.setCompressionFactor(compressionFactor);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "background", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Background color for the image. ")
	public void setBackgroundColor(
			@AConQATAttribute(name = "color", description = "Default is white.") Color backgroundColor) {
		paramInfo.setBackgroundColor(backgroundColor);
	}

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "finding-colors", attribute = "provider", optional = true, description = ""
			+ "Color provider for findings. Default is to produce random colors on the level of finding groups.")
	public IColorProvider<Finding> findingsColorProvider = new RandomColorProvider<Finding>() {
		/** Group colors by finding group. */
		@Override
		protected Object getKey(Finding finding) {
			return finding.getParent().getName();
		}
	};

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "syntax-highlighting", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Enable syntax highlighting.")
	public void setEnableSyntaxHighlighting(
			@AConQATAttribute(name = "enable", description = "Enabled by default.") boolean enableSyntaxHighlighting) {
		paramInfo.setSyntaxHighlightingEnabled(enableSyntaxHighlighting);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "findings-min-height", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "Minimal drawing height for findings.")
	public void setFindingsMinHeight(
			@AConQATAttribute(name = "value", description = "Default is 3 pixels.") int findingsMinHeight) {
		paramInfo.setFindingsMinHeight(findingsMinHeight);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "dimensions", minOccurrences = 1, maxOccurrences = 1, description = EImageFormat.DOC)
	public void setDimensions(
			@AConQATAttribute(name = "width", description = "The width of the image.") int width,
			@AConQATAttribute(name = "height", description = "The height of the image.") int height,
			@AConQATAttribute(name = "columnWidth", description = "The width of individual tiles to render.", defaultValue = "80") int columnWidth,
			@AConQATAttribute(name = "paddingHorizontal", description = "The horizontal padding of columns to each other.", defaultValue = "5") int paddingHorizontal,
			@AConQATAttribute(name = "paddingVertical", description = "The vertical padding of files to each other.", defaultValue = "5") int paddingVertical)
			throws ConQATException {

		EImageFormat.checkDimensions(width, height);

		paramInfo.setWidth(width);
		paramInfo.setHeight(height);
		paramInfo.setColumnWidth(columnWidth);
		paramInfo.setPadding(new Dimension(paddingHorizontal, paddingVertical));
	}

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "sort-by-local-name", attribute = "value", optional = true, description = ""
			+ "If set to true, elements in the scope are sorted by their local name. Default is to sort by uniform path.")
	public boolean sortByLocalName = false;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "findings-opaque", minOccurrences = 0, maxOccurrences = 1, description = ""
			+ "If set to true, no text is rendered on findings.")
	public void setFindingsOpaque(
			@AConQATAttribute(name = "value", description = "Default value is false.") boolean findingsOpaque) {
		paramInfo.setFindingsOpaque(findingsOpaque);
	}

	/** {@inheritDoc} */
	@Override
	public IImageDescriptor process() {
		List<ITokenElement> tokenElements = TokenElementUtils
				.listTokenElements(input);
		DisplayList displayList = NodeUtils.getDisplayList(input);

		if (sortByLocalName) {
			Collections.sort(tokenElements, new NodeNameComparator());
		} else {
			Collections.sort(tokenElements, new ElementUniformPathComparator());
		}

		return new SeeSoftImageDescriptor(tokenElements, getLogger(),
				paramInfo, displayList, findingsColorProvider);
	}

}