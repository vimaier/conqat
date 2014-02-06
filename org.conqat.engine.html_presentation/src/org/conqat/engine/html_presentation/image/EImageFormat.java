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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.TranscodingHints;
import org.apache.fop.svg.PDFTranscoder;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.core.IConQATProcessorInfo;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.filesystem.CanonicalFile;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

/**
 * This class represent the different image formats that
 * {@link IImageDescriptor}s can be written to.
 * 
 * @author $Author: kinnen $
 * @version $Rev: 41751 $
 * @ConQAT.Rating GREEN Hash: 7C2626CD14F97179F21B98CC0FEADE2B
 */
public enum EImageFormat {
	/** PNG format. */
	PNG {
		@Override
		protected void writeImage(File file, IImageDescriptor descriptor,
				Dimension dimensions) throws ConQATException {
			writeToPNG(file, descriptor, dimensions);
		}
	},

	/** SVG format. */
	SVG {
		@Override
		protected void writeImage(File file, IImageDescriptor descriptor,
				Dimension dimensions) throws ConQATException {
			writeToSVG(file, descriptor, dimensions);
		}
	},

	/** PDF format. */
	PDF {
		@Override
		protected void writeImage(File file, IImageDescriptor descriptor,
				Dimension dimensions) throws ConQATException {
			writeToPDF(file, descriptor, dimensions);
		}
	};

	/** Same as {@link #FLEXIBLE} to be used in comment. */
	public final static String FLEXIBLE_STRING = "-1";

	/** Indicates that a length is flexible. */
	public final static int FLEXIBLE = Integer.parseInt(FLEXIBLE_STRING);

	/** Documentation string. */
	public final static String DOC = "The dimensions of the generated image. If the dimensions are not specified, "
			+ "the image will be rendered with its prefered size. If only one dimension is specified, "
			+ "this will be respected and the the other dimension will be calculated using the "
			+ "aspect ration of the prefered size. If both dimensions are specified, these will be "
			+ "respected. This, however, may lead to skewed results for some image descriptors.";

	/**
	 * Write image descriptor to file in the specified format.
	 * 
	 * @param file
	 *            destination file.
	 * @param descriptor
	 *            the image descriptor
	 * @param width
	 *            image width (may be {@link #FLEXIBLE})
	 * @param height
	 *            image height (may be {@link #FLEXIBLE})
	 * @return the dimension of the generate image. This might be known in
	 *         advance when {@link #FLEXIBLE} is used.
	 * 
	 * @throws ConQATException
	 *             if writing the image fails.
	 */
	public Dimension writeImage(File file, IImageDescriptor descriptor,
			int width, int height) throws ConQATException {
		Dimension dimension = determineDimensions(descriptor, width, height);
		writeImage(file, descriptor, dimension);
		return dimension;
	}

	/** Determines the actual dimensions. */
	private Dimension determineDimensions(IImageDescriptor descriptor,
			int width, int height) throws ConQATException {
		Dimension preferredSize = descriptor.getPreferredSize();

		Dimension result = new Dimension(width, height);

		double aspectRatio;

		if (preferredSize.width == 0 || preferredSize.height == 0) {
			// prevent div-by-zero
			aspectRatio = 1;
		} else {
			aspectRatio = (double) preferredSize.width
					/ (double) preferredSize.height;
		}

		if (width == FLEXIBLE && height == FLEXIBLE) {
			result.width = preferredSize.width;
			result.height = preferredSize.height;
		} else if (width == FLEXIBLE) {
			result.width = (int) (aspectRatio * height);
		} else if (height == FLEXIBLE) {
			result.height = (int) (width / aspectRatio);
		}

		CCSMAssert.isTrue(
				result.width != FLEXIBLE && result.height != FLEXIBLE,
				"Should both be fixed");

		return result;
	}

	/**
	 * Write image descriptor to file in the specified format.
	 * 
	 * @param file
	 *            destination file.
	 * @param descriptor
	 *            the image descriptor
	 * @param dimensions
	 *            dimensions of the image
	 * 
	 * @throws ConQATException
	 *             if writing the image fails.
	 */
	protected abstract void writeImage(File file, IImageDescriptor descriptor,
			Dimension dimensions) throws ConQATException;

