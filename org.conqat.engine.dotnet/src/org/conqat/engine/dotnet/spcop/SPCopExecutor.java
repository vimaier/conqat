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
package org.conqat.engine.dotnet.spcop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.dotnet.DotnetExecutorBase;
import org.conqat.engine.resource.IContentAccessor;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.scope.memory.InMemoryContentAccessor;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.lib.commons.filesystem.CanonicalFile;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * {ConQAT.Doc}
 * 
 * @author $Author: pfaller $
 * @version $Rev: 47078 $
 * @ConQAT.Rating GREEN Hash: A409836BD21D8A4C6E4C367F1EAA6CFA
 */
@AConQATProcessor(description = "Executes the SPCop analysis tool for a given"
		+ "SharePoint project. SPCop is executed once for each element in the "
		+ "scope that ends with <em>.wsp</em>.")
public class SPCopExecutor extends DotnetExecutorBase {

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.INPUT_NAME, attribute = ConQATParamDoc.INPUT_REF_NAME, optional = false, description = ConQATParamDoc.INPUT_DESC)
	public IResource input;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "ruleset", attribute = "path", optional = true, description = "Path to the ruleset that should be used by SPCop. If not specified, SPCop's default ruleset is used.")
	public CanonicalFile ruleSetPath;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "spcop-executable", description = "The absolute path of the SPCop executable.", minOccurrences = 1, maxOccurrences = 1)
	public void setSPCopHome(
			@AConQATAttribute(name = "file", description = "Absolute path to SPCop.exe") File executable)
			throws ConQATException {
		setExecutable(executable.getPath());
	}

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "output-dir", attribute = "path", description = ""
			+ "Output directory where results xml are stored. If not set, the result files are only written "
			+ "to a temporary file", optional = true)
	public String outputDir = null;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "output-file", attribute = "prefix", description = "Preifx for result "
			+ "xml files, default is 'spcop-'.", optional = true)
	public String outputFilePrefix = "spcop-";

	/** {@inheritDoc} */
	@Override
	public IContentAccessor[] process() throws ConQATException {
		List<IContentAccessor> reports = new ArrayList<IContentAccessor>();

		for (IElement element : ResourceTraversalUtils.listElements(input)) {
			if (element.getLocation().endsWith(".wsp")) {
				reports.add(analyzeWSP(element));
			}
		}

		return reports.toArray(new IContentAccessor[reports.size()]);
	}

	/**
	 * Analyzes a single WSP file and returns an accessor for the resulting
	 * report.
	 */
	private IContentAccessor analyzeWSP(IElement element)
			throws ConQATException {
		String wspFilePath = getWSPFilePath(element);
		CanonicalFile report = getProcessorInfo().getTempFile(outputFilePrefix, ".xml");
		if (outputDir != null) {
			try {
				report = new CanonicalFile(outputDir, report.getName());
			} catch (IOException e) {
				// ignore, temp file will be used in that case
			}
		}
		outputFile = report.getCanonicalPath();
		report.deleteOnExit();

		List<String> arguments = createArguments(wspFilePath, report);
		execute(arguments);
		return readGeneratedReport(report);
	}

	/**
	 * Returns the location in the file system of the given element. If the
	 * element originates from a ZIP file, it is written to a temporary file of
	 * which the path is returned. Otherwise, the location of the element is
	 * returned.
	 */
	private String getWSPFilePath(IElement element) throws ConQATException {
		if (element.getLocation().toLowerCase().contains(".zip!")) {
			try {
				CanonicalFile wspFile = getProcessorInfo().getTempFile("",
						".wsp");
				wspFile.deleteOnExit();
				FileSystemUtils.writeFileBinary(wspFile, element.getContent());
				return wspFile.getCanonicalPath();
			} catch (IOException e) {
				throw new ConQATException(
						"Cannot write WSP element to temporary directory.", e);
			}
		}
		return element.getLocation();
	}

	/**
	 * Creates an {@link IContentAccessor} for the report file written by SPCop.
	 */
	private IContentAccessor readGeneratedReport(CanonicalFile report)
			throws ConQATException {
		String originalPath = report.getCanonicalPath();

		// SPCop always adds "_Rules" before the suffix of the provided path.
		String modifiedPath = StringUtils.stripSuffix(".xml", originalPath)
				+ "_Rules.xml";
		try {
			return new InMemoryContentAccessor(report.getName(),
					FileSystemUtils.readFileBinary(modifiedPath));
		} catch (IOException e) {
			throw new ConQATException("Error reading the SPCop report", e);
		}
	}

	/** Creates the command that is used to run the SPCop tool. */
	private List<String> createArguments(String wspFilePath,
			CanonicalFile reportFile) {
		ArrayList<String> arguments = new ArrayList<String>();

		// Input file (*.wsp).
		arguments.add("-i");
		arguments.add(wspFilePath);

		// Ruleset to be used.
		if (ruleSetPath != null) {
			arguments.add("-s");
			arguments.add(ruleSetPath.getCanonicalPath());
		}

		// Type of the generated report.
		arguments.add("-r");
		arguments.add("XML");

		// Output file. Note that SPCop adds "_Rules" before the file's suffix.
		arguments.add("-o");
		arguments.add(reportFile.getCanonicalPath());

		return arguments;
	}

	/** {@inheritDoc} */
	@Override
	protected int getSuccessExitCode() {
		return 1;
	}
}
