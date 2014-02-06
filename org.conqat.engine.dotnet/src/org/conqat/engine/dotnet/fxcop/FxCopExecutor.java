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
package org.conqat.engine.dotnet.fxcop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.conqat.engine.commons.ConQATParamDoc;
import org.conqat.engine.commons.execution.ProcessExecutorBase;
import org.conqat.engine.commons.logging.ListStructuredLogMessage;
import org.conqat.engine.commons.logging.StructuredLogTags;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATFieldParameter;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.ELogLevel;
import org.conqat.engine.dotnet.DotnetExecutorBase;
import org.conqat.engine.resource.IContentAccessor;
import org.conqat.engine.resource.IElement;
import org.conqat.engine.resource.IResource;
import org.conqat.engine.resource.scope.memory.InMemoryContentAccessor;
import org.conqat.engine.resource.util.ConQATFileUtils;
import org.conqat.engine.resource.util.ResourceTraversalUtils;
import org.conqat.lib.commons.assertion.CCSMAssert;
import org.conqat.lib.commons.collections.CollectionUtils;
import org.conqat.lib.commons.filesystem.CanonicalFile;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.string.StringUtils;

/**
 * {@ConQAT.Doc}
 * 
 * Command line options for FxCop can be found <a
 * href="http://msdn.microsoft.com/en-US/library/bb429449(v=VS.80).aspx"
 * >here</a>.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46337 $
 * @ConQAT.Rating GREEN Hash: 76F9356032E66A2BC26F4CEC37300FD6
 */
@AConQATProcessor(description = "This processor executes the FxCop command line tool. In most scenarios, this only needs a scope "
		+ "containing the assemblies and the FxCop install location. It will then run all FxCop checks. Additionally one can specify "
		+ "individual rule sets or use an FxCop project file with a selection of rules.")
public class FxCopExecutor extends DotnetExecutorBase {