	/**
	 * Write image descriptor to a temporary file.
	 * 
	 * @param processorInfo
	 *            processor info used to determine temporary file.
	 * @param descriptor
	 *            the image descriptor
	 * @param width
	 *            image width
	 * @param height
	 *            image height
	 * @return the file the image was written to
	 * @throws ConQATException
	 *             if writing the image fails.
	 */
	public CanonicalFile writeImage(IConQATProcessorInfo processorInfo,
			IImageDescriptor descriptor, int width, int height)
			throws ConQATException {
		CanonicalFile file = processorInfo.getTempFile("image", "."
				+ name().toLowerCase());
		writeImage(file, descriptor, width, height);
		return file;
	}

	/** Checks if dimensions are valid, i.e. positive or {@link #FLEXIBLE}.} */
	public static void checkDimensions(int width, int height)
			throws ConQATException {
		if ((width < 1 && width != FLEXIBLE)
				|| (height < 1 && height != FLEXIBLE)) {
			throw new ConQATException("Image dimensions must be positive!");
		}
	}

	/** Write image to PNG. */
	private static void writeToPNG(File file, IImageDescriptor descriptor,
			Dimension dimensions) throws ConQATException {
		BufferedImage image = new BufferedImage(dimensions.width,
				dimensions.height, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = image.createGraphics();
		ImageDescriptorUtils.initGraphics(graphics);
		descriptor.draw(graphics, dimensions.width, dimensions.height);
		try {
			FileSystemUtils.ensureParentDirectoryExists(file);
			ImageIO.write(image, "png", file);
		} catch (IOException e) {
			throw new ConQATException("Could not write image to " + file + ": "
					+ e.getMessage(), e);
		}
	}

	/** Write image to SVG. */
	private static void writeToSVG(File file, IImageDescriptor descriptor,
			Dimension dimensions) throws ConQATException {
		try {
			FileSystemUtils.writeFileUTF8(file,
					createSVG(descriptor, dimensions));
		} catch (IOException e) {
			throw new ConQATException(
					"Could not create SVG: " + e.getMessage(), e);
		}
	}

	/** Create SVG representation of the image descriptor. */
	private static String createSVG(IImageDescriptor descriptor,
			Dimension dimensions) throws ConQATException {
		SVGGraphics2D graphics = createSVGGraphics();
		descriptor.draw(graphics, dimensions.width, dimensions.height);
		StringWriter content = new StringWriter();
		try {
			graphics.stream(content);
		} catch (SVGGraphics2DIOException e) {
			throw new ConQATException(
					"Could not create SVG: " + e.getMessage(), e);
		}
		return content.toString();
	}

	/** Create graphics object for SVG. */
	private static SVGGraphics2D createSVGGraphics() {
		DOMImplementation domImpl = GenericDOMImplementation
				.getDOMImplementation();
		Document document = domImpl.createDocument(null, "svg", null);

		SVGGraphics2D graphics = new SVGGraphics2D(document);
		ImageDescriptorUtils.initGraphics(graphics);
		return graphics;
	}

	/** Write image to PDF. */
	private static void writeToPDF(File file, IImageDescriptor descriptor,
			Dimension dimensions) throws ConQATException {

		String svg = createSVG(descriptor, dimensions);

		TranscoderInput input = new TranscoderInput(new StringReader(svg));
		FileOutputStream outStream = null;

		try {
			FileSystemUtils.ensureParentDirectoryExists(file);
			outStream = new FileOutputStream(file);
			TranscoderOutput output = new TranscoderOutput(outStream);

			PDFTranscoder transcoder = new PDFTranscoder();
			HashMap<TranscodingHints.Key, Object> hints = new HashMap<TranscodingHints.Key, Object>();
			hints.put(SVGAbstractTranscoder.KEY_WIDTH, (float) dimensions.width);
			hints.put(SVGAbstractTranscoder.KEY_HEIGHT,
					(float) dimensions.height);
			transcoder.setTranscodingHints(hints);
			transcoder.transcode(input, output);
		} catch (TranscoderException e) {
			throw new ConQATException(
					"Could not create PDF: " + e.getMessage(), e);
		} catch (IOException e) {
			throw new ConQATException(
					"Could not create PDF: " + e.getMessage(), e);
		} finally {
			FileSystemUtils.close(outStream);
		}
	}
}