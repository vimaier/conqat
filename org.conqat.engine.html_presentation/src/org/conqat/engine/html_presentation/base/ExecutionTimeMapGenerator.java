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
package org.conqat.engine.html_presentation.base;

import static org.conqat.engine.html_presentation.CSSMananger.IMAGE_STYLE;
import static org.conqat.lib.commons.html.EHTMLAttribute.CLASS;
import static org.conqat.lib.commons.html.EHTMLAttribute.ID;
import static org.conqat.lib.commons.html.EHTMLAttribute.SRC;
import static org.conqat.lib.commons.html.EHTMLElement.IMG;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;

import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.driver.info.BlockInfo;
import org.conqat.engine.core.logging.testutils.ProcessorInfoMock;
import org.conqat.engine.html_presentation.HTMLPresentation;
import org.conqat.engine.html_presentation.IPageDescriptor;
import org.conqat.engine.html_presentation.PageDescriptor;
import org.conqat.engine.html_presentation.image.EImageFormat;
import org.conqat.engine.html_presentation.image.HTMLImageMapGenerator;
import org.conqat.engine.html_presentation.image.IImageDescriptor;
import org.conqat.engine.html_presentation.image.ITooltipDescriptor;
import org.conqat.engine.html_presentation.treemap.TreeMapCreator;
import org.conqat.engine.html_presentation.util.ResourcesManager;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.html.EHTMLElement;
import org.conqat.lib.commons.html.HTMLWriter;

/**
 * This class generates the page showing the execution time map.
 * 
 * @author $Author: hummelb $
 * @version $Rev: 41770 $
 * @ConQAT.Rating GREEN Hash: E01002031693BE68D9B30E7E95FCF65E
 */
public class ExecutionTimeMapGenerator {

	/** Name of the execution time map. */
	private static String MAP_FILENAME = ResourcesManager.IMAGES_DIRECTORY_NAME
			+ "/execution_time_map.png";

	/** The root block info that describes the config graph. */
	private final BlockInfo configGraph;

	/** The output directory. */
	private final File outputDirectory;

	/** Width of the generated image */
	private static final int IMAGE_WIDTH = 800;

	/** Height of the generated image */
	private static final int IMAGE_HEIGHT = 600;

	/**
	 * Create new config graph factory.
	 * 
	 * @param configurationInformation
	 *            The root block info that describes the config graph.
	 * @param outputDirectory
	 *            the output directory.
	 */
	public ExecutionTimeMapGenerator(File outputDirectory,
			BlockInfo configurationInformation) {
		this.configGraph = configurationInformation;
		this.outputDirectory = outputDirectory;
	}

	/** Create execution time page. */
	public IPageDescriptor createPage() throws ConQATException {
		IImageDescriptor descriptor = createDescriptor();

		PageDescriptor page = new PageDescriptor(
				"Execution Time Map (size is defined by execution time, color by processor status)",
				"Execution Time", HTMLPresentation.INFO_GROUP_NAME, null,
				"execution_time.gif", "execution_time_map.html");

		File pngFile = FileSystemUtils.newFile(outputDirectory, MAP_FILENAME);
		Dimension dimension = EImageFormat.PNG.writeImage(pngFile, descriptor,
				IMAGE_WIDTH, IMAGE_HEIGHT);

		writeHtml(page.getWriter(), descriptor.getTooltipDescriptor(
				dimension.width, dimension.height));

		return page;
	}

	/** Writes the HTML code. */
	private void writeHtml(HTMLWriter writer,
			ITooltipDescriptor<Object> tooltipDescriptor) {
		BaseUtils.createInfoTable(configGraph, "Execution Statistics", writer);
		HTMLImageMapGenerator gen = new HTMLImageMapGenerator(Color.white,
				tooltipDescriptor);
		writer.addClosedElement(IMG, CLASS, IMAGE_STYLE, SRC, MAP_FILENAME, ID,
				gen.getImageId());
		writer.addClosedElement(EHTMLElement.BR);
		writer.insertJavaScript(gen.generateJS());
	}

	/** Creates the image descriptor. */
	private IImageDescriptor createDescriptor() throws ConQATException {
		TreeMapCreator creator = new TreeMapCreator();
		creator.init(new ProcessorInfoMock());
		creator.setInput(new InfoTreeMapNode(configGraph, null));
		creator.setCushions(0.7, 0.8);
		creator.setDrawText(Color.black, null);
		creator.setSizeKey(InfoTreeMapNode.SIZE_KEY);

		return creator.process();
	}
}