	/** Prefix for the temporary folder. */
	private static final String TEMP_FILE_PREFIX = "FxCop";

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.INPUT_NAME, attribute = ConQATParamDoc.INPUT_REF_NAME, description = "A scope containing project assemblies.", optional = false)
	public IResource assemblyScope = null;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "dependency", attribute = ConQATParamDoc.INPUT_REF_NAME, description = "A scope containing those assemblies the input assemblies depend on.", optional = true)
	public IResource dependencyScope = null;

	/** Paths to additional FxCop ruleset DLLs. */
	private final Set<CanonicalFile> rulesetDllPaths = new HashSet<CanonicalFile>();

	/** The FxCop ruleset project file. */
	private CanonicalFile rulesetProjectFile = null;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "fxcop-home", description = "The location where FxCop is installed, i.e. the folder containing FxCopCmd.exe.", minOccurrences = 1, maxOccurrences = 1)
	public void setFxCopHome(
			@AConQATAttribute(name = "path", description = "FxCop install path.") String fxCopHome)
			throws ConQATException {
		setExecutable(new File(fxCopHome, "FxCopCmd.exe").getPath());
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "ruleset-dll", description = "Either a ruleset dll or a directory containing rule dlls.")
	public void addRulesetDllPath(
			@AConQATAttribute(name = "path", description = "The path") String path)
			throws ConQATException {
		CanonicalFile file = ConQATFileUtils
				.createCanonicalFile(new File(path));
		if (!file.canRead()) {
			throw new ConQATException("Ruleset dll cannot be read: " + file);
		}
		rulesetDllPaths.add(file);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "ruleset-project", description = "Ruleset project file (*.FxCop). This file may be used to specify specific rules.", maxOccurrences = 1)
	public void addProjectRuleset(
			@AConQATAttribute(name = "path", description = "The path") String projectRulesetPath)
			throws ConQATException {
		CanonicalFile providedRuleset = ConQATFileUtils
				.createCanonicalFile(new File(projectRulesetPath));
		if (!providedRuleset.isReadableFile()) {
			throw new ConQATException("Ruleset project cannot not be read: "
					+ providedRuleset);
		}

		// extract ruleset to temp directory, as FxCop cannot perform
		// cross-partition analysis
		rulesetProjectFile = getProcessorInfo()
				.getTempFile("Ruleset", ".FxCop");
		try {
			FileSystemUtils.copyFile(providedRuleset, rulesetProjectFile);
		} catch (IOException e) {
			throw new ConQATException("Cannot extract ruleset project: ", e);
		}
	}

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = ConQATParamDoc.LOG_LEVEL_NAME, attribute = ConQATParamDoc.VALUE_KEY_NAME, description = ConQATParamDoc.LOG_LEVEL_DESCRIPTION, optional = true)
	public ELogLevel errorLogLevel = ELogLevel.ERROR;

	/** {@ConQAT.Doc} */
	@AConQATFieldParameter(parameter = "missing-pdb-files", attribute = "ignore", description = "If true the analysis will skip assemblies with missing PDB files. Default: true", optional = true)
	public boolean ignoreAssembliesWithoutPdbFile = true;

	/** list of analysis execution log messages. */
	private final List<String> executionErrorLog = new ArrayList<String>();

	/** list of unresolved pdb files. */
	private final Set<String> unresolvedPdbLog = new HashSet<String>();

	/** Format string for the assembly target name in the report. */
	private static final String TARGET_NAME_FORMAT = "<Target Name=\"%s\">";

	/** Pattern for replacing the assembly target name of the report. */
	private static final Pattern TARGET_NAME_PATTERN = Pattern.compile(String
			.format(TARGET_NAME_FORMAT, ".*"));

	/** Format string for the module name in the report. */
	private static final String MODULE_NAME_FORMAT = "<Module Name=\"%s\">";

	/** Pattern for replacing the module name of the report. */
	private static final Pattern MODULE_NAME_PATTERN = Pattern.compile(String
			.format(MODULE_NAME_FORMAT, ".*"));

	/** {@inheritDoc} */
	@Override
	public IContentAccessor[] process() throws ConQATException {
		Map<CanonicalFile, IElement> extractedAssemblies = extractAnalysisAssemblies();

		logIfNotEmpty(unresolvedPdbLog, "%d PDB files could not be resolved");

		CanonicalFile dependenciesPath = extractDependencyAssemblies();

		// We need to analyze the assemblies separately as FxCop stops
		// the whole analysis if a single severe error occurs.
		List<IContentAccessor> codeAnalysisResults = new ArrayList<IContentAccessor>();
		for (CanonicalFile assembly : extractedAssemblies.keySet()) {
			analyzeAssemblyAndAddResult(assembly,
					extractedAssemblies.get(assembly), dependenciesPath,
					codeAnalysisResults);
		}

		logIfNotEmpty(executionErrorLog,
				"Error executing FxCop for %d assemblies");

		return codeAnalysisResults
				.toArray(new IContentAccessor[codeAnalysisResults.size()]);
	}

	/**
	 * Logs a {@link ListStructuredLogMessage} with the configured error level
	 * for the given collection, unless the collection is empty.
	 * 
	 * The <code>formatMessage</code> parameter expects a format string
	 * including a numeric placeholder (<code>%d</code>) to print the number of
	 * items in the collection.
	 */
	private void logIfNotEmpty(Collection<String> errors, String formatMessage) {
		CCSMAssert
				.isTrue(formatMessage.contains("%d"),
						"The format message is required to contain a numeric placeholder (%d)");

		if (CollectionUtils.isNullOrEmpty(errors)) {
			return;
		}

		getLogger().log(
				errorLogLevel,
				new ListStructuredLogMessage(String.format(formatMessage,
						errors.size()), errors,
						StructuredLogTags.THIRD_PARTY_TOOL));
	}

	/**
	 * Overrides the error logging behavior of {@link ProcessExecutorBase} by
	 * collecting the FxCop execution error output and logging it when the
	 * analysis terminates as structured log message.
	 */
	@Override
	protected void logStdErr(String stdErr) {
		executionErrorLog.add(stdErr);
	}

	/** Extracts the assemblies to be analyzed by FxCop. */
	private Map<CanonicalFile, IElement> extractAnalysisAssemblies()
			throws ConQATException {
		CanonicalFile tempDirectory = getProcessorInfo().getTempFile(
				TEMP_FILE_PREFIX, "Assemblies");
		tempDirectory.mkdir();
		return extractAssemblies(assemblyScope, tempDirectory, true);
	}

	/**
	 * Extracts the dependency assemblies from the dependency scope to a
	 * temporary directory. Returns null if no dependency scope defined.
	 */
	private CanonicalFile extractDependencyAssemblies() throws ConQATException {
		if (dependencyScope == null) {
			return null;
		}

		CanonicalFile tempDirectory = getProcessorInfo().getTempFile(
				TEMP_FILE_PREFIX, "Dependencies");
		tempDirectory.mkdir();

		extractAssemblies(dependencyScope, tempDirectory, false);

		return tempDirectory;
	}

	/**
	 * Extracts all assemblies of the provided scope into a temporary directory
	 * with flat hierarchy.
	 */
	private Map<CanonicalFile, IElement> extractAssemblies(
			IResource assemblyRoot, CanonicalFile tempDirectory,
			boolean extractPdbFile) throws ConQATException {

		Map<CanonicalFile, IElement> tempDirectoryMapping = new HashMap<CanonicalFile, IElement>();

		for (IElement element : ResourceTraversalUtils
				.listElements(assemblyRoot)) {

			try {
				// If configured, ignore assemblies without PDB files.
				if (extractPdbFile && !extractPdbFile(element, tempDirectory)
						&& ignoreAssembliesWithoutPdbFile) {
					continue;
				}

				CanonicalFile outFile = new CanonicalFile(tempDirectory,
						element.getName());
				FileSystemUtils.writeFileBinary(outFile, element.getContent());
				tempDirectoryMapping.put(outFile, element);
			} catch (IOException e) {
				throw new ConQATException("Unable to extract assembly file: "
						+ element.getName(), e);
			}
		}

		return tempDirectoryMapping;
	}

	/**
	 * Extracts the PDB file corresponding for the given assembly to the
	 * temporary folder.
	 * 
	 * @return boolean Flag indicating whether the PDB file exists.
	 */
	private boolean extractPdbFile(IElement element, File temporaryFolder)
			throws ConQATException {

		// This will handle assemblies with and without extension by simple
		// appending the PDB extension to the extension-less filename.
		String pdbName = StringUtils.removeLastPart(element.getName(), '.')
				+ ".pdb";

		IContentAccessor pdbAccessor = null;
		try {
			pdbAccessor = element.createRelativeAccessor(pdbName);
		} catch (ConQATException e) {
			unresolvedPdbLog.add(pdbName);
			return false;
		}

		File pdbFile = new File(temporaryFolder, pdbName);

		try {
			FileSystemUtils.writeFileBinary(pdbFile, pdbAccessor.getContent());
		} catch (IOException e) {
			throw new ConQATException("Unable to extract pdb file: "
					+ pdbFile.getName(), e);
		}

		return true;
	}

	/**
	 * Analyzes the provided assembly with FxCop and adds the resulting XML
	 * report to the text scope.
	 */
	private void analyzeAssemblyAndAddResult(CanonicalFile assembly,
			IElement element, CanonicalFile dependenciesPath,
			List<IContentAccessor> codeAnalysisResults) throws ConQATException {

		CanonicalFile analysisXml = getProcessorInfo().getTempFile(
				assembly.getName(), ".CodeAnlysisLog.xml");
		outputFile = analysisXml.getCanonicalPath();

		ArrayList<String> arguments = createArguments(assembly,
				dependenciesPath, analysisXml);
		execute(arguments);

		String xmlContent = replaceTemporaryNames(element, analysisXml);

		if (!StringUtils.isEmpty(xmlContent)) {
			IContentAccessor accessor = new InMemoryContentAccessor(
					analysisXml.getName(), xmlContent.getBytes());
			codeAnalysisResults.add(accessor);
		} else {
			getLogger().log(
					this.errorLogLevel,
					"Error executing FxCop for " + assembly.getName()
							+ ": The XML report file is empty");
		}
	}

	/**
	 * Creates the command arguments to invoke FxCop for the given assembly.
	 * 
	 * @param assembly
	 *            The assembly to analyze.
	 * @param dependenciesPath
	 *            An optional path to search for dependencies of the analyzed
	 *            assembly. May be null.
	 * @param analysisXml
	 *            Path to the XML output file of the analysis.
	 */
	private ArrayList<String> createArguments(CanonicalFile assembly,
			CanonicalFile dependenciesPath, CanonicalFile analysisXml) {
		ArrayList<String> command = new ArrayList<String>();

		command.add("/f:" + assembly.getCanonicalPath());

		if (dependenciesPath != null) {
			command.add("/d:" + dependenciesPath.getCanonicalPath());
		}

		if (rulesetProjectFile != null) {
			command.add("/p:" + rulesetProjectFile.getCanonicalPath());
		}

		for (CanonicalFile rulePath : rulesetDllPaths) {
			command.add("/r:" + rulePath.getCanonicalPath());
		}

		command.add("/out:" + analysisXml.getCanonicalPath());

		// search global assembly cache
		command.add("/gac");
		// forces xml output even for errors
		command.add("/fo");
		// allows to load assemblies even if version, signature and culture do
		// not match. This is FxCop10 exclusive.
		command.add("/assemblycomparemode:none");

		return command;
	}

	/**
	 * Replaces the temporary filenames in the FxCop XML report with uniform
	 * paths.
	 */
	private static String replaceTemporaryNames(IElement element,
			CanonicalFile analysisXml) throws ConQATException {
		try {
			String xmlContent = FileSystemUtils.readFile(analysisXml);

			Matcher matcher = TARGET_NAME_PATTERN.matcher(xmlContent);
			if (matcher.find()) {
				xmlContent = matcher.replaceFirst(String.format(
						TARGET_NAME_FORMAT, element.getUniformPath()));
			}
			matcher = MODULE_NAME_PATTERN.matcher(xmlContent);
			if (matcher.find()) {
				xmlContent = matcher.replaceFirst(String.format(
						MODULE_NAME_FORMAT, element.getName()));
			}
			return xmlContent;
		} catch (IOException e) {
			throw new ConQATException("Could not read FxCop output "
					+ analysisXml.getCanonicalPath(), e);
		}
	}
}
