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
import java.util.ArrayList;
import java.util.List;

import org.conqat.lib.commons.io.JavaUtils;
import org.conqat.lib.commons.string.StringUtils;
import org.conqat.engine.core.core.AConQATAttribute;
import org.conqat.engine.core.core.AConQATParameter;

/**
 * Base class for processors that execute Java applications.
 * 
 * @author deissenb
 * @author $Author: hummelb $
 * @version $Rev: 37013 $
 * @ConQAT.Rating GREEN Hash: 0B1D509EF5CCE9E216349CF4259229C6
 */
public abstract class JavaExecutorBase extends ProcessExecutorBase {

	/** List of VM arguments. */
	private final List<String> vmArguments = new ArrayList<String>();

	/** The class path for the executed application. */
	private final List<String> classPath = new ArrayList<String>();

	/** {@ConQAT.Doc} */
	@AConQATParameter(name = "vm-arg", description = "Arguments passed to the virtual machine.")
	public void addVMArgument(
			@AConQATAttribute(name = "value", description = "Argument value") String argument) {
		vmArguments.add(argument);
	}

	/** Add a class path element. */
	protected void addClassPathElement(String pathElement) {
		classPath.add(pathElement);
	}

	/** Obtain command to execute Java application. */
	@Override
	protected List<String> getCommand() {
		ArrayList<String> result = new ArrayList<String>();
		result.add(JavaUtils.obtainJavaExecutionCommand());
		result.addAll(vmArguments);
		if (!classPath.isEmpty()) {
			result.add("-cp");
			result.add(StringUtils.concat(classPath, File.pathSeparator));
		}
		String executeeName = getExecuteeName();
		if (executeeName.toLowerCase().endsWith(".jar")) {
			result.add("-jar");
		}
		result.add(executeeName);
		result.addAll(getArguments());
		return result;
	}

	/** Template method to obtain arguments for the executed Java application. */
	protected abstract List<String> getArguments();

	/**
	 * Template method to obtain name of executee. This can be a class name or
	 * an executable jar file.
	 */
	protected abstract String getExecuteeName();

}