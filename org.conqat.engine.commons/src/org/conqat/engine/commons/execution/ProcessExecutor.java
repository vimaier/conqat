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

import java.util.ArrayList;
import java.util.List;

import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;
import org.conqat.engine.core.core.AConQATProcessor;

/**
 * {@ConQAT.Doc}
 * 
 * @author deissenb
 * @author $Author: goede $
 * @version $Rev: 46403 $
 * @ConQAT.Rating GREEN Hash: 897AB22527966B98C12F1669A9E3EB0F
 */
@AConQATProcessor(description = "This processor executes an external process and "
		+ "waits until it terminates. The executed process is expected to create an "
		+ "output file whose name is returned by this processor. "
		+ ProcessExecutorBase.PROCESSOR_OUTPUT_FILE_ARG_DESCRIPTION)
public class ProcessExecutor extends ProcessExecutorBase {

	/** Path to the executable. */
	private String executablePath;

	/** {@ConQAT.Doc} */
	@Override
	@AConQATParameter(name = "arg", description = "Arguments for the process to execute")
	public void addArgument(
			@AConQATAttribute(name = "value", description = "Argument value") String argument) {
		super.addArgument(argument);
	}

	/** {@ConQAT.Doc} */
	@Override
	@AConQATParameter(name = PARAMETER_OUTPUT_FILE_ARG_NAME, description = PARAMETER_OUTPUT_FILE_ARG_DESCRIPTION, minOccurrences = 1, maxOccurrences = 1)
	public void setOutputFileArgument(
			@AConQATAttribute(name = "value", description = "Ouput file path") String outputFile) {
		super.setOutputFileArgument(outputFile);
	}

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "executable", description = "Name or fully qualified path of exectubale.", minOccurrences = 1, maxOccurrences = 1)
	public void setExecutable(
			@AConQATAttribute(name = "value", description = "Exectuable") String executable) {
		this.executablePath = executable;
	}

	/** Returns the executable path followed by the arguments. */
	@Override
	protected List<String> getCommand() {
		ArrayList<String> result = new ArrayList<String>();
		result.add(executablePath);
		result.addAll(arguments);
		return result;
	}

}