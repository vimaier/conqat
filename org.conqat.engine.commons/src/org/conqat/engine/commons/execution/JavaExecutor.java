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
import java.util.List;

import org.conqat.lib.commons.filesystem.FileExtensionFilter;
import org.conqat.lib.commons.filesystem.FileSystemUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author deissenb
 * @author $Author: goede $
 * @version $Rev: 46403 $
 * @ConQAT.Rating YELLOW Hash: 4A158BA15E0983769C68AE3FDF22BC09
 */
@AConQATProcessor(description = "This processor executes a Java application and "
		+ "waits until it terminates. The executed application is expected to create an "
		+ "output file whose name is returned by this processor. "
		+ ProcessExecutorBase.PROCESSOR_OUTPUT_FILE_ARG_DESCRIPTION)
public class JavaExecutor extends JavaExecutorBase {

	/** Name of the class or the jar file to be executed. */
	private String executeeName;

	/** {@ConQAT.Doc} */
	@Override
	@AConQATParameter(name = "arg", description = "Arguments for the application to execute")
	public void addArgument(
			@AConQATAttribute(name = "value", description = "Argument value") String argument) {
		super.addArgument(argument);
	}

	/** {@ConQAT.Doc} */
	@Override
	@AConQATParameter(name = PARAMETER_OUTPUT_FILE_ARG_NAME, description = PARAMETER_OUTPUT_FILE_ARG_DESCRIPTION, minOccurrences = 1, maxOccurrences = 1)
	public void setOutputFileArgument(
			@AConQATAttribute(name = "value", description = "Output file path") String outputFile) {
		super.setOutputFileArgument(outputFile);
	}

	/** {@ConQAT.Doc} */
	@Override
	@AConQATParameter(name = "classpath", description = "Add classpath element like "
			+ "directory or jar file.")
	public void addClassPathElement(
			@AConQATAttribute(name = "element", description = "Class path element") String pathElement) {
		super.addClassPathElement(pathElement);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "library-dir", description = "Add all jars contained (recursively) "
			+ "in the specified directory to the class path")
	public void addLibraryDirectory(
			@AConQATAttribute(name = "name", description = "Name of the library directory") String libraryDirName) {
		List<File> libraries = FileSystemUtils.listFilesRecursively(new File(
				libraryDirName), new FileExtensionFilter("jar"));
		for (File library : libraries) {
			addClassPathElement(library.getAbsolutePath());
		}
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "executee", description = "Name of the class "
			+ "or the jar file to be executed.", minOccurrences = 1, maxOccurrences = 1)
	public void setExecutee(
			@AConQATAttribute(name = "name", description = "Class name or name of executable jar file") String executeeName) {
		this.executeeName = executeeName;
	}

	/** Return executee name. */
	@Override
	protected String getExecuteeName() {
		return executeeName;
	}

	/** Return arguments. */
	@Override
	protected List<String> getArguments() {
		return arguments;
	}
}