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
package org.conqat.engine.simulink.output;

import java.io.File;
import java.io.IOException;

import org.conqat.engine.commons.node.NodeUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATKey;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.html_presentation.util.HTMLLink;
import org.conqat.engine.html_presentation.util.ResourcesManager;
import org.conqat.engine.simulink.clones.normalize.ISimulinkNormalizer;
import org.conqat.engine.simulink.scope.ISimulinkElement;
import org.conqat.engine.simulink.scope.ISimulinkResource;
import org.conqat.engine.simulink.util.SimulinkElementProcessorBase;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.graph.EGraphvizOutputFormat;
import org.conqat.lib.commons.graph.GraphvizException;
import org.conqat.lib.commons.graph.GraphvizGenerator;
import org.conqat.lib.simulink.model.SimulinkModel;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 36886 $
 * @ConQAT.Rating GREEN Hash: 8E0952083D29211C181C2E3E47F942DA
 */
@AConQATProcessor(description = "This processor layouts all simulink models in the given simulink "
		+ "scope and appends links to these images to the model nodes.")
public class SimulinkLayouter extends SimulinkElementProcessorBase {

	/** A link to the layouted model graph. */
	@AConQATKey(description = "A link to the layouted model graph.", type = "org.conqat.engine.html_presentation.util.HTMLLink")
	public static final String LAYOUT_KEY = "Layouted Model";

	/** The output directory of the HTML presentation. */
	private File outputDirectory;

	/** The (optional) normalizer used to determine block weights. */
	private ISimulinkNormalizer normalizer = null;

	/** Whether to show the hierarchy using clusters. */
	private boolean showHierarchy = true;

	/** Whether to suppress the names in the block labels. */
	private boolean suppressNames = false;

	/** Set the output directory. */
	@AConQATParameter(name = "output", minOccurrences = 1, maxOccurrences = 1, description = "The directory to write the M-files into.")
	public void setOutputDirectory(
			@AConQATAttribute(name = "dir", description = "The name of the directory.") String dir)
			throws ConQATException {
		outputDirectory = new File(dir);
		try {
			FileSystemUtils.ensureDirectoryExists(outputDirectory);
		} catch (IOException e) {
			throw new ConQATException("Could not create output directory!", e);
		}
		if (!outputDirectory.isDirectory()) {
			throw new ConQATException("Could not create output directory!");
		}
	}

	/** Set normalization. */
	@AConQATParameter(name = "norm", maxOccurrences = 1, description = ""
			+ "If provided this normalizer is used to append the weight to the block labels.")
	public void setNormalizer(
			@AConQATAttribute(name = "ref", description = "Reference to the normalizer") ISimulinkNormalizer normalizer) {
		this.normalizer = normalizer;
	}

	/** Determines whether to show the hierarchy. */
	@AConQATParameter(name = "hierarchy", maxOccurrences = 1, description = ""
			+ "Whether to show the hierarchy using clusters or not. Default is true.")
	public void setShowHierarchy(
			@AConQATAttribute(name = "show", description = "Whether to display hierarchy or not.") boolean showHierarchy) {
		this.showHierarchy = showHierarchy;
	}

	/** Determines whether to suppress names. */
	@AConQATParameter(name = "suppress", maxOccurrences = 1, description = ""
			+ "Whether to suppress names of blocks in labels or not. Default is false.")
	public void setSuppressNames(
			@AConQATAttribute(name = "names", description = "Whether to suppress names or not.") boolean suppressNames) {
		this.suppressNames = suppressNames;
	}

	/** {@inheritDoc} */
	@Override
	protected void setUp(ISimulinkResource root) throws ConQATException {
		super.setUp(root);
		NodeUtils.addToDisplayList(root, LAYOUT_KEY);
	}

	/** Does the actual layouting. */
	@Override
	protected void processElement(ISimulinkElement element)
			throws ConQATException {
		String filename = getProcessorInfo().getName() + "-"
				+ element.getId().replaceAll("[:/\\\\]", "_") + "."
				+ EGraphvizOutputFormat.PNG.getFileExtension();

		File outputFile = FileSystemUtils.newFile(outputDirectory,
				ResourcesManager.IMAGES_DIRECTORY_NAME, filename);
		try {
			createGraph(element.getModel(), outputFile);
		} catch (IOException e) {
			throw new ConQATException(e);
		} catch (GraphvizException e) {
			throw new ConQATException(e);
		}

		element.setValue(LAYOUT_KEY, new HTMLLink("graph",
				ResourcesManager.IMAGES_DIRECTORY_NAME + "/" + filename));

	}

	/** Create the graph. */
	private void createGraph(SimulinkModel model, File outputFile)
			throws ConQATException, IOException, GraphvizException {
		FileSystemUtils.ensureParentDirectoryExists(outputFile);

		String dotSource = SimulinkDotVisualizer.visualize(model, normalizer,
				showHierarchy, suppressNames);

		GraphvizGenerator generator = new GraphvizGenerator();
		generator
				.generateFile(dotSource, outputFile, EGraphvizOutputFormat.PNG);
	}
}