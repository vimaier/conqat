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
package org.conqat.engine.html_presentation.image;

import java.io.File;

import org.conqat.engine.commons.util.ConQATInputProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 77E549C5FB150431CFF4585BAA031B01
 */
@AConQATProcessor(description = "Writes an image to a file.")
public class ImageWriter extends ConQATInputProcessorBase<IImageDescriptor> {

	/** The width of the image generated. */
	private int width = EImageFormat.FLEXIBLE;

	/** The height of the image generated. */
	private int height = EImageFormat.FLEXIBLE;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "file", attribute = "name", description = "File to write image to.")
	public String filename;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "image", attribute = "format", description = "Image format [default is PNG]", optional = true)
	public EImageFormat imageFormat = EImageFormat.PNG;

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

	/** {@inheritDoc} */
	@Override
	public File process() throws ConQATException {
		File file = new File(filename);
		imageFormat.writeImage(file, input, width, height);
		return file;
	}
}
