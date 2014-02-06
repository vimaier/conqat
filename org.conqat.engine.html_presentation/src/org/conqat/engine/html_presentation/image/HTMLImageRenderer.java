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
package org.conqat.engine.html_presentation.image;

import static org.conqat.engine.html_presentation.CSSMananger.IMAGE_STYLE;
import static org.conqat.lib.commons.html.EHTMLAttribute.CLASS;
import static org.conqat.lib.commons.html.EHTMLAttribute.HREF;
import static org.conqat.lib.commons.html.EHTMLAttribute.ID;
import static org.conqat.lib.commons.html.EHTMLAttribute.ONCLICK;
import static org.conqat.lib.commons.html.EHTMLAttribute.SRC;
import static org.conqat.lib.commons.html.EHTMLAttribute.STYLE;
import static org.conqat.lib.commons.html.EHTMLElement.A;
import static org.conqat.lib.commons.html.EHTMLElement.DIV;
import static org.conqat.lib.commons.html.EHTMLElement.IMG;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.html_presentation.BundleContext;
import org.conqat.engine.html_presentation.IPageDescriptor;
import org.conqat.engine.html_presentation.PageWriter;
import org.conqat.engine.html_presentation.util.ImageCreatingLayouterBase;
import org.conqat.lib.commons.collections.PairList;
import org.conqat.lib.commons.color.ColorUtils;
import org.conqat.lib.commons.color.ECCSMColor;
import org.conqat.lib.commons.html.CSSDeclarationBlock;
import org.conqat.lib.commons.html.ECSSProperty;
import org.conqat.lib.commons.html.EHTMLElement;
import org.conqat.lib.commons.string.StringUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: goede $
 * @version $Rev: 45268 $
 * @ConQAT.Rating YELLOW Hash: CBF5642062584F1D84073852143FC8BB
 */
@AConQATProcessor(description = "This processor renders an image descriptor to an HTML page.")
public class HTMLImageRenderer extends ImageCreatingLayouterBase {

	/** The image descriptors and names. */
	private final PairList<IImageDescriptor, String> descriptors =
	        new PairList<IImageDescriptor, String>();

	/** The width of the image generated. */
	private int width = EImageFormat.FLEXIBLE;

	/** The height of the image generated. */
	private int height = EImageFormat.FLEXIBLE;

	/** Color of the dynamic frames used for tool tips. */
	private Color dynamicFrameColor = ECCSMColor.BLUE.getColor();

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "tooltips", attribute = "show", optional = true, description = "If this is true, JavaScript-based tooltips are included (default is true).")
	public boolean displayTooltips = true;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "page-link", attribute = "ref", optional = true, description = ""
	        + "If this is provided, the image will act as a link to the given page. Note that in this case no tooltips will be displayed.")
	public IPageDescriptor linkedPage = null;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "image", minOccurrences = 1, description = "Image descriptors to render. "
	        + "If more than one image descriptor is provided, all information (such as tooltips) are based on the first image and links to switch between the images are created.")
	public void addImageDescriptor(
	        @AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = "The image descriptor.") IImageDescriptor descriptor,
	        @AConQATAttribute(name = "name", defaultValue = "Image", description = "The name used for the links that switch between the images.") String name) {
		descriptors.add(descriptor, name);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "dimensions", minOccurrences = 0, maxOccurrences = 1, description = EImageFormat.DOC)
	public void setDimensions(
	        @AConQATAttribute(name = "width", description = "The width of the image.", defaultValue = EImageFormat.FLEXIBLE_STRING) int width,
	        @AConQATAttribute(name = "height", description = "The height of the image.", defaultValue = EImageFormat.FLEXIBLE_STRING) int height)
	        throws ConQATException {

		EImageFormat.checkDimensions(width, height);

		this.width = width;
		this.height = height;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "dynamic-frames", minOccurrences = 0, maxOccurrences = 1, description = ""
	        + "Determines whether hierarchy elements (i.e. packages, directories) should be made more visible "
	        + "using dynamic frames drawn. Default is to draw them in blue color.")
	public void setDrawDynamicFrames(
	        @AConQATAttribute(name = "color", description = "Color of the frame or an empty string to turn them off [default: blue].") String frameColor)
	        throws ConQATException {

		if (StringUtils.isEmpty(frameColor)) {
			dynamicFrameColor = null;
			return;
		}

		dynamicFrameColor = ColorUtils.fromString(frameColor);

		if (dynamicFrameColor == null) {
			throw new ConQATException(frameColor + " is not a color.");
		}
	}

	/** {@inheritDoc} */
	@Override
	protected String getIconName() {
		return descriptors.getFirst(0).getIconName();
	}

	/** {@inheritDoc} */
	@Override
	protected void layoutPage() throws ConQATException {

		File file = getFile("png");
		getLogger().info("Writing " + file);
		Dimension dimension =
		        EImageFormat.PNG.writeImage(file, descriptors.getFirst(0),
		                width, height);
		HTMLImageMapGenerator generator = getImageMapGenerator(dimension);

		if (linkedPage != null) {
			new PageWriter(outputDirectory, linkedPage).write();
			writer.openElement(A, HREF, linkedPage.getFilename());
		}

		String id =
		        BundleContext.getInstance().getHtmlPresentationManager()
		                .getAbbreviation(getProcessorInfo().getName());
		if (generator != null) {
			id = generator.getImageId();
		}

		writer.openElement(IMG, CLASS, IMAGE_STYLE, SRC, getHRef("png"), ID, id);
		writer.closeElement(IMG);

		if (linkedPage != null) {
			writer.closeElement(A);
		}

		addMultipleImageLinks(id);

		if (generator != null) {
			writer.insertJavaScript(generator.generateJS());
		}

		writer.addClosedElement(EHTMLElement.BR);
	}

	/**
	 * Adds the links for other images if more than one page descriptor is used.
	 */
	private void addMultipleImageLinks(String id) throws ConQATException {
		if (descriptors.size() <= 1) {
			return;
		}

		writer.openElement(DIV, STYLE, new CSSDeclarationBlock(
		        ECSSProperty.PADDING_LEFT, "10px"));
		writer.addText("Image: ");

		for (int i = 0; i < descriptors.size(); ++i) {
			String link = getHRef("png");
			if (i > 0) {
				EImageFormat.PNG.writeImage(getFile(i + ".png"),
				        descriptors.getFirst(i), width, height);
				link = getHRef(i + ".png");
			}
			writer.addClosedTextElement(A, descriptors.getSecond(i), STYLE,
			        new CSSDeclarationBlock(ECSSProperty.CURSOR, "pointer"),
			        ONCLICK, "javascript: document.getElementById(\"" + id
			                + "\").src = \"" + link + "\"");
			if (i < descriptors.size() - 1) {
				writer.addRawString(" &middot; ");
			}
		}
		writer.closeElement(DIV);
	}

	/** Returns the image map generator to use (or null). */
	private HTMLImageMapGenerator getImageMapGenerator(Dimension dimension)
	        throws ConQATException {
		ITooltipDescriptor<Object> tooltipDescriptor =
		        descriptors.getFirst(0).getTooltipDescriptor(dimension.width,
		                dimension.height);

		if (tooltipDescriptor == null || !displayTooltips || linkedPage != null) {
			return null;
		}
		return new HTMLImageMapGenerator(dynamicFrameColor, tooltipDescriptor);
	}

	/** {@inheritDoc} */
	@Override
	protected Object getSummary() {
		return descriptors.getFirst(0).getSummary();
	}

}