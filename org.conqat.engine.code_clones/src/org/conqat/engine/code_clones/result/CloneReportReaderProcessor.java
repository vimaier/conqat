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
package org.conqat.engine.code_clones.result;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.conqat.engine.code_clones.core.report.CloneReportReader;
import org.conqat.engine.code_clones.core.report.SourceElementDescriptor;
import org.conqat.engine.code_clones.detection.CloneDetectionResultElement;
import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.resource.IContentAccessor;
import org.conqat.engine.resource.build.ResourceBuilder;
import org.conqat.engine.resource.scope.filesystem.FileContentAccessor;
import org.conqat.engine.resource.scope.memory.InMemoryContentAccessor;
import org.conqat.engine.resource.text.ITextElement;
import org.conqat.engine.resource.text.ITextResource;
import org.conqat.engine.resource.text.TextContainer;
import org.conqat.engine.resource.text.TextElement;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.engine.resource.util.ResourceUtils;
import org.conqat.lib.commons.error.NeverThrownRuntimeException;
import org.conqat.lib.commons.factory.IParameterizedFactory;
import org.conqat.lib.commons.filesystem.CanonicalFile;
import org.conqat.lib.commons.string.StringUtils;

/**
 * {@ConQAT.Doc}
 * 
 * @author $Author: hummelb $
 * @version $Rev: 42161 $
 * @ConQAT.Rating GREEN Hash: E0DADF1F93A89057A1A1DDD373A964A9
 */
@AConQATProcessor(description = ""
		+ "Processor that reads clone reports and creates a"
		+ "{@link CloneDetectionResultElement} that can be used just as the result of a"
		+ "clone detection processor.")
public class CloneReportReaderProcessor extends ConQATProcessorBase {

	/** Factory for creating {@link TextContainer}s. */
	private static final IParameterizedFactory<TextContainer, String, NeverThrownRuntimeException> TEXT_CONTAINER_FACTORY = new IParameterizedFactory<TextContainer, String, NeverThrownRuntimeException>() {
		@Override
		public TextContainer create(String name) {
			return new TextContainer(name);
		}
	};

	/** Name of the report file */
	private File reportFile;

	/**
	 * The root of element trees that may provide the elements contained in the
	 * report.
	 */
	private final List<ITextResource> elementRoots = new ArrayList<ITextResource>();

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "artificial-elements", attribute = "value", optional = true, description = ""
			+ "Is this is set to true (default is false) artificial elements are created for elements which are neither found in the input scopes nor on disk.")
	public boolean useArtificialElements = false;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "report", minOccurrences = 1, maxOccurrences = 1, description = ""
			+ "Name of the report file")
	public void setReportFile(
			@AConQATAttribute(name = "filename", description = "Name of the report file") String reportFile) {
		this.reportFile = new File(reportFile);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "resources", description = "Resource tree, that can provide the elements required by the clone report.")
	public void addResourceTree(
			@AConQATAttribute(name = ConQATParamDoc.INPUT_REF_NAME, description = ConQATParamDoc.INPUT_REF_DESC) ITextResource root) {
		elementRoots.add(root);
	}

	/** {@inheritDoc} */
	@Override
	public CloneDetectionResultElement process() throws ConQATException {
		CloneReportReader reader = new CloneReportReader(reportFile);
		ITextResource root = createElementTree(reader
				.getSourceElementDescriptors());
		return new CloneDetectionResultElement(reader.getSystemDate(), root,
				reader.getCloneClasses());
	}

	/** Build an element tree from the file information in the clone report */
	private ITextResource createElementTree(
			List<SourceElementDescriptor> sourceFiles) throws ConQATException {

		Map<String, ITextElement> uniformPathToElement = new HashMap<String, ITextElement>();
		for (ITextResource root : elementRoots) {
			for (ITextElement element : ResourceTraversalUtils
					.listTextElements(root)) {
				uniformPathToElement.put(element.getUniformPath(), element);
			}
		}

		TextContainer root = new TextContainer(StringUtils.EMPTY_STRING);
		for (SourceElementDescriptor sourceFile : sourceFiles) {
			insert(determineTextElement(sourceFile, uniformPathToElement), root);
		}

		// discard artificial root if possible
		if (root.hasChildren() && root.getChildren().length == 1) {
			ITextResource newRoot = root.getChildren()[0];
			newRoot.setParent(null);
			return newRoot;
		}

		return root;
	}

	/**
	 * Determines the text element to be used for a
	 * {@link SourceElementDescriptor}
	 */
	private ITextElement determineTextElement(
			SourceElementDescriptor sourceFile,
			Map<String, ITextElement> uniformPathToElement)
			throws ConQATException {
		ITextElement textElement = uniformPathToElement.get(sourceFile
				.getUniformPath());
		if (textElement != null) {
			return textElement;
		}

		CanonicalFile file = ResourceUtils.getFileFromLocation(sourceFile
				.getLocation());
		IContentAccessor accessor;
		if (file == null) {
			if (!useArtificialElements) {
				throw new ConQATException(
						"Could not locate element with uniform path "
								+ sourceFile.getUniformPath()
								+ " and location " + sourceFile.getLocation());
			}
			accessor = new InMemoryContentAccessor(sourceFile.getUniformPath(),
					new byte[0]);
		} else {
			accessor = new FileContentAccessor(file,
					sourceFile.getUniformPath());
		}
		return new TextElement(accessor, Charset.defaultCharset());
	}

	/** Inserts the given element into the hierarchy. */
	private void insert(ITextElement element, TextContainer container) {
		ResourceBuilder.insert(element, container, TEXT_CONTAINER_FACTORY);
	}
}