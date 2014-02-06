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
package org.conqat.engine.commons.execution;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.commons.ConQATProcessorBase;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.ConQATException;
import org.conqat.engine.core.logging.ELogLevel;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.lib.commons.io.ProcessUtils;
import org.conqat.lib.commons.io.ProcessUtils.ExecutionResult;
import org.conqat.lib.commons.string.StringUtils;

/**
 * Base class for processors that execute external processes.
 * 
 * @author $Author: heinemann $
 * @version $Rev: 46407 $
 * @ConQAT.Rating GREEN Hash: 6D8141737F510C6398F27A761DE68C96
 */
public abstract class ProcessExecutorBase extends ConQATProcessorBase {

	/** Documentation to be reused in subclasses. */
	protected static final String PROCESSOR_OUTPUT_FILE_ARG_DESCRIPTION = "The argument that "
			+ "specifies the output file must be provided via the 'output-file-argument' "
			+ "parameter. Care needs to be taken if the order of the arguments matters "
			+ "for the executed process as this needs to reflect the order of the "
			+ "parameters.";

	/** Name of the output file argument parameter. */
	protected static final String PARAMETER_OUTPUT_FILE_ARG_NAME = "output-file-argument";

	/** Description of the output file argument parameter. */
	protected static final String PARAMETER_OUTPUT_FILE_ARG_DESCRIPTION = "The argument specified here is "
			+ "passed to the process just like the other arguments. However, the output of this "
			+ "processor is specified by this parameter.";

	/**
	 * Working directory of the executed process. If <code>null</code>, the
	 * working directory of the JVM is used.
	 */
	private File workingDirectory = null;

	/** Flag that steers if the processor fails if the executed process fails. */
	private boolean failOnError = true;

	/** List of arguments handed to the process. */
	protected final List<String> arguments = new ArrayList<String>();

	/** Name of the output file. */
	protected String outputFile;

	/** Log level for standard out. */
	private ELogLevel stdOutLevel = ELogLevel.OFF;

	/** Log level for standard error. */
	private ELogLevel stdErrLevel = ELogLevel.WARN;

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "working-directory", description = "Working directory for the execution "
			+ "[if unspecified, ConQAT's working directory will be used]", maxOccurrences = 1)
	public void setWorkingDirectory(
			@AConQATAttribute(name = "value", description = "Working directory") String workingDirectory) {
		this.workingDirectory = new File(workingDirectory);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "fail-on-error", description = "If set to true, the processor will fail if "
			+ "the executed process has an exit value != 0 [default is true]", maxOccurrences = 1)
	public void setFailOnError(
			@AConQATAttribute(name = "value", description = "Fail on error") boolean failOnError) {
		this.failOnError = failOnError;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "std-out-log", description = "Specifies the level used for "
			+ "loggin std-out. If not set, output is ignored.", maxOccurrences = 1)
	public void setStdOutLogLevel(
			@AConQATAttribute(name = "level", description = "Log level for std-out") ELogLevel level) {
		stdOutLevel = level;
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "std-err-log", description = "Specifies the level used for "
			+ "logging std-err. Use log-level off to disable. [default is log level warn].", maxOccurrences = 1)
	public void setStdErrLogLevel(
			@AConQATAttribute(name = "level", description = "Log level for std-out") ELogLevel level) {
		stdErrLevel = level;
	}

	/** Add an argument. */
	protected void addArgument(String argument) {
		arguments.add(argument);
	}

	/**
	 * Set output file argument. This also adds the argument to
	 * {@link #arguments}.
	 */
	protected void setOutputFileArgument(String outputFile) {
		arguments.add(outputFile);
		this.outputFile = outputFile;
	}

	/** {@inheritDoc} */
	@Override
	public Object process() throws ConQATException {

		try {
			FileSystemUtils.ensureParentDirectoryExists(new File(outputFile));
			execute();
			return outputFile;
		} catch (IOException e) {
			throw new ConQATException("Could not execute process: "
					+ e.getMessage(), e);
		}

	}

	/** Execute process. */
	private void execute() throws IOException, ConQATException {

		List<String> command = getCommand();

		getLogger().info("Command: " + StringUtils.concat(command));

		ProcessBuilder builder = new ProcessBuilder(command);

		if (workingDirectory != null) {
			builder.directory(workingDirectory);
		}

		ExecutionResult result = ProcessUtils.execute(builder);

		String stdOut = result.getStdout();
		if (!StringUtils.isEmpty(stdOut)) {
			logStdOut(stdOut);
		}

		String stdErr = result.getStderr();
		if (!StringUtils.isEmpty(stdErr)) {
			logStdErr(stdErr);
		}

		int exitValue = result.getReturnCode();

		if (failOnError && exitValue != getSuccessExitCode()) {
			throw new ConQATException("Process failed with exit value "
					+ exitValue + StringUtils.CR + stdErr);
		}

	}

	/** Logs the standard output message with the configured logging level. */
	protected void logStdOut(String stdOut) {
		getLogger().log(stdOutLevel, stdOut);
	}

	/** Logs the standard error message with the configured logging level. */
	protected void logStdErr(String stdErr) {
		getLogger().log(stdErrLevel, stdErr);
	}

	/** Template method to get command to be executed. */
	protected abstract List<String> getCommand();

	/**
	 * Retrieves the exit code returned by the executable when everything went
	 * fine. Classes should overwrite this method when the code is not 0.
	 */
	protected int getSuccessExitCode() {
		return 0;
	}
}